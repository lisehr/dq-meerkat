package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern.ledcpi;

import be.ugent.ledc.pi.io.JSON;
import be.ugent.ledc.pi.measure.QualityMeasure;
import be.ugent.ledc.pi.measure.predicates.Predicate;
import be.ugent.ledc.pi.property.Property;
import be.ugent.ledc.pi.property.PropertyParseException;
import be.ugent.ledc.pi.registries.MeasureRegistry;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.*;
import dqm.jku.dqmeerkat.util.Constants;
import lombok.SneakyThrows;
import science.aist.seshat.Logger;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static dqm.jku.dqmeerkat.util.GenericsUtil.cast;

/**
 * <h2>LEDCPIPatternRecognition</h2>
 * <summary>
 * {@link AbstractProfileStatistic} implementation for recognizing patterns in the records. Uses LEDC-PI QualityMeasure
 * to generate a stack of rules based on RegEx and Grex patterns, that need to match the given data samples.
 * <p>
 * The {@link AbstractProfileStatistic} needs either a JSON configuration file, the finished QualityMeasure, or a list of
 * {@link be.ugent.ledc.pi.measure.predicates.Predicate}s in order to generate (or use) the QualityMeasure.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 09.06.2022
 */
public class LEDCPIPatternRecognition<T > extends ProfileStatistic<T, Double> {
    // the generic type is double as we return a metric based on a relative value, not the ledc pi hits
    private static final Logger LOGGER = Logger.getInstance();
    private final QualityMeasure<String> measure;


    protected LEDCPIPatternRecognition(DataProfile referenceProfile, QualityMeasure<String> measure, Class<T> type) {
        super(StatisticTitle.pattern, StatisticCategory.dti, referenceProfile, type);
        this.measure = measure;
    }

    public LEDCPIPatternRecognition(DataProfile referenceProfile, List<Predicate<String>> predicates,
                                    String propertyName, URI propertyUri, LocalDate validAt, int sufficiencyThreshold,
                                    Class<T> type) throws PropertyParseException {
        this(referenceProfile, new QualityMeasure<>(predicates, Property.parseProperty(propertyName), propertyUri,
                validAt, sufficiencyThreshold), type);

    }

    @SneakyThrows
    public LEDCPIPatternRecognition(DataProfile referenceProfile, String propertyName, Path jsonConfig, Class<T> type) {
        super(StatisticTitle.pattern, StatisticCategory.dti, referenceProfile, type);
        JSON.restore(new File(String.valueOf(jsonConfig)));
        var rawMeasure = MeasureRegistry.getInstance()
                .getMeasureByProperty(Property.parseProperty(propertyName));

        // I tried to fix the raw types, but the library just throws them around to liberally
        //noinspection unchecked
        this.measure = new QualityMeasure<>((List<Predicate<String>>) rawMeasure.getPredicates(),
                rawMeasure.getProperty(), rawMeasure.getSource(),
                rawMeasure.getValidAt(), rawMeasure.getSufficiencyThreshold());
    }

    @Override
    public void calculation(RecordList rs, T oldVal) {
        Attribute attribute = (Attribute) super.getRefElem();
        double cnt = 0D;
        if (ensureDataTypeCorrect(attribute.getDataType())) {
            for (Record record : rs) {
                cnt += checkHit(cast(record.getField(attribute.getLabel())));
            }
            var numericValue = cnt / rs.size();
            setValue(numericValue);
        }
    }

    /**
     * Checks if the given record matches the QualityMeasure. Assumes that the record matches all QualityMeasure tests,
     * i.E. the result of the measurement is equal to the sufficiencyThreshold
     *
     * @param record the record, that needs to match the QualityMeasure
     * @return 1 if the record matches, 0 if it does not match
     */
    private double checkHit(T record) {
        var measureInput = record == null ? null : record.toString();
        var result = measure.measure(measureInput);
        return result >= measure.getSufficiencyThreshold() ? 1 : 0;
    }


    /**
     * Update the {@link AbstractProfileStatistic}s value using the given {@link RecordList}. Delegates to calculation.
     *
     * @param rs the recordset used for updating
     */
    @Override
    public void update(RecordList rs) {
        calculation(rs, null);
    }


    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }


    /**
     * Calculate the relative difference between this and another {@link AbstractProfileStatistic}. The threshold is used to
     * determine the lower and upperBound, in which the other {@link AbstractProfileStatistic} should lie in. So <i>this</i>
     * refers to the reference data profile, while other is the dataprofile.
     *
     * @param other     the {@link AbstractProfileStatistic}, whose value needs to be within the defined threshold of this value
     * @param threshold indicates allowed deviation from reference value in percent.
     *                  Defines the lower and upperBound for the conformance relative to this value
     * @return if the other {@link AbstractProfileStatistic} conforms to this one
     */
    @Override
    public boolean checkConformance(ProfileStatistic<T, Double> other, double threshold) {
        double rdpVal = getValue();
        double dpValue = other.getValue();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG) {
            LOGGER.info(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        }
        return conf;
    }
}
