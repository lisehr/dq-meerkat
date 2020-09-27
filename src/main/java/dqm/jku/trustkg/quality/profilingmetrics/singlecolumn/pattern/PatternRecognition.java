package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NumRows;
import dqm.jku.trustkg.util.FileSelectionUtil;
import dqm.jku.trustkg.util.Miscellaneous.DBType;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;

/**
 * Describes the metric PatternRecognition, which calculates hit rates for regex
 * patterns
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/PatternRecognition")
public class PatternRecognition extends DependentProfileMetric {
	
	private String filePathString;
	
  public PatternRecognition() {

  }
  
  public PatternRecognition(DataProfile d) {
  	super(pattern, dti, d);
  	setFilePathString(null);
  }

  public PatternRecognition(DataProfile d, String path) {
    super(pattern, dti, d);
    setFilePathString(path);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    this.dependencyCalculationWithRecordList(rs);
    Attribute a = (Attribute) super.getRefElem();
    this.setValueClass(PatternCounterList.class);
    PatternCounterList patterns = null;
    if (oldVal == null) patterns = initPatterns();
    else patterns = (PatternCounterList) oldVal;
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
      if (filePathString == null) regs = FileSelectionUtil.readAllPatternsOfFile(1, ((Attribute) this.getRefElem()).getConcept().getDatasource().getDBType() == DBType.PENTAHOETL);
      else regs = Files.readAllLines(Paths.get(filePathString));
      for (String s : regs) if (!s.isEmpty()) patterns.addPattern(s);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return patterns;
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
    this.dependencyCalculationWithNumericList(list);
    this.setValueClass(PatternCounterList.class);
    PatternCounterList patterns = null;
    if (oldVal == null) patterns = initPatterns();
    else patterns = (PatternCounterList) oldVal;
    for (Number n : list) patterns.checkPatterns(n.toString());
    this.setValue(patterns);
  }

  @Override
  public void update(RecordList rs) {
    Attribute a = (Attribute) super.getRefElem();
    PatternCounterList patterns = (PatternCounterList) getValue();
    for (Record r : rs) {
      Object field = r.getField(a);
      patterns.checkPatterns((String) field);
    }
    this.setValue(patterns);
  }

  @Override
  protected String getValueString() {
    if (getValue() == null) return "\tnull";
    int denominator = (int) super.getRefProf().getMetric(numrows).getValue();
    if (denominator == 0) return "\tnull";
    StringBuilder sb = new StringBuilder().append("\n");
    sb.append(((PatternCounterList) getValue()).getValueStrings(denominator));
    return sb.toString();
  }

  @Override
  protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
    if (super.getMetricPos(pattern) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculationNumeric(list, null);
  }

  @Override
  protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(pattern) - 1 <= super.getMetricPos(numrows)) super.getRefProf().getMetric(numrows).calculation(rl, null);
  }

  @Override
  protected void dependencyCheck() {
    ProfileMetric sizeM = super.getRefProf().getMetric(numrows);
    if (sizeM == null) {
      sizeM = new NumRows(super.getRefProf());
      super.getRefProf().addMetric(sizeM);
    }
  }

	public String getFilePathString() {
		return filePathString;
	}

	public void setFilePathString(String filePathString) {
		this.filePathString = filePathString;
	}
}
