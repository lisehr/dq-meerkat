package dqm.jku.trustkg.quality.profilingmetrics;

import java.util.List;
import java.util.Objects;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:ProfileMetric")
public abstract class ProfileMetric implements Comparable<ProfileMetric>{
  private MetricTitle title; // the naming of the metric
  private Class<?> valClass; // the class of the value
  private Object value; // the value itself
  private DataProfile refProf; // reference profile for calculations

  public ProfileMetric() {

  }

  public ProfileMetric(MetricTitle title, DataProfile refProf) {
    if (title == null || refProf == null) throw new IllegalArgumentException("Parameters cannot be null!");
    this.title = title;
    this.refProf = refProf;
    value = null;
  }

  /**
   * Gets the title
   * 
   * @return title of metric
   */
  @RDF("foaf:label")
  public MetricTitle getTitle() {
    return title;
  }
  
  /**
   * Directly gets the text of the title
   * @return label of title
   */
  public String getLabel() {
    return title.label();
  }

  /**
   * Sets the title (security threat but needed by rdfbeans)
   * 
   * @param title the title to set
   */
  public void setTitle(MetricTitle title) {
    this.title = title;
  }

  /**
   * Sets the reference profile (security threat but needed by rdfbeans)
   * 
   * @param refProf the refProf to set
   */
  public void setRefProf(DataProfile refProf) {
    this.refProf = refProf;
  }

  /**
   * Gets the value
   * 
   * @return value of metric
   */
  @RDF("foaf:value")
  public Object getValue() {
    return value;
  }

  /**
   * Gets the class object of a value
   * 
   * @return class of value
   */
  public Class<?> getValueClass() {
    return valClass;
  }

  /**
   * Sets the class of the value
   * 
   * @param cls the new value class to be set
   */
  protected void setValueClass(Class<?> cls) {
    this.valClass = cls;
  }

  /**
   * Gets a String representation of the value class, used for rdf transformation
   * 
   * @return string of value class
   */
  @RDF("foaf:valueClass")
  public String getValueClassString() {
    return this.valClass.getName();
  }

  /**
   * Sets the value Class via the class string, passed as parameter
   * 
   * @param valClass the string representation of the class
   */
  public void setValueClassString(String valClass) {
    try {
      this.valClass = Class.forName(valClass);
    } catch (ClassNotFoundException e) {
      System.err.println("Class not found!");
    }
  }

  /**
   * Sets the value of the metric
   * 
   * @param value the new value to be set
   */
  public void setValue(Object value) {
    this.value = value;
  }

  /**
   * Gets the reference DataProfile
   * 
   * @return the reference profile
   */
  @RDF("foaf:refProf")
  public DataProfile getRefProf() {
    return refProf;
  }

  /**
   * Gets the reference dsd element, used for calculation
   * 
   * @return the reference element
   */
  public DSDElement getRefElem() {
    return refProf.getElem();
  }

  /**
   * Method for calculating the profile metric, overridden by each metric
   * 
   * @param oldVal a oldValue to be updated, null for initial calculation
   * @param rs     the recordset used for calculation
   */
  public abstract void calculation(RecordList rs, Object oldVal);

  /**
   * Method for calculating the profile metric, overridden by each metric
   * 
   * @param oldVal a oldValue to be updated, null for initial calculation
   * @param list   a sorted list, containing all values
   * @throws NoSuchMethodException in cases like null values, since here records are not allowed for processing
   */
  public abstract void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException;

  /**
   * Method for updating the metric value, overriden by each metric
   * 
   * @param rs the recordset used for updating
   */
  public abstract void update(RecordList rs);

  /**
   * Returns a string representation of the metric value
   * 
   * @return string repr of value
   */
  protected abstract String getValueString();
  
  /**
   * Method for creating a simple string representation of the metric value
   * 
   * @return string repr of value
   */
  protected String getSimpleValueString() {
    if (getValue() == null) return "\tnull";
    else return "\t" + getValue().toString();
  }
  
  @Override
  public String toString() {
    if (value == null) return String.format("%s\tnull", title);
    else if (title.label().length() < 8) return String.format("%s\t%s", title, getValueString());
    else return String.format("%s%s", title, getValueString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, valClass, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ProfileMetric)) return false;
    ProfileMetric other = (ProfileMetric) obj;
    return Objects.equals(title, other.title) && Objects.equals(valClass, other.valClass) && Objects.equals(value, other.value);
  }
  
  public int compareTo(ProfileMetric other) {
    return this.title.label().compareTo(other.title.label());
  }
  
  public int getMetricPos(MetricTitle t) {
    List<ProfileMetric> metrics = this.getRefProf().getMetrics();
    for (int i = 0; i < metrics.size(); i++) if (metrics.get(i).getLabel().equals(t.label())) return i;
    return -1;
  }

}
