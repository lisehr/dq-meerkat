package dqm.jku.dqmeerkat.quality;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
@Getter
public class DataProfileCollection {

    private final List<DataProfile> profiles = new ArrayList<>();

    @Setter // setter to override for testing
    private LocalDateTime timestampOfCreation;

    private final String uri;

    public DataProfileCollection(LocalDateTime timestampOfCreation, String uri) {
        this.timestampOfCreation = timestampOfCreation;
        this.uri = uri;
    }

    public DataProfileCollection(String uri) {
        this(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC), uri);
    }


    public void addDataProfile(DataProfile dataProfile) {
        profiles.add(dataProfile);
    }
}
