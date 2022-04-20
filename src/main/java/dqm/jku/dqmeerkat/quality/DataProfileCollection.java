package dqm.jku.dqmeerkat.quality;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>DataProfileCollection</h2>
 * <summary>Contains a collection of {@link DataProfile}, that describe all {@link dqm.jku.dqmeerkat.dsd.elements.Attribute} of
 * a {@link dqm.jku.dqmeerkat.dsd.elements.Concept} in one easy to use domain object</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.04.2022
 */
public class DataProfileCollection {
    private final List<DataProfile> profiles = new ArrayList<>();


    public void addDataProfile(DataProfile dataProfile) {
        profiles.add(dataProfile);
    }
}
