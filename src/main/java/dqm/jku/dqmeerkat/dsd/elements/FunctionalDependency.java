package dqm.jku.dqmeerkat.dsd.elements;

import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV1;
import dqm.jku.dqmeerkat.util.AttributeSet;

public class FunctionalDependency extends DSDElement {

  private static final long serialVersionUID = 1L;

  private final Concept concept;
  private final Attribute rightSide;
  private final AttributeSet leftSide;

  public FunctionalDependency(Iterable<Attribute> left, Attribute right, Concept concept) {
    super("noStoredLabel");
    leftSide = new AttributeSet(left);
    rightSide = right;
    this.concept = concept;
  }

  @Override
  public String getURI() {
    StringBuilder sb = new StringBuilder(concept.getURI());
    sb.append("/FD_");
    for (Attribute a : leftSide) {
      String label = a.getLabel();
      sb.append(label.substring(0, 1).toUpperCase());
      sb.append(label.substring(1));
    }
    String label = rightSide.getLabel();
    sb.append(label.substring(0, 1).toUpperCase());
    sb.append(label.substring(1));
    return sb.toString();
  }

  public Concept getConcept() {
    return concept;
  }

  public Attribute getRightSide() {
    return rightSide;
  }

  public AttributeSet getLeftSide() {
    return leftSide;
  }

  @Override
  public String getLabel() {
    StringBuilder sb = new StringBuilder(leftSide.toString());
    sb.append("-->{");
    sb.append(rightSide.getLabel());
    sb.append("}");
    return sb.toString();
  }

  @Override
  public void addProfileToInflux(InfluxDBConnectionV1 connection) {
    super.storeProfile(connection);
  }

}
