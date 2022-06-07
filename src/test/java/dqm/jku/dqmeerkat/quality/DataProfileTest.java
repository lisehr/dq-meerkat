package dqm.jku.dqmeerkat.quality;

import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.FilePatternRecognitionGenerator;
import dqm.jku.dqmeerkat.quality.generator.FullSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <h2>DataProfileTest</h2>
 * <summary>Test class for {@link DataProfile}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 07.06.2022
 */
public class DataProfileTest {
    private DSDElement dsdElement;
    private RecordList recordList;
    private Concept concept;

    @Before
    public void setup() throws IOException {
        var conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/humidity_5000.csv");
        var ds = conn.loadSchema("http:/example.com", "hum");
        concept = ds.getConcepts().stream().findFirst().orElseThrow();
        dsdElement = concept.getAttributes().stream().findFirst().orElseThrow();
        recordList = conn.getRecordList(concept);
    }

    @After
    public void tearDown() {
        // ensure jep is always off unless specifically stated in test
        Constants.ENABLE_JEP = false;
    }


    @Test
    public void testDefaultCtor() {
        // given/when
        var dataprofile = new DataProfile();
        var statistics = dataprofile.getStatistics();
        var uri = dataprofile.getURI();
        var element = dataprofile.getElem();

        // then
        assertNull(element);
        assertEquals(0, statistics.size());
        assertNull(uri);
    }

    @Test
    public void testDefaultSkeletonFilePathCtor() throws NoSuchMethodException {
        // given
        // when
        var dataprofile = new DataProfile(recordList, dsdElement,
                "src/main/java/dqm/jku/dqmeerkat/resources/patterns/pattern_test.in");
        var statistics = dataprofile.getStatistics();
        // then
        assertEquals(18, statistics.size());

    }

    @Test
    public void testDefaultSkeletonJEPCtor() throws NoSuchMethodException {
        // given
        Constants.ENABLE_JEP = true;
        // when/then
        /* as JEP is not linked and only tested, it is good enough for now to see if the exceptions triggers
        as that states that it tried to instantiate the JEP components
         */
        assertThrows(UnsatisfiedLinkError.class, () -> new DataProfile(recordList, concept));

    }

    @Test
    public void testDefaultSkeletonCtor() throws NoSuchMethodException {
        // given
        // when
        var dataprofile = new DataProfile(recordList, dsdElement);

        // then
        assertEquals(17, dataprofile.getStatistics().size());
    }

    @Test
    public void testCustomSkeletonCtor() throws NoSuchMethodException {
        // given
        // when
        var dataprofile = new DataProfile(recordList, dsdElement,
                new FilePatternRecognitionGenerator(dsdElement,
                        "src/main/java/dqm/jku/dqmeerkat/resources/patterns/pattern_test.in"),
                new DataProfileSkeletonGenerator(dsdElement) {
                    @Override
                    protected List<ProfileStatistic> generateStatistics(DataProfile profile) {
                        return List.of(new NumRows(profile));
                    }
                });

        // then
        assertEquals(2, dataprofile.getStatistics().size());
    }

    @Test
    public void testCustomSkeletonPatternCtor() throws NoSuchMethodException {
        // given
        // when
        var dataprofile = new DataProfile(recordList, dsdElement,
                new FilePatternRecognitionGenerator(dsdElement,
                        "src/main/java/dqm/jku/dqmeerkat/resources/patterns/pattern_test.in"),
                new FullSkeletonGenerator(dsdElement));

        // then
        assertEquals(18, dataprofile.getStatistics().size());
    }

}
