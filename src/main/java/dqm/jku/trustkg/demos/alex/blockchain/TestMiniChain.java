package dqm.jku.trustkg.demos.alex.blockchain;

import java.io.IOException;
import java.util.ArrayList;

import dqm.jku.trustkg.blockchain.blocks.DSDBlock;
import dqm.jku.trustkg.blockchain.minichain.MiniBlockChain;
import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.elements.Datasource;

public class TestMiniChain {
  
  public static void main (String args[]) throws IOException {
    // Create Connection to CSV Connector
    DSInstanceConnector conn = new ConnectorCSV("src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);

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
      mbc.addBlock(new DSDBlock(mbc.findPreviousHash(e), e));
    }
    System.out.println("Hook for setting breakpoint!");
  }

}
