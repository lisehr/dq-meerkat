package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.quality.DataProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


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
        var generator = new FilePatternRecognitionGenerator("");
        attribute.setDataType(String.class); // any datatype will do
        var profile = new DataProfile();
        profile.setElem(attribute);
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(NR_OF_STATISTICS, ret.size());
    }

    @Test
    public void testGenerateNull() {
        // given
        // no data type is set!
        var generator = new FilePatternRecognitionGenerator("");
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateInvalid() {
        // given
        Concept concept = new Concept();
        var generator = new FilePatternRecognitionGenerator("");
        var profile = new DataProfile();
        profile.setElem(concept);
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(0, ret.size());
    }

    @Test
    public void testGenerateElementNull() {
        // given
        var generator = new FilePatternRecognitionGenerator("");
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        Assertions.assertEquals(0, ret.size());
    }
}
