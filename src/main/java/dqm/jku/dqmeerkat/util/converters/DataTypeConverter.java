package dqm.jku.dqmeerkat.util.converters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;

import dqm.jku.dqmeerkat.dsd.elements.ReferenceAssociation;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

import com.datastax.driver.core.DataType;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.ForeignKeyRule;
import dqm.jku.dqmeerkat.util.datastructures.TryParsers;

/**
 * @author lisa Conversion methods in this class have to convert to one of the
 *         following data types: - Object - String - Double - Float - Integer -
 *         Date - Boolean
 * 
 *         Please do not use any other data types.
 *
 */
public class DataTypeConverter {

	private static DateFormat format;

	public static Object getMySQLRecordvalue(Attribute a, ResultSet rs) throws SQLException, ParseException {
		if (Year.class.equals(a.getDataType())) {
			format = new SimpleDateFormat("YYYY-MM-DD");
			Date date = format.parse(rs.getString(a.getLabel()));
			SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY");
			String year = yearFormat.format(date);
			return Year.parse(year);
		}
		if (Date.class.equals(a.getDataType())) {
			return rs.getDate(a.getLabel());
		}
		if (Time.class.equals(a.getDataType())) {
			format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
			return format.parse(rs.getString(a.getLabel()));
		}
		if (Integer.class.isAssignableFrom(a.getDataType())) {
			return rs.getInt(a.getLabel());
		}
		if (Float.class.isAssignableFrom(a.getDataType())) {
			return rs.getFloat(a.getLabel());
		}
		if (Long.class.isAssignableFrom(a.getDataType())) {
			return rs.getLong(a.getLabel());
		}
		if (Double.class.isAssignableFrom(a.getDataType())) {
			return rs.getDouble(a.getLabel());
		}
		if (String.class.isAssignableFrom(a.getDataType())) {
			return rs.getString(a.getLabel());
		}
		if (Boolean.class.isAssignableFrom(a.getDataType())) {
			return rs.getBoolean(a.getLabel());
		}

		return rs.getObject(a.getLabel());
	}

