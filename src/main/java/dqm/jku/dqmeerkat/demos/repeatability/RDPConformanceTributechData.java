package dqm.jku.dqmeerkat.demos.repeatability;

import com.influxdb.client.domain.WritePrecision;
import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV2;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.DataProfiler;
import dqm.jku.dqmeerkat.quality.TributechDataProfiler;
import dqm.jku.dqmeerkat.quality.conformance.AllInOneRDPConformanceChecker;
import dqm.jku.dqmeerkat.quality.conformance.CompositeRDPConformanceChecker;
import dqm.jku.dqmeerkat.quality.conformance.RDPConformanceChecker;
import dqm.jku.dqmeerkat.resources.export.json.dtdl.DTDLExporter;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Properties;

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

        ConnectorCSV conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/humidity_5000.csv");
        conn.setLabel("humidity_data");
        Datasource ds = conn.loadSchema("http:/example.com", "hum");

//            var dtdlInterface = new DTDLImporter().importDataList("src/main/resource/data/dsd.json");

        try (var dsdKnowledgeGraph = new DSDKnowledgeGraph(ds.getLabel())) {
            dsdKnowledgeGraph.addDatasource(ds);
            dsdKnowledgeGraph.setExporter(new DTDLExporter(("")));
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

            DataProfiler profiler = new TributechDataProfiler(ds, conn, BATCH_SIZE, conn.getLabel());
            var ret = profiler.generateProfiles();


            // Continuous generation of DPs and conformance checking
            RDPConformanceChecker confChecker = new CompositeRDPConformanceChecker(THRESHOLD, ds, conn, BATCH_SIZE,
                    dsdKnowledgeGraph.getLabel());
            confChecker.runConformanceCheck();
//            // Finally: print evaluation report
            System.out.println(confChecker.getReport());


            try (InputStream input = new FileInputStream("src/main/resource/config.properties")) {
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
                        Thread.sleep(60000);
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
