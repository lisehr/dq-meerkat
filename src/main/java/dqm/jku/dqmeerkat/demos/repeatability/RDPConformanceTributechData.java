package dqm.jku.dqmeerkat.demos.repeatability;

import be.ugent.ledc.pi.grex.Grex;
import be.ugent.ledc.pi.measure.QualityMeasure;
import be.ugent.ledc.pi.measure.predicates.GrexComboPredicate;
import be.ugent.ledc.pi.measure.predicates.GrexFormula;
import be.ugent.ledc.pi.measure.predicates.PatternPredicate;
import be.ugent.ledc.pi.measure.predicates.Predicate;
import be.ugent.ledc.pi.property.Property;
import be.ugent.ledc.pi.registries.MeasureRegistry;
import com.influxdb.client.domain.WritePrecision;
import dqm.jku.dqmeerkat.api.rest.client.DataApiTestClient;
import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.dtdl.DtdlRetriever;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV2;
import dqm.jku.dqmeerkat.quality.BatchedDataProfiler;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.DataProfileCollection;
import dqm.jku.dqmeerkat.quality.DataProfiler;
import dqm.jku.dqmeerkat.quality.config.DataProfileConfiguration;
import dqm.jku.dqmeerkat.quality.conformance.CompositeRDPConformanceChecker;
import dqm.jku.dqmeerkat.quality.conformance.RDPConformanceChecker;
import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;
import dqm.jku.dqmeerkat.resources.export.json.dtdl.DataProfileExporter;
import dqm.jku.dqmeerkat.resources.export.json.dtdl.DtdlGraphExporter;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is to evaluate the extent to which the Tributech data loaded in batches of 1,000 records adheres to the RDP boundaries.
 * Threshold: 95 % (0.95)
 *
 * @author lisa
 * @author meindl rainer.meindl@scch.at
 */
public class RDPConformanceTributechData {
    private static final int FILEINDEX = 8;
    private static final double THRESHOLD = 0.1;        // Threshold indicates allowed deviation from reference value in percent
    private static final int RDP_SIZE = 10; // IF THIS IS LARGER THAN THE FILE SIZE THERE WILL BE NO DATA IN THE RDPs
    // i wasted way too much time on this...
    private static final int BATCH_SIZE = 10;        // Set to 1 to simulate streaming data

    /**
     * Boolean flag for debugging purposes. If set to true, the program will delete the database before and after
     * the execution.
     */
    private static final boolean DELETE_DATABASE = false;

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchMethodException, URISyntaxException {
        // default configuration
        DataProfileConfiguration configuration = DataProfileConfiguration.getInstance();

        // oauth2 tests
        var client  = new DataApiTestClient("https://auth.int.dataspace-hub.com/auth/realms/int-node-b/protocol/openid-connect/token",
                null, "data-api", "", "https://data-api.int-node-b.dataspace-node.com/",
                "");

        var response = client.get("values/double/58645c10-d751-4c79-beb1-5f641deea2de");


        // setup Property for LEDC-PI
        Property property = Property.parseProperty("at.fh.scch/identifier#humidity");
        var numberPattern = "(\\d?\\d)\\.(\\d+)"; // check if it is valid humidity (i.E. 2 numbers front >0 numbers back)

        var predicates = new ArrayList<Predicate<String>>();
        predicates.add(new PatternPredicate(numberPattern, "Not a valid double"));
        predicates.add(new GrexComboPredicate(
                new GrexFormula( // GREX to ensure the numbers are within realistic/acceptable boundaries
                        Stream.of(new Grex("::int @1 branch& 20 > 60 <")).collect(Collectors.toList())

                ),
                numberPattern,
                "Invalid Min/Max values"
        ));

        var measure = new QualityMeasure<>(predicates, property, new URI("https://www.scch.at"),
                LocalDate.now(), 1); // create the mesure

        // test it
        var ret1 = measure.measure("22.33");
        var ret2 = measure.measure("1.3");
        var ret3 = measure.measure("61.3");
        var ret4 = measure.measure("1.");
        var ret5 = measure.measure("1213");
        var ret6 = measure.measure("ß1.33434");

        // Register in order to be able to dump it as JSON
        MeasureRegistry
                .getInstance()
                .registerMeasure(measure);
        // dump it into a file, in order to reuse it later
//        JSON.dump(new File("src/main/resource/data/ledc-pi_definitions.json"));

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
//        var response = retriever.get();
//        retriever.post(graphWrapper);


        ConnectorCSV conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/simulated/nonfaultyToFaulty.csv");
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
                    a.annotateProfile(rs, configuration.getGenerators().toArray(new DataProfileSkeletonGenerator[0]));
                    // also print rdp per column
                    System.out.println(a.getProfileString());
                }
            }


            DataProfiler profiler = new BatchedDataProfiler(ds, conn, BATCH_SIZE, conn.getLabel(),
                    DataProfileConfiguration.getInstance());
            var ret = profiler.generateProfiles();

            // export data profile DTDL
            // streamId currently hardcoded, TODO extracted it from loaded json
            final var streamdtId = UUID.fromString("e564d3eb-2729-4ce2-88b9-7ac369f65010");
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
                    dsdKnowledgeGraph.getLabel(), DataProfileConfiguration.getInstance());
            confChecker.runConformanceCheck();
//            // Finally: print evaluation report
            // note that depending if the ledc pi is active or not, the evaluation report might not reach 1.0
            System.out.println(confChecker.getReport());


            try (InputStream input = new FileInputStream("src/main/resource/influx.properties")) {
                var properties = new Properties();
                properties.load(input);
                try (var influx = InfluxDBConnectionV2.builder()
                        .token(properties.getProperty("db.token"))
                        .orgId(properties.getProperty("db.orgId"))
                        .build()) {
                    influx.connect();
                    if (DELETE_DATABASE)
                        influx.deleteDatabase("default");
                    var token = influx.createDatabase("default", 0);

                    for (var collection : ret) {
                        collection.setTimestampOfCreation(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
                        for (DataProfile profile : collection.getProfiles()) {
                            dsdKnowledgeGraph.addProfilesToInflux(influx);
                            // also write the RDP for a "target line" relative to the dps
                            influx.write("default",
                                    profile.createMeasuringPoint(profile.getURI(),
                                            collection.getTimestampOfCreation()
                                                    .atZone(ZoneOffset.UTC)
                                                    .toInstant()
                                                    .toEpochMilli(),
                                            WritePrecision.MS));
                        }
                        Thread.sleep(3000);
                        System.out.println("Interval break");
                    }
                    if (DELETE_DATABASE)
                        influx.deleteDatabase("default");
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
