package quality;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import dsd.elements.DSDElement;

public class QualityRating implements Iterable<QualityDimension>, Serializable {

	private static final long serialVersionUID = 1L;
	private final DSDElement element;
	private HashMap<String, QualityDimension> dimensions = new HashMap<String, QualityDimension>();

	QualityRating(DSDElement element) {
		super();
		this.element = element;
	}

	void setDimension(String dimLabel, String metricLabel, double value) {
		if (!dimensions.containsKey(dimLabel)) {
			dimensions.put(dimLabel, new QualityDimension(dimLabel));
		}
		dimensions.get(dimLabel).setMetric(metricLabel, value);
	}

	double getValue(String dimLabel, String metricLabel) {
		return dimensions.get(dimLabel).getValue(metricLabel);
	}

	boolean hasValue(String dimLabel, String metricLabel) {
		return dimensions.containsKey(dimLabel) ? dimensions.get(dimLabel).hasValue(metricLabel) : false;
	}

	public DSDElement getDSDElement() {
		return element;
	}

	@Override
	public Iterator<QualityDimension> iterator() {
		return dimensions.values().iterator();
	}

	QualityDimension getDimension(String dimLabel) {
		return dimensions.get(dimLabel);
	}
}
