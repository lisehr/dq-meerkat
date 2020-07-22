package dqm.jku.trustkg.pentaho.rdp;

import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class RDPStepMeta extends BaseStepMeta implements StepMetaInterface {
	
	public RDPStepMeta() {
		super();
	}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StepDataInterface getStepData() {
		// TODO Auto-generated method stub
		return null;
	}

}
