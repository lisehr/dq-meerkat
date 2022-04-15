package dqm.jku.dqmeerkat.demos.repeatability;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV2;
import dqm.jku.dqmeerkat.quality.RDPConformanceChecker;
import dqm.jku.dqmeerkat.resources.export.json.dtdl.DTDLExporter;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final int BATCH_SIZE = 50;        // Set to 1 to simulate streaming data


    public static void main(String[] args) throws IOException, InterruptedException, NoSuchMethodException {
        for (var i = 0; i < 10; i++) {
            ConnectorCSV conn = FileSelectionUtil.getConnectorCSV(
                    String.format("src/main/resource/data/split_test_data/split_data_%d.csv", i));
            conn.setLabel("humidity_data");
            System.out.println("read file " + i);
            Datasource ds = conn.loadSchema("http:/example.com/humidity_data", "ex");


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
                    }
                }


                // Continuous generation of DPs and conformance checking
                RDPConformanceChecker confChecker = new RDPConformanceChecker(ds, conn, BATCH_SIZE, THRESHOLD);
                confChecker.run();
                // Finally: print evaluation report
                System.out.println(confChecker.getReport());

                // also print rdp per column
                for (var concept : ds.getConcepts()) {
                    for (var attribute : concept.getSortedAttributes()) {
                        System.out.println(attribute.getProfileString());
                    }
                }
                try (InputStream input = new FileInputStream("src/main/resource/config.properties")) {
                    var properties = new Properties();
                    properties.load(input);
                    try (var influx = InfluxDBConnectionV2.builder()
                            .token(properties.getProperty("db.token"))
                            .orgId(properties.getProperty("db.orgId"))
                            .build()) {
                        influx.connect();
                        dsdKnowledgeGraph.addProfilesToInflux(influx);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Done with " + i + " now sleeping");
                Thread.sleep(60000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

        System.out.println("Done! Exiting...");
    }
}
