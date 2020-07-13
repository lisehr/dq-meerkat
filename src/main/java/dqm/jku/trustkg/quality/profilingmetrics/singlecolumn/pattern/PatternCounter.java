package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/**
 * Data structure to handle single patterns with their hit rates
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:PatternCounter")
public class PatternCounter {
  private Pattern pattern; // pattern object
  private String altPattern; // string representation of pattern
  private int cnt = 0; // counter for amount of hits

  public PatternCounter() {

  }

  public PatternCounter(String regex) {
    try {
      pattern = Pattern.compile(regex);
    } catch (PatternSyntaxException e) {
      pattern = null;
      altPattern = regex;
    }
  }

  /**
   * Gets the pattern
   * 
   * @return pattern
   */
  @RDF("foaf:pattern")
  public Pattern getPattern() {
    return pattern;
  }

  /**
   * Sets the pattern
   * 
   * @param p the pattern to be set
   */
  public void setPattern(Pattern p) {
    this.pattern = p;
  }

  /**
   * Get counter value
   * 
   * @return counter value
   */
  @RDF("foaf:cnt")
  public int getCnt() {
    return cnt;
  }

  /**
   * Sets the counter value
   * 
   * @param cnt the counter value to be set
   */
  public void setCnt(int cnt) {
    this.cnt = cnt;
  }

  /**
   * Method to check a string, if it matches with the pattern
   * 
   * @param toCheck the string to be checked
   */
  public void checkPattern(String toCheck) {
    if (pattern == null) return;
    Matcher m = pattern.matcher(toCheck);
    if (m.matches()) cnt++;
  }

  /**
   * Method for calculating the hit rate of a Pattern
   * 
   * @param size the size of the data set
   * @return hit rate in %
   */
  public float calcHitRate(int size) {
    return ((float) this.cnt / (float) size) * 100.0f;
  }

  @Override
  public String toString() {
    if (pattern == null) return String.format("%s:%s%d ", altPattern, tabulatorInsert(), cnt);
    else return String.format("%s:%s%d ", pattern.toString(), tabulatorInsert(), cnt);
  }

  /**
   * Helper method for inserting tabulators into string representation
   * 
   * @return indented string representation
   */
  private String tabulatorInsert() {
    int len = 0;
    if (pattern == null) len = altPattern.toString().length();
    else len = pattern.toString().length();
    if (len < 6) return "\t\t\t\t\t";
    else if (len < 11) return "\t\t\t\t";
    else if (len < 22) return "\t\t\t";
    else if (len < 30) return "\t\t";
    else return "\t";
  }
}
