package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern.ledcpi;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

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

    // TODO constructors; (i) json path (ii) List of Predicates (iii) instantiated qualitymeasure


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
