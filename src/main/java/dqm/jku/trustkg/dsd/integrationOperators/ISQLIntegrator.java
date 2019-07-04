package dqm.jku.trustkg.dsd.integrationOperators;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dqm.jku.trustkg.connectors.DSInstanceConnector;
import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.integrated.IntegratedConcept;
import dqm.jku.trustkg.dsd.integrated.IntegratedDatasource;
import dqm.jku.trustkg.util.AttributeSet;
import dqm.jku.trustkg.util.validators.AndValidator;
import dqm.jku.trustkg.util.validators.ComparisonValidator;
import dqm.jku.trustkg.util.validators.ConstComparisonValidator;
import dqm.jku.trustkg.util.validators.ConstEqualsValidator;
import dqm.jku.trustkg.util.validators.EqualsValidator;
import dqm.jku.trustkg.util.validators.NotValidator;
import dqm.jku.trustkg.util.validators.OrValidator;
import dqm.jku.trustkg.util.validators.SubstringValidator;
import dqm.jku.trustkg.util.validators.Validator;

public class ISQLIntegrator {

	private enum ValidationOperator {
		EQAUALS, NEQUALS, SMALLER, SMALLEREQ, GREATER, GREATEREQ, SUBSTRING_OF;
	}

	private final Map<Datasource, DSInstanceConnector> conns = new HashMap<>();
	private IntegratedDatasource ds;

	public void add(Datasource ds, DSInstanceConnector conn) {
		conns.put(ds, conn);
	}

	public ISQLIntegrator(IntegratedDatasource ds) {
		super();
		this.ds = ds;
	}

	public IntegratedDatasource getDs() {
		return ds;
	}

	public void setDs(IntegratedDatasource ds) {
		this.ds = ds;
	}

	public IntegratedConcept makeIntegratedConceptFromString(String sql, String label) throws ParseException {
		List<String> list = new LinkedList<String>();
		list = tokenize(sql);
		IntegratedConcept c = DSDFactory.makeIntegratedConcept(label, ds);
		Operator o = union(list, c);
		if (list.size() != 0) {
			throw new ParseException("Not all Tokens consumed", 0);
		}
		c.initIntegrationOperator(o);
		return c;
	}

	private List<String> tokenize(String sql) throws ParseException {
		List<String> tokens = new ArrayList<String>();
		String token = "";
		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			if (c == ' ' || c == '\t' || c == '\n' && c == '\r') {// white
																	// characters
				if (token.length() != 0) {
					tokens.add(token.toLowerCase());
					token = "";
				}
			} else if (c == '.' || c == '(' || c == ')' || c == ',' || c == '*' || c == '\'') {// single
				// character
				// token
				if (token.length() != 0) {
					tokens.add(token.toLowerCase());
					token = "";
				}
				token += c;
				tokens.add(token.toLowerCase());
				token = "";
			} else if (c == '<' || c == '>' || c == '=' || c == '!') {
				if (token.length() != 0) {
					tokens.add(token.toLowerCase());
					token = "";
				}
				token += c;
				char c1 = sql.charAt(i + 1);
				if (c1 == '=') {// get second comparison char if it exists
					token += c;
					i++;
				}
				tokens.add(token.toLowerCase());
				token = "";
			} else if ((c <= 'Z' && c >= 'A') || (c <= 'z' || c >= 'a') || (c <= '9' || c >= '0') || c == '_') { // alpahnumerics
																													// +
																													// underscore
				token += c;
			} else {
				throw new ParseException("Illegal Character in ISQL-Statement", i);
			}
		}
		if (token.length() != 0) {
			tokens.add(token.toLowerCase());
			token = "";
		}

