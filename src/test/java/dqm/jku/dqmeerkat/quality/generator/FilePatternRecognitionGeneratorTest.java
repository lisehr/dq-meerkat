package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.quality.DataProfile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <h2>FilePatternRecognitionGeneratorTest</h2>
 * <summary>Test class for {@link FilePatternRecognitionGenerator}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 07.06.2022
 */
public class FilePatternRecognitionGeneratorTest {
    private static final int NR_OF_STATISTICS = 1;

    @Test
    public void testGenerate() {
        // given
        var attribute = new Attribute();
        var generator = new FilePatternRecognitionGenerator(attribute, "");
        attribute.setDataType(String.class); // any datatype will do
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(NR_OF_STATISTICS, ret.size());
    }

    @Test
    public void testGenerateNull() {
        // given
        var attribute = new Attribute(); // no data type is set!
        var generator = new FilePatternRecognitionGenerator(attribute, "");
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateInvalid() {
        // given
        var attribute = new Concept(); // concept is not correct type
        var generator = new FilePatternRecognitionGenerator(attribute, "");
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateElementNull() {
        // given
        var generator = new FilePatternRecognitionGenerator(null, "");
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(0, ret.size());
    }
}
