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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
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
	private Button btnEnableVerboseLogging;

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

		combo = new Combo(shell, SWT.READ_ONLY);
		combo.setItems(FileOutputType.getTypes());
		combo.select(combo.indexOf(meta.getType()));
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -10);
		fd_combo.bottom = new FormAttachment(lblNewLabel, 0, SWT.BOTTOM);
		combo.setLayoutData(fd_combo);

		lblFileLocation = new Label(shell, SWT.NONE);
		FormData fd_lblFileLocation = new FormData();
		fd_lblFileLocation.top = new FormAttachment(lblNewLabel, 17);
		fd_lblFileLocation.left = new FormAttachment(checkBoxOutput, 0, SWT.LEFT);
		lblFileLocation.setLayoutData(fd_lblFileLocation);
		lblFileLocation.setText("File Location");

		lblNewLabel_1 = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblFileLocation, 20);
		fd_lblNewLabel_1.left = new FormAttachment(checkBoxOutput, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("File Name");

		fileName = new Text(shell, SWT.BORDER);
		fileName.setText(meta.getOutputFileName());
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(lblNewLabel_1, -2, SWT.TOP);
		fd_text.left = new FormAttachment(lblNewLabel_1, 37);
		fd_text.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fileName.setLayoutData(fd_text);

		fileLoc = new Text(shell, SWT.BORDER);
		fileLoc.setText(meta.getOutputDirPath());
		FormData fd_text_1 = new FormData();
		fd_text_1.left = new FormAttachment(lblFileLocation, 20);
		fd_text_1.top = new FormAttachment(lblFileLocation, -2, SWT.TOP);
		fileLoc.setLayoutData(fd_text_1);

		btnDirButton = new Button(shell, SWT.NONE);
		fd_text_1.right = new FormAttachment(100, -98);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(lblFileLocation, -5, SWT.TOP);
		fd_btnNewButton.right = new FormAttachment(combo, 0, SWT.RIGHT);
		btnDirButton.setLayoutData(fd_btnNewButton);
		btnDirButton.setText("Browse...");
		btnDirButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);

				// Set the initial filter path according
				// to anything they've selected or typed in
				dlg.setFilterPath(fileLoc.getText());

				// Change the title bar text
				dlg.setText("Selection of Output Directory");

				// Customizable message displayed in the dialog
				dlg.setMessage("Select a directory");

				// Calling open() will open and run the dialog.
				// It will return the selected directory, or
				// null if user cancels
				String dir = dlg.open();
				if (dir != null) {
					// Set the text box to the new selection
					fileLoc.setText(dir);
				}
			}
		});

		Label lblNumberOfRows = new Label(shell, SWT.NONE);
		FormData fd_lblNumberOfRows = new FormData();
		fd_lblNumberOfRows.top = new FormAttachment(0, 25);
		fd_lblNumberOfRows.left = new FormAttachment(0, 10);
		lblNumberOfRows.setLayoutData(fd_lblNumberOfRows);
		lblNumberOfRows.setText("Number of Rows for RDP Size");

		spinner = new Spinner(shell, SWT.BORDER);
		FormData fd_spinner = new FormData();
		fd_spinner.right = new FormAttachment(100, -10);
		fd_spinner.bottom = new FormAttachment(lblNumberOfRows, 0, SWT.BOTTOM);
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

		Button patternDirButton = new Button(shell, SWT.NONE);
		fd_text_2.top = new FormAttachment(patternDirButton, -26);
		fd_text_2.bottom = new FormAttachment(patternDirButton, 0, SWT.BOTTOM);
		fd_text_2.right = new FormAttachment(patternDirButton, -6);
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.right = new FormAttachment(100, -10);
		fd_btnNewButton_1.top = new FormAttachment(lblPatternFile, -5, SWT.TOP);
		patternDirButton.setLayoutData(fd_btnNewButton_1);
		patternDirButton.setText("Browse...");
		patternDirButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.in", "*.*" };
				fd.setFilterExtensions(filterExt);
				String file = fd.open();
				if (file != null) patternFileName.setText(file);
			}
		});

		btnEnableVerboseLogging = new Button(shell, SWT.CHECK);
		fd_lblNewLabel.top = new FormAttachment(btnEnableVerboseLogging, 15);
		fd_btnCheckButton.bottom = new FormAttachment(btnEnableVerboseLogging, -6);
		btnEnableVerboseLogging.setText("Enable Verbose Logging");
		btnEnableVerboseLogging.setSelection(meta.isVerboseLogEnabled());
		FormData fd_btnEnableVerboseLogging = new FormData();
		fd_btnEnableVerboseLogging.top = new FormAttachment(0, 144);
		fd_btnEnableVerboseLogging.left = new FormAttachment(checkBoxOutput, 0, SWT.LEFT);
		btnEnableVerboseLogging.setLayoutData(fd_btnEnableVerboseLogging);

		setOutSettings(meta.isOutEnabled());

		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
		setButtonPositions(new Button[] { wOK, wCancel }, margin + 20, fileLoc);
		shell.pack();

		shell.setTabList(new Control[] { spinner, patternFileName, patternDirButton, checkBoxOutput, btnEnableVerboseLogging, combo, fileLoc, btnDirButton, fileName, wOK, wCancel });

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
				meta.setChanged();
			}
		});

		btnEnableVerboseLogging.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				meta.setChanged();
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
		meta.setVerboseLogEnabled(btnEnableVerboseLogging.getSelection());
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
		btnEnableVerboseLogging.setEnabled(setting);
	}
}
