package dqm.jku.trustkg.pentaho.rdp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import dqm.jku.trustkg.pentaho.FileOutputType;

public class RDPStepDialog extends BaseStepDialog implements StepDialogInterface {

	/**
	 * The PKG member is used when looking up internationalized strings. The
	 * properties file with localized keys is expected to reside in {the package of
	 * the class specified}/messages/messages_{locale}.properties
	 */
	@SuppressWarnings("unused")
	private static Class<?> PKG = RDPStepMeta.class; // for i18n purposes

	// this is the object the stores the step's settings
	// the dialog reads the settings from it when opening
	// the dialog writes the settings to it when confirmed
	private RDPStepMeta meta;

	// text field holding the name of the field to add to the row stream
	private Text fileName;
	private Text fileLoc;
	private Text patternFileName;
	private Label lblNewLabel;
	private Label lblFileLocation;
	private Label lblNewLabel_1;
	private Button btnDirButton;
	private Combo combo;
	private Spinner spinner;
	private Button checkBoxOutput;

	public RDPStepDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) in, transMeta, stepname);
		meta = (RDPStepMeta) in;
	}

	@Override
	public String open() {
		// store some convenient SWT variables
		Shell parent = getParent();
		Display display = parent.getDisplay();

		// SWT code for preparing the dialog
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);

		// Save the value of the changed flag on the meta object. If the user cancels
		// the dialog, it will be restored to this saved value.
		// The "changed" variable is inherited from BaseStepDialog
		changed = meta.hasChanged();

		// The ModifyListener used on all controls. It will update the meta object to
		// indicate that changes are being made.
		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				meta.setChanged(); 
			}
		};

		// ------------------------------------------------------- //
		// SWT code for building the actual settings dialog //
		// ------------------------------------------------------- //
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "RDPStep.Shell.Title"));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		int margin = Const.MARGIN;

		checkBoxOutput = new Button(shell, SWT.CHECK);
		FormData fd_btnCheckButton = new FormData();
		fd_btnCheckButton.left = new FormAttachment(0, 10);
		checkBoxOutput.setLayoutData(fd_btnCheckButton);
		checkBoxOutput.setText("Enable File-Output");
		checkBoxOutput.setSelection(meta.isOutEnabled());

		lblNewLabel = new Label(shell, SWT.NONE);
		fd_btnCheckButton.bottom = new FormAttachment(lblNewLabel, -16);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Output Format");

		combo = new Combo(shell, SWT.NONE);
		combo.setItems(FileOutputType.getTypes());
		combo.select(combo.indexOf(meta.getType()));
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -10);
		fd_combo.bottom = new FormAttachment(lblNewLabel, 0, SWT.BOTTOM);
		combo.setLayoutData(fd_combo);

		
		lblFileLocation = new Label(shell, SWT.NONE);
		fd_lblNewLabel.bottom = new FormAttachment(lblFileLocation, -14);
		FormData fd_lblFileLocation = new FormData();
		fd_lblFileLocation.top = new FormAttachment(0, 155);
		fd_lblFileLocation.left = new FormAttachment(0, 10);
		lblFileLocation.setLayoutData(fd_lblFileLocation);
		lblFileLocation.setText("File Location");

		lblNewLabel_1 = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblFileLocation, 16);
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("File Name");

		fileName = new Text(shell, SWT.BORDER);
		fileName.setText(meta.getOutputFileName());
		FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(lblNewLabel_1, 0, SWT.BOTTOM);
		fd_text.left = new FormAttachment(lblFileLocation, 20);
		fd_text.right = new FormAttachment(100, -10);
		fileName.setLayoutData(fd_text);

		fileLoc = new Text(shell, SWT.BORDER);
		fileLoc.setText(meta.getOutputDirPath());
		FormData fd_text_1 = new FormData();
		fd_text_1.bottom = new FormAttachment(fileName, -10);
		fd_text_1.left = new FormAttachment(fileName, 0, SWT.LEFT);
		fileLoc.setLayoutData(fd_text_1);

		btnDirButton = new Button(shell, SWT.NONE);
		fd_text_1.right = new FormAttachment(btnDirButton, -6);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.bottom = new FormAttachment(lblFileLocation, 0, SWT.BOTTOM);
		fd_btnNewButton.right = new FormAttachment(100, -10);
		btnDirButton.setLayoutData(fd_btnNewButton);
		btnDirButton.setText("Directory...");

		Label lblNumberOfRows = new Label(shell, SWT.NONE);
		FormData fd_lblNumberOfRows = new FormData();
		fd_lblNumberOfRows.top = new FormAttachment(0, 25);
		fd_lblNumberOfRows.left = new FormAttachment(0, 10);
		lblNumberOfRows.setLayoutData(fd_lblNumberOfRows);
		lblNumberOfRows.setText("Number of Rows for RDP Size");

		spinner = new Spinner(shell, SWT.BORDER);
		FormData fd_spinner = new FormData();
		fd_spinner.bottom = new FormAttachment(lblNumberOfRows, 0, SWT.BOTTOM);
		fd_spinner.right = new FormAttachment(combo, 0, SWT.RIGHT);
		spinner.setLayoutData(fd_spinner);
		spinner.setMaximum(100000000);
		spinner.setMinimum(1);
		spinner.setSelection(meta.getRowCnt());
		
		Label lblPatternFile = new Label(shell, SWT.NONE);
		FormData fd_lblPatternFile = new FormData();
		fd_lblPatternFile.top = new FormAttachment(lblNumberOfRows, 12);
		fd_lblPatternFile.left = new FormAttachment(checkBoxOutput, 0, SWT.LEFT);
		lblPatternFile.setLayoutData(fd_lblPatternFile);
		lblPatternFile.setText("Pattern File");
		
		patternFileName = new Text(shell, SWT.BORDER);
		FormData fd_text_2 = new FormData();
		patternFileName.setText(meta.getFilePath());
		fd_text_2.left = new FormAttachment(lblPatternFile, 31);
		patternFileName.setLayoutData(fd_text_2);
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		fd_text_2.top = new FormAttachment(btnNewButton_1, -26);
		fd_text_2.bottom = new FormAttachment(btnNewButton_1, 0, SWT.BOTTOM);
		fd_text_2.right = new FormAttachment(btnNewButton_1, -6);
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.top = new FormAttachment(lblPatternFile, -5, SWT.TOP);
		fd_btnNewButton_1.right = new FormAttachment(combo, 0, SWT.RIGHT);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.setText("File...");
		
		
		setOutSettings(meta.isOutEnabled());

		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
		setButtonPositions(new Button[] { wOK, wCancel }, margin + 30, fileLoc);
		shell.pack();

		// Add listeners for cancel and OK
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};
		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
		
		checkBoxOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button button = (Button) event.widget;
        if (button.getSelection()) setOutSettings(true);
        else setOutSettings(false);
			}

		});
		

		// default listener (for hitting "enter")
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};
		wOK.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window and cancel the dialog
		// properly
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});
		
		// Listeners for meta changes
		fileName.addModifyListener(lsMod);
		fileLoc.addModifyListener(lsMod);
		patternFileName.addModifyListener(lsMod);
		combo.addModifyListener(lsMod);
		spinner.addModifyListener(lsMod);
		
		// Set/Restore the dialog size based on last position on screen
		// The setSize() method is inherited from BaseStepDialog
		setSize();

		// populate the dialog with the values from the meta object
		populateDialog();

		// restore the changed flag to original value, as the modify listeners fire
		// during dialog population
		meta.setChanged(changed);

		// open dialog and enter event loop
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// at this point the dialog has closed, so either ok() or cancel() have been
		// executed
		// The "stepname" variable is inherited from BaseStepDialog
		return stepname;
	}

	/**
	 * This helper method puts the step configuration stored in the meta object and
	 * puts it into the dialog controls.
	 */
	private void populateDialog() {
		// wStepname.selectAll();
	}

	/**
	 * Called when the user cancels the dialog.
	 */
	private void cancel() {
		// The "stepname" variable will be the return value for the open() method.
		// Setting to null to indicate that dialog was cancelled.
		stepname = null;
		// Restoring original "changed" flag on the met aobject
		meta.setChanged(changed);
		// close the SWT dialog window
		dispose();
	}

	/**
	 * Called when the user confirms the dialog
	 */
	private void ok() {
		meta.setCSV(combo.getText());
		meta.setFilePath(patternFileName.getText());
		meta.setOutputDirPath(fileLoc.getText());
		meta.setOutputFileName(fileName.getText());
		meta.setRowCnt(spinner.getSelection());
		meta.setOutEnabled(checkBoxOutput.getSelection());
		// close the SWT dialog window
		dispose();
	}
	
	private void setOutSettings(boolean setting) {
		lblNewLabel.setEnabled(setting);
		lblFileLocation.setEnabled(setting);
		lblNewLabel_1.setEnabled(setting);
		btnDirButton.setEnabled(setting);
		combo.setEnabled(setting);
		fileName.setEnabled(setting);
		fileLoc.setEnabled(setting);
	}
}
