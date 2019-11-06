package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.histogram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:SerializableMap")
public class SerializableMap {
  Set<FrequencyClass> classes = new HashSet<>();

  public SerializableMap() {

  }

  /**
   * Puts a new Frequency class into the map
   * 
   * @param key   the classno
   * @param value the frequency
   */
  public void put(int key, int value) {
    classes.add(new FrequencyClass(key, value));
  }

  /**
   * Increments the frequency via the classno
   * 
   * @param key the classno
   */
  public void incrementFrequency(int key) {
    for (FrequencyClass f : classes) {
      if (f.getClassNo() == key) f.incrementFrequency();
    }
  }

  /**
   * Gets the classes
   * 
   * @return the classes
   */
  @RDF("foaf:hasClass")
  public Set<FrequencyClass> getClasses() {
    return classes;
  }

  /**
   * Sets the classes (security threat but needed by rdfbeans)
   * 
   * @param classes the classes to set
   */
  public void setClasses(Set<FrequencyClass> classes) {
    this.classes = classes;
  }

  /**
   * Creates a List of all values (frequencies)
   * 
   * @return frequency list
   */
  public List<Integer> values() {
    List<Integer> list = new ArrayList<>();
    for (FrequencyClass f : classes) {
      list.add(f.getFrequency());
    }
    return list;
  }

}
