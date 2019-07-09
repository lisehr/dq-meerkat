package dqm.jku.trustkg.util.graphdb;

import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * Utility class for evaluating SPARQL queries.
 */
public class QueryUtil {
    /**
     * Prepares (parses) any kind of SPARQL query.
     *
     * @param connection a connection to a repository
     * @param query      a SPARQL query in text form
     * @return the prepared query (an instance of TupleQuery, BooleanQuery etc.)
     * @throws MalformedQueryException
     * @throws RepositoryException
     */
    public static Query prepareQuery(RepositoryConnection connection, String query)
            throws MalformedQueryException, RepositoryException {
        return connection.prepareQuery(QueryLanguage.SPARQL, query);
    }

    /**
     * Evaluates a SPARQL SELECT query and returns the result.
     *
     * @param connection a connection to a repository
     * @param query      a SPARQL SELECT query in text form
     * @param bindings   optional bindings to set on the prepared query
     * @return a TupleQueryResult that can be used to retrieve bindings
     * @throws MalformedQueryException
     * @throws RepositoryException
     * @throws QueryEvaluationException
     */
    public static TupleQueryResult evaluateSelectQuery(RepositoryConnection connection, String query,
                                                       Binding... bindings)
            throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        // Preparing a new query
        TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

        // Setting any potential bindings (query parameters)
        for (Binding b : bindings) {
            tupleQuery.setBinding(b.getName(), b.getValue());
        }

        // Sending the query to GraphDB, evaluating it and returning the result
        return tupleQuery.evaluate();
    }

    /**
     * Evaluates a SPARQL CONSTRUCT query and returns the result.
     *
     * @param connection a connection to a repository
     * @param query      a SPARQL CONSTRUCT query in text form
     * @param bindings   optional bindings to set on the prepared query
     * @return a GraphQueryResult that can be used to retrieve the graph
     * @throws MalformedQueryException
     * @throws RepositoryException
     * @throws QueryEvaluationException
     */
    public static GraphQueryResult evaluateConstructQuery(RepositoryConnection connection, String query,
                                                          Binding... bindings)
            throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        // Preparing a new query
        GraphQuery graphdQuery = connection.prepareGraphQuery(QueryLanguage.SPARQL, query);

        // Setting any potential bindings (query parameters)
        for (Binding b : bindings) {
            graphdQuery.setBinding(b.getName(), b.getValue());
        }

        // Sending the query to GraphDB, evaluating it and returning the result
        return graphdQuery.evaluate();
    }

    /**
     * Evaluates a SPARQL ASK query and returns the result.
     *
     * @param connection a connection to a repository
     * @param query      a SPARQL ASK query in text form
     * @param bindings   optional bindings to set on the prepared query
     * @return a boolean that represents the result of the ASK query
     * @throws MalformedQueryException
     * @throws RepositoryException
     * @throws QueryEvaluationException
     */
    public static boolean evaluateAskQuery(RepositoryConnection connection, String query, Binding... bindings)
            throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        // Preparing a new query
        BooleanQuery booleanQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, query);

        // Setting any potential bindings (query parameters)
        for (Binding b : bindings) {
            booleanQuery.setBinding(b.getName(), b.getValue());
        }

        // Sending the query to GraphDB and evaluating it
        return booleanQuery.evaluate();
    }
}
