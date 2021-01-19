package dqm.jku.dqmeerkat.pentaho.rdp;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import dqm.jku.dqmeerkat.pentaho.util.FileOutputType;
import dqm.jku.dqmeerkat.pentaho.util.PentahoRowUtils;

@Step(id = "RDPStep", 
			name = "RDPStep.Name", 
			image = "dqm/jku/dqmeerkat/pentaho/rdp/resources/meerkat.svg", 
			i18nPackageName = "dqm.jku.trustkg.pentaho.rdp", 
			description = "RDPStep.TooltipDesc", 
			categoryDescription = "RDP.Category"
)
public class RDPStepMeta extends BaseStepMeta implements StepMetaInterface {

	// fields for parameters in RDP creation process
	private int rowCnt;
	private String patternFilePath;
	private String outputDirPath;
	private String outputFileName;
	private String type; 
	private boolean outEnabled;
	private boolean verboseLogEnabled;
	private RowMetaInterface outputRowMeta;

	@SuppressWarnings("unused")
	private static final Class<?> PKG = RDPStepMeta.class; // i18n purposes
	private static final String STD_FILE_NAME = "rdp_report";
	private static final String PENT_PREFIX_FILE = "\\plugins\\DQ-MeeRKat\\patterns\\pattern_test.in";
	private static final String PENT_PREFIX_DIR = "\\plugins\\DQ-MeeRKat\\output";

	public RDPStepMeta() {
		super();
		setDefault();
		outputRowMeta = null;
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
		File file = new File(filePath);
		if (file.exists()) this.patternFilePath = filePath;
		else patternFilePath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX_FILE;
	}

	public String getType() {
		return type;
	}

	public void setCSV(String type) {
		this.type = type;
	}

