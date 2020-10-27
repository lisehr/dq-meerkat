package dqm.jku.trustkg.util.datastructures;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArrayListMap<K, V> implements Map<K, V> {
	private ArrayList<Entry<K, V>> map;
	private final Class<K> keyClass;
	private final Class<V> valClass;
	
	@SuppressWarnings("unchecked")
	public ArrayListMap() {
		this.keyClass = (Class<K>) Object.class;
		this.valClass = (Class<V>) Object.class;
	}

	public ArrayListMap(Class<K> keyClass, Class<V> valClass) {
		map = new ArrayList<Entry<K, V>>();
		this.keyClass = keyClass;
		this.valClass = valClass;
	}

	public ArrayListMap(int initCapacity, Class<K> keyClass, Class<V> valClass) {
		map = new ArrayList<Entry<K, V>>(initCapacity);
		this.keyClass = keyClass;
		this.valClass = valClass;
	}

	public V put(K k, V v)
	// If an entry in this map with key k already exists then the value
	// associated with that entry is replaced by value v and the original
	// value is returned; otherwise, adds the (k, v) pair to the map and
	// returns null.
	{
		if (k == null) throw new IllegalArgumentException("Maps do not allow null keys.");

		Entry<K, V> entry = new AbstractMap.SimpleEntry<K, V>(k, v);

		Entry<K, V> temp;
		Iterator<Entry<K, V>> search = map.iterator(); // Arraylist iterator
		while (search.hasNext()) {
			temp = search.next();
			if (temp.getKey().equals(k)) {
				search.remove();
				map.add(entry);
				return temp.getValue(); // k found, exits method
			}
		}

		// No entry is associated with k.
		map.add(entry);
		return null;
	}

	public boolean contains(K k)
	// Returns true if an entry in this map with key k exists;
	// Returns false otherwise.
	{
		if (k == null) throw new IllegalArgumentException("Maps do not allow null keys.");

		for (Entry<K, V> temp : map) if (temp.getKey().equals(k)) return true; // k found, exits method

		// No entry is associated with k.
		return false;
	}

	public boolean isEmpty()
	// Returns true if this map is empty; otherwise, returns false.
	{
		return (map.size() == 0); // uses ArrayList size
	}

	public boolean isFull()
	// Returns true if this map is full; otherwise, returns false.
	{
		return false; // An ArrayListMap is never full
	}

	public int size()
	// Returns the number of entries in this map.
	{
		return map.size(); // uses ArrayList size
	}

	public Iterator<Entry<K, V>> iterator()
	// Returns the Iterator provided by ArrayList.
	{
		return map.iterator(); // returns ArrayList iterator
	}

	@Override
	public boolean containsKey(Object key) {
		for (Entry<K, V> entry : map) if (key.getClass().isAssignableFrom(keyClass) && entry.getValue().equals(key)) return true;
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<K, V> entry : map) if (value.getClass().isAssignableFrom(valClass) && entry.getValue().equals(value)) return true;
		return false;
	}

	@Override
	public V get(Object key) {
		if (key == null) throw new IllegalArgumentException("Maps do not allow null keys.");

		for (Entry<K, V> temp : map) // uses ArrayList iterator
		  if (temp.getKey().equals(key)) return temp.getValue(); // k found, exits method

		// No entry is associated with k.
		return null;
	}

	@Override
	public V remove(Object key) {
		if (key == null) throw new IllegalArgumentException("Maps do not allow null keys.");

		Entry<K, V> temp;
		Iterator<Entry<K, V>> search = map.iterator(); // Arraylist iterator
		while (search.hasNext()) {
			temp = search.next();
			if (temp.getKey().equals(key)) {
				search.remove();
				return temp.getValue(); // k found, exits method
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
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		HashSet<K> set = new HashSet<>();
		for (Entry<K, V> entry : map) set.add(entry.getKey());
		return set;
	}

	@Override
	public Collection<V> values() {
		HashSet<V> set = new HashSet<>();
		for (Entry<K, V> entry : map) set.add(entry.getValue());
		return set;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.stream().collect(Collectors.toSet());
	}

	public Class<K> getKeyClass() {
		return keyClass;
	}

	public Class<V> getValClass() {
		return valClass;
	}
}