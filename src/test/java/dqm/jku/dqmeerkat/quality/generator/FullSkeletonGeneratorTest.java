package dqm.jku.dqmeerkat.quality.generator;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.quality.DataProfile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <h2>FullSkeletonGeneratorTest</h2>
 * <summary>TODO Insert do cheader</summary>
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
        var generator = new FullSkeletonGenerator(attribute);
        attribute.setDataType(String.class); // any datatype will do
        var profile = new DataProfile();
        // when
        var ret = generator.generateSkeleton(profile);
        // then
        assertEquals(NR_OF_STATISTICS, ret.size());
    }
}
