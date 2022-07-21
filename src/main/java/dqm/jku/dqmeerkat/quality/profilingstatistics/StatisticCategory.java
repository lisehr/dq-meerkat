package dqm.jku.dqmeerkat.quality.profilingstatistics;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/**
 * Enumeration for different DP metric categories
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/MetricCategory")
public enum StatisticCategory {
  cardCat("Cardinalities"), 
  dti("Data type info"), 
  histCat("Histogram"), 
  depend("Dependencies"),
  out("Outliers"),
  graphCat("Graph Categroy"),
  summaryCategory("Data Summary Category");

  private String label; // the label of the category (string representation)
  @SuppressWarnings("unused")
  private String uri;

  private StatisticCategory(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }
  
  
  @RDFSubject(prefix = "dsd:quality/structures/MetricCategory/")
  public String getUri() {
	  return getLabel().replaceAll("\\s+", "");
  }
  
  public void setUri(String uri) {
	  this.uri = uri;
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

  /**
   * Sets the label
   * 
   * @param label the label to be set
   */
  public void setLabel(String label) {
    this.label = label;
  }

}
