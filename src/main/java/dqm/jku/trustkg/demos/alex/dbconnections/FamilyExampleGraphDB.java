package dqm.jku.trustkg.demos.alex;

import java.io.IOException;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.ValidatingValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;

import dqm.jku.trustkg.graphdb.EmbeddedGraphDB;
import dqm.jku.trustkg.util.graphdb.QueryUtil;
import dqm.jku.trustkg.util.graphdb.UpdateUtil;

/**
 * An example that illustrates loading of ontologies, data, querying and
 * modifying data.
 */
public class FamilyExampleGraphDB {
  private RepositoryConnection connection;

  public FamilyExampleGraphDB(RepositoryConnection connection) {
    this.connection = connection;
  }

  /**
   * Loads the ontology and the sample data into the repository.
   *
   * @throws RepositoryException
   * @throws IOException
   * @throws RDFParseException
   */
  public void loadData() throws RepositoryException, IOException, RDFParseException {
    System.out.println("# Loading ontology and data");

    // When adding data we need to start a transaction
    connection.begin();

    // Adding the family ontology
    connection.add(FamilyExampleGraphDB.class.getResourceAsStream("../../graphdb/family-ontology.ttl"), "urn:base", RDFFormat.TURTLE);

    // Adding some family data
    connection.add(FamilyExampleGraphDB.class.getResourceAsStream("../../graphdb/family-data.ttl"), "urn:base", RDFFormat.TURTLE);

    // Committing the transaction persists the data
    connection.commit();
  }

  /**
   * Lists family relations for a given person. The output will be printed to
   * stdout.
   *
   * @param person a person (the local part of a URI)
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  public void listRelationsForPerson(String person) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    System.out.println("# Listing family relations for " + person);

    // A simple query that will return the family relations for the provided person
    // parameter
    TupleQueryResult result = QueryUtil
        .evaluateSelectQuery(connection, "PREFIX family: <http://examples.ontotext.com/family#>" + "SELECT ?p1 ?r ?p2 WHERE {" + "?p1 ?r ?p2 ." + "?r rdfs:subPropertyOf family:hasRelative ."
            + "FILTER(?r != family:hasRelative)" + "}", new SimpleBinding("p1", uriForPerson(person)));

    while (result.hasNext()) {
      BindingSet bindingSet = result.next();
      IRI p1 = (IRI) bindingSet.getBinding("p1").getValue();
      IRI r = (IRI) bindingSet.getBinding("r").getValue();
      IRI p2 = (IRI) bindingSet.getBinding("p2").getValue();

      System.out.println(p1.getLocalName() + " " + r.getLocalName() + " " + p2.getLocalName());
    }
    // Once we are done with a particular result we need to close it
    result.close();
  }

  /**
   * Deletes all triples that refer to a person (i.e. where the person is the
   * subject or the object).
   *
   * @param person the local part of a URI referring to a person
   * @throws RepositoryException
   */
  public void deletePerson(String person) throws RepositoryException {
    System.out.println("# Deleting " + person);

    // When removing data we need to start a transaction
    connection.begin();

    // Removing a person means deleting all triples where the person is the subject
    // or the object.
    // Alternatively, this can be done with SPARQL.
    connection.remove(uriForPerson(person), null, null);
    connection.remove((IRI) null, null, uriForPerson(person));

    // Committing the transaction persists the changes
    connection.commit();
  }

  /**
   * Adds a child relation to a person, i.e. inserts the triple :person :hasChild
   * :child.
   *
   * @param child  the local part of a URI referring to a person (the child)
   * @param person the local part of a URI referring to a person
   * @throws MalformedQueryException
   * @throws RepositoryException
   * @throws UpdateExecutionException
   */
  public void addChildToPerson(String child, String person) throws MalformedQueryException, RepositoryException, UpdateExecutionException {
    System.out.println("# Adding " + child + " as a child to " + person);

    IRI childURI = uriForPerson(child);
    IRI personURI = uriForPerson(person);

    // When adding data we need to start a transaction
    connection.begin();

    // We interpolate the URIs inside the string as INSERT DATA may not contain
    // variables (bindings)
    UpdateUtil.executeUpdate(connection, String.format("PREFIX family: <http://examples.ontotext.com/family#>" + "INSERT DATA {" + "<%s> family:hasChild <%s>" + "}", personURI, childURI));

    // Committing the transaction persists the changes
    connection.commit();
  }

  private IRI uriForPerson(String person) {
    return new ValidatingValueFactory().createIRI("http://examples.ontotext.com/family/data#" + person);
  }

  public static void main(String[] args) throws Exception {
    // Open connection to a new temporary repository
    // (in order to infer grandparents/grandchildren we need the OWL2-RL ruleset)
    RepositoryConnection connection = EmbeddedGraphDB.openConnectionToTemporaryRepository("owl2-rl-optimized");

    /*
     * Alternative: connect to a remote repository
     * 
     * // Abstract representation of a remote repository accessible over HTTP
     * HTTPRepository repository = new
     * HTTPRepository("http://localhost:8080/graphdb/repositories/myrepo");
     * 
     * // Separate connection to a repository RepositoryConnection connection =
     * repository.getConnection();
     * 
     */

    // Clear the repository before we start
    connection.clear();

    FamilyExampleGraphDB familyRelations = new FamilyExampleGraphDB(connection);

    try {
      familyRelations.loadData();

      // Once we've loaded the data we should see all explicit and implicit relations
      // for John
      familyRelations.listRelationsForPerson("John");

      // Let's delete Mary
      familyRelations.deletePerson("Mary");

      // Deleting Mary also removes Kate from John's list of relatives as Kate is his
      // relative through Mary
      familyRelations.listRelationsForPerson("John");

      // Let's add some children to Charles
      familyRelations.addChildToPerson("Bob", "Charles");
      familyRelations.addChildToPerson("Annie", "Charles");

      // After adding two children to Charles John's family is big again
      familyRelations.listRelationsForPerson("John");
    } finally {
      // It is best to close the connection in a finally block
      connection.close();
    }
  }
}