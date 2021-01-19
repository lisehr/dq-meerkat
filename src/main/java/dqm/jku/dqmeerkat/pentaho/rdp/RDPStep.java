package dqm.jku.dqmeerkat.pentaho.rdp;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import dqm.jku.dqmeerkat.connectors.ConnectorPentaho;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.pentaho.util.FileOutputType;
import dqm.jku.dqmeerkat.pentaho.util.PentahoRowUtils;
import dqm.jku.dqmeerkat.util.export.ExportUtil;

public class RDPStep extends BaseStep implements StepInterface {

	@SuppressWarnings("unused")
	private static final Class<?> PKG = RDPStepMeta.class; // i18n purposes
	private static final boolean DEBUG = false;
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
				RDPStepData data = (RDPStepData) sdi;
				boolean firstRow = true;
				for (Attribute a : c.getAttributes()) {
					try {
						this.logBasic("Calculating Data Profile for Attribute: " + a.getLabel());
						this.logBasic("Datatype:" + a.getDataTypeString());
						a.annotateProfile(records, metaRDPStep.getFilePath());
						if (a.getProfile().getMetrics().size() == 0) {						
							this.logBasic("Datatype:" + a.getDataTypeString() + " is not supported. Skipping calculation!");
						} else {
							List<ValueMetaAndData> values = PentahoRowUtils.createPentahoOutputRowMeta(a.getProfile());
							if (firstRow) {
								data.outputRowMeta = metaRDPStep.getOutputRowMeta().clone();
								firstRow = false;
							}
							Object[] objects = new Object[data.outputRowMeta.size()];
							int i = 0;
							for (ValueMetaAndData v : values) {
								objects[i] = v.getValueData();
								i++;
							}

							putRow(data.outputRowMeta, objects);
							if ((metaRDPStep.isOutEnabled() && metaRDPStep.isVerboseLogEnabled()) || !metaRDPStep.isOutEnabled()) this.logBasic(a.getProfileString());
							System.out.println(a.getDataType().getSimpleName() + "\t" + a.getURI());
							a.printAnnotatedProfile();
						}
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
				System.out.println();
			}
			if (metaRDPStep.isOutEnabled()) {
				switch (FileOutputType.asFileOutputType(metaRDPStep.getType())) {
				case csv:
					ExportUtil.exportToCSV(ds, metaRDPStep.getOutputDirPath(), metaRDPStep.getOutputFileName() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
					break;
				case json:
					this.logBasic("Not implemented yet!");
					break;
				case text:
					ExportUtil.exportReportOfDatasource(ds, metaRDPStep.getOutputDirPath(), metaRDPStep.getOutputFileName() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
					break;
				default:
					break;
				}
			}
			setOutputDone();
			return false;
		}
		if (DEBUG) this.logBasic(r.toString());

		if (first) {
			meta = this.getInputRowMeta();
			ConnectorPentaho conn = new ConnectorPentaho();
			try {
				ds = conn.loadSchema("http://www.example.com/", "pentaho:", meta);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Record rec = PentahoRowUtils.generateRecordFromFirstConceptObjectRow(ds, r, first, this, DEBUG);
			if (DEBUG) this.logBasic(rec.toString());
			records.addRecord(rec);
			first = false;
		} else records.addRecord(PentahoRowUtils.generateRecordFromFirstConceptObjectRow(ds, r, first, null, DEBUG));
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
