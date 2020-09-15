package dqm.jku.trustkg;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class testdialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text text;
	private Text text_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public testdialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Button btnCheckButton = new Button(shell, SWT.CHECK);
		FormData fd_btnCheckButton = new FormData();
		fd_btnCheckButton.left = new FormAttachment(0, 10);
		btnCheckButton.setLayoutData(fd_btnCheckButton);
		btnCheckButton.setText("Enable File-Output");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		fd_btnCheckButton.bottom = new FormAttachment(lblNewLabel, -16);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Output Format");
		
		Combo combo = new Combo(shell, SWT.NONE);
		combo.setItems(new String[] {"CSV", "JSON", "txt"});
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -10);
		fd_combo.bottom = new FormAttachment(lblNewLabel, 0, SWT.BOTTOM);
		combo.setLayoutData(fd_combo);
		
		Label lblFileLocation = new Label(shell, SWT.NONE);
		fd_lblNewLabel.bottom = new FormAttachment(lblFileLocation, -14);
		FormData fd_lblFileLocation = new FormData();
		fd_lblFileLocation.top = new FormAttachment(0, 155);
		fd_lblFileLocation.left = new FormAttachment(0, 10);
		lblFileLocation.setLayoutData(fd_lblFileLocation);
		lblFileLocation.setText("File Location");
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblFileLocation, 16);
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("File Name");
		
		text = new Text(shell, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(lblNewLabel_1, 0, SWT.BOTTOM);
		fd_text.left = new FormAttachment(lblFileLocation, 20);
		fd_text.right = new FormAttachment(100, -10);
		text.setLayoutData(fd_text);
		
		text_1 = new Text(shell, SWT.BORDER);
		FormData fd_text_1 = new FormData();
		fd_text_1.bottom = new FormAttachment(text, -10);
		fd_text_1.left = new FormAttachment(text, 0, SWT.LEFT);
		text_1.setLayoutData(fd_text_1);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		fd_text_1.right = new FormAttachment(btnNewButton, -6);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.bottom = new FormAttachment(lblFileLocation, 0, SWT.BOTTOM);
		fd_btnNewButton.right = new FormAttachment(100, -10);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("Directory...");
		
		Button btnOk = new Button(shell, SWT.NONE);
		FormData fd_btnOk = new FormData();
		fd_btnOk.bottom = new FormAttachment(100, -10);
		fd_btnOk.right = new FormAttachment(btnCheckButton, 0, SWT.RIGHT);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(btnOk, 0, SWT.BOTTOM);
		fd_btnCancel.right = new FormAttachment(text_1, 0, SWT.RIGHT);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		Label lblNumberOfRows = new Label(shell, SWT.NONE);
		FormData fd_lblNumberOfRows = new FormData();
		fd_lblNumberOfRows.top = new FormAttachment(0, 25);
		fd_lblNumberOfRows.left = new FormAttachment(0, 10);
		lblNumberOfRows.setLayoutData(fd_lblNumberOfRows);
		lblNumberOfRows.setText("Number of Rows for RDP Size");
		
		Spinner spinner = new Spinner(shell, SWT.BORDER);
		FormData fd_spinner = new FormData();
		fd_spinner.bottom = new FormAttachment(lblNumberOfRows, 0, SWT.BOTTOM);
		fd_spinner.right = new FormAttachment(combo, 0, SWT.RIGHT);
		spinner.setLayoutData(fd_spinner);
		spinner.setMaximum(100000000);

	}
}
