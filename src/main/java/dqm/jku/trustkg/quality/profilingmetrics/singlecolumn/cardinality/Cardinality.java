package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;


/**
 * Describes the metric Cardinality, the amount of different values in a data
 * set
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/cardinality/Cardinality")
public class Cardinality extends ProfileMetric {

  public Cardinality() {

  }

  public Cardinality(DataProfile d) {
    super(card, cardCat, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Set<Number> set = new HashSet<Number>();
    Set<String> strSet = new HashSet<String>();
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) strSet.add(r.getField(a).toString());
      else {
        field = (Number) r.getField(a);
        if (field != null) set.add(field);
      }
    }
    if (a.getDataType().equals(String.class)) {
    	this.setValue((long) strSet.size());
    	this.setNumericVal((Number) strSet.size());
    }
    else {
    	this.setValue((long) set.size());
    	this.setNumericVal((Number) set.size());
    }
    this.setValueClass(Long.class);
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    Set<Number> set = new HashSet<Number>();
    set.addAll(list);
    this.setValue((long) set.size());
    this.setValueClass(Long.class);
  }

  @Override
  public void update(RecordList rs) {
    calculation(rs, super.getValue());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }
}
