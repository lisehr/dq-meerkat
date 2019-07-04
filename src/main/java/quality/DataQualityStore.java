package quality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dsd.elements.DSDElement;

public class DataQualityStore implements Serializable {

	// TODO persistieren

	private static final long serialVersionUID = 1L;
	private static HashMap<DSDElement, QualityRating> ratings = new HashMap<DSDElement, QualityRating>();
	private static HashMap<DSDElement, QualityAnnotation> annotations = new HashMap<DSDElement, QualityAnnotation>();
	private static DataQualityRecords records = new DataQualityRecords();

	public static void setDQValue(DSDElement element, String dimLabel, String metricLabel, double value) {
		if (!ratings.containsKey(element)) {
			ratings.put(element, new QualityRating(element));
		}
		ratings.get(element).setDimension(dimLabel, metricLabel, value);
	}

	public static void setAnnotation(DSDElement element, String annLabel, Object value) {
		if (!annotations.containsKey(element)) {
			annotations.put(element, new QualityAnnotation(element));
		}
		annotations.get(element).setAnnotation(annLabel, value);
	}

	public static Object getAnnotation(DSDElement element, String annLabel) {
		if (annotations.containsKey(element)) {
			return annotations.get(element).getValue(annLabel);
		}
		return null;
	}

	public static Map<DSDElement, QualityAnnotation> getAnnotations() {
		return Collections.unmodifiableMap(annotations);
	}

	public static boolean hasAnnotation(DSDElement element, String annLabel) {
		return annotations.containsKey(element) ? annotations.get(element).hasValue(annLabel) : false;
	}

	public static Map<DSDElement, QualityRating> getRatings() {
		return Collections.unmodifiableMap(ratings);
	}

	public static DataQualityRecords getDQRecords() {
		return records;
	}

	public static List<String> getDimensions(DSDElement element) {
		List<String> dimLabels = new ArrayList<String>();
		QualityRating rating = ratings.get(element);
		if (rating == null) {
			return dimLabels;
		}
		for (QualityDimension i : rating) {
			dimLabels.add(i.getLabel());
		}
		return dimLabels;
	}

	public static List<String> getMetrics(DSDElement element, String dimLabel) {
		List<String> metricLabels = new ArrayList<String>();
		QualityRating rating = ratings.get(element);
		if (rating == null) {
			return metricLabels;
		}
		QualityDimension dim = rating.getDimension(dimLabel);
		if (dim == null) {
			return metricLabels;
		}
		for (QualityMetric i : dim) {
			metricLabels.add(i.getLabel());
		}
		return metricLabels;
	}

	public static Double getDQValue(DSDElement element, String dimLabel, String metricLabel) {
		if (ratings.containsKey(element)) {
			return ratings.get(element).getValue(dimLabel, metricLabel);
		}
		return null;
	}

	public static boolean hasDQValue(DSDElement element, String dimLabel, String metricLabel) {
		return ratings.containsKey(element) ? ratings.get(element).hasValue(dimLabel, metricLabel) : false;
	}

	public static QualityAnnotation getAnnotations(DSDElement element) {
		return annotations.get(element);
	}

}
