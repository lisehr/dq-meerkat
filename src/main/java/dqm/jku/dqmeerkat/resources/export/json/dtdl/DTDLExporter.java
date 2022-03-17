package dqm.jku.dqmeerkat.resources.export.json.dtdl;

import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.resources.export.Exporter;

/**
 * <h2>DTDLExporter</h2>
 * <summary>{@link Exporter} implementation for Digital Twin Definition Language (DTDL)</summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
public class DTDLExporter extends Exporter {
    public DTDLExporter(String path) {
        super(path, ".json");
    }

    @Override
    public void export(DSDKnowledgeGraph knowledgeGraph, String fileName) {
        for (Datasource ds : knowledgeGraph.getDatasources().values()) {

            System.out.println(ds.getConcepts());
        }
    }
}
