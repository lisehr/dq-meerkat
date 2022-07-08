package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpaceSavingSummarySkeletonGeneratorTest {

    @Test
    void generateStatistics() {
        // given
        var generator = new SpaceSavingSummarySkeletonGenerator(10);
        var profile = new DataProfile();

        // when
        var statistics = generator.generateStatistics(profile);

        // then
        assertNotNull(statistics);
        assertEquals(1, statistics.size());
    }
}