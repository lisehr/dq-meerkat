package dqm.jku.trustkg.quality.profilingmetrics;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/**
 * Enumeration for different DP metric categories
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:MetricCategory")
public enum MetricCategory {
  cardCat("Cardinalities"), 
  dti("Data type info"), 
  histCat("Histogram"), 
  depend("Dependencies");

  private String label; // the label of the category (string representation)

  private MetricCategory(String label) {
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
