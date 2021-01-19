package dqm.jku.dqmeerkat.util;

import java.util.ArrayList;
import java.util.List;

public class AttributeValueSet {

	List<Object> values = new ArrayList<Object>();

	void addValue(Object o) {
		values.add(o);
	}

	@Override
	public int hashCode() {
		int sum = 0;
		for (Object o : values) {
			if (o != null) {
				sum += o.hashCode();
			} else {
				sum++;
			}
		}
		return sum;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeValueSet other = (AttributeValueSet) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		}
		if (values.size() != other.values.size())
			return false;
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) == null || other.values.get(i) == null) {
				return values.get(i) == null && other.values.get(i) == null;
			}

			if (!values.get(i).equals(other.values.get(i)))
				return false;
		}
		return true;
	}

}
