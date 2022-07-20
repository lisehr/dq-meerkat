package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.pattern;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.FileSelectionUtil;
import dqm.jku.dqmeerkat.util.Miscellaneous.DBType;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.pattern;

/**
 * Describes the metric PatternRecognition, which calculates hit rates for regex
 * patterns
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/PatternRecognition")
public class PatternRecognition extends DependentProfileStatistic<PatternCounterList, PatternCounterList> {

    private String filePathString;

    public PatternRecognition(DataProfile d) {
        super(pattern, dti, d, PatternCounterList.class);
        setFilePathString(null);
    }

    public PatternRecognition(DataProfile d, String path) {
        super(pattern, dti, d, PatternCounterList.class);
        setFilePathString(path);
    }

    @Override
    public void calculation(RecordList rs, PatternCounterList oldVal) {
        this.dependencyCalculationWithRecordList(rs);
        Attribute a = (Attribute) super.getRefElem();
        this.setInputValueClass(PatternCounterList.class);
        PatternCounterList patterns = null;
        if (oldVal == null) patterns = initPatterns();
        else patterns = oldVal;
        for (Record r : rs) {
            Object field = r.getField(a);
            patterns.checkPatterns((String) field);
        }
        this.setValue(patterns);
    }

    private PatternCounterList initPatterns() {
        PatternCounterList patterns = new PatternCounterList(this.getUri());
        try {
            List<String> regs;
            if (filePathString == null)
                regs = FileSelectionUtil.readAllPatternsOfFile(1, ((Attribute) this.getRefElem()).getConcept().getDatasource().getDBType() == DBType.PENTAHOETL);
            else regs = Files.readAllLines(Paths.get(filePathString));
            for (String s : regs) if (!s.isEmpty()) patterns.addPattern(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patterns;
    }

    @Override
    public void update(RecordList rs) {
        Attribute a = (Attribute) super.getRefElem();
        PatternCounterList patterns = getValue();
        for (Record r : rs) {
            Object field = r.getField(a);
            patterns.checkPatterns((String) field);
        }
        this.setValue(patterns);
    }

    @Override
    protected String getValueString() {
        if (getValue() == null) return "\tnull";
        long denominator = (long) super.getRefProf().getStatistic(numrows).getValue();
        if (denominator == 0) return "\tnull";
        return "\n" + getValue().getValueStrings(denominator);
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(pattern) - 1 <= super.getMetricPos(numrows))
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
    }

    @Override
    protected void dependencyCheck() {
        var sizeM = super.getRefProf().getStatistic(numrows);
        if (sizeM == null) {
            sizeM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(sizeM);
        }
    }

    public void setFilePathString(String filePathString) {
        this.filePathString = filePathString;
    }

    @Override
    public boolean checkConformance(ProfileStatistic<PatternCounterList, PatternCounterList> m, double threshold) {
        if (filePathString == null)
            return true;

//		double rdpVal = this.g
//		double dpValue = ((Number) m.getValue()).doubleValue();
//		
//		double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
//		double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);
//		
//		boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
//		if(!conf && Constants.DEBUG) System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        return true;
    }
}
