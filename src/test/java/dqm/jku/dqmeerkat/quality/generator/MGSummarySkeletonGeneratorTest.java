package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.quality.DataProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MGSummarySkeletonGeneratorTest {

    @Test
    void generateStatistics() {
        // given
        var generator = new MGSummarySkeletonGenerator(10);
        var profile = new DataProfile();
        // when
        var statistics = generator.generateStatistics(profile);

        // then
        assertNotNull(statistics);
        assertEquals(1, statistics.size());
    }
}