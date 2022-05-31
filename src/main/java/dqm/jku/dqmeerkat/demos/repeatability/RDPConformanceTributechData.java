package dqm.jku.dqmeerkat.demos.repeatability;

import com.influxdb.client.domain.WritePrecision;
import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dtdl.DtdlRetriever;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.dtdl.dto.*;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV2;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.DataProfileCollection;
import dqm.jku.dqmeerkat.quality.DataProfiler;
import dqm.jku.dqmeerkat.quality.BatchedDataProfiler;
import dqm.jku.dqmeerkat.quality.conformance.CompositeRDPConformanceChecker;
import dqm.jku.dqmeerkat.quality.conformance.RDPConformanceChecker;
import dqm.jku.dqmeerkat.resources.export.json.dtdl.DataProfileExporter;
import dqm.jku.dqmeerkat.resources.export.json.dtdl.DtdlGraphExporter;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is to evaluate the extent to which the Tributech data loaded in batches of 1,000 records adheres to the RDP boundaries.
 * Threshold: 95 % (0.95)
 *
 * @author lisa
 */
public class RDPConformanceTributechData {
    private static final int FILEINDEX = 8;
    private static final double THRESHOLD = 0.1;        // Threshold indicates allowed deviation from reference value in percent
    private static final int RDP_SIZE = 500; // IF THIS IS LARGER THAN THE FILE SIZE THERE WILL BE NO DATA IN THE RDPs
    // i wasted way too much time on this...
    private static final int BATCH_SIZE = 500;        // Set to 1 to simulate streaming data


    public static void main(String[] args) throws IOException, InterruptedException, NoSuchMethodException {

        // retrieve DTDL stuff
//        DtdlRetriever retriever = new DtdlRetriever();
//        var statisticDto = ProfileStatisticDto.builder()
//                .metaData(new MetaDataDto("dtmi:scch:at:dq:ProfileStatistic;1"))
//                .category("the Mightiest of numbers")
//                .title("Seven")
//                .value("7")
//                .build();
//        var profileDto = DatasourceDto.builder()
//                .metaData(new MetaDataDto("dtmi:scch:at:dq:Dataprofile;1"))
//                .build();
//        var graph = new DtdlGraph();
//        graph.addDigitalTwin(profileDto);
//        graph.addDigitalTwin(statisticDto);
//        graph.addRelationship(RelationshipDto.builder()
//                .relationshipName("dataprofile_statistic")
//                .sourceId(profileDto.getDtId())
//                .targetId(statisticDto.getDtId())
//                .build());
//        var graphWrapper = new DtdlGraphWrapper(graph);
////        retriever.retrieve();
//        retriever.post(graphWrapper);

        ConnectorCSV conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/humidity_5000.csv");
        conn.setLabel("humidity_data");
        Datasource ds = conn.loadSchema("http:/example.com", "hum");

//            var dtdlInterface = new DTDLImporter().importDataList("src/main/resource/data/dsd.json");

        try (var dsdKnowledgeGraph = new DSDKnowledgeGraph(ds.getLabel())) {
            dsdKnowledgeGraph.addDatasource(ds);
//        dsdKnowledgeGraph.exportKGToFile("Test");
            // Initialization of RDPs
            for (Concept c : ds.getConcepts()) {
                RecordList rs = conn.getPartialRecordList(c, 0, RDP_SIZE);
                for (Attribute a : c.getSortedAttributes()) {
                    a.annotateProfile(rs);
                    // also print rdp per column
                    System.out.println(a.getProfileString());
                }
            }


            DataProfiler profiler = new BatchedDataProfiler(ds, conn, BATCH_SIZE, conn.getLabel());
            var ret = profiler.generateProfiles();

            // export data profile DTDL
            // streamId currently hardcoded, TODO extracted it from loaded json
            final var streamdtId = UUID.fromString( "e564d3eb-2729-4ce2-88b9-7ac369f65010");
            var exporter = new DataProfileExporter();
            var graphExporter = new DtdlGraphExporter(streamdtId);

            var jsonStrings = ret.stream()
                    .map(DataProfileCollection::getProfiles)
                    .map(dataProfiles -> dataProfiles.stream()
                            .map(graphExporter::export)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
            var dsToExport = ret.get(0).getProfiles().get(0);
            exporter.export(dsToExport, "output/", "test.json");


            // Continuous generation of DPs and conformance checking
            RDPConformanceChecker confChecker = new CompositeRDPConformanceChecker(THRESHOLD, ds, conn, BATCH_SIZE,
                    dsdKnowledgeGraph.getLabel());
            confChecker.runConformanceCheck();
//            // Finally: print evaluation report
            System.out.println(confChecker.getReport());


            try (InputStream input = new FileInputStream("src/main/resource/influx.properties")) {
                var properties = new Properties();
                properties.load(input);
                try (var influx = InfluxDBConnectionV2.builder()
                        .token(properties.getProperty("db.token"))
                        .orgId(properties.getProperty("db.orgId"))
                        .build()) {
                    influx.connect();
                    for (var collection : ret) {
                        collection.setTimestampOfCreation(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
                        for (DataProfile profile : collection.getProfiles()) {
                            dsdKnowledgeGraph.addProfilesToInflux(influx);
                            influx.write("default",
                                    profile.createMeasuringPoint(profile.getURI(),
                                            collection.getTimestampOfCreation().minus(10, ChronoUnit.SECONDS)
                                                    .atZone(ZoneOffset.UTC)
                                                    .toInstant()
                                                    .toEpochMilli(),
                                            WritePrecision.MS));
                        }
                        Thread.sleep(5000);
                        System.out.println("Interval break");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        System.out.println("Done! Exiting...");
    }
}
