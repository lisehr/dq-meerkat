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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    @BeforeEach
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
        LEDCPIPatternRecognition<Double> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), Double.class);

        // then
        Assertions.assertNotNull(recognition);
        Assertions.assertEquals("Pattern recognition", recognition.getLabel());
        Assertions.assertEquals(StatisticTitle.pattern, recognition.getTitle());
        Assertions.assertEquals(StatisticCategory.dti, recognition.getCat());
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
        LEDCPIPatternRecognition<Double> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                measure, Double.class);

        // then
        Assertions.assertNotNull(recognition);
        Assertions.assertEquals("Pattern recognition", recognition.getLabel());
        Assertions.assertEquals(StatisticTitle.pattern, recognition.getTitle());
        Assertions.assertEquals(StatisticCategory.dti, recognition.getCat());
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
        LEDCPIPatternRecognition<Double> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                predicates, "at.fh.scch/identifier#humidity", uri, LocalDate.now(), 1, Double.class);

        // then
        Assertions.assertNotNull(recognition);
        Assertions.assertEquals("Pattern recognition", recognition.getLabel());
        Assertions.assertEquals(StatisticTitle.pattern, recognition.getTitle());
        Assertions.assertEquals(StatisticCategory.dti, recognition.getCat());
    }

    @Test
    public void testCalculation() throws NoSuchMethodException {
        // given
        LEDCPIPatternRecognition<Double> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), Double.class);

        // when
        recognition.calculation(recordList, null);
        var ret = recognition.getValue();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertEquals(.8598D, ret);
    }

    @Test
    public void testCheckConformance() throws NoSuchMethodException {
        // given
        LEDCPIPatternRecognition<Double> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), Double.class);
        LEDCPIPatternRecognition<Double> other = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), Double.class);
        recognition.calculation(recordList, null);
        other.calculation(recordList, null);

        // when
        var ret = recognition.checkConformance(other, 1D);
        // then
        Assertions.assertTrue(ret); // as ProfileStatistics are equal this should work out with threshold of 1
    }

    @Test
    public void testCheckConformanceFail() throws NoSuchMethodException {
        // given
        LEDCPIPatternRecognition<Double> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), Double.class);
        LEDCPIPatternRecognition<Double> other = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), Double.class);
        recognition.calculation(recordList, null);
        other.calculation(new RecordList(recordList.toList()
                .stream()
                .skip(2000)
                .map(record -> (Double) record.getField("values"))
                .map(number -> number - 20D) // introduce some error
                .collect(Collectors.toList()), "dummy"), null);

        // when
        var ret = recognition.checkConformance(other, .1D);

        // then
        Assertions.assertFalse(ret);
    }

    @Test
    public void testCalculationInvalidType() throws NoSuchMethodException, IOException {
        // given
        var conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/humidity_5000.csv");
        var ds = conn.loadSchema("http:/example.com", "hum");
        var concept = ds.getConcepts().stream().findFirst().orElseThrow();
        var dsdElement = concept.getAttribute("valueMetadataId");
        var recordList = conn.getRecordList(concept);
        LEDCPIPatternRecognition<Double> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), Double.class);

        // when
        recognition.calculation(recordList, null);
        var ret = recognition.getValue();

        // then
        Assertions.assertNull(ret); // null as the type is not a Double
    }

    @Test
    public void testCalculationString() throws NoSuchMethodException, IOException {
        // given
        var conn = FileSelectionUtil.getConnectorCSV("src/main/resource/data/humidity_5000.csv");
        var ds = conn.loadSchema("http:/example.com", "hum");
        var concept = ds.getConcepts().stream().findFirst().orElseThrow();
        var dsdElement = concept.getAttribute("valueMetadataId");
        var recordList = conn.getRecordList(concept);
        LEDCPIPatternRecognition<String> recognition = new LEDCPIPatternRecognition<>(new DataProfile(recordList, dsdElement),
                "at.fh.scch/identifier#humidity:*",
                Path.of("src/main/resource/data/ledc-pi_definitions.json"), String.class);

        // when
        recognition.calculation(recordList, null);
        var ret = recognition.getValue();

        // then
        Assertions.assertNotNull(ret);
        Assertions.assertEquals(0D, ret); // 0 as no value is remotely similar to a double
    }
}
