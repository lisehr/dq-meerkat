package util.sparse;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SparseLabeledVector<L> implements Iterable<L> {

	private Map<L, Double> values = new ConcurrentHashMap<L, Double>();

	public double get(L lable) {
		Double d = values.get(lable);
		return d == null ? 0 : d;
	}

	public void inc(L lable, double value) {
		set(lable, get(lable) + value);
	}

	public void inc(L lable) {
		inc(lable, 1.0);
	}

	public void set(L lable, double value) {
		values.put(lable, value);
	}

	public void multiply(double value) {
		for (L lable : values.keySet()) {
			set(lable, get(lable) * value);
		}
	}

	public double sum() {
		double sum = 0;
		for (L lable : values.keySet()) {
			sum += get(lable);
		}
		return sum;
	}

	public double length() {
		double sum = 0;
		for (L lable : values.keySet()) {
			double d = get(lable);
			sum += d * d;
		}
		return Math.sqrt(sum);
	}

	public double getMax() {
		double max = 0;
		for (L lable : values.keySet()) {
			max = Math.max(max, values.get(lable));
		}
		return max;
	}

	public L argMax() {
		L arg = null;
		double max = 0;
		for (L lable : values.keySet()) {
			if (values.get(lable) > max) {
				max = values.get(lable);
				arg = lable;
			}
		}
		return arg;
	}

	public double getMin() {
		double min = Double.MAX_VALUE;
		for (L label : values.keySet()) {
			min = Math.min(min, values.get(label));
		}
		return min;
	}

	public L argMin() {
		L arg = null;
		double max = 0;
		for (L lable : values.keySet()) {
			if (values.get(lable) < max) {
				max = values.get(lable);
				arg = lable;
			}
		}
		return arg;
	}

	public double dot(SparseLabeledVector<L> other) {
		double sum = 0;
		for (L lable : values.keySet()) {
			if (other.values.containsKey(lable)) {
				sum += get(lable) * other.get(lable);
			}
		}
		return sum;
	}

	public double cosSimilarity(SparseLabeledVector<L> other) {
		return dot(other) / (length() * other.length());
	}

	/**
	 * The vector gets to have a length of 1 (Eucledian length)
	 */
	public void normalize() {
		multiply(1 / length());
	}

	@Override
	public Iterator<L> iterator() {
		return values.keySet().iterator();
	}

	public int getNrDims() {
		return values.size();
	}

	public Set<L> keySet() {
		return values.keySet();
	}

	public boolean remove(L lable) {
		return values.remove(lable) != null;
	}

}
