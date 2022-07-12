package dqm.jku.dqmeerkat.dsd.elements;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import dqm.jku.dqmeerkat.influxdb.InfluxDBConnection;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFContainer;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;

import dqm.jku.dqmeerkat.blockchain.blocks.DSDBlock;
import dqm.jku.dqmeerkat.blockchain.standardchain.BlockChain;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.AbstractProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.Miscellaneous.DBType;

@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:Datasource")
public class Datasource extends DSDElement {

	private static final long serialVersionUID = 1L;

	private DBType dbtype;
	private String prefix;
	private Set<Concept> concepts = new TreeSet<Concept>();
	private Set<Association> associations = new TreeSet<Association>();
	private Set<Constraint> constraints = new TreeSet<Constraint>();

	public Datasource() {

	}

	public Datasource(String label) {
		super(label, Constants.DEFAULT_URI);
		this.dbtype = DBType.UNDEFINED;
		this.prefix = Constants.DEFAULT_PREFIX;
	}

	public Datasource(String label, DBType dbtype) {
		super(label, Constants.DEFAULT_URI);
		this.dbtype = dbtype;
		this.prefix = Constants.DEFAULT_PREFIX;
	}

	public Datasource(String label, DBType dbtype, String uri, String prefix) {
		super(label, uri);
		this.dbtype = dbtype;
		this.prefix = prefix;
	}

	@RDF("dsd:hasConcept")
	@RDFContainer
	public Set<Concept> getConcepts() {
		return Collections.unmodifiableSet(concepts);
	}

	public void setConcepts(Set<Concept> concepts) {
		this.concepts = concepts;
	}

	public Set<Concept> getConceptsAndAssociations() {
		Set<Concept> unionSet = new TreeSet<Concept>();
		unionSet.addAll(concepts);
		unionSet.addAll(associations);
		return unionSet;
	}

	public void addConcept(Concept concept) {
		concepts.add(concept);
	}

	public Set<Association> getAssociations() {
		return Collections.unmodifiableSet(associations);
	}

	public Set<Association> getAssociations(Concept c) {
		Set<Association> assoc = new HashSet<Association>();
		for (Association ac : associations) {
			if (ac.getConcepts().contains(c)) assoc.add(ac);
		}
		return assoc;
	}

	public <T extends Association> Set<T> getAssociations(Predicate<T> predicate, Class<T> clazz) {
		Set<T> assoc = new HashSet<T>();

		for (Association ac : associations) {
			if (clazz.isAssignableFrom(ac.getClass())) {
				@SuppressWarnings("unchecked")
				T ac1 = (T) ac;
				if (predicate.test(ac1)) {
					assoc.add(ac1);
				}
			}

		}
		return assoc;
	}

	public void addAssociation(Association association) {
		associations.add(association);
	}

	public Set<Constraint> getConstraints() {
		return Collections.unmodifiableSet(constraints);
	}

	public void addConstraint(Constraint constraint) {
		this.constraints.add(constraint);
	}

	public Set<ConceptConstraint> getConceptConstraints() {
		Set<ConceptConstraint> cc = new HashSet<ConceptConstraint>();

		for (Constraint con : constraints) {
			if (con instanceof ConceptConstraint) cc.add((ConceptConstraint) con);
		}

		return cc;
	}

	public Concept getConcept(String name) {
		for (Concept c : concepts) {
			if (c.getLabel().equalsIgnoreCase(name)) return c;
		}
		return null;
	}

	public Association getAssociation(String name) {
		for (Association ac : associations) {
			if (ac.getLabel().equalsIgnoreCase(name)) return ac;
		}
		return null;
	}

	public int getNumberOfConceptsAndAssociations() {
		return concepts.size() + associations.size();
	}

	public void fillBlockChain(BlockChain bc) {
		if (bc == null) throw new IllegalArgumentException("Blockchain has to exist!");
		bc.addBlock(new DSDBlock(bc.getId(), bc.getPreviousHash(), this));
		for (Concept c : concepts) {
			c.fillBlockChain(bc);
		}
		for (Association a : associations) {
			bc.addBlock(new DSDBlock(bc.getId(), bc.getPreviousHash(), a));
		}
		for (Constraint c : constraints) {
			bc.addBlock(new DSDBlock(bc.getId(), bc.getPreviousHash(), c));
		}

	}

