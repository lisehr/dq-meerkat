package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern.ledcpi;

import be.ugent.ledc.pi.grex.Grex;
import be.ugent.ledc.pi.measure.QualityMeasure;
import be.ugent.ledc.pi.measure.predicates.GrexComboPredicate;
import be.ugent.ledc.pi.measure.predicates.GrexFormula;
import be.ugent.ledc.pi.measure.predicates.PatternPredicate;
import be.ugent.ledc.pi.measure.predicates.Predicate;
import be.ugent.ledc.pi.property.Property;
import be.ugent.ledc.pi.property.PropertyParseException;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * <h2>LEDCPIPatternRecognitionTest</h2>
 * <summary>Test class for {@link LEDCPIPatternRecognition}</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 09.06.2022
 */
public class LEDCPIPatternRecognitionTest {


    private Attribute dsdElement;
    private RecordList recordList;

    @Before
    public void setup() throws IOException {
        var conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/humidity_5000.csv");
        var ds = conn.loadSchema("http:/example.com", "hum");
        var concept = ds.getConcepts().stream().findFirst().orElseThrow();
        dsdElement = concept.getAttribute("values");
        recordList = conn.getRecordList(concept);
    }

    @Test
    public void testPathCtor() throws NoSuchMethodException {
        // given


        // when
        LEDCPIPatternRecognition recognition = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"));

        // then
        assertNotNull(recognition);
        assertEquals("Pattern recognition", recognition.getLabel());
        assertEquals(StatisticTitle.pattern, recognition.getTitle());
        assertEquals(StatisticCategory.dti, recognition.getCat());
    }

    @Test
    public void testComponentCtor() throws NoSuchMethodException, PropertyParseException, URISyntaxException {
        // given
        Property property = Property.parseProperty("at.fh.scch/identifier#humidity");
        var numberPattern = "(\\d?\\d)\\.(\\d+)";

        var predicates = new ArrayList<Predicate<String>>();
        predicates.add(new PatternPredicate(numberPattern, "Not a valid double"));
        predicates.add(new GrexComboPredicate(
                new GrexFormula(
                        Stream.of(new Grex("::int @1 branch& 20 > 60 <")).collect(Collectors.toList())

                ),
                numberPattern,
                "Invalid Min/Max values"
        ));

        var measure = new QualityMeasure<>(predicates, property, new URI("https://www.scch.at"),
                LocalDate.now(), 1);

        // when
        LEDCPIPatternRecognition recognition = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                measure);

        // then
        assertNotNull(recognition);
        assertEquals("Pattern recognition", recognition.getLabel());
        assertEquals(StatisticTitle.pattern, recognition.getTitle());
        assertEquals(StatisticCategory.dti, recognition.getCat());
    }

    @Test
    public void testMeasureCtor() throws NoSuchMethodException, URISyntaxException, PropertyParseException {
        // given
        var numberPattern = "(\\d?\\d)\\.(\\d+)";

        var predicates = new ArrayList<Predicate<String>>();
        predicates.add(new PatternPredicate(numberPattern, "Not a valid double"));
        predicates.add(new GrexComboPredicate(
                new GrexFormula(
                        Stream.of(new Grex("::int @1 branch& 20 > 60 <")).collect(Collectors.toList())

                ),
                numberPattern,
                "Invalid Min/Max values"
        ));
        var uri = new URI("https://www.scch.at");

        // when
        LEDCPIPatternRecognition recognition = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                predicates, "at.fh.scch/identifier#humidity", uri, LocalDate.now(), 1);

        // then
        assertNotNull(recognition);
        assertEquals("Pattern recognition", recognition.getLabel());
        assertEquals(StatisticTitle.pattern, recognition.getTitle());
        assertEquals(StatisticCategory.dti, recognition.getCat());
    }

    @Test
    public void testCalculation() throws NoSuchMethodException {
        // given
        LEDCPIPatternRecognition recognition = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"));

        // when
        recognition.calculation(recordList, null);
        var ret = recognition.getValue();

        // then
        assertNotNull(ret);
        assertTrue(ret instanceof Number);
        assertEquals(1D, ret);
    }

    @Test
    public void testCalculationNumeric() throws NoSuchMethodException {
        // given
        LEDCPIPatternRecognition recognition = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"));

        // when
        recognition.calculationNumeric(recordList.toList().stream()
                .map(record -> (Number) record.getField(dsdElement))
                .collect(Collectors.toList()), null);
        var ret = recognition.getValue();

        // then
        assertNotNull(ret);
        assertTrue(ret instanceof Number);
        assertEquals(1D, ret);
    }

    @Test
    public void testCheckConformance() throws NoSuchMethodException {
        // given
        LEDCPIPatternRecognition recognition = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"));
        LEDCPIPatternRecognition other = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"));
        recognition.calculation(recordList, null);
        other.calculation(recordList, null);

        // when
        var ret = recognition.checkConformance(other, 1D);
        // then
        assertTrue(ret); // as ProfileStatistics are equal this should work out with threshold of 1
    }

    @Test
    public void testCheckConformanceFail() throws NoSuchMethodException {
        // given
        LEDCPIPatternRecognition recognition = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"));
        LEDCPIPatternRecognition other = new LEDCPIPatternRecognition(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"));
        recognition.calculation(recordList, null);
        other.calculationNumeric(recordList.toList()
                .stream()
                .skip(2000)
                .map(record -> (Double) record.getField("values"))
                .map(number -> number - 20D) // introduce some error
                .collect(Collectors.toList()), null);

        // when
        var ret = recognition.checkConformance(other, .1D);

        // then
        assertFalse(ret);
    }
}
