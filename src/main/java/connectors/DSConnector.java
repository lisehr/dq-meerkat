package connectors;

import java.io.IOException;

import dsd.elements.Datasource;

public abstract class DSConnector {
	
	public abstract Datasource loadSchema() throws IOException;

}
