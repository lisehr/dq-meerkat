package dqm.jku.dqmeerkat.demos.architecture.connector;

import dqm.jku.dqmeerkat.connectors.ConnectorMongoDB;
import dqm.jku.dqmeerkat.connectors.ConnectorMongoDBFlatten;
import dqm.jku.dqmeerkat.connectors.ConnectorMongoDBSimple;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;

import java.io.IOException;


public class TestConnectorMongoDB {
    public static void main(String args[]) {
        try {
            DSConnector conn = new ConnectorMongoDBSimple("localhost", "sample_training", "admin", "adminpw");
            Datasource ds;

            ds = conn.loadSchema();
            for (Concept c : ds.getConcepts()) {
                System.out.println(c.getURI());

                for (Attribute a : c.getAttributes()) {
                    System.out.println(a.getDataType().toString() + "\t" + a.getURI());
                }
                System.out.println();
            }

        } catch (IOException e) {
            System.err.println("Could not load Schema!");
        }

    }
}
