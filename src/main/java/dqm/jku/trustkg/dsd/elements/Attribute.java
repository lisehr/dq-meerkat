package dqm.jku.trustkg.dsd.elements;

public class Attribute extends DSDElement {

	private static final long serialVersionUID = 1L;

	private final Concept concept;
	private boolean nullable;
	private boolean unique;
	private boolean autoIncrement;
	private int ordinalPosition;
	private Object defaultValue;
	private Class<?> dataType;

	public Attribute(String label, Concept concept) {
		super(label);
		this.concept = concept;
	}

	@Override
	public String getURI() {
		return concept.getURI() + "/" + label;
	}
	
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	public void setOrdinalPosition(int ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		if (dataType.isInstance(defaultValue)) {
			this.defaultValue = defaultValue;
		} else {
			throw new IllegalArgumentException("Type of defaultValue does not match with type of Attribute.");
		}
	}

	public Concept getConcept() {
		return concept;
	}

	public Class<?> getDataType() {
		return dataType;
	}

	public void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}

	public void duplicate(Attribute newAttribute) {
		newAttribute.dataType = dataType;
		newAttribute.nullable = nullable;
		newAttribute.defaultValue = defaultValue;
		newAttribute.unique = unique;
		newAttribute.ordinalPosition = ordinalPosition;
		newAttribute.autoIncrement = autoIncrement;
	}

}
