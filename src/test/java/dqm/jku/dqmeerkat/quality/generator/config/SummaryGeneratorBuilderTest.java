package dqm.jku.dqmeerkat.quality.generator.config;

import dqm.jku.dqmeerkat.quality.config.DataSummaryConfigComponent;
import dqm.jku.dqmeerkat.quality.config.FullProfileConfigComponent;
import dqm.jku.dqmeerkat.quality.config.SummaryType;
import dqm.jku.dqmeerkat.quality.generator.MGSummarySkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.SpaceSavingSummarySkeletonGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SummaryGeneratorBuilderTest {

    @Test
    void fromConfigMGTest() {
        // given
        var builder = new SummaryGeneratorBuilder();
        var configComponent = new DataSummaryConfigComponent(1, SummaryType.MG_SUMMARY);

        // when
        var result = builder.fromConfig(configComponent);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(MGSummarySkeletonGenerator.class, result.get().getClass());
    }

    @Test
    void fromConfigSSTest() {
        // given
        var builder = new SummaryGeneratorBuilder();
        var configComponent = new DataSummaryConfigComponent(1, SummaryType.SPACE_SAVING_SUMMARY);

        // when
        var result = builder.fromConfig(configComponent);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(SpaceSavingSummarySkeletonGenerator.class, result.get().getClass());
    }

    @Test
    void fromConfigInvalidTest() {
        // given
        var builder = new SummaryGeneratorBuilder();
        var configComponent = new FullProfileConfigComponent();

        // when
        var result = builder.fromConfig(configComponent);

        // then
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void fromConfigNullTest() {
        // given
        var builder = new SummaryGeneratorBuilder();
        // when
        var ret = builder.fromConfig(null);
        // then
        assertNotNull(ret);
        assertTrue(ret.isEmpty());
    }
}