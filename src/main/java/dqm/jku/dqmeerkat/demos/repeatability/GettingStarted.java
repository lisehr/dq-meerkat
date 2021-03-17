package dqm.jku.dqmeerkat.demos.repeatability;

import java.io.IOException;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.dsd.DSDKnowledgeGraph;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnection;
import dqm.jku.dqmeerkat.util.Constants;

/**
 * Getting started class for first steps with DQ-MeeRKat
 * 
 * @author lisa
 *
 */
public class GettingStarted {

  private static final int RDP_SIZE = 1000;

  public static void main(String[] args) throws IOException, NoSuchMethodException {

    /** PHASE 2: SYSTEM SETUP & RDP CREATION *****/

    // Create Connection to Supplychain dataset (once with CSV connector for
    // repeatability; and once with MySQL connector as used for the demo)
//		ConnectorMySQL conn1 = ConnectorMySQL.getInstance("jdbc:mysql://localhost:3366/", "DataCo_Supplychain", args[0], args[1]);
    ConnectorCSV conn1 = new ConnectorCSV(Constants.RESOURCES_FOLDER + "csv/DataCoSupplyChainDataset.csv", ",", "\n", "SupplyChain");

    // Create Connection to Tributech car engine data stream (once with CSV
    // connector for repeatability; and once with Cassandra connector as used for
    // the demo)
//		ConnectorCassandra conn2 = new ConnectorCassandra("bursa.scch.at", "Tributech-datastream", args[2], args[3]);
    ConnectorCSV conn2 = new ConnectorCSV(Constants.RESOURCES_FOLDER + "csv/DataCoSupplyChainDataset.csv", ",", "\n", "CarEngineStream");

    // Optionally, create connection to Test data
//		ConnectorCSV conn3 = new ConnectorCSV(Constants.RESSOURCES + "csv/Test.csv", ",", "\n", "Test data");

    // Init KG by loading DSD elements
    Datasource dsSC = conn1.loadSchema("https://faw.jku.at", "sc:");
    Datasource dsCE = conn2.loadSchema("https://faw.jku.at", "ce:");
//		Datasource dsTD = conn3.loadSchema("https://faw.jku.at", "td:");

    // Create Knowledge Graph and add (possibly multiple) data sources
    DSDKnowledgeGraph kg = new DSDKnowledgeGraph("automotive");
    kg.addDatasourceAndConnector(dsSC, conn1);
    kg.addDatasourceAndConnector(dsCE, conn2);
//		kg.addDatasourceAndConnector(ds, conn);

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
