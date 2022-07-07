package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <h2>IsolationForestSkeletonGeneratorTest</h2>
 * <summary>Test class for {@link IsolationForestSkeletonGenerator}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 07.06.2022
 */
public class IsolationForestSkeletonGeneratorTest {
    private static final int NR_OF_STATISTICS = 3;

    @BeforeEach
    public void setup() {
        Constants.ENABLE_JEP = true; // temporarily "enable" it
    }

    @AfterEach
    public void teardown() {
        Constants.ENABLE_JEP = false;
    }

    @Test
    public void testGenerate() {
        // given
        var concept = new Concept();
        var generator = new IsolationForestSkeletonGenerator();

        var profile = new DataProfile();
        profile.setElem(concept);
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(NR_OF_STATISTICS, ret.size());
    }

    @Test
    public void testGenerateNoJEP() {
        // given
        Constants.ENABLE_JEP = false;
        var concept = new Concept(); // no data type is set!
        var generator = new IsolationForestSkeletonGenerator();
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateInvalid() {
        var attribute = new Attribute(); // concept is not correct type
        var generator = new IsolationForestSkeletonGenerator();
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateElementNull() {
        // given
        var generator = new IsolationForestSkeletonGenerator();
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(0, ret.size());
    }
}
