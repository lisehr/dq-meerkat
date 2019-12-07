package dqm.jku.trustkg.demos;

import java.io.IOException;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.DSDKnowledgeGraph;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.util.Constants;

public class GettingStarted {

  private static final int RDP_SIZE = 1000;

  public static void main(String[] args) throws IOException, NoSuchMethodException {
	  
	/** PHASE 2: SYSTEM SETUP & RDP CREATION *****/
	// Create Connection to CSV Connector
    ConnectorCSV conn = new ConnectorCSV(Constants.RESSOURCES + "csv/DataCoSupplyChainDataset.csv", ",", "\n", "SupplyChain");

    // Init KG by loading DSD elements
    Datasource ds = conn.loadSchema("https://faw.jku.at", "sc:");

    // Create Knowledge Graph and add (possibly multiple) data sources
    DSDKnowledgeGraph kg = new DSDKnowledgeGraph("supplychain");
    kg.addDatasourceAndConnector(ds, conn);

    // Annotate reference data profile to KG
    kg.addDataProfile(RDP_SIZE);

    // Persist KG to Embedded GraphDB
    // done automatically

    // Export KG to .ttl file
    kg.exportKGToFile("supplychain");
    
    
    /** PHASE 2: CONTINUOUS DQ MONITORING *****/
    InfluxDBConnection influx = new InfluxDBConnection();
    kg.addProfilesToInflux(influx);
    
  }

}
