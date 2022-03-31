package dqm.jku.dqmeerkat.resources.loading.json;

import java.util.List;

/**
 * <h2>Importer</h2>
 * <summary>Load data and transform it into domain objects of T. Use implementations to laod schemas for datasets</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 31.03.2022
 */
public abstract class Importer<T> {

    /**
     * <p>Opens the file at the given path and loads the schema into a domain object of T.</p>
     * @param path the path to the file, including filename and extension
     * @return domain object representing the contents of the file
     */
    public abstract T importData(String path);

    /**
     * <p>Opens the file at the given path and loads the schema into a domain object of T. It is assumed that the
     * schema contains multiple objects of T, hence a List is returned</p>
     *
     * @param path the path to the file, including filename and extension
     * @return List of T containing domain object representing the contents of the file
     */
    public abstract List<T> importDataList(String path);
}
