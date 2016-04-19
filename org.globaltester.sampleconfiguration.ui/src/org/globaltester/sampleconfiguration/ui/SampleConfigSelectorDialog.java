package org.globaltester.sampleconfiguration.ui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.sampleconfiguration.SampleConfig;

public class SampleConfigSelectorDialog extends Dialog implements INewConfigWizardClosedListener{

	private Shell dialog;
	private SampleConfigSelector selectorWidget;
	private SampleConfigEditorWidget editorWidget;
	private int status;
	protected SampleConfig selectedConfig;
	private Button okButton;

	public SampleConfigSelectorDialog(Shell parentShell) {
		super(parentShell);
	}

	public int open() {
		createDialog();
		
		// get notified, if the new sample configuration wizard is closed
		selectorWidget.addNewSampleConfigDoneListener(this);
		
		status = Window.CANCEL;

		dialog.open();
		Display display = getParent().getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return status;
	}

	private void createDialog() {
		dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText("Select SampleConfig");
		configureLayout(dialog);
		createSelector(dialog);
		createEditorWidget(dialog);
		createButtons(dialog);
		dialog.setSize(dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		update();
	}

	private void configureLayout(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
	}

	private void createSelector(Composite parent) {
		selectorWidget = new SampleConfigSelector(parent, SampleConfigSelector.BTN_NEW);
		selectorWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		selectorWidget.addSelectionListener(new SelectionListener() {
		      public void widgetSelected(SelectionEvent e) {
		        update();
		      }

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		    });
		selectedConfig = selectorWidget.getSelectedConfig();
	}

	private void createEditorWidget(Composite parent) {
		editorWidget = new SampleConfigEditorWidget(parent);
		editorWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//		editorWidget.setEditable(false);
	}

	private void createButtons(Composite parent) {
		new Label(parent, SWT.NONE);
		
		okButton = new Button(parent, SWT.PUSH);
		okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				status = Window.OK;
				selectedConfig = selectorWidget.getSelectedConfig();
				editorWidget.doSave();
				dialog.close();
			}
		});
	}
	
	/**
	 * Update the buttons and editor widget
	 */
	private void update(){
		selectedConfig = selectorWidget.getSelectedConfig();
		if (getSelectedSampleConfig() == null){
			okButton.setEnabled(false);
		} else {
			okButton.setEnabled(true);
		}
		editorWidget.setInput(getSelectedSampleConfig());
	}

	public SampleConfig getSelectedSampleConfig() {
		return selectedConfig;
	}

	@Override
	public void wizardClosed() {
		update();
	}
}
