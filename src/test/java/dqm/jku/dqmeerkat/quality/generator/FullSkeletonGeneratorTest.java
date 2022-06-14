package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.quality.DataProfile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <h2>FullSkeletonGeneratorTest</h2>
 * <summary>Test class for {@link FullSkeletonGenerator}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 03.06.2022
 */
public class FullSkeletonGeneratorTest {

    private static final int NR_OF_STATISTICS = 17;

    @Test
    public void testGenerate() {
        // given
        var attribute = new Attribute();
        var generator = new FullSkeletonGenerator();
        attribute.setDataType(String.class); // any datatype will do
        var profile = new DataProfile();
        profile.setElem(attribute);
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(NR_OF_STATISTICS, ret.size());
    }

    @Test
    public void testGenerateNull() {
        // given
        var attribute = new Attribute(); // no data type is set!
        var generator = new FullSkeletonGenerator();
        var profile = new DataProfile();
        profile.setElem(attribute);
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateInvalid() {
        var attribute = new Concept(); // concept is not correct type
        var generator = new FullSkeletonGenerator();
        var profile = new DataProfile();
        profile.setElem(attribute);
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateElementNull() {
        // given
        var generator = new FullSkeletonGenerator();
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }
}
