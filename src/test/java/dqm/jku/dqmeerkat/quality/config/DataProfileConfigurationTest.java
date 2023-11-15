package dqm.jku.dqmeerkat.quality.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;


/**
 * <h2>DataProfileConfigurationTest</h2>
 * <summary>{@link DataProfileConfiguration} test class</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 21.06.2022
 */
public class DataProfileConfigurationTest {

    @AfterEach
    public void tearDown() {
        DataProfileConfiguration.resetInstance();
    }

    @Test
    public void testLoadConfig() {
        // given

        // when
        var config = DataProfileConfiguration.getInstance();
        var generators = config.getGenerators();
        // then
        Assertions.assertNotNull(config);
        Assertions.assertNotNull(generators);
        Assertions.assertEquals(2, generators.size());
    }

    @Test
    public void testLoadConfigCached() {
        // given
        var config = DataProfileConfiguration.getInstance();

        // when
        var configCached = DataProfileConfiguration.getInstance();
        var generators = config.getGenerators();
        // then
        Assertions.assertNotNull(config);
        Assertions.assertNotNull(configCached);
        Assertions.assertSame(config, configCached);
        Assertions.assertNotNull(generators);
        Assertions.assertEquals(2, generators.size());
    }

    @Test
    public void testLoadConfigLargerJson() {
        // given


        // when
        var configJson = DataProfileConfiguration.getInstance("[\n" +
                "  {\n" +
                "    \"type\": \"full\",\n" +
                "    \"someParameter\": \"test\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"full\",\n" +
                "    \"someParameter\": \"test2\"\n" +
                "  }]");
        var generators = configJson.getGenerators();
        // then
        Assertions.assertNotNull(configJson);
        Assertions.assertEquals(2, generators.size());
    }

    @Test
    public void testLoadConfigFile() {
        // given
        var configFile = new File("src/test/resources/dqConfig.json");

        // when
        var config = DataProfileConfiguration.getInstance(configFile);
        var generators = config.getGenerators();

        // then
        Assertions.assertNotNull(config);
        Assertions.assertNotNull(generators);
        Assertions.assertEquals(3, generators.size());
    }

}
