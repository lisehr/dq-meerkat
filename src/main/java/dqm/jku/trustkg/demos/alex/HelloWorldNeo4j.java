package dqm.jku.trustkg.demos.alex;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;
  
// Important: Server needs to be running!

public class HelloWorldNeo4j implements AutoCloseable {
  private final Driver driver;

  public HelloWorldNeo4j(String uri, String user, String password) {
    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
  }

  @Override
  public void close() throws Exception {
    driver.close();
  }

  public void printGreeting(final String message) {
    try (Session session = driver.session()) {
      String greeting = session.writeTransaction(new TransactionWork<String>() {
        @Override
        public String execute(Transaction tx) {
          StatementResult result = tx
              .run("CREATE (a:Greeting) " + "SET a.message = $message " + "RETURN a.message + ', from node ' + id(a)",
                  parameters("message", message));
          return result.single().get(0).asString();
        }
      });
      System.out.println(greeting);
    }
  }

  public static void main(String... args) throws Exception {
    try (HelloWorldNeo4j greeter = new HelloWorldNeo4j("bolt://localhost:7687", "neo4j", "password")) {
      greeter.printGreeting("hello, world");
    }
  }
}
