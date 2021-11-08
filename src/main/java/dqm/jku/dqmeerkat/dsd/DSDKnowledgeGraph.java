package dqm.jku.dqmeerkat.dsd;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

import dqm.jku.dqmeerkat.connectors.ConnectorCSV;
import dqm.jku.dqmeerkat.connectors.DSConnector;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.graphdb.EmbeddedGraphDB;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnection;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.Miscellaneous.DBType;
import dqm.jku.dqmeerkat.util.export.ExportUtil;

/**
 * @author Lisa
 * @author optimusseptim
 * 
 *         Wrapper class for the entire KG to manage multiple data sources and
 *         their connections. Allows to automatically annotate data profiles for
 *         multiple data sources, persist, or export the KG.
 *
 */
public class DSDKnowledgeGraph {
	private String label;
	private EmbeddedGraphDB kgstore = null;
	private HashMap<String, Datasource> dss = new HashMap<String, Datasource>();
	private HashMap<String, DSConnector> conns = new HashMap<String, DSConnector>();

	public DSDKnowledgeGraph(String label) {
		this.label = label;
		kgstore = new EmbeddedGraphDB(label);
	}
	
	public DSDKnowledgeGraph(String label, boolean graphdb) {
		this.label = label;
		if (graphdb) kgstore = new EmbeddedGraphDB(label);
	}

	
	public void addDatasource(Datasource ds) throws RepositoryConfigException, RDFHandlerException, RDFParseException, RepositoryException, IOException {
		dss.put(ds.getLabel(), ds);
		if (kgstore != null) {
			kgstore.createRepositoryIfNotExists(ds.getLabel());
			RepositoryConnection repConn = kgstore.getRepository(ds.getLabel()).getConnection();
			repConn.add(ds.getGraphModel(new ModelBuilder()));
		}
	}

	public void addDatasourceAndConnector(Datasource ds, DSConnector conn) {
		dss.put(ds.getLabel(), ds);
		conns.put(ds.getLabel(), conn);
	}

	public String getLabel() {
		return label;
	}

	public HashMap<String, Datasource> getDatasources(){
		return dss;
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
	 * Method to create a datasource via the passed connections with standard uri and prefix
	 * @param connls a list of Datasource connectors
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
				DBType dbtype = ds.getDBType();
				if(dbtype.equals(DBType.CSV) || dbtype.equals(DBType.MYSQL)) {
					DSConnector conn = conns.get(ds.getLabel());
					RecordList rs = conn.getPartialRecordList(c, 0, noRecords);
					for (Attribute a : c.getAttributes()) {
						a.annotateProfile(rs);
					}
				} else {
					System.err.println("Data Profile Generation for DBType " + dbtype + " not implemented.");
				}
			}
			dss.put(ds.getLabel(), ds);
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
				DBType dbtype = ds.getDBType();
				if(dbtype.equals(DBType.CSV)) {
					ConnectorCSV conn = (ConnectorCSV) conns.get(ds.getLabel());
					RecordList rs = conn.getRecordList(c);
					for (Attribute a : c.getAttributes()) {
						a.annotateProfile(rs);
					}
				} else {
					System.err.println("Data Profile Generation for DBType " + dbtype + " not implemented.");
				}
			}
			dss.put(ds.getLabel(), ds);
		}
	}

	/**
	 * Method to export knowledge graph file in Turtle syntax to export folder.
	 * TODO: refine to make comprehensive .ttl export.
	 * 
	 * @param fileName
	 */
	public void exportKGToFile(String fileName) {
		Path path = Paths.get(Constants.RESOURCES_FOLDER + "export/ttl/");
		if (Files.notExists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (PrintWriter out = new PrintWriter(new File(Constants.RESOURCES_FOLDER + "export/ttl/" + fileName + ".ttl"))){
			
			ModelBuilder builder = new ModelBuilder();
			builder.setNamespace("dsd", "http://dqm.faw.jku.at/dsd" +"/");
			for (Datasource ds : dss.values()) {
				ds.getGraphModel(builder);
			
				for (Concept c : ds.getConcepts()) {
					builder.subject("dsd:DataSources")
					.add("dsd:hasDataSource", ds.getPrefix() +":" + c.getLabel());
				}
			}
			Rio.write(builder.build(), out, RDFFormat.TURTLE);
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

	/**
	 * Method for printing out the Structure of the KG in Tree-format
	 */
	public void printKGStructure() {
		System.out.print("Structure of KG: ");
		System.out.println(label);
		for (Datasource ds : dss.values()) {
			ds.printStructure();
		}
	}

	/**
	 * Method for printing out the Structure of the KG
	 */
	public void printDataprofiles() {
		System.out.print("Dataprofiles of KG: ");
		System.out.println(label);
		for (Datasource ds : dss.values()) {
			System.out.print("Dataprofiles of Datasource: ");
			System.out.println(ds.getLabel());
			for (Concept c : ds.getConcepts()) {
				System.out.print("Dataprofiles of Concept: ");
				System.out.println(ds.getLabel());
				for (Attribute a : c.getAttributes()) a.printAnnotatedProfile();   
			}
		}    
	}

	/**
	 * Method for getting a complete print of both structure and data profiles
	 */
	public void printAll() {
		printKGStructure();
		printDataprofiles();
	}

	/**
	 * Method for exporting the overview of the KG to a textfile
	 */
	public void exportReport() {
		ExportUtil.exportReport(this);
	}

	/**
	 * Method for adding all data profiles for all data sources to influx
	 */
	public void addProfilesToInflux(InfluxDBConnection influx) {
		for (Datasource ds : dss.values()) {
			ds.addProfileToInflux(influx);
		}
	}

}
