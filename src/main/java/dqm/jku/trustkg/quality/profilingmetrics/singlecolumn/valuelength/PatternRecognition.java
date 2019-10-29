package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.io.IOException;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.FileSelectionUtil;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:PatternCounter")
public class PatternRecognition extends ProfileMetric {
  public PatternRecognition() {

  }

  public PatternRecognition(DataProfile d) {
    super(pattern, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
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
    PatternCounterList patterns = new PatternCounterList();
    try {
      List<String> regs = FileSelectionUtil.readAllPatternsOfFile(1);
      for (String s : regs) if (!s.isEmpty()) patterns.addPattern(s);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return patterns;
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
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
    int denominator = (int) super.getRefProf().getMetric(size).getValue();
    if (denominator == 0) return "\tnull";
    StringBuilder sb = new StringBuilder().append("\n");
    sb.append(((PatternCounterList) getValue()).getValueStrings(denominator));
    return sb.toString();
  }
}
