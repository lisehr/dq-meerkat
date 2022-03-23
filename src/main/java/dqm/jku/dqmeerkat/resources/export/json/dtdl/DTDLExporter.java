package dqm.jku.dqmeerkat.resources.export.json.dtdl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.domain.dtdl.DtdlInterface;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
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

    /**
     * TODO transform knowledgegraph to dtdl objects
     * @param knowledgeGraph
     * @param fileName
     */
    @Override
    public void export(DSDKnowledgeGraph knowledgeGraph, String fileName) {
        for (Datasource ds : knowledgeGraph.getDatasources().values()) {
            var dtdlInterface = new DtdlInterface("dtmi:scch:at:dsd:Datasource;1");
            ObjectMapper mapper = new ObjectMapper();
            try {
                var ret = mapper.writeValueAsString(dtdlInterface);
                System.out.println(ret);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


        }
    }
}
