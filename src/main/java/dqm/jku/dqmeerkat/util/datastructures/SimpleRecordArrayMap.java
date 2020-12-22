package dqm.jku.dqmeerkat.util.datastructures;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dqm.jku.dqmeerkat.dsd.elements.Concept;

public class SimpleRecordArrayMap<K, V> implements Map<K, V> {
	private Concept structureConcept;
	private Object[] values;
	private int size;
	private final Class<V> valClass;
	
	@SuppressWarnings("unchecked")
	public SimpleRecordArrayMap() {
		this.valClass = (Class<V>) Object.class;
	}

	public SimpleRecordArrayMap(Class<V> valClass, int size, Concept structure) {
		this.setSize(size);
		this.structureConcept = structure;
		setValues(new Object[size]);
		this.valClass = valClass;
	}

	public SimpleRecordArrayMap(int initCapacity, Class<V> valClass, int size, Concept structure) {
		this.setSize(size);
		this.structureConcept = structure;
		setValues(new Object[size]);
		this.valClass = valClass;
	}

	@SuppressWarnings("unchecked")
	public V put(K k, V v)
	// If an entry in this map with key k already exists then the value
	// associated with that entry is replaced by value v and the original
	// value is returned; otherwise, adds the (k, v) pair to the map and
	// returns null.
	{
		if (k == null) throw new IllegalArgumentException("Maps do not allow null keys.");
		for (int i = 0; i < size; i++) {
			V retVal = (V) values[i];
			if (k.equals(structureConcept.getRecordStructure()[i])) { 
				values[i] = v;
				return retVal;
			}
		}
		return null;
	}

	public boolean contains(K k)
	// Returns true if an entry in this map with key k exists;
	// Returns false otherwise.
	{
		if (k == null) throw new IllegalArgumentException("Maps do not allow null keys.");
		for (int i = 0; i < size; i++) {
			if (k.equals(structureConcept.getRecordStructure()[i])) return true;
		}
		// No entry is associated with k.
		return false;
	}

	public boolean isEmpty()
	// Returns true if this map is empty; otherwise, returns false.
	{
		return (size == 0); // uses ArrayList size
	}

	public boolean isFull()
	// Returns true if this map is full; otherwise, returns false.
	{
		return false; // An ArrayListMap is never full
	}

	public int size()
	// Returns the number of entries in this map.
	{
		return size; 
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator<Entry<K, V>> iterator()
	// Returns the Iterator provided by ArrayList.
	{
		ArrayList<Entry<K, V>> list = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			list.add(new AbstractMap.SimpleEntry((K) structureConcept.getRecordStructure()[i], (V) values[i]));
		}
		return list.iterator();
	}

	@Override
	public boolean containsKey(Object key) {
		for (int i = 0; i < size; i++) {
			if (key.getClass().isAssignableFrom(structureConcept.getRecordStructure()[0].getClass()) && structureConcept.getRecordStructure()[i].equals(key)) return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for (int i = 0; i < size; i++) {
			if (value.getClass().isAssignableFrom(valClass) && values[i].equals(value)) return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if (key == null) throw new IllegalArgumentException("Maps do not allow null keys.");

		for (int i = 0; i < size; i++) {
			if (structureConcept.getRecordStructure()[i].equals(key)) return (V) values[i];
		}

		// No entry is associated with k.
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		if (key == null) throw new IllegalArgumentException("Maps do not allow null keys.");

		for (int i = 0; i < size; i++) {
			if (structureConcept.getRecordStructure()[i].equals(key)) {
				V val = (V) values[i];
				for (int j = i + 1; j < size; j++) {
					structureConcept.getRecordStructure()[j] = structureConcept.getRecordStructure()[j-1];
					values[j] = values[j-1]; 
				}
				return val;
			}
		}
		// No entry is associated with k.
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> e : m.entrySet()) put(e.getKey(), e.getValue());
	}

	@Override
	public void clear() {
		this.structureConcept = null;
		setValues(new Object[size]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keySet() {
		HashSet<K> set = new HashSet<>();
		for (int i = 0; i < size; i++) {
			set.add((K) structureConcept.getRecordStructure()[i]);			
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		HashSet<V> set = new HashSet<>();
		for (int i = 0; i < size; i++) {
			set.add((V) values[i]);			
		}
		return set;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Set<Entry<K, V>> entrySet() {
		ArrayList<Entry<K, V>> list = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			list.add(new AbstractMap.SimpleEntry((K) structureConcept.getRecordStructure()[i], (V) values[i]));
		}
		return list.stream().collect(Collectors.toSet());
	}

	public Class<V> getValClass() {
		return valClass;
	}

	public Object[] getKeys() {
		return structureConcept.getRecordStructure();
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Concept getStructureConcept() {
		return structureConcept;
	}

	public void setStructureConcept(Concept structureConcept) {
		this.structureConcept = structureConcept;
	}
}