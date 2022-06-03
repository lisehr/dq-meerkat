package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>DataProfileSkeletonGenerator</h2>
 * <summary>
 * Base class for generating the basic structure of a {@link DataProfile}. Different implementations and their configuration
 * define the characteristics being monitored by the {@link DataProfile}
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 17.05.2022
 */
public abstract class DataProfileSkeletonGenerator {

    /**
     * where the DataProfile is annotated to
     */
    protected DSDElement element;


    public DataProfileSkeletonGenerator(DSDElement element) {
        this.element = element;
    }

    protected boolean checkValidity() {
        if (element != null &&
                element instanceof Attribute) {
            Attribute a = (Attribute) element;
            Class<?> clazz = a.getDataType();
            return clazz != null &&
                    (String.class.isAssignableFrom(clazz) ||
                    Number.class.isAssignableFrom(clazz) ||
                    clazz.equals(Object.class));
        }
        return false;
    }

    protected abstract List<ProfileStatistic> generateStatistics(DataProfile profile);

    public List<ProfileStatistic> generateSkeleton(DataProfile profile) {
        if (checkValidity())
            return generateStatistics(profile);
        // TODO Log error message, dont just return empty list
//        throw new IllegalStateException("Provided element" + element + "does not have measurable data type");
        return new ArrayList<>();
    }
}
