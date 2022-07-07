package dqm.jku.dqmeerkat.quality.generator;

/**
 * <h2>DataSummarySkeletonGenerator</h2>
 * <summary>Base class for SkeletonGenerators, that focus on calculating a summary of the data. It provides the
 * necessary parameters, such as the summary size k</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 06.07.2022
 */
public abstract class DataSummarySkeletonGenerator extends DataProfileSkeletonGenerator {

    protected final int k;

    public DataSummarySkeletonGenerator(int k) {
        this.k = k;
    }
}
