package dqm.jku.dqmeerkat.quality;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class QualityDimension implements Iterable<QualityMetric>, Serializable {

	private static final long serialVersionUID = 1L;
	private final String label;
	private HashMap<String, QualityMetric> metrics = new HashMap<String, QualityMetric>();

	QualityDimension(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	double getValue(String metricLabel) {
		return metrics.get(metricLabel).getValue();
	}

	boolean hasValue(String metricLabel) {
		return metrics.containsKey(metricLabel);
	}

	void setMetric(String metricLabel, double value) {
		if (!metrics.containsKey(metricLabel)) {
			metrics.put(metricLabel, new QualityMetric(metricLabel));
		}
		metrics.get(metricLabel).setValue(value);
	}

	@Override
	public Iterator<QualityMetric> iterator() {
		return metrics.values().iterator();
	}

}
