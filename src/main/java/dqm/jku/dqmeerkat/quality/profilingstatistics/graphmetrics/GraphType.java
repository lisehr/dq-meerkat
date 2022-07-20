package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.ReferenceAssociation;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.graphCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.graphType;

public class GraphType extends ProfileStatistic<String, String> {


    public GraphType(DataProfile d) {
        super(graphType, graphCat, d, String.class);
    }

    @Override
    public void calculation(RecordList rs, String oldVal) {
        Concept c = (Concept) this.getRefProf().getElem();
        ReferenceAssociation association = (ReferenceAssociation) c.getDatasource().getAssociation("Ref/" + c.getLabelOriginal());
        String neoType = association.getNeo4JType();

        super.setValue(neoType);
        super.setInputValueClass(String.class);
    }

    @Override
    public void update(RecordList rs) {
        calculation(rs, value);
    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }


}
