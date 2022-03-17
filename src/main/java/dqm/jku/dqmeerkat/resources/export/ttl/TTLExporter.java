package dqm.jku.dqmeerkat.resources.export.ttl;

import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.resources.export.Exporter;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <h2>TTLExporter</h2>
 * <summary>{@link Exporter} implementation for TTL files</summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
public class TTLExporter extends Exporter {
    public TTLExporter(String path) {
        super(path);
    }

    @Override
    public void export(DSDKnowledgeGraph knowledgeGraph) {
        try (PrintWriter out = new PrintWriter(path)) {

            ModelBuilder builder = new ModelBuilder();
            builder.setNamespace("dsd", "http://dqm.faw.jku.at/dsd" + "/");
            for (Datasource ds : knowledgeGraph.getDatasources().values()) {
                ds.getGraphModel(builder);

                for (Concept c : ds.getConcepts()) {
                    builder.subject("dsd:DataSources")
                            .add("dsd:hasDataSource", ds.getPrefix() + ":" + c.getLabel());
                }
            }
            Rio.write(builder.build(), out, RDFFormat.JSONLD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
