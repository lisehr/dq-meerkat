package dqm.jku.dqmeerkat.resources.export;

/**
 * <h2>SchemaExporter</h2>
 * <summary>Interface defining how the schema of generic objects can be exported and persisted</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 29.04.2022
 */
public interface SchemaExporter<T> {
    /**
     * <p>Transforms the given into a schema definition and exports it into a file at the given path, with the given
     * name and file ending.</p>
     *
     * @param toExport the object, whose schema is to be exported to the file at the given path with the given name
     * @param filePath the path, in which directory the schema should be persisted in
     * @param fileName the name of the generated file, containing both the file name and extension
     */
    void export(T toExport, String filePath, String fileName);

    /**
     * <p>Transform the given object into a schema definition and export it as a string</p>
     *
     * @param toExport the object, whose schema is to be exported
     * @return a String describing the schema in another format
     */
    String export(T toExport);

}
