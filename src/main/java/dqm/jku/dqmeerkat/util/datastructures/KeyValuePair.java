package dqm.jku.dqmeerkat.util.datastructures;

public class KeyValuePair {
	private int key;
	private Object keyObject;
	private Object value;

	KeyValuePair(int i, Object kObject, Object o) {
		setKey(i);
		setKeyObject(kObject);
		setValue(o);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getKeyObject() {
		return keyObject;
	}

	public void setKeyObject(Object keyObject) {
		this.keyObject = keyObject;
	}
	
	
}
