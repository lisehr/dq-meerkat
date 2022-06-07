package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.util.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <h2>IsolationForestSkeletonGeneratorTest</h2>
 * <summary>Test class for {@link IsolationForestSkeletonGenerator}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 07.06.2022
 */
public class IsolationForestSkeletonGeneratorTest {
    private static final int NR_OF_STATISTICS = 3;

    @Before
    public void setup() {
        Constants.ENABLE_JEP = true; // temporarily "enable" it
    }

    @After
    public void teardown() {
        Constants.ENABLE_JEP = false;
    }

    @Test
    public void testGenerate() {
        // given
        var concept = new Concept();
        var generator = new IsolationForestSkeletonGenerator(concept);

        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(NR_OF_STATISTICS, ret.size());
    }

    @Test
    public void testGenerateNoJEP() {
        // given
        Constants.ENABLE_JEP = false;
        var concept = new Concept(); // no data type is set!
        var generator = new IsolationForestSkeletonGenerator(concept);
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateInvalid() {
        var attribute = new Attribute(); // concept is not correct type
        var generator = new IsolationForestSkeletonGenerator(attribute);
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateElementNull() {
        // given
        var generator = new IsolationForestSkeletonGenerator(null);
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }
}
