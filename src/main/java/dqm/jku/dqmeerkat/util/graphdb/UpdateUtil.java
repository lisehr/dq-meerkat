package dqm.jku.dqmeerkat.util.graphdb;

import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * Utility methods for executing updates.
 */
public class UpdateUtil {
    /**
     * Executes a SPARQL UPDATE (INSERT or DELETE) statement.
     *
     * @param repositoryConnection a connection to a repository
     * @param update               the SPARQL UPDATE query in text form
     * @param bindings             optional bindings to set on the prepared query
     * @throws MalformedQueryException
     * @throws RepositoryException
     */
    public static void executeUpdate(RepositoryConnection repositoryConnection, String update, Binding... bindings)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {
        Update preparedUpdate = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, update);
        // Setting any potential bindings (query parameters)
        for (Binding b : bindings) {
            preparedUpdate.setBinding(b.getName(), b.getValue());
        }
        preparedUpdate.execute();
    }

    /**
     * Executes a SPARQL UPDATE (INSERT or DELETE) statement in a separate transaction,
     * i.e. the execution will be wrapped in connection.begin() / connection.commit() block.
     *
     * @param repositoryConnection a connection to a repository
     * @param update               the SPARQL UPDATE query in text form
     * @param bindings             optional bindings to set on the prepared query
     * @throws MalformedQueryException
     * @throws RepositoryException
     */
    public static void executeUpdateInTransaction(RepositoryConnection repositoryConnection, String update,
                                                  Binding... bindings)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {
        repositoryConnection.begin();

        executeUpdate(repositoryConnection, update, bindings);

        repositoryConnection.commit();
    }

}
