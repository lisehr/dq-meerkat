package dqm.jku.trustkg.demos.lisa;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import dqm.jku.trustkg.graphdb.EmbeddedGraphDB;

public class RetrieveDataProfileFromGraphDB {
  public void hello() throws Exception {
    // Open connection to a new temporary repository
    // (ruleset is irrelevant for this example)
    RepositoryConnection connection = EmbeddedGraphDB.openConnectionToTemporaryRepository("rdfs");

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

    try {
      // Preparing a SELECT query for later evaluation
      TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?x WHERE {" + "BIND('Hello world!' as ?x)" + "}");

      // Evaluating a prepared query returns an iterator-like object
      // that can be traversed with the methods hasNext() and next()
      TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
      while (tupleQueryResult.hasNext()) {
        // Each result is represented by a BindingSet, which corresponds to a result row
        BindingSet bindingSet = tupleQueryResult.next();

        // Each BindingSet contains one or more Bindings
        for (Binding binding : bindingSet) {
          // Each Binding contains the variable name and the value for this result row
          String name = binding.getName();
          Value value = binding.getValue();

          System.out.println(name + " = " + value);
        }

        // Bindings can also be accessed explicitly by variable name
        // Binding binding = bindingSet.getBinding("x");
      }

      // Once we are done with a particular result we need to close it
      tupleQueryResult.close();

      // Doing more with the same connection object
      // ...
    } finally {
      // It is best to close the connection in a finally block
      connection.close();
    }
  }

  public static void main(String[] args) throws Exception {
    new RetrieveDataProfileFromGraphDB().hello();
  }
}