package dqm.jku.trustkg.dsd.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.cyberborean.rdfbeans.annotations.*;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.quality.DataProfile;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:DSDElement")
public abstract class DSDElement implements Serializable, Comparable<DSDElement> {

  private static final long serialVersionUID = 1L;
  private static HashMap<String, DSDElement> cache = new HashMap<String, DSDElement>();
  private String uri;
  
  protected String label;
  protected String labelOriginal;

  private DataProfile dataProfile;

  @RDFSubject
  public String getURI() {
    return uri;
  }

  public void setURI(String uri) {
    this.uri = uri;
  }

  public DSDElement() {

  }

  public DSDElement(String label) {
    this.label = label.toLowerCase();
    this.labelOriginal = label;
  }

  public DSDElement(String label, String uri) {
    this.label = label.toLowerCase();
    this.labelOriginal = label;
    this.uri = uri + '/' + label;
  }

  @RDF("foaf:dataProfile")
  public DataProfile getProfile() {
    return dataProfile;
  }
  
  public boolean hasProfile() {
	  return !(dataProfile == null);
  }

  public void setProfile(DataProfile dataProfile) {
    this.dataProfile = dataProfile;
  }

  public void annotateProfile(RecordList rs) throws NoSuchMethodException {
    dataProfile = new DataProfile(rs, this);
  }
  
  public DataProfile createDataProfile(RecordList rs) throws NoSuchMethodException {
    return new DataProfile(rs, this);
  }

  public void printAnnotatedProfile() {
    if(dataProfile != null) {
    	System.out.println("Annotated data profile for DSDElement: " + label);
    	dataProfile.printProfile();
    } else {
    	System.out.println("No data profile annotated for DSDElement: " + label);
    }
  }
  
  public String getProfileString() {
    StringBuilder sb = new StringBuilder();
    if(dataProfile != null) {
      sb.append("Annotated data profile for DSDElement: " + label);
      sb.append('\n');
      sb.append(dataProfile.getProfileString());
    } else {
      sb.append("No data profile annotated for DSDElement: " + label);
      sb.append('\n');
    }
    return sb.toString();
  }

  @RDF("foaf:label")
  public String getLabel() {
    return label;
  }
  
  public String getLabelWithoutBlanks(String replmt) {
	  return label.replace(" ", replmt);
  }

  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * The original label is currently only used for calculating the readability
   * dimension
   **/
  public String getLabelOriginal() {
    return labelOriginal;
  }

  @Override
  public String toString() {
    return getURI();
  }

  @Override
  public int compareTo(DSDElement other) {
    return getURI().compareTo(other.getURI());
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, getURI());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DSDElement other = (DSDElement) obj;
    String uri = getURI();
    String otherUri = other.getURI();
    if (uri == null) {
      if (otherUri != null) return false;
    } else if (!uri.equals(otherUri)) return false;
    return true;
  }

  public static List<Datasource> getAllDatasources() {
    List<Datasource> list = new ArrayList<Datasource>();
    for (DSDElement e : cache.values()) {
      if (e instanceof Datasource) {
        list.add((Datasource) e);
      }
    }
    return list;
  }

  public static Optional<Datasource> getDatasource(String label) {
    return getAllDatasources().stream().filter(x -> x.label.equalsIgnoreCase(label)).findFirst();
  }

  public static List<Concept> getAllConcepts() {
    List<Concept> list = new ArrayList<Concept>();
    for (DSDElement e : cache.values()) {
      if (e instanceof Concept) {
        list.add((Concept) e);
      }
    }
    return list;
  }

  public static DSDElement get(String uri) {
    return cache.get(uri);
  }

  @SuppressWarnings("unchecked")
  public static <T extends DSDElement> T get(T elem) {
    String uri = elem.getURI();
    if (!cache.containsKey(uri)) {
      cache.put(uri, elem);
    }
    return (T) cache.get(uri);
  }

  public static Collection<DSDElement> getCache() {
    return cache.values();
  }

  public static void replace(DSDElement elem) {
    String uri = elem.getURI();
    if (cache.containsKey(uri)) {
      cache.remove(uri);
    }
    cache.put(uri, elem);
  }

  public abstract void addProfileToInflux(InfluxDBConnection connection);

  public void storeProfile(InfluxDBConnection connection) {
    if (this.dataProfile == null) return;
    if (this.dataProfile.getMetrics().stream().allMatch(m -> (m.getValue() == null))) return;
    Builder measure = Point.measurement(getURI()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    connection.write(this.dataProfile.createMeasuringPoint(measure));
  }
  
  public void storeProfile(InfluxDBConnection connection, DataProfile profile) {
    if (profile == null) return;
    if (profile.getMetrics().stream().allMatch(m -> (m.getValue() == null))) return;
    Builder measure = Point.measurement(getURI()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    connection.write(profile.createMeasuringPoint(measure));
  }
}
