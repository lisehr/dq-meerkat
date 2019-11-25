package dqm.jku.trustkg.dsd;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.graphdb.EmbeddedGraphDB;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.export.ExportUtil;

/**
 * @author Lisa
 * 
 *         Wrapper class for the entire KG to manage multiple data sources and
 *         their connections. Allows to automatically annotate data profiles for
 *         multiple data sources, persist, or export the KG.
 *
 */
public class DSDKnowledgeGraph {

  EmbeddedGraphDB kgstore;

  HashMap<String, Datasource> dss = new HashMap<String, Datasource>();
  HashMap<String, DSConnector> conns = new HashMap<String, DSConnector>();

  public DSDKnowledgeGraph(String label) {
    kgstore = new EmbeddedGraphDB(label);
  }

  public void addDatasource(Datasource ds) throws RepositoryConfigException, RDFHandlerException, RDFParseException, RepositoryException, IOException {
    dss.put(ds.getLabel(), ds);
    kgstore.createRepositoryIfNotExists(ds.getLabel());
    RepositoryConnection repConn = kgstore.getRepository(ds.getLabel()).getConnection();
    repConn.add(ds.getGraphModel());
  }

  public void addDatasourceAndConnector(Datasource ds, DSConnector conn) {
    dss.put(ds.getLabel(), ds);
    conns.put(ds.getLabel(), conn);
  }

  /**
   * Method to create a datasource via the passed connection, uri and prefix
   * @param conn the Datasource connector
   * @param uri uri of the datasource
   * @param prefix prefix of the datasource
   * @throws IOException
   */
  public void addDatasourceViaConnector(DSConnector conn, String uri, String prefix) throws IOException {
    Datasource ds = conn.loadSchema(uri, prefix);
    dss.put(ds.getLabel(), ds);
    conns.put(ds.getLabel(), conn);
  }
  
  /**
   * Method to create a datasource via the passed connection with standard uri and prefix
   * @param conn the Datasource connector
   * @throws IOException
   */
  public void addDatasourceViaConnector(DSConnector conn) throws IOException {
    Datasource ds = conn.loadSchema();
    dss.put(ds.getLabel(), ds);
    conns.put(ds.getLabel(), conn);
  }
  
  /**
   * Method to create a datasource via the passed connection with standard uri and prefix
   * @param conn the Datasource connector
   * @throws IOException
   */
  public void addDatasourcesViaConnectors(List<DSConnector> connls) throws IOException {
   for (DSConnector conn : connls) {
     Datasource ds = conn.loadSchema();
     dss.put(ds.getLabel(), ds);
     conns.put(ds.getLabel(), conn);
   }
  }


  /**
   * Method to automatically add data profile to all data sources (currently works
   * only for CSV connector)
   * 
   * @param noRecords
   * @throws IOException
   * @throws NoSuchMethodException
   */
  public void addDataProfile(Integer noRecords) throws IOException, NoSuchMethodException {
    for (Datasource ds : dss.values()) {
      for (Concept c : ds.getConceptsAndAssociations()) {
        ConnectorCSV conn = (ConnectorCSV) conns.get(ds.getLabel());
        RecordList rs = conn.getPartialRecordSet(c, 0, noRecords);
        for (Attribute a : c.getAttributes()) {
          a.annotateProfile(rs);
        }
      }
    }
  }

  /**
   * Method to automatically add data profile with all available records to all
   * data sources (currently works only for CSV connector)
   * 
   * @throws IOException
   * @throws NoSuchMethodException
   */
  public void addDataProfile() throws IOException, NoSuchMethodException {
    for (Datasource ds : dss.values()) {
      for (Concept c : ds.getConceptsAndAssociations()) {
        ConnectorCSV conn = (ConnectorCSV) conns.get(ds.getLabel());
        RecordList rs = conn.getRecordSet(c);
        for (Attribute a : c.getAttributes()) {
          a.annotateProfile(rs);
        }
      }
    }
  }

  /**
   * Method to export knowledge graph file in Turtle syntax to export folder.
   * TODO: refine to make comprehensive .ttl export.
   * 
   * @param fileName
   */
  public void exportKGToFile(String fileName) {
    Path path = Paths.get(Constants.RESSOURCES + "export/ttl/");
    if (Files.notExists(path)) {
      try {
        Files.createDirectory(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try (PrintWriter out = new PrintWriter(new File(Constants.RESSOURCES + "export/ttl/" + fileName + ".ttl"))){      
      for (Datasource ds : dss.values()) {
        Rio.write(ds.getGraphModel(), out, RDFFormat.TURTLE);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method to export data profiles in CSV format to export folder.
   * 
   */
  public void exportToCSV() {
    ExportUtil.exportToCSV(new ArrayList<Datasource>(dss.values()));
  }

}
