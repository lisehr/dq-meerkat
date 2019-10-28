package dqm.jku.trustkg.quality.profilingmetrics;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:MetricTitle")
public enum MetricTitle {
  card("Cardinality"),
  nullVal("Null Values"),
  size("Size"),
  unique("Uniqueness"),
  keyCand("isCandidateKey"),
  hist("Histogram"),
  avg("Average"),
  dec("Decimals"),
  dig("Digits"),
  max("Maximum"),
  med("Median"),
  min("Minimum");
  
  private String label;

  private MetricTitle(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }

  @RDF("foaf:label")
  public String label() {
    return label;
  }
  
  public void setLabel(String label) {
    this.label = label;
  }

}
