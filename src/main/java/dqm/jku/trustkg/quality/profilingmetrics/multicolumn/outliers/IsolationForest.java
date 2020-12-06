package dqm.jku.trustkg.quality.profilingmetrics.multicolumn.outliers;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.converters.NDArrayConverter;
import dqm.jku.trustkg.util.python.JepInterpreter;
import jep.Interpreter;
import jep.JepException;
import jep.NDArray;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;

public class IsolationForest extends ProfileMetric{
	
	public IsolationForest() {
		
	}
	
	public IsolationForest(DataProfile dp) {
		super(isoF, out, dp);
	}

	@Override
	public void calculation(RecordList rs, Object oldVal) {
		NDArray<?> resultingRecords = null;
		try (Interpreter py = JepInterpreter.getInterpreter()){
			JepInterpreter.initFunctionsAndLibs(py);
			NDArray<double[]> array = NDArrayConverter.extractNumericFeaturesFromRecordList(rs);
			py.set("x", array);
			py.exec("from sklearn.ensemble import IsolationForest");
			py.exec("train_scaled = np.apply_along_axis(robust_scaler, 0, x)");
			//JepInterpreter.printNDArr((NDArray<?>)py.getValue("train_scaled"));
			py.exec("np.nan_to_num(train_scaled, copy=False)"); // replacement of nan values, since null values are not handled 
			py.exec("ISO_forest = IsolationForest(n_estimators = 100, max_samples = 400, contamination='auto').fit(train_scaled)");
			py.exec("outliers = ISO_forest.predict(train_scaled)");
			resultingRecords = (NDArray<?>) py.getValue("outliers");
			py.exec("score = ISO_forest.score_samples(train_scaled)");
			
			// maybe increase granularity of results by adding stricter thresholds
		} catch (JepException e) {
			e.printStackTrace();
		}
		int[] results = (int[]) resultingRecords.getData();
		ArrayList<Integer> recordNos = new ArrayList<>();
		for (int i = 0; i < results.length; i++) {
			if (results[i] == -1) recordNos.add(i + 1); // +1 for alignment (arrays start with 0, so record 1 is index 0)
		}
		this.setValueClass(ArrayList.class);
		this.setValue(recordNos);
	}

	@Override
	public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
		throw new NoSuchMethodException("IsolationForests only work with record lists");
	}

	@Override
	public void update(RecordList rs) {
		this.calculation(rs, null);
	}

	@Override
	protected String getValueString() {
		return this.getSimpleValueString();
	}

	@Override
	public boolean checkConformance(ProfileMetric m, double threshold) {
		// TODO Auto-generated method stub
		return false;
	}

}
