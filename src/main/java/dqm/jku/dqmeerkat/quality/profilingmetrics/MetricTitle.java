package dqm.jku.dqmeerkat.quality.profilingmetrics;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/**
 * Enumeration for all DP metrics
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/MetricTitle")
public enum MetricTitle {
  card("Cardinality"),
  nullVal("# Null Values"),
  nullValP("% Null Values"),
  numrows("Number of Rows"),
  unique("Uniqueness"),
  keyCand("isCandidateKey"),
  hist("Histogram"),
  avg("Average"),
  dec("Decimals"),
  dig("Digits"),
  max("Maximum"),
  med("Median"),
  min("Minimum"),
  sd("Standard Deviation"),
  mad("Mean Absolute Deviation"),
  pattern("Pattern recognition"),
  bt("Basic type"),
  dt("Data type"),
  histCls("Number of classes"),
  histCR("Class range"),
  histVal("Values"),
  isoF("Isolation Forest"),
  isoFP("Isolation Forest Outlier %"),
  lof("Local outlier factor"),

  // graph metrics
  numEntries("Number of Entries"),
  distinctEntries("Number of Distinct Entries"),
  graphType ("Type of the Graph Element"),
  maximum ("Maximum Graph Element"),
  minimum ("Minimum Graph Element"),
  median ("Median Graph Element");

  
  private String label; // the label of the title (string representation)
  @SuppressWarnings("unused")
  private String uri;

  private MetricTitle(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }
  
  /**
   * Gets the label
   * 
   * @return label
   */
  @RDF("dsd:hasLabel")
  public String getLabel() {
    return label;
  }
  
  @RDFSubject(prefix = "dsd:quality/structures/MetricTitle/")
  public String getUri() {
	  return getLabel().replaceAll("\\s+", "");
  }
  
  public void setUri(String uri) {
	  this.uri = uri;
  }
  
  /**
   * Sets the label
   * 
   * @param label the label to be set
   */
  public void setLabel(String label) {
    this.label = label;
  }

}
