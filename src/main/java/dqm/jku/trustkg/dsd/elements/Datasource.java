package dqm.jku.trustkg.dsd.elements;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFContainer;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.blockchain.blocks.DSDBlock;
import dqm.jku.trustkg.blockchain.standardchain.BlockChain;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.util.Miscellaneous.DBType;



@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Datasource")
public class Datasource extends DSDElement {
  
  private static final long serialVersionUID = 1L;
  private static final String prefix = "http://example.com/";
  
  private DBType dbtype;
  private Set<Concept> concepts = new TreeSet<Concept>();
  private Set<Association> associations = new TreeSet<Association>();
  private Set<Constraint> constraints = new TreeSet<Constraint>();

  public Datasource() {
    
  }
  
  public Datasource(String label) {
	super(label, prefix + label);
	this.dbtype = DBType.UNDEFINED;
  }
  
  public Datasource(String label, DBType dbtype) {
    super(label, prefix + label);
    this.dbtype = dbtype;
  }

  @RDF("foaf:concept")
  @RDFContainer
  public Set<Concept> getConcepts() {
    return Collections.unmodifiableSet(concepts);
  }
  
  public void setConcepts(Set<Concept> concepts) {
    this.concepts = concepts;
  }

  public Set<Concept> getConceptsAndAssociations() {
    Set<Concept> unionSet = new TreeSet<Concept>();
    unionSet.addAll(concepts);
    unionSet.addAll(associations);
    return unionSet;
  }

  public void addConcept(Concept concept) {
    concepts.add(concept);
  }

  public Set<Association> getAssociations() {
    return Collections.unmodifiableSet(associations);
  }

  public Set<Association> getAssociations(Concept c) {
    Set<Association> assoc = new HashSet<Association>();
    for (Association ac : associations) {
      if (ac.getConcepts().contains(c)) assoc.add(ac);
    }
    return assoc;
  }

  public <T extends Association> Set<T> getAssociations(Predicate<T> predicate, Class<T> clazz) {
    Set<T> assoc = new HashSet<T>();

    for (Association ac : associations) {
      if (clazz.isAssignableFrom(ac.getClass())) {
        @SuppressWarnings("unchecked")
        T ac1 = (T) ac;
        if (predicate.test(ac1)) {
          assoc.add(ac1);
        }
      }

    }
    return assoc;
  }

  public void addAssociation(Association association) {
    associations.add(association);
  }

  public Set<Constraint> getConstraints() {
    return Collections.unmodifiableSet(constraints);
  }

  public void addConstraint(Constraint constraint) {
    this.constraints.add(constraint);
  }

  public Set<ConceptConstraint> getConceptConstraints() {
    Set<ConceptConstraint> cc = new HashSet<ConceptConstraint>();

    for (Constraint con : constraints) {
      if (con instanceof ConceptConstraint) cc.add((ConceptConstraint) con);
    }

    return cc;
  }

  public Concept getConcept(String name) {
    for (Concept c : concepts) {
      if (c.getLabel().equalsIgnoreCase(name)) return c;
    }
    return null;
  }

  public Association getAssociation(String name) {
    for (Association ac : associations) {
      if (ac.getLabel().equalsIgnoreCase(name)) return ac;
    }
    return null;
  }

  public int getNumberOfConceptsAndAssociations() {
    return concepts.size() + associations.size();
  }
  
  public void fillBlockChain(BlockChain bc) {
    if (bc == null) throw new IllegalArgumentException("Blockchain has to exist!");
    bc.addBlock(new DSDBlock(bc.getPreviousHash(), this));
    for (Concept c : concepts) {
      c.fillBlockChain(bc);
    }
    for (Association a : associations) {
      bc.addBlock(new DSDBlock(bc.getPreviousHash(), a));
    }
    for (Constraint c : constraints) {
      bc.addBlock(new DSDBlock(bc.getPreviousHash(), c));
    }

  }
  
  public void addProfileToInflux(InfluxDBConnection connection) {
    super.storeProfile(connection);
    for (Concept c : concepts) {
      c.addProfileToInflux(connection);
    }
    for (Association a : associations) {
      a.addProfileToInflux(connection);
    }
    for (Constraint c : constraints) {
      c.addProfileToInflux(connection);
    }
  }

  public DBType getDBType() {
	return dbtype;
  }
}
