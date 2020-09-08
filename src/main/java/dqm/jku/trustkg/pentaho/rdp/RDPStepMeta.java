package dqm.jku.trustkg.pentaho.rdp;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

@Step(
		id = "RDPStep", 
		name = "RDPStep.Name", 
		image = "dqm/jku/trustkg/pentaho/rdp/resources/meerkat.svg",
	  i18nPackageName = "dqm.jku.trustkg.pentaho.rdp",
	  description = "RDPStep.TooltipDesc",
	  categoryDescription = "RDP.Category"
)
public class RDPStepMeta extends BaseStepMeta implements StepMetaInterface {
	
	@SuppressWarnings("unused")
	private static final Class<?> PKG = RDPStepMeta.class; // i18n purposes

	public RDPStepMeta() {
		super();
	}

	@Override
	public void setDefault() {
		setOutputField("demo_field");
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new RDPStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new RDPStepData();
	}

	public String getOutputField() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setOutputField(String text) {
	}

}
