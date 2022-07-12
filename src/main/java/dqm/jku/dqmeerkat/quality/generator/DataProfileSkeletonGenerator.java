package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import science.aist.seshat.Logger;

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

    protected static final Logger LOGGER = Logger.getInstance();


    /**
     * <p>
     * Checks if the given {@link DSDElement} is eligible for the skeleton generation. It is eligible, if its class
     * is correct, depending on the implementation of this method.
     * </p>
     *
     * @param element the element, which needs to be annotated by a data profile
     * @return true if the element is of correct type and structure, otherwise false
     */
    protected boolean checkValidity(DSDElement element) {
        if (element instanceof Attribute) {
            Attribute a = (Attribute) element;
            Class<?> clazz = a.getDataType();
            return clazz != null &&
                    (String.class.isAssignableFrom(clazz) ||
                            Number.class.isAssignableFrom(clazz) ||
                            clazz.equals(Object.class));
        }
        return false;
    }

    protected abstract List<AbstractProfileStatistic> generateStatistics(DataProfile profile);

    public List<AbstractProfileStatistic> generateSkeleton(DataProfile profile) {
        if (checkValidity(profile.getElem()))
            return generateStatistics(profile);
        LOGGER.warn("Provided element" + profile.getElem() + "does not have measurable data type");
        return new ArrayList<>();
    }
}
