package dqm.jku.trustkg.quality.profilingmetrics;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/**
 * Enumeration for all DP metrics
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:MetricTitle")
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
  pattern("Pattern recognition"),
  bt("Basic type"),
  dt("Data type"),
  histCls("Number of classes"),
  histCR("Class range"),
  histVal("Values");
  
  private String label; // the label of the title (string representation)

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
  @RDF("foaf:label")
  public String label() {
    return label;
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
