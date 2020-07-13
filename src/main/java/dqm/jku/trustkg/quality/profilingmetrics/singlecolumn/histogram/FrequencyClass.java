package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.histogram;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/**
 * Data structure to simulate histogram bins.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:FrequencyClass")
public class FrequencyClass implements Comparable<FrequencyClass> {
  private int classNo; // number of class
  private int frequency; // amount of hits in class

  public FrequencyClass() {

  }

  public FrequencyClass(int key, int value) {
    this.classNo = key;
    this.frequency = value;
  }

  /**
   * Gets the classnumber
   * 
   * @return the classNo
   */
  @RDF("foaf:classNo")
  public int getClassNo() {
    return classNo;
  }

  /**
   * Sets the classnumber (security threat but needed for rdfbeans)
   * 
   * @param classNo the classNo to set
   */
  public void setClassNo(int classNo) {
    this.classNo = classNo;
  }

  /**
   * Gets the frequency
   * 
   * @return the frequency
   */
  @RDF("foaf:frequency")
  public int getFrequency() {
    return frequency;
  }

  /**
   * Sets the frequency (security threat but needed by rdfbeans)
   * 
   * @param frequency the frequency to set
   */
  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  /**
   * Increments the frequency
   */
  public void incrementFrequency() {
    frequency++;
  }

  @Override
  public int compareTo(FrequencyClass other) {
    if (classNo < other.classNo) return -1;
    else if (classNo > other.classNo) return 1;
    else return 0;
  }

}