		return tokens;
	}

	private Operator union(List<String> list, IntegratedConcept c) throws ParseException {
		Operator o = selectFromWhere(list, c);
		while (option(list, "union")) {
			o = new Union(o, selectFromWhere(list, c));
		}
		return o;

	}

	private Operator selectFromWhere(List<String> list, IntegratedConcept c) throws ParseException {
		Map<String, String> aliases = new HashMap<String, String>();
		List<String> selectedAttributes = new ArrayList<String>();
		boolean selectAll = false;
		Operator o = null;

		// Select list
		if (option(list, "select")) {
			if (option(list, "*")) {
				selectAll = true;
			} else {
				selectList(list, aliases, selectedAttributes);
			}
		} else {
			throw new ParseException("select not found", 0);
		}

		// From list
		if (option(list, "from")) {
			o = fromList(list, c);
		} else {
			throw new ParseException("from not found", 0);
		}

		// Where condition
		if (option(list, "where")) {
			o = buildSelection(o, list);

		}

		// Selection BEFORE Projection
		if (!selectAll) {
			o = buildProjection(o, selectedAttributes);
			o = buildRename(o, aliases);
		}

		return o;
	}

	private Operator buildSelection(Operator o, List<String> list) throws ParseException {
		Validator v = validator(list);
		return new Selection(o, v);
	}

	private Validator validator(List<String> list) throws ParseException {
		return orValidator(list);
	}

	private Validator orValidator(List<String> list) throws ParseException {
		Validator v = null;
		do {
			if (v == null) {
				v = andValidator(list);
			} else {
				v = new OrValidator(v, andValidator(list));
			}
			if (list.size() == 0)
				break;// normal ending
		} while (option(list, "or"));
		return v;
	}

	private Validator andValidator(List<String> list) throws ParseException {
		Validator v = null;
		do {
			if (v == null) {
				v = notValidator(list);
			} else {
				v = new AndValidator(v, notValidator(list));
			}
			if (list.size() == 0)
				break;// normal ending
		} while (option(list, "and"));
		return v;
	}

	private Validator notValidator(List<String> list) throws ParseException {
		if (option(list, "not")) {
			return new NotValidator(atomicValidator(list));
		}

		return atomicValidator(list);
	}

	private Validator atomicValidator(List<String> list) throws ParseException {

		if (option(list, "(")) {
			Validator v = validator(list);
			if (!option(list, ")")) {
				throw new ParseException("Parenthesis not closed correctly", 0);
			}
			return v;
		}

		boolean leftc = false, rightc = false;
		String left, right;
		if (option(list, "'")) {
			left = constant(list);
			leftc = true;
		} else {
			left = name(list);
		}

		ValidationOperator middle = voperator(list);

		if (option(list, "'")) {
			right = constant(list);
			rightc = true;
		} else {
			right = name(list);
		}

		Validator v = null;

		ComparisonValidator.Comaparison cv = null;
		switch (middle) {
		case SMALLER:
			cv = ComparisonValidator.Comaparison.SMALLER;
			v = buildComparion(cv, leftc, rightc, left, right);
		case SMALLEREQ:
			cv = ComparisonValidator.Comaparison.SMALLER_EQUALS;
			v = buildComparion(cv, leftc, rightc, left, right);
			break;
		case GREATER:
			cv = ComparisonValidator.Comaparison.GREATER;
			v = buildComparion(cv, leftc, rightc, left, right);
			break;
		case GREATEREQ:
			cv = ComparisonValidator.Comaparison.GREATER_EQUALS;
			v = buildComparion(cv, leftc, rightc, left, right);
			break;
		case EQAUALS:
			v = buildEquals(leftc, rightc, left, right);
			break;
		case NEQUALS:
			v = new NotValidator(buildEquals(leftc, rightc, left, right));
			break;
		case SUBSTRING_OF:
			v = buildSubstringValidator(leftc, rightc, left, right);
			break;
		default: // this can not be reached
			throw new IllegalArgumentException("Comparison not fully impelemented in the parser");
		}
		return v;

	}

	private Validator buildSubstringValidator(boolean leftc, boolean rightc, String left, String right) throws ParseException {
		if (leftc && rightc) {
			throw new ParseException("Comparison of two Constants can be neglected", 0);
		}
		return new SubstringValidator(leftc, rightc, left, right);
	}

	private Validator buildComparion(ComparisonValidator.Comaparison cv, boolean leftc, boolean rightc, String left, String right) throws ParseException {
		if (leftc && rightc) {
			throw new ParseException("Comparison of two Constants can be neglected", 0);
		}
		if (leftc) {
			return new ConstComparisonValidator(left, right, cv);
		}
		if (rightc) {
			return new ConstComparisonValidator(right, left, cv);
		}
		return new ComparisonValidator(left, right, cv);
	}

	private Validator buildEquals(boolean leftc, boolean rightc, String left, String right) throws ParseException {
		if (leftc && rightc) {
			throw new ParseException("Comparison of two Constants can be neglected", 0);
		}
		if (leftc) {
			return new ConstEqualsValidator(left, right);
		}
		if (rightc) {
			return new ConstEqualsValidator(right, left);
		}
		return new EqualsValidator(left, right);
	}

	private ValidationOperator voperator(List<String> list) throws ParseException {
		if (list.size() == 0) {
			throw new ParseException("List ended unexpectedly", 0);
		}
		String s = list.remove(0);
		switch (s) {
		case "<=":
			return ValidationOperator.SMALLEREQ;
		case "<":
			return ValidationOperator.SMALLER;
		case ">":
			return ValidationOperator.GREATER;
		case ">=":
			return ValidationOperator.GREATEREQ;
		case "==":
			return ValidationOperator.EQAUALS;
		case "!=":
			return ValidationOperator.NEQUALS;
		case "substringof":
			return ValidationOperator.SUBSTRING_OF;
		default:
			throw new ParseException("unexcpected comparison operator " + s, 0);

		}
	}

	private Operator buildRename(Operator o, Map<String, String> aliases) {
		return new Rename(o, aliases);
	}

	private Operator buildProjection(Operator o, List<String> selectedAttributes) {
		AttributeSet attr = new AttributeSet(o.getAttributes().stream().filter(x -> selectedAttributes.contains(x.getLabel())).collect(Collectors.toList()));
		return new Projection(o, attr);
	}

	private Operator fromList(List<String> list, IntegratedConcept c) throws ParseException {
		Operator id = null;
		do {
			if (list.size() == 0) {
				throw new ParseException("selectList ended unexpectedly", 0);
			}
			if (id == null) {
				id = identity(list, c);
			} else {
				id = new CrossProduct(id, identity(list, c));
			}
		} while (option(list, ","));
		return id;
	}

	private Operator identity(List<String> list, IntegratedConcept c) throws ParseException {
		String dsName = name(list);
		if (!option(list, ".")) {
			throw new ParseException("From identifiers need to have both datasource and concept name", 0);
		}
		String cName = name(list);
		Datasource ds = DSDElement.getDatasource(dsName).get();
		Identity id = new Identity(c, ds.getConcept(cName), conns.get(ds));
		return id;
	}

	private void selectList(List<String> list, Map<String, String> aliases, List<String> selectedAttributes) throws ParseException {
		do {
			if (list.size() == 0) {
				throw new ParseException("selectList ended unexpectedly", 0);
			}
			alias(aliases, list, selectedAttributes);
		} while (option(list, ","));
	}

	private void alias(Map<String, String> aliases, List<String> list, List<String> selectedAttributes) throws ParseException {
		String oldName = name(list);
		selectedAttributes.add(oldName);
		if (option(list, "as")) {
			String newName = name(list);
			aliases.put(oldName, newName);
		}
	}

	private String name(List<String> list) throws ParseException {
		if (list.size() == 0) {
			throw new ParseException("No name found", 0);
		}
		String name = list.get(0);
		if (!name.matches("^[a-zA-Z0-9_]*$")) {
			throw new ParseException("Illegal name found " + name, 0);
		}
		list.remove(0);
		return name;
	}

	private boolean option(List<String> list, String s) {
		if (list.isEmpty())
			return false;
		if (list.get(0).equalsIgnoreCase(s)) {
			list.remove(0);
			return true;
		}
		return false;
	}

	private String constant(List<String> list) throws ParseException {
		String s = list.remove(0);
		if (!option(list, "'")) {
			throw new ParseException("End of constant ' expected ", 0);
		}
		return s;
	}
}
