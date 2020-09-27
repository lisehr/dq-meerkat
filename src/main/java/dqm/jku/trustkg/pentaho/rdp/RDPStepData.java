package dqm.jku.trustkg.pentaho.rdp;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class RDPStepData extends BaseStepData implements StepDataInterface {
	public RowMetaInterface outputRowMeta;
	
	public RDPStepData() {
		super();
	}

}