	public void addProfileToInflux(InfluxDBConnection connection) {
		super.storeProfile(connection);
		for (Concept c : concepts) {
			c.addProfileToInflux(connection);
		}
		for (Association a : associations) {
			a.addProfileToInflux(connection);
		}
		for (Constraint c : constraints) {
			c.addProfileToInflux(connection);
		}
	}

	// transforms the datasource to a rdf model
	public Model getGraphModel(ModelBuilder builder) {

		System.out.println(this.getURI());
		this.prefix = prefix.replace(":", "");
		builder.setNamespace(prefix, this.getURI() +"/");
		builder.setNamespace("dsd", "http://dqm.faw.jku.at/dsd" +"/");
		builder.setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns");
		builder.setNamespace("attribute", "http://dqm.faw.jku.at/dsd/Attribute/");


		for (Concept c : this.getConcepts()) {
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix +":"+ c.getLabel())
			.add("rdf:#type", "dsd:DataSource");
			addForeignKeysToModel(c,builder);
			addPrimaryKeysToModel(c,builder);
			addFunctionalDependenciesToModel(c,builder);
			for (Attribute a : c.getAttributes()) {
				builder.namedGraph(prefix +":"+ c.getLabel())
				.subject(prefix +":"+ c.getLabel())
				.add("dsd:hasAttribute", prefix  + ":"+ a.getLabel());
				addAttributeProperties(a,builder,c);
				addDataProfile(builder,c,a);
			}

		}
		return builder.build();
	}



	private void addDataProfile(ModelBuilder builder, Concept concept, Attribute a) {
		DataProfile profile = a.getProfile();
		if(profile == null) return;
		builder.setNamespace("metric", "http://dqm.faw.jku.at/dsd/quality/structures/" );
		builder.namedGraph(prefix +":"+ concept.getLabel())
		.subject(prefix+ ":"+ a.getLabel())
		.add("dsd:hasDataprofile" ,prefix + ":" + a.getLabel() + "/" + "DataProfile");
		for(AbstractProfileStatistic metric : profile.getStatistics()) {

			if(metric.getValue() != null) {
				builder.namedGraph(prefix +":"+ concept.getLabel())
				.subject(prefix + ":" + a.getLabel() + "/" + "DataProfile")
				.add("rdf:#type", "dsd:Dataprofile")
				.add("metric:hasMetric",prefix + ":" + a.getLabel() + "/" + metric.getTitle().getLabel().replace(" ","").replace("%","Percent"));

				builder.namedGraph(prefix +":"+ concept.getLabel())
				.subject(prefix + ":" + a.getLabel() + "/"+ metric.getTitle().getLabel().replace(" ","").replace("%","Percent"))
				.add("metric:hasValue",metric.getValue())
				.add("rdf:#type", "dsd:DataProfileMetric");
			}
		}
	}

	private void addSingleAttributeProperty(Attribute a, ModelBuilder builder, Concept c, String property) {
		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix + ":" + a.getLabel())
		.add("attribute:hasAttributeProperty", prefix + ":" + a.getLabel() + "/" + property)
		.add("rdf:#type","dsd:Attribute");

		switch(property) {
		case "DataType":
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix + ":" + a.getLabel() + "/" + property)
			.add("attribute" +":" +"hasValue",a.getDataTypeString())
			.add("rdf" +":" +"#type","dsd" +":" +"AttributeProperty");
			break;

		case "Nullable":
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix + ":" + a.getLabel() + "/" + property)
			.add("attribute" +":" +"hasValue",a.isNullable())
			.add("rdf" +":" +"#type","dsd" +":" +"AttributeProperty");
			break;

		case "AutoIncrement":
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix + ":" + a.getLabel() + "/" + property)
			.add("attribute" +":" +"hasValue",a.isAutoIncrement())
			.add("rdf" +":" +"#type","dsd" +":" +"AttributeProperty");
			break;

		case "Unique":
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix + ":" + a.getLabel() + "/" + property)
			.add("attribute" +":" +"hasValue",a.isUnique())
			.add("rdf" +":" +"#type","dsd" +":" +"AttributeProperty");
			break;

		case "DefaultValue":
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix + ":" + a.getLabel() + "/" + property)
			.add("attribute" +":" +"hasValue",a.getDefaultValue())
			.add("rdf" +":" +"#type","dsd" +":" +"AttributeProperty");
			break;
		}
	}



