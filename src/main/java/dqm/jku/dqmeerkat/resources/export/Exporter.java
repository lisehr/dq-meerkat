package dqm.jku.dqmeerkat.resources.export;

import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import lombok.AllArgsConstructor;

/**
 * <h2>Exporter</h2>
 * <summary>
 * Abstract base class to export a {@link dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph} into a text file format
 * </summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
@AllArgsConstructor
public abstract class Exporter {
    protected String path;

    public abstract void export(DSDKnowledgeGraph knowledgeGraph);
}
