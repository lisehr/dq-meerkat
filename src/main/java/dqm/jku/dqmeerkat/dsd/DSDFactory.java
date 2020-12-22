package dqm.jku.dqmeerkat.dsd;

import dqm.jku.dqmeerkat.dsd.elements.AggregationAssociation;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.ConceptConstraint;
import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.elements.ForeignKey;
import dqm.jku.dqmeerkat.dsd.elements.FunctionalDependency;
import dqm.jku.dqmeerkat.dsd.elements.InheritanceAssociation;
import dqm.jku.dqmeerkat.dsd.elements.ReferenceAssociation;
import dqm.jku.dqmeerkat.dsd.integrated.IntegratedConcept;
import dqm.jku.dqmeerkat.dsd.integrated.IntegratedDatasource;
import dqm.jku.dqmeerkat.util.Miscellaneous.DBType;
import dqm.jku.dqmeerkat.util.validators.Validator;

public class DSDFactory {

	public static Datasource makeDatasource(String label, DBType dbtype) {
		return DSDElement.get(new Datasource(label, dbtype));
	}
	
	public static Datasource makeDatasource(String label, DBType dbtype, String uri, String prefix) {
		return DSDElement.get(new Datasource(label, dbtype, uri, prefix));
	}

	public static Concept makeConcept(String label, Datasource datasource) {
		Concept var = DSDElement.get(new Concept(label, datasource));
		datasource.addConcept(var);
		return var;
	}

	public static Attribute makeAttribute(String label, Concept concept) {
		Attribute var = DSDElement.get(new Attribute(label, concept));
		concept.addAttribute(var);
		return var;
	}

	public static Attribute makeBlindAttribute(String label, IntegratedConcept iconcept) {
		Attribute var = DSDElement.get(new Attribute(label, iconcept));
		return var;
	}

	public static Attribute makeAttribute(IntegratedConcept iconcept, Attribute blindAttribute) {
		Attribute var = DSDElement.get(blindAttribute);
		iconcept.addAttribute(var);
		return var;
	}

	public static FunctionalDependency makeFunctionalDependency(Iterable<Attribute> left, Attribute right, Concept concept) {
		for (Attribute a : left) {
			if (!concept.containsAttribute(a)) {
				return null;
			}
		}
		if (!concept.containsAttribute(right)) {
			return null;
		}
		FunctionalDependency var = DSDElement.get(new FunctionalDependency(left, right, concept));
		concept.addFunctionalDependency(var);
		return var;
	}

	public static InheritanceAssociation makeInheritanceAssociation(String label, Datasource datasource) {
		InheritanceAssociation var = DSDElement.get(new InheritanceAssociation(label, datasource));
		datasource.addAssociation(var);
		return var;
	}

	public static ReferenceAssociation makeReferenceAssociation(String label, Datasource datasource) {
		ReferenceAssociation var = DSDElement.get(new ReferenceAssociation(label, datasource));
		datasource.addAssociation(var);
		return var;
	}

	public static AggregationAssociation makeAggregationAssociation(String label, Datasource datasource) {
		AggregationAssociation var = DSDElement.get(new AggregationAssociation(label, datasource));
		datasource.addAssociation(var);
		return var;
	}

	public static ConceptConstraint makeConceptConstraint(String label, Datasource datasource, Concept concept, Validator validator) {
		ConceptConstraint var = DSDElement.get(new ConceptConstraint(label, datasource, concept, validator));
		datasource.addConstraint(var);
		return var;
	}

	public static ForeignKey makeForeignKey(String label, Concept referencing, Concept referenced) {
		ForeignKey var = DSDElement.get(new ForeignKey(label, referencing, referenced));
		referencing.getDatasource().addConstraint(var);
		referencing.addForeignKey(var);
		referenced.addForeignKey(var);
		return var;
	}

	public static IntegratedConcept makeIntegratedConcept(String label, IntegratedDatasource datasource) {
		IntegratedConcept var = DSDElement.get(new IntegratedConcept(label, datasource));
		datasource.addConcept(var);
		return var;
	}

	public static IntegratedDatasource makeIntegratedDatasource(String label) {
		return DSDElement.get(new IntegratedDatasource(label));
	}

}