	public static void getTypeFromMySQL(Attribute a, String type, String defaultVal) throws ParseException {
		// for mapping hints see:
		// https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html

		switch (type) {
		case "timestamp":
			a.setDataType(Date.class);
			if (defaultVal != null) {
				format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
				if (defaultVal.toUpperCase().equals("CURRENT_TIMESTAMP")) {
					Date now = new Date();
					a.setDefaultValue(now);
					break;
				}
				a.setDefaultValue(format.parse(defaultVal));
			}
			break;
		case "datetime":
		case "date":
			a.setDataType(Date.class);
			if (defaultVal != null) {
				format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
				Date parsedDate = new Date();
				try {
					parsedDate = format.parse(defaultVal);
				} catch (ParseException e) {
					System.err.println(e.toString() + " --> inserted " + parsedDate.toString());
				}
				a.setDefaultValue(parsedDate);
			}
			break;
		case "time":
			a.setDataType(Time.class);
			if (defaultVal != null)
				a.setDefaultValue(Time.valueOf(defaultVal));
			break;
		case "year":
			a.setDataType(Year.class);
			if (defaultVal != null) {
				format = new SimpleDateFormat("YYYY");
				a.setDefaultValue(format.parse(defaultVal));
			}
			break;
		case "boolean":
		case "bit":
			a.setDataType(Boolean.class);
			if (defaultVal != null)
				a.setDefaultValue(Boolean.valueOf(defaultVal));
			break;
		case "tinyint":
		case "smallint":
		case "mediumint":
		case "int":
		case "integer":
			a.setDataType(Integer.class);
			if (defaultVal != null)
				a.setDefaultValue(Integer.valueOf(defaultVal));
			break;
		case "bigint":
			a.setDataType(BigInteger.class);
			if (defaultVal != null)
				a.setDefaultValue(new BigInteger(defaultVal));
			break;
		case "float":
			a.setDataType(Float.class);
			if (defaultVal != null)
				a.setDefaultValue(Float.valueOf(defaultVal));
			break;
		case "double":
			a.setDataType(Double.class);
			if (defaultVal != null)
				a.setDefaultValue(Double.valueOf(defaultVal));
			break;
		case "decimal":
			a.setDataType(BigDecimal.class);
			if (defaultVal != null)
				a.setDefaultValue(new BigDecimal(defaultVal.replaceAll(",", "")));
			break;
		case "varchar":
		case "enum":
		case "text":
		case "tinytext":
		case "mediumtext":
		case "longtext":
		case "set":
		case "char":
			a.setDataType(String.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		case "blob":
		case "longblob":
		case "geometry":
			a.setDataType(Object.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		case "binary":
			a.setDataType(Byte.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		default:
			throw new IllegalArgumentException("No mapping known for this MySQL data type: " + type);
		}
	}

	public static ForeignKeyRule getFKRuleFromString(String rule) {
		switch (rule.toUpperCase()) {
		case "NO ACTION":
			return ForeignKeyRule.NO_ACTION;
		case "CASCADE":
			return ForeignKeyRule.CASCADE;
		case "SET NULL":
			return ForeignKeyRule.SET_NULL;
		case "RESTRICT":
			return ForeignKeyRule.RESTRICT;
		default:
			throw new IllegalArgumentException("No mapping known for this MySQL foreign key rule: " + rule);
		}
	}

	public static Class<?> getTypeFromOntology(RDFNode dataType) {
		// for the standard mapping see:
		// https://jena.apache.org/documentation/notes/typed-literals.html
		Resource type = dataType.asResource();

		if (type.hasURI(XSD.dateTime.getURI()) || type.hasURI(XSD.date.getURI()) || type.hasURI(XSD.dateTimeStamp.getURI())) {
			return Date.class;
		} else if (type.hasURI(XSD.time.getURI())) {
			return Time.class;
		} else if (type.hasURI(XSD.gYear.getURI())) {
			return Year.class;
		} else if (type.hasURI(XSD.xboolean.getURI())) {
			return Boolean.class;
		} else if (type.hasURI(XSD.xint.getURI())) {
			return Integer.class;
		} else if (type.hasURI(XSD.integer.getURI())) {
			return BigInteger.class;
		} else if (type.hasURI(XSD.xfloat.getURI())) {
			return Float.class;
		} else if (type.hasURI(XSD.xdouble.getURI())) {
			return Double.class;
		} else if (type.hasURI(XSD.decimal.getURI())) {
			return BigDecimal.class;
		} else if (type.hasURI(XSD.xlong.getURI())) {
			return Long.class;
		} else if (type.hasURI(XSD.xshort.getURI())) {
			return Short.class;
		} else if (type.hasURI(XSD.xstring.getURI())) {
			return String.class;
		} else if (type.hasURI(XSD.base64Binary.getURI())) {
			return Object.class;
		} else if (type.hasURI(XSD.xbyte.getURI())) {
			return Byte.class;
		} else {
			throw new IllegalArgumentException("No mapping known for this XSD data type: " + type);
		}
	}

	public static void getTypeFromOracleSQL(Attribute a, String type, String defaultVal) throws ParseException {
		// for mapping hints see:
		// https://docs.oracle.com/cd/B19306_01/java.102/b14188/datamap.htm

		switch (type) {
		case "timestamp":
		case "timestamptz":
		case "timestampltz":
			a.setDataType(Date.class); // in miscellaneous auslagern --> time stamp format (als globale variable)
			if (defaultVal != null) {
				format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
				if (defaultVal.toUpperCase().equals("CURRENT_TIMESTAMP")) {
					Date now = new Date();
					a.setDefaultValue(now);
					break;
				}
				a.setDefaultValue(format.parse(defaultVal));
			}
			break;
		case "date":
			a.setDataType(Date.class);
			if (defaultVal != null) {
				format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
				a.setDefaultValue(format.parse(defaultVal));
			}
			break;
		case "boolean":
			a.setDataType(Boolean.class);
			if (defaultVal != null)
				a.setDefaultValue(Boolean.valueOf(defaultVal));
			break;
		case "number":
			a.setDataType(Integer.class);
			if (defaultVal != null)
				a.setDefaultValue(Integer.valueOf(defaultVal));
			break;
		case "varchar":
		case "varchar2":
		case "String":
		case "nchar":
		case "char":
			a.setDataType(String.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		case "blob":
		case "clob":
		case "nclob":
		case "rowid":
		case "bfile":
			a.setDataType(Object.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		case "array":
			a.setDataType(Array.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		case "ref":
			a.setDataType(Ref.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		case "ResultSet":
			a.setDataType(ResultSet.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		case "raw":
			a.setDataType(Byte.class);
			if (defaultVal != null)
				a.setDefaultValue(defaultVal);
			break;
		default:
			throw new IllegalArgumentException("No mapping known for this MySQL data type: " + type);
		}
	}

	public static void getTypeFromCassandra(Attribute a, DataType t) {
		// For mapping hints see:
		// https://docs.datastax.com/en/cql/3.3/cql/cql_reference/cql_data_types_c.html
		switch (t.getName().toString()) {
		case "timestamp":
		case "date":
			a.setDataType(Date.class);
			break;
		case "time":
			a.setDataType(Time.class);
			break;
		case "ascii":
		case "inet":
		case "text":
		case "varchar":
			a.setDataType(String.class);
			break;
		case "bigint":
		case "counter":
		case "int":
		case "smallint":
		case "tinyint":
			a.setDataType(Integer.class);
			break;
		case "varint":
			a.setDataType(BigInteger.class);
			break;
		case "boolean":
			a.setDataType(Boolean.class);
			break;
		case "blob":
		case "frozen":
		case "tuple":
			a.setDataType(Object.class);
			break;
		case "decimal":
			a.setDataType(BigDecimal.class);
			break;
		case "double":
			a.setDataType(Double.class);
			break;
		case "float":
			a.setDataType(Float.class);
			break;
		case "list":
			a.setDataType(List.class);
			break;
		case "map":
			a.setDataType(Map.class);
			break;
		case "set":
			a.setDataType(Set.class);
			break;
		case "timeuuid":
		case "uuid":
			a.setDataType(UUID.class);
			break;
		default:
			throw new IllegalArgumentException("No mapping known for this MySQL data type: " + t);
		}
	}
	
	/**
	 * Get a data type from a csv record. It uses TryParsers for recognizing the data type.
	 * Integer was scrapped because in big numbers (e.g. order ids) an overflow can happen
	 * Opportunity for future research on creating CSV data type parsers!
	 * 
	 * @param a the corresponding attribute
	 * @param val the String, from which the data type should be recognized
	 */
	public static void getDataTypeFromCSVRecord(Attribute a, String val) {
	  if (val == null) throw new IllegalArgumentException("Cannot work with invalid String!");
	  if (StringUtils.isBlank(val) || val.isEmpty()) a.setDataType(Object.class);
	  else if (TryParsers.tryParseInt(val)) a.setDataType(Integer.class);
	  else if (TryParsers.tryParseLong(val)) a.setDataType(Long.class);
	  else if (TryParsers.tryParseDouble(val)) a.setDataType(Double.class);
	  else a.setDataType(String.class);
	}
	
	/**
	 * Get a value from the string read in a csv record. If a new Value is assignable to the saved class,
	 * but the parser creates an error, e.g. if a double value is found, when parsing an long, the data type is refined to its 
	 * next hierarchical class.
	 * @param a the corresponding attribute to the value
	 * @param val the value in String-form
	 * @return the Object parsed from the String
	 */
	public static Object getDataValueFromCSV(Attribute a, String val) {
	  Class<?> clazz = a.getDataType();
	  if (StringUtils.isBlank(val) || val.isEmpty()) return null;
	  if (clazz.equals(Object.class)) return refineCSVDataType(a, val);
	  try {
	    if (Integer.class.isAssignableFrom(clazz)) return Integer.parseInt(val);
	    else if (Long.class.isAssignableFrom(clazz)) return Long.parseLong(val);
	    else if (Double.class.isAssignableFrom(clazz)) return Double.parseDouble(val);
	    else return val; // case Class == String

	  } catch (NumberFormatException e) {
	    return refineCSVDataType(a, val);
	  }
	}

	public static void getTypeFromNeo4J(Attribute a, String datatype, String defaultVal) throws ParseException {
		switch (datatype) {
			case "String":
				a.setDataType(String.class);
				//if (defaultVal != null) {
				//	a.setDefaultValue(defaultVal);
				//}
				break;
			case "int":
				a.setDataType(Integer.class);
				//if (defaultVal != null) {
				//	a.setDefaultValue(Integer.valueOf(defaultVal));
				//}
				break;
			case "boolean":
				a.setDataType(Boolean.class);
				//if (defaultVal != null) {
				//	a.setDefaultValue(Boolean.valueOf(defaultVal));
				//}
				break;
			default:
				throw new IllegalArgumentException("No mapping known for this Neo4J data type: " + datatype);
		}
	}

	public static Object getNeo4JRecordvalue(Attribute a, Object o) {

		if(String.class.isAssignableFrom(a.getDataType())) {
			return o.toString().replaceAll("[\\[\\]\"]", "");
		}

		return null;
	}

		/**
         * Method for refining a data type when hitting a change in the record set.
         * @param a the attribute to be refined
         * @param val the string causing the refine
         * @return a value parsed with the refined type
         */
  private static Object refineCSVDataType(Attribute a, String val) {
    getDataTypeFromCSVRecord(a, val);
    return getDataValueFromCSV(a, val);
  }
}
