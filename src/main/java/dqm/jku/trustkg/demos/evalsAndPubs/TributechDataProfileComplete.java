package dqm.jku.trustkg.demos.evalsAndPubs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.histogram.Histogram;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.export.ExportUtil;
import javafx.util.Pair;

/**
 * This demo learns RDPs for the Tributech data.
 * 
 * 
 * @author lisa
 *
 */
public class TributechDataProfileComplete {
	
	private static final int RDP_SIZE = 2000;
//	private static final String[] FILENAMES = { "Acceleration breaking or forward", "Acceleration side to side",
//			"Acceleration up or down", "Device Voltage", "Engine RPM", "Engine Speed", "Engine Temperature" };
	private static final String[] FILENAMES = { "Acceleration breaking or forward"};

	private static HashMap<Datasource, ConnectorCSV> dss = new HashMap<Datasource, ConnectorCSV>();

	public static void main(String[] args) throws IOException, NoSuchMethodException {
		int[] indices = {0};
		loadDataSets(indices);

		for (Datasource ds : dss.keySet()) {
			for (Concept c : ds.getConceptsAndAssociations()) {
				ConnectorCSV conn = dss.get(ds);
				RecordList rs = conn.getPartialRecordList(c, 0, RDP_SIZE);
				for(Attribute a : c.getAttributes()) {
					a.annotateProfile(rs);
					System.out.println("=== " + ds.getLabel() + " " + a.getLabel() + "===");
					a.printAnnotatedProfile();
				}				
			}
		}
		
//		exportDataProfileToCSV(dss.keySet(), RESOURCE_PATH + "export/TributechDP.csv");
		List<Datasource> dssList = new ArrayList<Datasource>();
		dssList.addAll(dss.keySet());
		ExportUtil.exportToCSV(dssList);
	}

	private static void loadDataSets(int[] ind) throws IOException {
		for (int i : ind) {
			String fname = FILENAMES[i];
			String fpath = Constants.RESOURCES + "csv/Telematic Device Report - " + fname + ".csv";
			ConnectorCSV conn = new ConnectorCSV(fpath, ",", "\n", fname, true);
			Datasource ds = conn.loadSchema();
			dss.put(ds, conn);
		}
	}

	public static void exportDataProfileToCSV(Set<Datasource> dss, String filepath) {
		HashMap<String, LinkedList<Object>> metricValues = new HashMap<String, LinkedList<Object>>();
		LinkedList<Pair<String, String>> metricLabels = getMetricLabelsForCSVExport();
		LinkedList<String> elementLabels = new LinkedList<String>();

		for(Datasource ds : dss) {
			for(Concept c : ds.getConceptsAndAssociations()) {
				// currently only data profiles on attribute-level
				Attribute a = c.getAttribute("value");
				elementLabels.add(c.getLabel());


				// Currently: only the value
				//				for(Attribute a : c.getAttributes()) {

				if(a.getProfile() != null) {
					DataProfile dp = a.getProfile();
					List<ProfileMetric> metrics = dp.getMetrics();
					for(ProfileMetric m : metrics) {
						String key = m.getLabel();
						if(!key.contains("Histogram")) {
							LinkedList<Object> list = new LinkedList<Object>();
							if(metricValues.containsKey(key)) list = metricValues.get(key);
							list.add(m.getValue());
							metricValues.put(key, list);
						} else { // implement special case for histogram: split into 3 lines
							Histogram hist = (Histogram) m;
							String s1 = "Histogram-classes";
							String s2 = "Histogram-range";
							String s3 = "Histogram-values";
							LinkedList<Object> list1 = new LinkedList<Object>();
							LinkedList<Object> list2 = new LinkedList<Object>();
							LinkedList<Object> list3 = new LinkedList<Object>();

							if(metricValues.containsKey(s1)) {
								list1 = metricValues.get(s1);
								list2 = metricValues.get(s2);
								list3 = metricValues.get(s3);
							}
							list1.add(hist.getNumberOfClasses());
							list2.add(hist.getClassrange());
							list3.add(hist.getClassValues());
							metricValues.put(s1, list1);
							metricValues.put(s2, list2);
							metricValues.put(s3, list3);						
						}
					}
				}
				//				}
			}
		}

		// Create header line
		StringBuilder sb = new StringBuilder();
		sb.append("Metric");
		for(String el : elementLabels) {
			sb.append(";");
			sb.append(el);
		}
		sb.append("\n");

		// Create Cardinalities category
		//		sb.append("Cardinalities;Num rows (size);");
		for(Pair<String, String> ml : metricLabels) {
			String label = ml.getKey();
			LinkedList<Object> list = metricValues.get(label);
			sb.append(ml.getValue());

			for(Object o : list) {
				sb.append(";");
				if(o != null) {
					if(o instanceof Number) {
						sb.append(formatFloat(o));
					} else {
						sb.append(o.toString());
					}
				}
			}
			sb.append("\n");
		}
		
		Path path = Paths.get(Constants.RESOURCES + "export");
		if (Files.notExists(path)) {
		  try {
        Files.createDirectory(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
		}
		
		try (PrintWriter writer = new PrintWriter(new File(filepath))) {
			writer.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static LinkedList<Pair<String, String>> getMetricLabelsForCSVExport() {
		LinkedList<Pair<String, String>> metricLabels = new LinkedList<Pair<String, String>>();
		metricLabels.add(new Pair<>("Size", "Num rows (size)"));
		metricLabels.add(new Pair<>("Null Values", "No. nulls"));
		metricLabels.add(new Pair<>("Null Values", "% nulls"));
		metricLabels.add(new Pair<>("Cardinality", "No. distinct"));
		metricLabels.add(new Pair<>("Uniqueness", "Uniqueness"));
		metricLabels.add(new Pair<>("Minimum", "Minimum"));
		metricLabels.add(new Pair<>("Maximum", "Maximum"));
		metricLabels.add(new Pair<>("Average", "Average"));
		metricLabels.add(new Pair<>("Median", "Median"));
		metricLabels.add(new Pair<>("Digits", "# Digits"));
		metricLabels.add(new Pair<>("Decimals", "# Decimals"));
		metricLabels.add(new Pair<>("Histogram-classes", "# Classes"));
		metricLabels.add(new Pair<>("Histogram-range", "Class range"));
		metricLabels.add(new Pair<>("Histogram-values", "Values"));
		metricLabels.add(new Pair<>("isCandidateKey", "is key candidate"));
		return metricLabels;
	}

	private static String formatFloat(Object num) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.0000");
		return decimalFormat.format(num);
	}
}
