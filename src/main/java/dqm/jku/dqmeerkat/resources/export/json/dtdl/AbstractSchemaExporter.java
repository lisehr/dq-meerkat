package dqm.jku.dqmeerkat.resources.export.json.dtdl;

import dqm.jku.dqmeerkat.resources.export.SchemaExporter;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <h2>AbstractSchemaExporter</h2>
 * <summary>Base class for {@link SchemaExporter}, that handles the file writing</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 02.05.2022
 */
public abstract class AbstractSchemaExporter<T> implements SchemaExporter<T> {
    @SneakyThrows
    @Override
    public void export(T toExport, String filePath, String fileName) {
        var jsonString = export(toExport);
        Files.createDirectories(Path.of(filePath));
        Files.write(Path.of(filePath + fileName), jsonString.getBytes(StandardCharsets.UTF_8));
    }
}
