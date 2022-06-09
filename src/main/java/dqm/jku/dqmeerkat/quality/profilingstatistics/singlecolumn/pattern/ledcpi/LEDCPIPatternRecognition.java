package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern.ledcpi;

import be.ugent.ledc.pi.io.JSON;
import be.ugent.ledc.pi.measure.QualityMeasure;
import be.ugent.ledc.pi.measure.predicates.Predicate;
import be.ugent.ledc.pi.property.Property;
import be.ugent.ledc.pi.registries.MeasureRegistry;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * <h2>LEDCPIPatternRecognition</h2>
 * <summary>
 * {@link ProfileStatistic} implementation for recognizing patterns in the records. Uses LEDC-PI QualityMeasure
 * to generate a stack of rules based on RegEx and Grex patterns, that need to match the given data samples.
 * <p>
 * The {@link ProfileStatistic} needs either a JSON configuration file, the finished QualityMeasure, or a list of
 * {@link be.ugent.ledc.pi.measure.predicates.Predicate}s in order to generate (or use) the QualityMeasure.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 09.06.2022
 */
public class LEDCPIPatternRecognition extends ProfileStatistic {
    private final QualityMeasure<String> measure;


    public LEDCPIPatternRecognition(DataProfile referenceProfile, QualityMeasure<String> measure) {
        super(StatisticTitle.pattern, StatisticCategory.dti, referenceProfile);
        this.measure = measure;
    }

    @SneakyThrows
    public LEDCPIPatternRecognition(DataProfile referenceProfile, List<Predicate<String>> predicates,
                                    String propertyName, URI propertyUri, LocalDate validAt, int sufficiencyThreshold) {
        this(referenceProfile, new QualityMeasure<>(predicates, Property.parseProperty(propertyName), propertyUri,
                validAt, sufficiencyThreshold));

    }

    @SneakyThrows
    public LEDCPIPatternRecognition(DataProfile referenceProfile, Path jsonConfig) {
        super(StatisticTitle.pattern, StatisticCategory.dti, referenceProfile);
        JSON.restore(new File(String.valueOf(jsonConfig)));
        var rawMeasure = MeasureRegistry.getInstance()
                .getMeasureByProperty(Property.parseProperty("some"));

        // I tried to fix the raw types, but the library just throws them around to liberally
        //noinspection unchecked
        this.measure = new QualityMeasure<>((List<Predicate<String>>) rawMeasure.getPredicates(),
                rawMeasure.getProperty(), rawMeasure.getSource(),
                rawMeasure.getValidAt(), rawMeasure.getSufficiencyThreshold());
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {

    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {

    }

    @Override
    public void update(RecordList rs) {

    }

    @Override
    protected String getValueString() {
        return null;
    }

    @Override
    public boolean checkConformance(ProfileStatistic m, double threshold) {
        return false;
    }
}
