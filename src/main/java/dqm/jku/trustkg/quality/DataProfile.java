package dqm.jku.trustkg.quality;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength.*;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.distribution.*;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

@RDFNamespaces({ 
  "foaf = http://xmlns.com/foaf/0.1/",
})
@RDFBean("foaf:DataProfile")
public class DataProfile {
  private Set<ProfileMetric> metrics = new HashSet<>();
  private DSDElement elem;
  private int recordsProcessed;
  private String uri;

  public DataProfile() {
    
  }
  
  public DataProfile(RecordSet rs, DSDElement d) {
    this.elem = d;
    this.uri = elem.getURI() + "/profile";
    createStandardProfile();
    calculateInitialProfile(rs);
  }

  /**
   * calculates an initial data profile based on the values inserted in the
   * standard profile
   * 
   * @param rs the set of records used for calculation
   * @param d  the dsd element to be annotated (currently only Attribute for
   *           single column metrics)
   */
  private void calculateInitialProfile(RecordSet rs) {
    recordsProcessed = rs.size();
    if (elem instanceof Attribute) calculateSingleColumn(rs);
  }
  
  @RDFSubject
  public String getURI() {
    return uri;
  }
  
  public void setURI(String uri) {
    this.uri = uri;
  }

  private void calculateSingleColumn(RecordSet rs) {
    List<Number> l = createValueList(rs);
    for (ProfileMetric p : metrics) {
      p.calculationNumeric(l, p.getValue());
    }
    
  }

  private List<Number> createValueList(RecordSet rs) {
    List<Number> list = new ArrayList<Number>();
    Attribute a = (Attribute) elem;
    for (Record r : rs) {
      Number field = null;
      if (a.getDataType().equals(String.class) && r.getField(a) != null) field = ((String) r.getField(a)).length();
      else field = (Number) r.getField(a);
      if (field != null) list.add(field);
    }
    list.sort(new NumberComparator());
    return list;
  }

  /**
   * creates a standard data profile on which calculations can be made
   */
  private void createStandardProfile() {
    if (elem instanceof Attribute) {
      ProfileMetric min = new Minimum(this);
      metrics.add(min);
      ProfileMetric max = new Maximum(this);
      metrics.add(max);
      ProfileMetric avg = new Average(this);
      metrics.add(avg);
      ProfileMetric med = new Median(this);
      metrics.add(med);
      ProfileMetric hist = new Histogram(this);
      metrics.add(hist);
    }
  }

  /**
   * Method for printing out the data profile
   */
  public void printProfile() {
    System.out.println("Data Profile:");
    if (metrics.stream().anyMatch(p -> p.getValueClass().equals(String.class))) System.out.println("Strings use String length for value length metrics!");
    for (ProfileMetric p : metrics) {
      System.out.println(p.toString());
    }
    System.out.println();
  }
  
  /**
   * Method for getting the set of metrics
   * @return set of metrics
   */
  @RDF("foaf:includes")
  public Set<ProfileMetric> getMetrics(){
    return metrics;
  }
  
  
  
  /**
   * @param metrics the metrics to set
   */
  public void setMetrics(Set<ProfileMetric> metrics) {
    this.metrics = metrics;
  }

  /**
   * @param elem the elem to set
   */
  public void setElem(DSDElement elem) {
    this.elem = elem;
  }

  /**
   * @param uri the uri to set
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Gets the reference dsd element, used for calculation
   * 
   * @return the reference element
   */
  @RDF("foaf:annotatedTo")
  public DSDElement getElem() {
    return elem;
  }

  @RDF("foaf:recordsProcessed")
  public int getRecordsProcessed() {
    return recordsProcessed;
  }

  public void setRecordsProcessed(int recordsProcessed) {
    this.recordsProcessed = recordsProcessed;
  }

  public Point createMeasuringPoint(Builder measure) {
    for (ProfileMetric p : metrics) {
      if (!p.getLabel().equals("Histogram")) addMeasuringValue(p, measure);
    }
    return measure.build();
  }

  private void addMeasuringValue(ProfileMetric p, Builder measure) {
    if (p.getValueClass().equals(Long.class)) measure.addField(p.getLabel(), (long)p.getValue());
    else if (p.getValueClass().equals(Double.class)) measure.addField(p.getLabel(), (double)p.getValue());
    else measure.addField(p.getLabel(), (int)p.getValue());
  }

}
