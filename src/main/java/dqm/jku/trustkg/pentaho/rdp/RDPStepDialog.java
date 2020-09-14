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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.LabelText;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class RDPStepDialog extends BaseStepDialog implements StepDialogInterface {

	/**
	 * The PKG member is used when looking up internationalized strings. The
	 * properties file with localized keys is expected to reside in {the package of
	 * the class specified}/messages/messages_{locale}.properties
	 */
	private static Class<?> PKG = RDPStepMeta.class; // for i18n purposes

	private static final String CSV = "CSV";
	
	// this is the object the stores the step's settings
	// the dialog reads the settings from it when opening
	// the dialog writes the settings to it when confirmed
	private RDPStepMeta meta;

	// text field holding the name of the field to add to the row stream
	private LabelText wHelloFieldName;
	

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
	  GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    shell.setLayout(gridLayout);
		shell.setText(BaseMessages.getString(PKG, "Demo.Shell.Title"));
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label(shell, SWT.NONE);
		wlStepname.setText("Pattern file: ");
		fdlStepname = new FormData();
//		fdlStepname.left = new FormAttachment(0, 0);
//		fdlStepname.right = new FormAttachment(middle, -margin);
//		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.NONE);
		wStepname.setText(stepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
//		fdStepname.left = new FormAttachment(middle, 0);
//		fdStepname.top = new FormAttachment(0, margin);
//		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		wHelloFieldName = new LabelText(shell, BaseMessages.getString(PKG, "Demo.FieldName.Label"), null);
		props.setLook(wHelloFieldName);
		wHelloFieldName.addModifyListener(lsMod);
		FormData fdValName = new FormData();
		fdValName.left = new FormAttachment(0, 0);
		fdValName.right = new FormAttachment(100, 0);
		fdValName.top = new FormAttachment(wStepname, margin);
		wHelloFieldName.setLayoutData(fdValName);
		
		//dropdown for export
		Text label = new Text(shell, SWT.NONE);
		label.setText("Output-Type:");
		props.setLook(label);
     
		Combo combo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		String[] items = new String[] { CSV, "None" };
		combo.setItems(items);
		combo.select(1);
		combo.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
          int idx = combo.getSelectionIndex();
          String language = combo.getItem(idx);
          if (language.equals(CSV)) meta.setCSV(true);
          else meta.setCSV(false);
      }
  });


		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
		setButtonPositions(new Button[] { wOK, wCancel }, margin, combo);

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
		
		
		
		// default listener (for hitting "enter")
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};
		wStepname.addSelectionListener(lsDef);
		wHelloFieldName.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window and cancel the dialog
		// properly
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

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
		wStepname.selectAll();
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
		// The "stepname" variable will be the return value for the open() method.
		// Setting to step name from the dialog control
		stepname = wStepname.getText();
		// close the SWT dialog window
		dispose();
	}

}
