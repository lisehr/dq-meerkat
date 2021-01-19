package dqm.jku.dqmeerkat.dsd.elements;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.influxdb.InfluxDBConnection;

@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:Attribute")
public class Attribute extends DSDElement {

  private static final long serialVersionUID = 1L;

  private Concept concept;
  private boolean nullable;
  private boolean unique;
  private boolean autoIncrement;
  private int ordinalPosition;
  private Object defaultValue;
  private Class<?> dataType;

  public Attribute() {
    super();
  }
  
  public Attribute(String label, Concept concept) {
    super(label, concept.getURI());
    this.concept = concept;
  }

  @RDF("dsd:isNullable")
  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  @RDF("dsd:isUnique")
  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  @RDF("dsd:isAutoIncrement")
  public boolean isAutoIncrement() {
    return autoIncrement;
  }

  public void setAutoIncrement(boolean autoIncrement) {
    this.autoIncrement = autoIncrement;
  }

  @RDF("dsd:hasOrdinalPosition")
  public int getOrdinalPosition() {
    return ordinalPosition;
  }

  public void setOrdinalPosition(int ordinalPosition) {
    this.ordinalPosition = ordinalPosition;
  }

  @RDF("dsd:hasDefaultValue")
  public Object getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(Object defaultValue) {
    if (dataType.isInstance(defaultValue)) {
      this.defaultValue = defaultValue;
    } else {
      throw new IllegalArgumentException("Type of defaultValue does not match with type of Attribute.");
    }
  }

  @RDF("dsd:hasConcept")
  public Concept getConcept() {
    return concept;
  }
  
  public void setConcept(Concept concept) {
    this.concept = concept;
  }


  public Class<?> getDataType() {
    return dataType;
  }

  public void setDataType(Class<?> dataType) {
    this.dataType = dataType;
  }

  @RDF("dsd:isOfDataType")
  public String getDataTypeString() {
    return this.dataType.getName();
  }

  public void setDataTypeString(String type) {
    try {
      this.dataType = Class.forName(type);
    } catch (ClassNotFoundException e) {
      System.err.println("Class not found!");
    }
  }

  public void duplicate(Attribute newAttribute) {
    newAttribute.dataType = dataType;
    newAttribute.nullable = nullable;
    newAttribute.defaultValue = defaultValue;
    newAttribute.unique = unique;
    newAttribute.ordinalPosition = ordinalPosition;
    newAttribute.autoIncrement = autoIncrement;
  }

  @Override
  public void addProfileToInflux(InfluxDBConnection connection) {
    super.storeProfile(connection);
  }
  
  public boolean hasNumericDataType() {
  	return dataType == Integer.class || dataType == Long.class || dataType == Double.class;
  }

}
