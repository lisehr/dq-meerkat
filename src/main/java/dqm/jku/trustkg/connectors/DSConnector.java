package dqm.jku.trustkg.connectors;

import java.io.IOException;

import dqm.jku.trustkg.dsd.elements.Datasource;

public abstract class DSConnector {
	
	public abstract Datasource loadSchema() throws IOException;

}
