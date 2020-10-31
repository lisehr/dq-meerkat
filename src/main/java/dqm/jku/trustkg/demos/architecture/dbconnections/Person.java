package dqm.jku.trustkg.demos.architecture.dbconnections;

import java.net.URI;
import java.util.Collection;
import java.util.Date;

import org.cyberborean.rdfbeans.annotations.*;
import org.cyberborean.rdfbeans.annotations.RDFContainer.ContainerType;

/**
 * Person class for testing purposes
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://xmlns.com/dsd/0.1/", "persons = http://rdfbeans.viceversatech.com/test-ontology/persons/" })
@RDFBean("dsd:Person")
public class Person {

  private String id;
  private String name;
  private String email;
  private URI homepage;
  private Date birthday;
  private String[] nick;
  private Collection<Person> knows;

  /** Default no-arg constructor */
  public Person() {
  }

  @RDFSubject(prefix = "persons:")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @RDF("dsd:name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @RDF("dsd:mbox")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @RDF("dsd:homepage")
  public URI getHomepage() {
    return homepage;
  }

  public void setHomepage(URI homepage) {
    this.homepage = homepage;
  }

  @RDF("dsd:birthday")
  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  @RDF("dsd:nick")
  @RDFContainer(ContainerType.ALT)
  public String[] getNick() {
    return nick;
  }

  public void setNick(String[] nick) {
    this.nick = nick;
  }

  public String getNick(int i) {
    return nick[i];
  }

  public void setNick(int i, String nick) {
    this.nick[i] = nick;
  }

  @RDF("dsd:knows")
  public Collection<Person> getKnows() {
    return knows;
  }

  public void setKnows(Collection<Person> knows) {
    this.knows = knows;
  }
}