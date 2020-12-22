package dqm.jku.dqmeerkat.quality;

import java.io.Serializable;

public class QualityMetric implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final String label;
	private double value;
	
	QualityMetric(String label) {
		super();
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public double getValue() {
		return value;
	}

	void setValue(double value) {
		this.value = value;
	}
}
