package dqm.jku.dqmeerkat.demos.repeatability;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.RDPConformanceChecker;
import dqm.jku.dqmeerkat.resources.export.json.dtdl.DTDLExporter;
import dqm.jku.dqmeerkat.resources.loading.json.dtdl.DTDLImporter;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;

import java.io.IOException;

/**
 * This class is to evaluate the extent to which the Tributech data loaded in batches of 1,000 records adheres to the RDP boundaries.
 * Threshold: 95 % (0.95)
 *
 * @author lisa
 */
public class RDPConformanceTributechData {
    private static final int FILEINDEX = 8;
    private static final double THRESHOLD = 0.1;        // Threshold indicates allowed deviation from reference value in percent
    private static final int RDP_SIZE = 1000;
    private static final int BATCH_SIZE = 1000;        // Set to 1 to simulate streaming data

    public static void main(String args[]) throws IOException, InterruptedException, NoSuchMethodException {
        ConnectorCSV conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/humidity_5000.csv");
        Datasource ds = conn.loadSchema();

        var dtdlInterface = new DTDLImporter().importDataList("src/main/resource/data/dsd.json");

        var dsdKnowledgeGraph = new DSDKnowledgeGraph(ds.getLabel());
        dsdKnowledgeGraph.addDatasource(ds);
        dsdKnowledgeGraph.setExporter(new DTDLExporter(("")));
        dsdKnowledgeGraph.exportKGToFile("Test");
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
        System.out.println("Done! Exiting...");
    }
}