private void addAttributeProperties(Attribute a, ModelBuilder builder, Concept c) {

	addSingleAttributeProperty(a,builder,c, "DataType");
	addSingleAttributeProperty(a,builder,c, "Nullable");
	addSingleAttributeProperty(a,builder,c, "AutoIncrement");
	addSingleAttributeProperty(a,builder,c, "Unique");

	//		.add("rdf" +":" +"type","dsd" +":" +"Attribute");

	if(a.getDefaultValue() != null) {
		addSingleAttributeProperty(a,builder,c, "DefaultValue");
	}
}

// this method add all functional dependencies to the builder
private void addFunctionalDependenciesToModel(Concept c, ModelBuilder builder) {

	if(c.getFunctionalDependencies().size() != 0) {
		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":"+ c.getLabel())
		.add("dsd:hasFunctionalDependencies", prefix  + ":FunctionalDependencies");
	}

	int index = 0;
	for(FunctionalDependency dependency : c.getFunctionalDependencies()) {

		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":FunctionalDependencies")
		.add("dsd:isFunctionalDependency", prefix + ":FunctionalDependency" + index);

		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":FunctionalDependency" + index)
		.add("rdf:#type", "dsd:FunctionalDependency")
		.add("dsd:" +"RightSide",prefix + dependency.getRightSide().getLabel());
		for(Attribute at : dependency.getLeftSide().getAttributes()) {
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix +":FunctionalDependency" + index)
			.add(prefix +":" +"LeftSide",at.getLabel());
		}
	}
}

// this method adds primary keys to the modelbuilder
private void addPrimaryKeysToModel(Concept c, ModelBuilder builder) {

	if(c.getPrimaryKeys().getSize() != 0) {
		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":"+ c.getLabel())
		.add("dsd:hasPrimaryKey", prefix  + ":PrimaryKey");
	}

	for(Attribute at : c.getPrimaryKeySet()) {
		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":PrimaryKey")
		.add("rdf:#type", "dsd:PrimaryKey")
		.add(prefix +":" +"PrimaryKeyAttribute",prefix + at.getLabel());
	}
}

// this method adds foreign keys to the modelbuilder
private void addForeignKeysToModel(Concept c, ModelBuilder builder) {

	if(!c.getForeignKeys().isEmpty()) {
		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":"+ c.getLabel())
		.add("dsd:hasForeignKeys", prefix  + ":ForeignKeys");
	}
	int index = 0;
	for(ForeignKey key: c.foreignKeys) {
		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":ForeignKeys")
		.add("dsd:isForeignKey", prefix + ":foreignKey" + index);

		builder.namedGraph(prefix +":"+ c.getLabel())
		.subject(prefix +":ForeignKey" + index)
		.add("rdf:#type", "dsd:ForeignKey")
		.add(prefix +":" +"ReferencedConcept",key.getReferencedConcept().getLabel())
		.add(prefix +":" +"DeleteRule",key.getDeleteRule().toString())
		.add(prefix +":" +"UpdateRule",key.getUpdateRule().toString());

		for(Attribute at : key.getReferencingAttributes()) {
			builder.namedGraph(prefix +":"+ c.getLabel())
			.subject(prefix +":ForeignKey" + index)
			.add(prefix +":" +"ReferencingAttribute",prefix +at.getLabel());
		}
		for(Attribute at : key.getReferencedAttributes()) {
			builder.namedGraph(prefix +":" + c.getLabel())
			.subject(prefix + ":ForeignKey" + index)
			.add(prefix +":" +"ReferencedAttribute",prefix + at.getLabel());
		}
	}
}

public DBType getDBType() {
	return dbtype;
}

public void printStructure() {
	for (Concept c : getConcepts()) {
		System.out.println(c.getURI());
		for (Attribute a : c.getAttributes()) {
			System.out.print("\t");
			System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
		}
		System.out.println();
	}
}

public String getStructureString() {
	StringBuilder sb = new StringBuilder();
	for (Concept c : getConcepts()) {
		sb.append(c.getURI());
		sb.append('\n');
		for (Attribute a : c.getAttributes()) {
			sb.append("\t");
			sb.append(a.getDataType().getSimpleName() + "\t" + a.getURI());
			sb.append('\n');
		}
	}
	return sb.toString();
}

	public ReferenceAssociation getReferenceAssociations(String name) {
		for (Association ac : associations) {
			if (ac.getLabel().contains(name)) {
				return (ReferenceAssociation) ac;
			}
		}
		return null;
	}
public String getPrefix() {
	return prefix;
}

public void setPrefix(String prefix) {
	this.prefix = prefix;
}

}
