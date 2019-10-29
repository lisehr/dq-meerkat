package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.valuelength;

import java.util.ArrayList;
import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:PatternCounterList")
public class PatternCounterList {
  private List<PatternCounter> list = new ArrayList<>();

  public PatternCounterList() {

  }

  @RDF("foaf:list")
  public List<PatternCounter> getList() {
    return list;
  }

  public void setList(List<PatternCounter> list) {
    this.list = list;
  }

  public void addPattern(String regex) {
    this.list.add(new PatternCounter(regex));
  }

  public void checkPatterns(String toCheck) {
    for (PatternCounter pc : list) pc.checkPattern(toCheck);
  }

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
    if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);    
    return sb.toString();
  }

}
