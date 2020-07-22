package dqm.jku.trustkg.pentaho.rdp;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class RDPStep extends BaseStep implements StepInterface {

	public RDPStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		// TODO Auto-generated constructor stub
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		return false;
		
	}
	
  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
	return false;

	
  }
  
  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {

  }
  
  
  
}
