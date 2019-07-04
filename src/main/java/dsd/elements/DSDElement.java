package dsd.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class DSDElement implements Serializable, Comparable<DSDElement> {

	private static final long serialVersionUID = 1L;
	private static HashMap<String, DSDElement> cache = new HashMap<String, DSDElement>();

	public abstract String getURI();

	protected String label;
	protected String labelOriginal;

	public DSDElement(String label) {
		this.label = label.toLowerCase();
		this.labelOriginal = label;
	}

	public String getLabel() {
		return label;
	}
	
	/** The original label is currently only used for calculating the readability dimension **/
	public String getLabelOriginal() {
		return labelOriginal;
	}

	@Override
	public String toString() {
		return getURI();
	}

	@Override
	public int compareTo(DSDElement other) {
		return getURI().compareTo(other.getURI());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getURI() == null) ? 0 : getURI().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DSDElement other = (DSDElement) obj;
		String uri = getURI();
		String otherUri = other.getURI();
		if (uri == null) {
			if (otherUri != null)
				return false;
		} else if (!uri.equals(otherUri))
			return false;
		return true;
	}

	public static List<Datasource> getAllDatasources() {
		List<Datasource> list = new ArrayList<Datasource>();
		for (DSDElement e : cache.values()) {
			if (e instanceof Datasource) {
				list.add((Datasource) e);
			}
		}
		return list;
	}

	public static Optional<Datasource> getDatasource(String label) {
		return getAllDatasources().stream().filter(x -> x.label.equalsIgnoreCase(label)).findFirst();
	}

	public static List<Concept> getAllConcepts() {
		List<Concept> list = new ArrayList<Concept>();
		for (DSDElement e : cache.values()) {
			if (e instanceof Concept) {
				list.add((Concept) e);
			}
		}
		return list;
	}

	public static DSDElement get(String uri) {
		return cache.get(uri);
	}

	@SuppressWarnings("unchecked")
	public static <T extends DSDElement> T get(T elem) {
		String uri = elem.getURI();
		if (!cache.containsKey(uri)) {
			cache.put(uri, elem);
		}
		return (T) cache.get(uri);
	}

	public static Collection<DSDElement> getCache() {
		return cache.values();
	}

	public static void replace(DSDElement elem) {
		String uri = elem.getURI();
		if (cache.containsKey(uri)) {
			cache.remove(uri);
		}
		cache.put(uri, elem);
	}

}
