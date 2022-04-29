package dqm.jku.dqmeerkat.resources.export;

import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;

/**
 * <h2>AbstractExporter</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 29.04.2022
 */
public interface AbstractExporter<T> {
    void export(T toExport, String fileName);

}
