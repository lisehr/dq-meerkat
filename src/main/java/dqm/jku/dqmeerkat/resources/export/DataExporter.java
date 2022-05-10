package dqm.jku.dqmeerkat.resources.export;

public interface DataExporter<T> {
    void export(T toExport, String filePath, String fileName);

    String export(T toExport);
}
