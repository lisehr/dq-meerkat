package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.distribution;

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
  
  public void put(int key, int value) {
    classes.add(new FrequencyClass(key, value));
  }
  
  public void incrementFrequency(int key) {
    for (FrequencyClass f : classes) {
      if(f.getClassNo() == key) f.incrementFrequency();
    }
  }

  /**
   * @return the classes
   */
  @RDF("foaf:hasClass")
  public Set<FrequencyClass> getClasses() {
    return classes;
  }

  /**
   * @param classes the classes to set
   */
  public void setClasses(Set<FrequencyClass> classes) {
    this.classes = classes;
  }

  public List<Integer> values() {
    List<Integer> list = new ArrayList<>();
    for (FrequencyClass f : classes) {
      list.add(f.getFrequency());
    }
    return list;
  }
  
  
}
