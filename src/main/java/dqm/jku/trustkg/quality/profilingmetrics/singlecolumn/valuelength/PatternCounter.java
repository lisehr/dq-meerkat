package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:PatternCounter")
public class PatternCounter {
  private Pattern pattern;
  private int cnt = 0;

  
  public PatternCounter() {
    
  }
  
  public PatternCounter(String regex) {
    pattern = Pattern.compile(regex);
  }
  
  @RDF("foaf:pattern")
  public Pattern getPattern() {
    return pattern;
  }
  
  public void setPattern(Pattern p) {
    this.pattern = p;
  }
  
  @RDF("foaf:cnt")
  public int getCnt() {
    return cnt;
  }
  
  public void setCnt(int cnt) {
    this.cnt = cnt;
  }

  public void checkPattern(String toCheck) {
    Matcher m = pattern.matcher(toCheck);
    if (m.matches()) cnt++;
  }
  
  public float calcHitRate(int size) {
    return (float) ((this.cnt / size) * 100.0);
  }
  
  @Override
  public String toString() {
    return String.format("%s:\t\t%d ", pattern.toString(), cnt);
  }
}
