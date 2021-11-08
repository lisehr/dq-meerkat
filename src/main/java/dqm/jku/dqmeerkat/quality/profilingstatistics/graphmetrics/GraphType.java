package dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics;

import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.elements.ReferenceAssociation;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.*;


import java.util.List;

public class GraphType extends ProfileStatistic {

    public GraphType() {

    }

    public GraphType(DataProfile d) {
        super(graphType, graphCat, d);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        Concept c = (Concept) this.getRefProf().getElem();
        ReferenceAssociation association = (ReferenceAssociation) c.getDatasource().getAssociation("Ref/" + c.getLabelOriginal());
        String neoType = association.getNeo4JType();

        super.setValue(neoType);
        super.setNumericVal((String) neoType);
        super.setValueClass(String.class);
    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {

    }

    @Override
    public void update(RecordList rs) {

    }

    @Override
    protected String getValueString() {
        return super.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileStatistic m, double threshold) {
        return false;
    }
}
