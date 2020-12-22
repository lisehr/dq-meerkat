package dqm.jku.dqmeerkat.demos.graphmodels;

import java.io.IOException;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;

public class TestConnectorCSV2 {
  public static void main(String args[]) {
    // Create Connection to CSV Connector
    DSConnector conn = new ConnectorCSV("src/main/java/dqm/jku/trustkg/resources/Telematic Device Report - Device Voltage.csv", ",", "\n", "Device Voltage", true);

    // Create Schema from it
    Datasource ds;
    try {
      ds = conn.loadSchema();

      // URI of the DataSource
      System.out.println(ds.getURI());

      // add a new concept to the DataSource
      Concept conc1 = new Concept("device usage", ds);
      ds.addConcept(conc1);

      for (Concept c : ds.getConcepts()) {
        System.out.println(c.toString());
        // how to get an attribute and how to check if a concept contains an attribute
        System.out.println(c.containsAttribute(c.getAttribute("vehicle")));

        // when you create a new attribute, it is not automatically linked to the
        // concept, despite having to specify the concept
        Attribute a1 = new Attribute("testAttribute", c);
        System.out.println(c.containsAttribute(a1));

        // so you have to manually add it to the concept
        c.addAttribute(a1);
        System.out.println(c.containsAttribute(a1));

        // however, the new attribute's datatype has to be specified, otherwise it
        // causes a nullpointer exception
        a1.setDataType("String".getClass());

        // testing the newly created containsAttribute(String attribute) method
        System.out.println("This works: " + c.containsAttribute("testAttribute"));
        for (Attribute a : c.getAttributes()) {
          if (a.getDataType() != null) System.out.println("Attribute: " + a.getDataType().toString() + "\t" + a.toString());
        }
        System.out.println();
      }

    } catch (IOException e) {
      System.err.println("Could not load Schema!");
    }

  }
}
