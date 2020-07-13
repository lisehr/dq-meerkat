package dqm.jku.trustkg.demos.alex.blockchain;

import java.io.IOException;
import java.util.ArrayList;

import dqm.jku.trustkg.blockchain.minichain.MiniBlockChain;
import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.elements.Datasource;

/**
 * Test class for testing minichains
 * 
 * @author optimusseptim
 *
 */
public class TestMiniChain {

  public static void main(String args[]) throws IOException {
    // Create Connection to CSV Connector
    DSConnector conn = new ConnectorCSV("src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);

    MiniBlockChain mbc = new MiniBlockChain();

    Datasource ds = conn.loadSchema();
    ArrayList<DSDElement> elements = new ArrayList<>();
    for (Concept c : ds.getConcepts()) {
      elements.add(c);
      for (Attribute a : c.getAttributes()) {
        elements.add(a);
      }
    }

    for (DSDElement e : elements) {
      mbc.addDSDElement(e);
    }
    System.out.println("Hook for setting breakpoint!");
  }

}
