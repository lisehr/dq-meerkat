package dqm.jku.trustkg.pentaho.rdp;

import java.io.IOException;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import dqm.jku.trustkg.connectors.ConnectorPentaho;
import dqm.jku.trustkg.dsd.DSDKnowledgeGraph;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.pentaho.FileOutputType;
import dqm.jku.trustkg.util.RecordGenUtils;
import dqm.jku.trustkg.util.export.ExportUtil;

public class RDPStep extends BaseStep implements StepInterface {

	@SuppressWarnings("unused")
	private static final Class<?> PKG = RDPStepMeta.class; // i18n purposes
	private RecordList records;
	private RowMetaInterface meta;
	private Datasource ds;
	private int rowCnt;

	public RDPStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		records = new RecordList();
		rowCnt = 0;
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		// Casting to step-specific implementation classes is safe
		RDPStepMeta metaRDPStep = (RDPStepMeta) smi;
		RDPStepData data = (RDPStepData) sdi;
		if (!super.init((StepMetaInterface) metaRDPStep, data)) {
			return false;
		}
		// Add any step-specific initialization that may be needed here
		return true;
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		RDPStepMeta metaRDPStep = (RDPStepMeta) smi;
		Object[] r = getRow();

		if (rowCnt == metaRDPStep.getRowCnt() || r == null) {
			if (r != null) return true;
			this.logBasic("Information about Input Rows:");
			this.logBasic("RecordList size: " + records.size());
			this.logBasic("Rows converted, beginning Calculation of RDPs.");
			for (Concept c : ds.getConcepts()) {
				System.out.println(c.getURI());
				for (Attribute a : c.getAttributes()) {
					try {
						this.logBasic("Calculating Data Profile for Attribute: " + a.getLabel());
						this.logBasic(a.getDataTypeString());
						a.annotateProfile(records);
						this.logBasic(a.getProfileString());
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
					a.printAnnotatedProfile();
				}
				System.out.println();
			}
			if (metaRDPStep.isOutEnabled()) {
				switch (FileOutputType.asFileOutputType(metaRDPStep.getType())) {
				case csv:
					ExportUtil.exportToCSV(ds, metaRDPStep.getOutputDirPath(), metaRDPStep.getOutputFileName());
					break;
				case json:
					this.logBasic("Not implemented yet!");
					break;
				case text:
					DSDKnowledgeGraph kg = new DSDKnowledgeGraph("Pentaho");
					try {
						kg.addDatasource(ds);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ExportUtil.exportReport(kg, metaRDPStep.getOutputDirPath(), metaRDPStep.getOutputFileName());
					break;
				default:
					break;
				}
			}
			setOutputDone();
			return false;
		}
		// this.logBasic(r.toString());

		if (first) {
			meta = this.getInputRowMeta();
			ConnectorPentaho conn = new ConnectorPentaho();
			try {
				ds = conn.loadSchema("http://www.example.com/", "pentaho:", meta);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Record rec = RecordGenUtils.generateRecordFromFirstConceptObjectRow(ds, r, first, this);
			this.logBasic(rec.toString());
			records.addRecord(rec);
			first = false;
		} else records.addRecord(RecordGenUtils.generateRecordFromFirstConceptObjectRow(ds, r, first, null));
		rowCnt++;

		return true;
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		// Casting to step-specific implementation classes is safe
		RDPStepMeta meta = (RDPStepMeta) smi;
		RDPStepData data = (RDPStepData) sdi;
		// Add any step-specific initialization that may be needed here
		// Call superclass dispose()
		super.dispose(meta, data);
	}

}
