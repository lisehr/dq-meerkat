package dqm.jku.trustkg;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * This class serves the lonely purpose of designing dialog windows in the windowbuilder
 * @author alexg
 *
 */
public class testdialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;

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
		shell.setSize(450, 385);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Button btnCheckButton = new Button(shell, SWT.CHECK);
		FormData fd_btnCheckButton = new FormData();
		btnCheckButton.setLayoutData(fd_btnCheckButton);
		btnCheckButton.setText("Enable File-Output");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		fd_btnCheckButton.bottom = new FormAttachment(lblNewLabel, -19);
		fd_btnCheckButton.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Output Format");
		
		Combo combo = new Combo(shell, SWT.NONE);
		combo.setItems(new String[] {"CSV", "JSON", "txt"});
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		combo.setLayoutData(fd_combo);
		
		Label lblFileLocation = new Label(shell, SWT.NONE);
		fd_lblNewLabel.bottom = new FormAttachment(lblFileLocation, -19);
		FormData fd_lblFileLocation = new FormData();
		fd_lblFileLocation.left = new FormAttachment(0, 10);
		lblFileLocation.setLayoutData(fd_lblFileLocation);
		lblFileLocation.setText("File Location");
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("File Name");
		
		text = new Text(shell, SWT.BORDER);
		fd_combo.right = new FormAttachment(text, 0, SWT.RIGHT);
		fd_lblNewLabel_1.top = new FormAttachment(text, 2, SWT.TOP);
		FormData fd_text = new FormData();
		fd_text.left = new FormAttachment(lblNewLabel_1, 37);
		fd_text.right = new FormAttachment(100, -10);
		text.setLayoutData(fd_text);
		
		text_1 = new Text(shell, SWT.BORDER);
		fd_lblFileLocation.top = new FormAttachment(text_1, 2, SWT.TOP);
		FormData fd_text_1 = new FormData();
		fd_text_1.left = new FormAttachment(0, 114);
		fd_text_1.bottom = new FormAttachment(text, -19);
		text_1.setLayoutData(fd_text_1);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		fd_text_1.right = new FormAttachment(btnNewButton, -6);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.right = new FormAttachment(100, -10);
		fd_btnNewButton.top = new FormAttachment(text_1, -3, SWT.TOP);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("Directory...");
		
		Button btnOk = new Button(shell, SWT.NONE);
		fd_text.bottom = new FormAttachment(btnOk, -45);
		FormData fd_btnOk = new FormData();
		fd_btnOk.left = new FormAttachment(0, 126);
		fd_btnOk.bottom = new FormAttachment(100, -10);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -98);
		fd_btnCancel.bottom = new FormAttachment(btnOk, 0, SWT.BOTTOM);
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
		fd_spinner.right = new FormAttachment(100, -10);
		fd_spinner.bottom = new FormAttachment(lblNumberOfRows, 0, SWT.BOTTOM);
		spinner.setLayoutData(fd_spinner);
		spinner.setMaximum(100000000);
		
		Label lblPatternFile = new Label(shell, SWT.NONE);
		FormData fd_lblPatternFile = new FormData();
		fd_lblPatternFile.top = new FormAttachment(lblNumberOfRows, 12);
		fd_lblPatternFile.left = new FormAttachment(btnCheckButton, 0, SWT.LEFT);
		lblPatternFile.setLayoutData(fd_lblPatternFile);
		lblPatternFile.setText("Pattern File");
		
		text_2 = new Text(shell, SWT.BORDER);
		FormData fd_text_2 = new FormData();
		fd_text_2.left = new FormAttachment(lblPatternFile, 31);
		text_2.setLayoutData(fd_text_2);
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		fd_text_2.top = new FormAttachment(btnNewButton_1, -26);
		fd_text_2.bottom = new FormAttachment(btnNewButton_1, 0, SWT.BOTTOM);
		fd_text_2.right = new FormAttachment(btnNewButton_1, -6);
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.top = new FormAttachment(lblPatternFile, -5, SWT.TOP);
		fd_btnNewButton_1.right = new FormAttachment(combo, 0, SWT.RIGHT);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.setText("File...");

	}
}
