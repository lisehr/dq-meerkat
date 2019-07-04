package dqm.jku.trustkg.demos.alex;

import java.io.IOException;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Datasource;

public class TestConnectorCSV {
  public static void main(String args[]) {
    // Create Connection to CSV Connector
    DSInstanceConnector conn = new ConnectorCSV(
        "src/dqm/jku/trustkg/resources/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage",
        true);

    // Create Schema from it
    try {
      Datasource ds = conn.loadSchema();
    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }

    // TODO: Operations with Schemas
  }
}
