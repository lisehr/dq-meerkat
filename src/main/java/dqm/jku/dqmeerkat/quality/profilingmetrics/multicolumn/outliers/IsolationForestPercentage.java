package dqm.jku.dqmeerkat.quality.profilingmetrics.multicolumn.outliers;

import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricCategory.out;
import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle.isoF;
import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle.isoFP;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingmetrics.DependentProfileMetric;
import dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle;
import dqm.jku.dqmeerkat.quality.profilingmetrics.ProfileMetric;

public class IsolationForestPercentage extends DependentProfileMetric {

	public IsolationForestPercentage() {
		
	}
	
	public IsolationForestPercentage(DataProfile dp) {
		super(isoFP, out, dp);
	}

	
	@Override
	public void calculation(RecordList rs, Object oldVal) {
		calculation(rs, oldVal, false);
	}
	
  private void calculation(RecordList rl, Object oldVal, boolean checked) {
    if (!checked) dependencyCalculationWithRecordList(rl);
    int size = ((ArrayList<?>)super.getRefProf().getMetric(isoF).getValue()).size();
    int numRecs = rl.size();
    double result = size * 100.0 / numRecs;
    this.setValue(result);
    this.setNumericVal((Number) result);
    this.setValueClass(Double.class);

  }

	@Override
	public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
		throw new NoSuchMethodException("IsolationForests only work with record lists");
	}

	@Override
	public void update(RecordList rs) {
		calculation(rs, null, false);
	}

	@Override
	protected String getValueString() {
    if (getValue() == null) return "\tnull";
    else return "\t" + getValue().toString() + "%";
	}

	@Override
	protected void dependencyCalculationWithNumericList(List<Number> list) throws NoSuchMethodException {
		throw new NoSuchMethodException("IsolationForests only work with record lists");
	}

	@Override
	protected void dependencyCalculationWithRecordList(RecordList rl) {
    if (super.getMetricPos(MetricTitle.isoFP) - 1 <= super.getMetricPos(isoF)) super.getRefProf().getMetric(isoF).calculation(rl, null);
	}

	@Override
	protected void dependencyCheck() {
    ProfileMetric isoM = super.getRefProf().getMetric(isoF);
    if (isoM == null || (isoM != null && isoM.getValue() == null)) {
    	isoM = new IsolationForest(super.getRefProf());
      super.getRefProf().addMetric(isoM);
    }
	}

	@Override
	public boolean checkConformance(ProfileMetric m, double threshold) {
		// TODO Auto-generated method stub
		return false;
	}

}