	public void setDefault() {
		rowCnt = 5000;
		patternFilePath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX_FILE;
		outputDirPath = Paths.get("").toAbsolutePath().toString() + PENT_PREFIX_DIR;
		outputFileName = STD_FILE_NAME;
		type = FileOutputType.none.label();
		outEnabled = false;
		verboseLogEnabled = true;
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

	/**
	 * This method is called by Spoon when a step needs to serialize its
	 * configuration to a repository. The repository implementation provides the
	 * necessary methods to save the step attributes.
	 *
	 * @param rep               the repository to save to
	 * @param metaStore         the metaStore to optionally write to
	 * @param id_transformation the id to use for the transformation when saving
	 * @param id_step           the id to use for the step when saving
	 */
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "rowCnt", rowCnt);
			rep.saveStepAttribute(id_transformation, id_step, "patternFilePath", patternFilePath);
			rep.saveStepAttribute(id_transformation, id_step, "outputDirPath", outputDirPath);
			rep.saveStepAttribute(id_transformation, id_step, "outputFileName", outputFileName);
			rep.saveStepAttribute(id_transformation, id_step, "typeString", type);
			rep.saveStepAttribute(id_transformation, id_step, "outEnabled", outEnabled);
			rep.saveStepAttribute(id_transformation, id_step, "verboseLogEnabled", verboseLogEnabled);
		} catch (Exception e) {
			throw new KettleException("Unable to save step into repository: " + id_step, e);
		}
	}

	/**
	 * This method is called by PDI when a step needs to read its configuration from
	 * a repository. The repository implementation provides the necessary methods to
	 * read the step attributes.
	 * 
	 * @param rep       the repository to read from
	 * @param metaStore the metaStore to optionally read from
	 * @param id_step   the id of the step being read
	 * @param databases the databases available in the transformation
	 */
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException {
		try {
			rowCnt = (int) rep.getStepAttributeInteger(id_step, "rowCnt");
			patternFilePath = rep.getStepAttributeString(id_step, "patternFilePath");
			outputDirPath = rep.getStepAttributeString(id_step, "outputDirPath");
			outputFileName = rep.getStepAttributeString(id_step, "outputFileName");
			type = rep.getStepAttributeString(id_step, "typeString");
			outEnabled = rep.getStepAttributeBoolean(id_step, "outEnabled");
			verboseLogEnabled = rep.getStepAttributeBoolean(id_step, "verboseLogEnabled");
		} catch (Exception e) {
			throw new KettleException("Unable to load step from repository", e);
		}
	}

	/**
	 * This method is called by Spoon when a step needs to serialize its
	 * configuration to XML. The expected return value is an XML fragment consisting
	 * of one or more XML tags.
	 * 
	 * Please use org.pentaho.di.core.xml.XMLHandler to conveniently generate the
	 * XML.
	 * 
	 * @return a string containing the XML serialization of this step
	 */
	public String getXML() throws KettleValueException {
		StringBuilder xml = new StringBuilder();
		xml.append(XMLHandler.addTagValue("rowCnt", rowCnt));
		xml.append(XMLHandler.addTagValue("patternFilePath", patternFilePath));
		xml.append(XMLHandler.addTagValue("outputDirPath", outputDirPath));
		xml.append(XMLHandler.addTagValue("outputFileName", outputFileName));
		xml.append(XMLHandler.addTagValue("typeString", type));
		xml.append(XMLHandler.addTagValue("outEnabled", outEnabled));
		xml.append(XMLHandler.addTagValue("verboseLogEnabled", verboseLogEnabled));
		return xml.toString();
	}

	/**
	 * This method is called by PDI when a step needs to load its configuration from
	 * XML.
	 * 
	 * Please use org.pentaho.di.core.xml.XMLHandler to conveniently read from the
	 * XML node passed in.
	 * 
	 * @param stepnode  the XML node containing the configuration
	 * @param databases the databases available in the transformation
	 * @param metaStore the metaStore to optionally read from
	 */
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		try {
			rowCnt = Integer.parseInt(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "rowCnt")));
			patternFilePath = XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "patternFilePath"));
			outputDirPath = XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputDirPath"));
			outputFileName = XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputFileName"));
			type = XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "typeString"));
			outEnabled = "Y".equalsIgnoreCase(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outEnabled")));
			verboseLogEnabled = "Y".equalsIgnoreCase(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "verboseLogEnabled")));
		} catch (Exception e) {
			throw new KettleXMLException("Demo plugin unable to read step info from XML node", e);
		}
	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	/**
	 * This method is called to determine the changes the step is making to the
	 * row-stream. To that end a RowMetaInterface object is passed in, containing
	 * the row-stream structure as it is when entering the step. This method must
	 * apply any changes the step makes to the row stream. Usually a step adds
	 * fields to the row-stream.
	 * 
	 * @param inputRowMeta the row structure coming in to the step
	 * @param name         the name of the step making the changes
	 * @param info         row structures of any info steps coming in
	 * @param nextStep     the description of a step this step is passing rows to
	 * @param space        the variable space for resolving variables
	 * @param repository   the repository instance optionally read from
	 * @param metaStore    the metaStore to optionally read from
	 */
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
//		if (outputRowMeta == null) {			
			inputRowMeta.clear();
			List<ValueMetaInterface> values = new ArrayList<>();
			try {
				values = PentahoRowUtils.createPentahoOutputMeta();
			} catch (KettleValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (ValueMetaInterface v : values) inputRowMeta.addValueMeta(v);
			outputRowMeta = inputRowMeta.clone();
//		} else inputRowMeta = outputRowMeta.clone();
	}

	/**
	 * This method is called when the user selects the "Verify Transformation"
	 * option in Spoon. A list of remarks is passed in that this method should add
	 * to. Each remark is a comment, warning, error, or ok. The method should
	 * perform as many checks as necessary to catch design-time errors.
	 * 
	 * Typical checks include: - verify that all mandatory configuration is given -
	 * verify that the step receives any input, unless it's a row generating step -
	 * verify that the step does not receive any input if it does not take them into
	 * account - verify that the step finds fields it relies on in the row-stream
	 * 
	 * @param remarks   the list of remarks to append to
	 * @param transMeta the description of the transformation
	 * @param stepMeta  the description of the step
	 * @param prev      the structure of the incoming row-stream
	 * @param input     names of steps sending input to the step
	 * @param output    names of steps this step is sending output to
	 * @param info      fields coming in from info steps
	 * @param metaStore metaStore to optionally read from
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository, IMetaStore metaStore) {
	}

	public boolean isOutEnabled() {
		return outEnabled;
	}

	public void setOutEnabled(boolean isOutEnabled) {
		this.outEnabled = isOutEnabled;
	}
	

	public boolean isVerboseLogEnabled() {
		return verboseLogEnabled;
	}

	public void setVerboseLogEnabled(boolean verboseLogEnabled) {
		this.verboseLogEnabled = verboseLogEnabled;
	}
	
	public RowMetaInterface getOutputRowMeta() {
		return outputRowMeta;
	}

	public void setOutputRowMeta(RowMetaInterface outputRowMeta) {
		this.outputRowMeta = outputRowMeta;
	}




}
