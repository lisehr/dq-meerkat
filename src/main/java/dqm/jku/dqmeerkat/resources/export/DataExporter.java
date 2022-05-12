package dqm.jku.dqmeerkat.resources.export;

/**
 * <h2>DataExporter</h2>
 * <p>Defines how POJOs are exported to another format. Should always comply to a schema extracted by a
 * {@link SchemaExporter}</p>
 *
 * @param <T>
 */
public interface DataExporter<T> {
    /**
     * <p>Transforms the given object into a schema compliant machine readable data format and exports it into a file at
     * the given path, with the given name and file ending.</p>
     *
     * @param toExport the object, which is to be exported to the file at the given path with the given name
     * @param filePath the path, in which directory the schema should be persisted in
     * @param fileName the name of the generated file, containing both the file name and extension
     */
    void export(T toExport, String filePath, String fileName);

    /**
     * <p>Transforms the given object into a schema compliant machine readable data format</p>
     *
     * @param toExport the object, which is to be exported into a string
     * @return a string containing the serialized object
     */

    String export(T toExport);
}
