package dqm.jku.trustkg.pentaho.rdp;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class RDPStepDialog extends BaseStepDialog implements StepDialogInterface {

	public RDPStepDialog(Shell parent, BaseStepMeta baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, baseStepMeta, transMeta, stepname);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String open() {
		// TODO Auto-generated method stub
		return null;
	}

}
