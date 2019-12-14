package dqm.jku.trustkg.connectors;

import java.io.IOException;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;

import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.util.DataTypeConverter;
import dqm.jku.trustkg.util.Miscellaneous.DBType;

/**
 * The connector for the NoSQL database Cassandra
 * 
 * @author Julia
 */
public class ConnectorCassandra extends DSConnector {

	private final Session session;
	private final Cluster cluster;
	private final String keyspace;

	/**
	 * Constructor for the Cassandra connector
	 * 
	 * @param address
	 *            IP address for the connection to the database
	 * @param keyspace
	 *            name of the investigated Cassandra's keyspace
	 */
	public ConnectorCassandra(String address, String keyspace) {
		this(address, keyspace, null, null);
	}

	public ConnectorCassandra(String address, String keyspace, String user, String pw) {
		// Store address and keyspace for further processing
		this.keyspace = keyspace;

		// Building a cluster
		Builder builder = Cluster.builder();
		builder.addContactPoint(address);
		if (user != null && pw != null) {
			builder.withCredentials(user, pw);
		}
		this.cluster = builder.build();
		this.session = cluster.connect(keyspace);
	}

	@Override
	public Datasource loadSchema() throws IOException {
		// Create empty datasource object
		Datasource ds = DSDFactory.makeDatasource(keyspace, DBType.CASSANDRA);
		// Go over all tables in the keyspace
		for (TableMetadata table : this.session.getCluster().getMetadata().getKeyspace(keyspace).getTables()) {
			// Create concept
			Concept c = DSDFactory.makeConcept(table.getName(), ds);
			// Get attributes
			loadAttributes(table, c);
		}
		return ds;
	}

	/**
	 * Loads attributes into the DSD vocabulary/Concept c from a Cassandra table
	 * 
	 * @param table
	 * @param c
	 */
	private void loadAttributes(TableMetadata table, Concept c) {
		// Get the list of primary keys
		List<ColumnMetadata> primary = table.getPrimaryKey();
		// Go over all columns
		int i = 0;
		for (ColumnMetadata column : table.getColumns()) {
			Attribute a = DSDFactory.makeAttribute(column.getName(), c);
			// Set primary key
			if (primary.contains(column)) {
				a.setNullable(false);
				a.setUnique(true);
				c.addPrimaryKeyAttribute(a);
			} else {
				a.setNullable(true);
			}
			a.setAutoIncrement(false);
			a.setOrdinalPosition(i);
			i++;
			// Set data type
			DataTypeConverter.getTypeFromCassandra(a, column.getType());
		}
	}

	public void close() {
		cluster.close();
		session.close();
	}

	@Override
	public dqm.jku.trustkg.dsd.elements.Datasource loadSchema(String uri, String prefix) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
