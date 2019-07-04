package dqm.jku.trustkg.quality;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import dqm.jku.trustkg.dsd.elements.DSDElement;

public class QualityAnnotation implements Iterable<Object>, Serializable {

	private static final long serialVersionUID = 1L;
	private final DSDElement element;
	private HashMap<String, Object> annotationObjects = new HashMap<String, Object>();

	QualityAnnotation(DSDElement element) {
		super();
		this.element = element;
	}

	void setAnnotation(String annLabel, Object value) {
		annotationObjects.put(annLabel, value);
	}

	Object getValue(String annLabel) {
		return annotationObjects.get(annLabel);
	}

	boolean hasValue(String annLabel) {
		return annotationObjects.containsKey(annLabel);
	}

	public DSDElement getDSDElement() {
		return element;
	}

	@Override
	public Iterator<Object> iterator() {
		return annotationObjects.values().iterator();
	}

	public Map<String, Object> getAllAnnotationObjects() {
		return Collections.unmodifiableMap(annotationObjects);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(element.getLabel());
		sb.append(": ");

		for (Entry<String, Object> entry : annotationObjects.entrySet()) {
			sb.append(entry.getKey() + " - " + entry.getValue().toString());
			sb.append(",");
		}
		return sb.toString();
	}

}
