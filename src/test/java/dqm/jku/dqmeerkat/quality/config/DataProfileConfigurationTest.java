package dqm.jku.dqmeerkat.quality.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <h2>DataProfileConfigurationTest</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 21.06.2022
 */
public class DataProfileConfigurationTest {

    @Test
    public void testLoadConfig() {
        // given

        // when
        var config = DataProfileConfiguration.getInstance();
        var generators = config.getGenerators();
        // then
        assertNotNull(config);
        assertNotNull(generators);
        assertEquals(2, generators.size());
    }

    @Test
    public void testLoadConfigCached() {
        // given

        // when

        // then
        fail("not implemented!");
    }
}
