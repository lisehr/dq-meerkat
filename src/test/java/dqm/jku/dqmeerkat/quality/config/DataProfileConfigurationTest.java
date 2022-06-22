package dqm.jku.dqmeerkat.quality.config;

import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * <h2>DataProfileConfigurationTest</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 21.06.2022
 */
public class DataProfileConfigurationTest {

    @After
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
        assertNotNull(config);
        assertNotNull(generators);
        assertEquals(2, generators.size());
    }

    @Test
    public void testLoadConfigCached() {
        // given
        var config = DataProfileConfiguration.getInstance();

        // when
        var configCached = DataProfileConfiguration.getInstance();
        var generators = config.getGenerators();
        // then
        assertNotNull(config);
        assertNotNull(configCached);
        assertSame(config, configCached);
        assertNotNull(generators);
        assertEquals(2, generators.size());
    }

    @Test
    public void testLoadConfigJson() {
        // given


        // when
        var configJson = DataProfileConfiguration.getInstance("[\n" +
                "  {\n" +
                "    \"type\": \"ledcpi\",\n" +
                "    \"ledcPiId\": \"at.fh.scch/identifier#humidity:*\",\n" +
                "    \"ledcPiFilePath\": \"src/main/resource/data/ledc-pi_definitions.json\"\n" +
                "  }]");
        var generators = configJson.getGenerators();
        // then
        assertNotNull(configJson);
        assertEquals(1, generators.size());
    }

    @Test
    public void testLoadConfigLargerJson() {
        // given


        // when
        var configJson = DataProfileConfiguration.getInstance("[\n" +
                "  {\n" +
                "    \"type\": \"ledcpi\",\n" +
                "    \"ledcPiId\": \"at.fh.scch/identifier#humidity:*\",\n" +
                "    \"ledcPiFilePath\": \"src/main/resource/data/ledc-pi_definitions.json\"\n" +
                "  }," +
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
        assertNotNull(configJson);
        assertEquals(3, generators.size());
    }

    @Test
    public void testLoadConfigFile() {
        // given
        var configFile = new File("src/test/resources/dqConfig.json");

        // when
        var config = DataProfileConfiguration.getInstance(configFile);
        var generators = config.getGenerators();

        // then
        assertNotNull(config);
        assertNotNull(generators);
        assertEquals(4, generators.size());
    }

}
