package dqm.jku.dqmeerkat.resources.export;

import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <h2>Exporter</h2>
 * <summary>
 * Abstract base class to export a {@link dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph} into a text file format
 * </summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractKnowledgeGraphExporter implements AbstractExporter<DSDKnowledgeGraph>{
    protected String path;
    protected String fileExtension;

    /**
     * <p>Checks if the given path exists. If not it is created. Given an error while creating the path false is returned</p>
     *
     * @return if the path does not exist and could not be created: false. If the path exists (independent of creation time): true
     */
    protected boolean ensurePathExists() {
        if (Files.notExists(Path.of(path))) {
            try {
                Files.createDirectory(Path.of(path));
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public abstract void export(DSDKnowledgeGraph knowledgeGraph, String fileName);
}
