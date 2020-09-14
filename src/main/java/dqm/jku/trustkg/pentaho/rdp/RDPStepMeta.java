package dqm.jku.trustkg.pentaho.rdp;

import java.nio.file.Paths;

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
	
	// fields for parameters in RDP creation process
	private int rowCnt;
	private String patternFilePath;
	private boolean isCSV; // true CSV, false nothing

	
	@SuppressWarnings("unused")
	private static final Class<?> PKG = RDPStepMeta.class; // i18n purposes
  private static final String PENT_PREFIX = "/plugins/DQ-MeeRKat/patterns";

	public RDPStepMeta() {
		super();
	}
	
	public int getRowCnt() {
		return rowCnt;
	}

	public void setRowCnt(int rowCnt) {
		this.rowCnt = rowCnt;
	}

	public String getFilePath() {
		return patternFilePath;
	}

	public void setFilePath(String filePath) {
		this.patternFilePath = filePath;
	}

	public boolean isCSV() {
		return isCSV;
	}

	public void setCSV(boolean isCSV) {
		this.isCSV = isCSV;
		this.log.logBasic("Hello there! " + isCSV);
	}

	@Override
	public void setDefault() {
		rowCnt = 5000;
		patternFilePath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX;
		isCSV = false;
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new RDPStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new RDPStepData();
	}

}
