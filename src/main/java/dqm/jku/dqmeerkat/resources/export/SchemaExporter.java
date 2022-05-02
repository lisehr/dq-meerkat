package dqm.jku.dqmeerkat.resources.export;

/**
 * <h2>AbstractExporter</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 29.04.2022
 */
public interface SchemaExporter<T> {
    void export(T toExport, String filePath, String fileName);

    String export(T toExport);

}
