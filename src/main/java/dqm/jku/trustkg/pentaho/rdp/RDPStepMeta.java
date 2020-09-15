package dqm.jku.trustkg.pentaho.rdp;

import java.io.File;
import java.nio.file.Paths;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import dqm.jku.trustkg.pentaho.FileOutputType;

@Step(id = "RDPStep", name = "RDPStep.Name", image = "dqm/jku/trustkg/pentaho/rdp/resources/meerkat.svg", i18nPackageName = "dqm.jku.trustkg.pentaho.rdp", description = "RDPStep.TooltipDesc", categoryDescription = "RDP.Category")
public class RDPStepMeta extends BaseStepMeta implements StepMetaInterface {

	// fields for parameters in RDP creation process
	private int rowCnt;
	private String patternFilePath;
	private String outputDirPath;
	private String outputFileName;
	private String type; // true CSV, false nothing

	@SuppressWarnings("unused")
	private static final Class<?> PKG = RDPStepMeta.class; // i18n purposes
	private static final String STD_FILE_NAME = "rdp_report";
	private static final String PENT_PREFIX_FILE = "\\plugins\\DQ-MeeRKat\\patterns";
	private static final String PENT_PREFIX_DIR = "\\plugins\\DQ-MeeRKat\\output";

	public RDPStepMeta() {
		super();
		setDefault();
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
		File file = new File(outputDirPath);
		if (file.exists()) this.patternFilePath = filePath;
		else patternFilePath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX_FILE;
	}

	public String getType() {
		return type;
	}

	public void setCSV(String type) {
		this.type = type;
	}

	@Override
	public void setDefault() {
		rowCnt = 5000;
		patternFilePath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX_FILE;
		outputDirPath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX_DIR;
		outputFileName = STD_FILE_NAME;
		type = FileOutputType.none.label();
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new RDPStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new RDPStepData();
	}

	public String getOutputDirPath() {
		return outputDirPath;
	}

	public void setOutputDirPath(String outputDirPath) {
		File file = new File(outputDirPath);
		if (file.isDirectory()) this.outputDirPath = outputDirPath;
		else outputDirPath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX_DIR;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

}
