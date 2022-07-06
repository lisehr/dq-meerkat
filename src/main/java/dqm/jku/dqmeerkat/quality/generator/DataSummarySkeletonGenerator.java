package dqm.jku.dqmeerkat.quality.generator;

/**
 * <h2>DataSummarySkeletonGenerator</h2>
 * <summary>TODO Insert do cheader</summary>
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
