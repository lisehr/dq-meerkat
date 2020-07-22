package dqm.jku.trustkg.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFContainer;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.profilingmetrics.MetricTitle;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.Cardinality;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NullValues;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NullValuesPercentage;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.NumRows;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.cardinality.Uniqueness;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo.*;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.dependency.KeyCandidate;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.histogram.*;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.pattern.PatternRecognition;
import dqm.jku.trustkg.util.Miscellaneous.DBType;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;

/**
 * Class for creating the data structure of a DataProfile. A DataProfile (DP)
 * can contain multiple metrics, which are calulated and evaluated
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/DataProfile")
public class DataProfile {
  private List<ProfileMetric> metrics = new ArrayList<>(); // a list containing all profile metrics
  private DSDElement elem; // DSDElement, where this DataProfile is annotated to
  private String uri; // uniform resource identifier of this profile

  public DataProfile() {

  }

  public DataProfile(RecordList rs, DSDElement d) throws NoSuchMethodException {
    this.elem = d;
    this.uri = elem.getURI() + "/profile";
    // TODO: distinguish between Neo4J and relational DB
    createDataProfileSkeletonRDB();
    calculateReferenceDataProfile(rs);
  }

  /**
   * calculates an initial data profile based on the values inserted in the
   * reference profile
   * 
   * @param rl the list of records used for calculation
   * @param d  the dsd element to be annotated (currently only Attribute for
   *           single column metrics)
   * @throws NoSuchMethodException
   */
  private void calculateReferenceDataProfile(RecordList rl) throws NoSuchMethodException {
    if (elem instanceof Attribute) calculateSingleColumn(rl);
  }

  /**
   * Gets the URI
   * 
   * @return uri
   */
  @RDFSubject
  public String getURI() {
    return uri;
  }

  /**
   * Sets the URI (security threat but used by rdfbeans)
   * 
   * @param uri
   */
  public void setURI(String uri) {
    this.uri = uri;
  }

  /**
   * Helper method for calculating the single column values for the profile
   * 
   * @param rl the recordlist for measuring
   * @throws NoSuchMethodException
   */
  private void calculateSingleColumn(RecordList rl) throws NoSuchMethodException {
    List<Number> l = createValueList(rl);
    for (ProfileMetric p : metrics) {
      if (needsRecordListCalc(p)) p.calculation(rl, p.getValue());
      else p.calculationNumeric(l, p.getValue());
    }
  }

  /**
   * Helper method to determine if a record list calculation is needed (i.e. a
   * list of numeric values can lead to incorrect metrics)
   * 
   * @param p the profile metric to check
   * @return true, if the profile metric needs to be calculated with a record
   *         list, false if not
   */
  private boolean needsRecordListCalc(ProfileMetric p) {
    return p.getTitle().equals(unique) || p.getTitle().equals(keyCand) || p.getTitle().equals(nullValP) || p.getTitle().equals(nullVal) || p.getTitle().equals(numrows) || p.getTitle().equals(card) || p.getTitle().equals(hist);
  }

  /**
   * Helper method for creating a list of numeric values
   * 
   * @param rl the record list for measuring
   * @return list of numeric values of the records
   */
  private List<Number> createValueList(RecordList rl) {
    List<Number> list = new ArrayList<Number>();
    Attribute a = (Attribute) elem;
    for (Record r : rl) {
      Number field = null;
      Class<?> clazz = a.getDataType();
      if (String.class.isAssignableFrom(clazz) && r.getField(a) != null) field = r.getField(a).toString().length();
      else if (a.getConcept().getDatasource().getDBType().equals(DBType.CSV)) field = (Number) r.getField(a);
      else if (a.getConcept().getDatasource().getDBType().equals(DBType.MYSQL)) {
        if (Number.class.isAssignableFrom(clazz)) field = (Number) r.getField(a);
      }
      if (field != null) list.add(field);
    }
    list.sort(new NumberComparator());
    return list;
  }

  /**
   * Helper method to create a reference data profile on which calculations can be
   * made.
   */
  private void createDataProfileSkeletonRDB() {
    if (elem instanceof Attribute) {
      Attribute a = (Attribute) elem;
      Class<?> clazz = a.getDataType();
      if (String.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz) || clazz.equals(Object.class)) {
        ProfileMetric size = new NumRows(this);
        metrics.add(size);
        ProfileMetric min = new Minimum(this);
        metrics.add(min);
        ProfileMetric max = new Maximum(this);
        metrics.add(max);
        ProfileMetric avg = new Average(this);
        metrics.add(avg);
        ProfileMetric med = new Median(this);
        metrics.add(med);
        ProfileMetric card = new Cardinality(this);
        metrics.add(card);
        ProfileMetric uniq = new Uniqueness(this);
        metrics.add(uniq);
        ProfileMetric nullVal = new NullValues(this);
        metrics.add(nullVal);
        ProfileMetric nullValP = new NullValuesPercentage(this);
        metrics.add(nullValP);
        ProfileMetric hist = new Histogram(this);
        metrics.add(hist);
        ProfileMetric digits = new Digits(this);
        metrics.add(digits);
        ProfileMetric isCK = new KeyCandidate(this);
        metrics.add(isCK);
        ProfileMetric decimals = new Decimals(this);
        metrics.add(decimals);
        ProfileMetric patterns = new PatternRecognition(this);
        metrics.add(patterns);
        ProfileMetric basicType = new BasicType(this);
        metrics.add(basicType);
        ProfileMetric dataType = new DataType(this);
        metrics.add(dataType);
      } else {
        System.err.println("Attribute '" + a.getLabel() + "' has data type '" + a.getDataTypeString() + "', which is currently not handled. ");
      }
    }
  }

  /**
   * Method for printing out the data profile.
   */
  public void printProfile() {
    System.out.println("Data Profile:");
    if (metrics.stream().anyMatch(p -> p.getValueClass().equals(String.class))) System.out.println("Strings use String length for value length metrics!");
    SortedSet<ProfileMetric> metricSorted = new TreeSet<>();
    metricSorted.addAll(metrics);
    for (ProfileMetric p : metricSorted) {
      System.out.println(p.toString());
    }
    System.out.println();
  }

  /**
   * Method for printing out the data profile as a string.
   */
  public String getProfileString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Data Profile:");
    sb.append('\n');
    if (metrics.stream().anyMatch(p -> p.getValueClass().equals(String.class))) {
      sb.append("Strings use String length for value length metrics!");
      sb.append('\n');
    }
    SortedSet<ProfileMetric> metricSorted = new TreeSet<>();
    metricSorted.addAll(metrics);
    for (ProfileMetric p : metricSorted) {
      sb.append(p.toString());
      sb.append('\n');
    }
    sb.append('\n');
    return sb.toString();
  }

  /**
   * Method for getting the set of metrics
   * 
   * @return set of metrics
   */
  @RDF("dsd:includesMetric")
  @RDFContainer
  public List<ProfileMetric> getMetrics() {
    return metrics;
  }

  /**
   * Sets the metrics (security threat but used by rdfbeans)
   * 
   * @param metrics the metrics to set
   */
  public void setMetrics(List<ProfileMetric> metrics) {
    this.metrics = metrics;
  }

  /**
   * Method for adding a metric via a data profile object
   * 
   * @param m the metric to be added
   */
  public void addMetric(ProfileMetric m) {
    this.metrics.add(m);
  }

  /**
   * Method for getting a specific ProfileMetric with its corresponding label.
   * 
   * @param label the label to compare with the profile metrics
   * @return ProfileMetric if found, null otherwise
   */
  public ProfileMetric getMetric(MetricTitle title) {
    for (ProfileMetric m : metrics) {
      if (m.getTitle().equals(title)) return m;
    }
    return null;
  }

  /**
   * Sets the dsd element (security threat but used by rdfbeans)
   * 
   * @param elem the elem to set
   */
  public void setElem(DSDElement elem) {
    this.elem = elem;
  }

  /**
   * Gets the reference dsd element, used for calculation
   * 
   * @return the reference element
   */
  @RDF("dsd:annotatedTo")
  public DSDElement getElem() {
    return elem;
  }

  /**
   * Method for creating a point of measure for InfluxDB.
   * 
   * @param measure the builder for a measurement
   * @return a measuring point for insertion into InfluxDB
   */
  public Point createMeasuringPoint(Builder measure) {
    SortedSet<ProfileMetric> metricSorted = new TreeSet<>();
    metricSorted.addAll(metrics);
    for (ProfileMetric p : metricSorted) {
      if (!p.getTitle().equals(hist)) addMeasuringValue(p, measure);
    }
    return measure.build();
  }

  /**
   * Helper method for adding the correct measuring value (including its data
   * type) to the builder
   * 
   * @param p       the profile metric to add
   * @param measure the builder for a measurement
   */
  private void addMeasuringValue(ProfileMetric p, Builder measure) {
    if (p.getValue() == null || p.getLabel().equals(pattern.getLabel())) measure.addField(p.getLabel(), 0); // TODO: replace 0 with NaN, when hitting v2.0 of influxdb
    else if (p.getValueClass().equals(Long.class)) measure.addField(p.getLabel(), (long) p.getValue());
    else if (p.getValueClass().equals(Double.class)) measure.addField(p.getLabel(), (double) p.getValue());
    else if (p.getValueClass().equals(String.class) && (p.getLabel().equals(bt.getLabel()) || p.getLabel().equals(dt.getLabel()))) measure.addField(p.getLabel(), (String) p.getValue());
    else if (p.getValueClass().equals(Boolean.class)) measure.addField(p.getLabel(), (boolean) p.getValue());
    else measure.addField(p.getLabel(), (int) p.getValue());
  }

}
