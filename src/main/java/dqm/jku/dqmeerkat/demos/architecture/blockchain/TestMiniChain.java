package dqm.jku.dqmeerkat.demos.architecture.blockchain;

import java.io.IOException;
import java.util.ArrayList;

import dqm.jku.dqmeerkat.blockchain.minichain.MiniBlockChain;
import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;

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
