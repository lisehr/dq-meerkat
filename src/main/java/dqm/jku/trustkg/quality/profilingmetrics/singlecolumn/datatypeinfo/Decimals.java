package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.dec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Decimals")
public class Decimals extends ProfileMetric {
	public Decimals() {

	}

	public Decimals(DataProfile d) {
		super(dec, d);
	}

	@Override
	public void calculation(RecordList rs, Object oldVal) {
		Attribute a = (Attribute) super.getRefElem();
		this.setValueClass(Integer.class);
		if (a.getDataType() == Object.class) return;
		if (a.getDataType() == Integer.class || a.getDataType() == Long.class || a.getDataType() == String.class) {
			this.setValue(0);
			return;
		}

		int decimals;    
		if (oldVal == null) decimals = 0;
		else decimals = (int) oldVal;
		for (Record r : rs) {
			Object field = r.getField(a);
			decimals = getDecimals(decimals, (Number) field);
		}
		if (decimals == -1) this.setValue(null);
		else this.setValue(decimals);
	}

	private int getDecimals(int decimals, Number field) {
		String numStr = field.toString();
		if (StringUtils.isBlank(numStr) || numStr.isEmpty()) return -1;
		int pointPos = numStr.indexOf('.');
		int dec = numStr.length() - pointPos - 1;
		if (dec > decimals) return dec;
		else return decimals;
	}

	@Override
	public void calculationNumeric(List<Number> list, Object oldVal) {
		Attribute a = (Attribute) super.getRefElem();
		this.setValueClass(Integer.class);
		if (a.getDataType() == Object.class) return;
		if (list.isEmpty()) {
			this.setValue(null);
			return;
		}
		if (a.getDataType() == Integer.class || a.getDataType() == Long.class || a.getDataType() == String.class) {
			this.setValue(0);
			return;
		}

		int decimals;    
		if (oldVal == null) decimals = 0;
		else decimals = (int) oldVal;
		for (Number n : list) {
			decimals = getDecimals(decimals, (Number) n);
		}
		this.setValue(decimals);
	}

	@Override
	public void update(RecordList rs) {
		calculation(rs, super.getValue());
	}

	@Override
	protected String getValueString() {
		return super.getSimpleValueString();
	}
}
