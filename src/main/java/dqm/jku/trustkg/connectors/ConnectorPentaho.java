package dqm.jku.trustkg.connectors;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;

import dqm.jku.trustkg.dsd.DSDFactory;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.Miscellaneous.DBType;

public class ConnectorPentaho extends DSConnector {
	private String label;
	private RecordList records;

	public ConnectorPentaho() {
		this("PentahoDatasource");
	}
	
	public ConnectorPentaho(String label) {
		this.label = label;
	}

	
	@Override
	public Datasource loadSchema() throws IOException {
		return loadSchema(Constants.DEFAULT_URI, Constants.DEFAULT_PREFIX);
	}

	@Override
	public Datasource loadSchema(String uri, String prefix) throws IOException {
		Datasource ds = DSDFactory.makeDatasource(label, DBType.PENTAHOETL, uri, prefix);
		return ds;
	}
	
	public Datasource loadSchema(String uri, String prefix, RowMetaInterface meta) throws IOException {
		Datasource ds = loadSchema(uri, prefix);
		Concept c = DSDFactory.makeConcept(label, ds);
		int i = 0;
		String[] metaStr = meta.getFieldNames();
		for (String s : metaStr) {
			Attribute a = DSDFactory.makeAttribute(s, c);
			a.setDataType(this.getJavaClass(meta.searchValueMeta(s)));
			a.setOrdinalPosition(i++);
			a.setNullable(true);
			a.setAutoIncrement(false);
		}
		return ds;
	}

	@Override
	public Iterator<Record> getRecords(Concept concept) throws IOException {
		return records.iterator();
	}

	@Override
	public int getNrRecords(Concept c) throws IOException {
		return records.size();
	}

	@Override
	public RecordList getRecordList(Concept concept) throws IOException {
		return records;
	}

	@Override
	public RecordList getPartialRecordList(Concept concept, int offset, int noRecords) throws IOException {
		return records.splitPartialRecordList(offset, noRecords);
	}
	
	public void setRecords(RecordList rl) {
		this.records = rl;
	}
	
	private Class<?> getJavaClass( ValueMetaInterface vmi ) {
	  Class<?> metaClass = String.class;
	  
	  switch ( vmi.getType() ) {
	    case ValueMetaInterface.TYPE_BIGNUMBER:
	      metaClass = BigDecimal.class;
	      break;
	    case ValueMetaInterface.TYPE_BINARY:
	      metaClass = byte[].class;
	      break;
	    case ValueMetaInterface.TYPE_BOOLEAN:
	      //metaClass = Boolean.class;
	      break;
	    case ValueMetaInterface.TYPE_DATE:
	      //metaClass = Date.class;
	      break;
	    case ValueMetaInterface.TYPE_INTEGER:
	      metaClass = Long.class;
	      break;
	    case ValueMetaInterface.TYPE_NUMBER:
	      metaClass = Double.class;
	      break;
	    case ValueMetaInterface.TYPE_STRING:
	      //metaClass = String.class;
	      break;
	    case ValueMetaInterface.TYPE_SERIALIZABLE:
	      //metaClass = Object.class;
	      break;
	  }
	  return metaClass;
	}


}
