package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.datatypeinfo;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.dti;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.bt;


/**
 * Describes the metric Basic Type, which categorizes an Attribute as a String,
 * Null or Numeric value type.
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/BasicType")
public class BasicType extends ProfileStatistic<String, String> {
    public BasicType(DataProfile d) {
        super(bt, dti, d, String.class);
    }

    @Override
    public void calculation(RecordList rs, String oldVal) {
        if (((Attribute) super.getRefElem()).getDataType().equals(String.class)) {
            super.setValue("String");
        } else if (((Attribute) super.getRefElem()).getDataType().equals(Object.class)) {
            super.setValue("Object");
        } else {
            super.setValue("Numeric");
        }
        super.setInputValueClass(String.class);
    }


    @Override
    public void update(RecordList rs) {
        calculation(null, null);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic<String, String> m, double threshold) {
        String rdpVal = this.getSimpleValueString();
        String dpValue = this.getSimpleValueString();

        boolean conf = rdpVal.equals(dpValue);
        if (!conf && Constants.DEBUG) System.out.println(this.getTitle() + " exceeded: " + dpValue + " != " + rdpVal);
        return conf;
    }
}
