package dqm.jku.dqmeerkat.quality.profilingmetrics.singlecolumn.pattern;

import java.util.ArrayList;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFContainer;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/**
 * Wrapper data structure to properly handle all Pattern counters
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/PatternCounterList")
public class PatternCounterList {
  private List<PatternCounter> list = new ArrayList<>(); // list for pattern counters
  private String uri;

  public PatternCounterList() {

  }
  
  public PatternCounterList(String uri) {
	  this.uri = uri + "/patternCounterList";
  }

  @RDFSubject
  public String getURI() {
	  return this.uri;
  }
  
  public void setURI(String uri) {
	  this.uri = uri;
  }

  
  /**
   * Gets the list of pattern counters
   * 
   * @return list of pattern counters
   */
  @RDF("dsd:hasPattern")
  @RDFContainer
  public List<PatternCounter> getList() {
    return list;
  }

  /**
   * Sets the list of pattern counters
   * 
   * @param list the list of pattern counters to be set
   */
  public void setList(List<PatternCounter> list) {
    this.list = list;
  }

  /**
   * Method for adding a new pattern to the list
   * 
   * @param regex the regex string to be added
   */
  public void addPattern(String regex) {
    this.list.add(new PatternCounter(regex, uri));
  }

  /**
   * Method for checking string with all patterns available in the list
   * 
   * @param toCheck the string to be checked
   */
  public void checkPatterns(String toCheck) {
    for (PatternCounter pc : list) pc.checkPattern(toCheck);
  }

  /**
   * Method for returning the string representation of all pattern counters
   * 
   * @param size the size of the data set
   * @return string representation of all patterns
   */
  public String getValueStrings(int size) {
    StringBuilder sb = new StringBuilder();
    for (PatternCounter pc : list) {
      if (pc.getPattern() == null) sb.append("  Invalid Pattern!\n");
      else {
        sb.append("  ");
        sb.append(pc.toString());
        sb.append(String.format("(%.2f", pc.calcHitRate(size)));
        sb.append('%');
        sb.append(String.format(")\n"));
      }
    }
    if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

}
