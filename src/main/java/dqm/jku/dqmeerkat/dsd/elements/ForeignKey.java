package dqm.jku.dqmeerkat.dsd.elements;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV1;

public class ForeignKey extends Constraint {

	private static final long serialVersionUID = 1L;
	private final Concept referencingConcept;
	private final Concept referencedConcept;
	private ForeignKeyRule deleteRule;
	private ForeignKeyRule updateRule;

	private final List<Attribute> referencing = new ArrayList<Attribute>();
	private final List<Attribute> referenced = new ArrayList<Attribute>();

	public ForeignKey(String label, Concept fromC, Concept toC) {
		super(label, fromC.getDatasource());
		this.referencingConcept = fromC;
		this.referencedConcept = toC;
	}

	public Concept getReferencingConcept() {
		return referencingConcept;
	}

	public Concept getReferencedConcept() {
		return referencedConcept;
	}

	public List<Attribute> getReferencingAttributes() {
		return referencing;
	}

	public List<Attribute> getReferencedAttributes() {
		return referenced;
	}

	public void addAttributePair(Attribute referencingAtt, Attribute referencedAtt) {
		if (referencedAtt == null || referencingAtt == null)
			throw new IllegalArgumentException("Attributes must not be null.");
		if (referencedAtt.getConcept() != referencedConcept || referencingAtt.getConcept() != referencingConcept)
			throw new IllegalArgumentException("Attributes must match Concepts.");
		if (!referencingAtt.getDataType().equals(referencedAtt.getDataType()))
			throw new IllegalArgumentException("Datatypes of Attributes do not match.");
		referencing.add(referencingAtt);
		referenced.add(referencedAtt);
	}

	public boolean validate(Record fromInstance, Record toInstance) {
		for (int i = 0; i < referencing.size(); i++) {
			Object o = fromInstance.getField(referencing.get(i));
			Object o2 = toInstance.getField(referenced.get(i));
			if ((o == null) != (o2 == null)) {
				return false;
			}
			if (!(o == null || o.equals(o2))) {
				return false;
			}
		}
		return true;
	}

	public ForeignKeyRule getDeleteRule() {
		return deleteRule;
	}

	public void setDeleteRule(ForeignKeyRule deleteRule) {
		this.deleteRule = deleteRule;
	}

	public ForeignKeyRule getUpdateRule() {
		return updateRule;
	}

	public void setUpdateRule(ForeignKeyRule updateRule) {
		this.updateRule = updateRule;
	}

  @Override
  public void addProfileToInflux(InfluxDBConnectionV1 connection) {
    super.storeProfile(connection);
  }

}
