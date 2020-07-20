package dqm.jku.trustkg.dsd.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFContainer;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import dqm.jku.trustkg.blockchain.blocks.DSDBlock;
import dqm.jku.trustkg.blockchain.standardchain.BlockChain;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.util.AttributeSet;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Concept")
public class Concept extends DSDElement {

  private static final long serialVersionUID = 1L;
  private Datasource datasource;
  private HashSet<Attribute> attributes = new HashSet<Attribute>();
  private HashSet<Attribute> primaryKeys = new HashSet<Attribute>();
  private List<FunctionalDependency> functionalDependencies = new ArrayList<FunctionalDependency>();
  protected Set<ForeignKey> foreignKeys = new HashSet<ForeignKey>();

  public Concept() {
    super();
  }

  public Concept(String label, Datasource datasource) {
    super(label, datasource.getURI());
    this.datasource = datasource;
  }

  /**
   * @param datasource the datasource to set
   */
  public void setDatasource(Datasource datasource) {
    this.datasource = datasource;
  }

  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(AttributeSet attributes) {
    this.attributes = (HashSet<Attribute>) attributes.stream().collect(Collectors.toSet());
  }

  /**
   * @param primaryKey the primaryKey to set
   */
  public void setPrimaryKeySet(AttributeSet primaryKey) {
    this.primaryKeys = (HashSet<Attribute>) primaryKey.stream().collect(Collectors.toSet());
  }

  /**
   * @param functionalDependencies the functionalDependencies to set
   */
  public void setFunctionalDependencies(List<FunctionalDependency> functionalDependencies) {
    this.functionalDependencies = functionalDependencies;
  }

  /**
   * @param foreignKeys the foreignKeys to set
   */
  public void setForeignKeys(Set<ForeignKey> foreignKeys) {
    this.foreignKeys = foreignKeys;
  }

  @RDF("foaf:hasDatasource")
  public Datasource getDatasource() {
    return datasource;
  }

  public AttributeSet getAttributes() {
    return new AttributeSet(attributes);
  }
  
  @RDF("foaf:hasAttribute")
  @RDFContainer
  public HashSet<Attribute> getAttributeList(){
	  return attributes;
  }
  
  public void setAttributeList(HashSet<Attribute> att){
	  this.attributes = att;
  }


  public void addAttribute(Attribute attribute) {
    attributes.add(attribute);
  }

  public boolean containsAttribute(Attribute attribute) {
    return attributes.contains(attribute) && attribute.getConcept() == this;
  }

  public boolean containsAttribute(String attribute) {
    return this.getAttribute(attribute) != null;
  }

  @RDF("foaf:hasPK")
  @RDFContainer
  public HashSet<Attribute> getPrimaryKeySet(){
	return primaryKeys;  
  }
  
  public AttributeSet getPrimaryKeys() {
    return new AttributeSet(primaryKeys);
  }

  public void addPrimaryKeyAttribute(Attribute primaryKey) {
    if (attributes.contains(primaryKey)) {
      this.primaryKeys.add(primaryKey);
    } else {
      throw new IllegalArgumentException("Primary key is not contained in attributes list of concept.");
    }
  }

  public void addFunctionalDependency(FunctionalDependency fd) {
    functionalDependencies.add(fd);
    Collections.sort(functionalDependencies);
  }

  public List<Attribute> getSortedAttributes() {
    List<Attribute> list = new ArrayList<Attribute>(attributes);
    Collections.sort(list, new Comparator<Attribute>() {

      @Override
      public int compare(Attribute o1, Attribute o2) {
        return Integer.compare(o1.getOrdinalPosition(), o2.getOrdinalPosition());
      }

    });
    return list;
  }

  @RDF("foaf:hasFD")
  @RDFContainer
  public List<FunctionalDependency> getFunctionalDependencies() {
    return Collections.unmodifiableList(functionalDependencies);
  }

  public Attribute getAttribute(String name) {
    for (Attribute a : attributes) {
      if (a.getLabel().equalsIgnoreCase(name)) return a;
    }
    return null;
  }

  public List<FunctionalDependency> getFunctionalDependencies(Attribute right) {
    List<FunctionalDependency> res = new ArrayList<FunctionalDependency>();
    for (FunctionalDependency fd : functionalDependencies) {
      if (fd.getRightSide().equals(right)) res.add(fd);
    }
    return res;
  }

  public List<FunctionalDependency> getFunctionalDependencies(AttributeSet left) {
    List<FunctionalDependency> res = new ArrayList<FunctionalDependency>();
    for (FunctionalDependency fd : functionalDependencies) {
      if (fd.getLeftSide().equals(left)) res.add(fd);
    }
    return res;
  }

  public List<FunctionalDependency> getViableFunctionalDependencies(AttributeSet left) {
    List<FunctionalDependency> res = new ArrayList<FunctionalDependency>();
    for (FunctionalDependency fd : functionalDependencies) {
      if (left.contains(fd.getLeftSide())) res.add(fd);
    }
    return res;
  }

  @RDF("foaf:hasFK")
  @RDFContainer
  public Set<ForeignKey> getForeignKeys() {
    return Collections.unmodifiableSet(foreignKeys);
  }

  public Set<ForeignKey> getReferencingForeignKeys() {
    return foreignKeys.stream().filter(x -> x.getReferencingConcept() == this).collect(Collectors.toSet());
  }

  public Set<ForeignKey> getReferencedForeignKeys() {
    return foreignKeys.stream().filter(x -> x.getReferencedConcept() == this).collect(Collectors.toSet());
  }

  public void addForeignKey(ForeignKey foreignKey) {
    if (foreignKey.getReferencedConcept() == this || foreignKey.getReferencingConcept() == this) {
      foreignKeys.add(foreignKey);
    } else {
      throw new IllegalArgumentException("Foreignkey is not connected to this Concept.");
    }
  }

  public void fillBlockChain(BlockChain bc) {
    bc.addBlock(new DSDBlock(bc.getPreviousHash(), this));
    for (Attribute a : attributes) {
      bc.addBlock(new DSDBlock(bc.getPreviousHash(), a));
    }
  }

  @Override
  public void addProfileToInflux(InfluxDBConnection connection) {
    super.storeProfile(connection);
    for (Attribute a : attributes)
      a.addProfileToInflux(connection);
    for (FunctionalDependency fd : functionalDependencies)
      fd.addProfileToInflux(connection);
    for (ForeignKey fk : foreignKeys)
      fk.addProfileToInflux(connection);
  }

  /**
   * creates a point of measurements, which can be used for storing in influxDB
   * 
   * @param record the record to be stored
   * @return a measurement point or null if the record is null
   */
  public Point createMeasurement(Record record) {
    if (record == null) return null;
    Builder measure = Point.measurement(getLabel()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    for (Attribute a : record.getFields()) {
      if (record.getField(a) != null) {
        if (a.getDataType().equals(Integer.class)) measure.addField(a.getLabel(), (int) record.getField(a));
        else if (a.getDataType().equals(Long.class)) measure.addField(a.getLabel(), (long) record.getField(a));
        else if (a.getDataType().equals(Double.class)) measure.addField(a.getLabel(), (double) record.getField(a));
        else measure.addField(a.getLabel(), (int) ((String) record.getField(a)).length());
      }
    }
    return measure.build();
  }

}