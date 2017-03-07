package org.globaltester.sampleconfiguration.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.sampleconfiguration.SampleConfig;

public class SampleConfigViewerDialog extends Dialog {

	private SampleConfig sampleConfig;
	private SampleConfigEditorWidget editorWidget;
	private Shell dialog;

	public SampleConfigViewerDialog(Shell parentShell,
			SampleConfig selectedSampleConfig) {
		super(parentShell);
		sampleConfig = selectedSampleConfig;
	}

	public String open() {
		createDialog();

		dialog.open();
		Display display = getParent().getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return "";
	}

	private void createDialog() {
		dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		dialog.setText(sampleConfig.getName());
		configureLayout(dialog);
		createEditorWidget(dialog);
		createOkButton(dialog);
		dialog.setSize(dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
	}

	private void configureLayout(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
	}

	private void createEditorWidget(Composite parent) {
		editorWidget = new SampleConfigEditorWidget(parent);
		editorWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		editorWidget.setInput(sampleConfig);
		editorWidget.setActive(false);
	}

	private void createOkButton(Composite parent) {
		new Label(parent, SWT.NONE);
		
		Button okButton = new Button(parent, SWT.PUSH);
		okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
	}
}
