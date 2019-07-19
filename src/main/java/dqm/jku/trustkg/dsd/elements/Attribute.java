package dqm.jku.trustkg.dsd.elements;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;


import dqm.jku.trustkg.influxdb.InfluxDBConnection;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Attribute")
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
    super(label, concept.getURI() + "/" + label);
    this.concept = concept;
  }

  @RDF("foaf:isNullable")
  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  @RDF("foaf:isUnique")
  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  @RDF("foaf:isAutoIncrement")
  public boolean isAutoIncrement() {
    return autoIncrement;
  }

  public void setAutoIncrement(boolean autoIncrement) {
    this.autoIncrement = autoIncrement;
  }

  @RDF("foaf:ordinalPosition")
  public int getOrdinalPosition() {
    return ordinalPosition;
  }

  public void setOrdinalPosition(int ordinalPosition) {
    this.ordinalPosition = ordinalPosition;
  }

  @RDF("foaf:defaultValue")
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

  @RDF("foaf:concept")
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

  @RDF("foaf:dataType")
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
  public void addMeasurementToInflux(InfluxDBConnection connection) {
    super.storeProfile(connection);
  }

}
