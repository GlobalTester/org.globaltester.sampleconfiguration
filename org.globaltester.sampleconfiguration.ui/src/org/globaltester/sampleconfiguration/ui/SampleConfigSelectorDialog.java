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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.sampleconfiguration.SampleConfig;

public class SampleConfigSelectorDialog extends Dialog implements INewConfigWizardClosedListener{

	private Shell dialog;
	private SampleConfigSelector selectorWidget;
	private SampleConfigEditorWidget editorWidget;
	private int status;
	protected SampleConfig selectedConfig;
	private Button runButton;
	private Button saveButton;
	private Button cancelButton;
	private boolean isValid;

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
		dialog.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(editorWidget.wasChanged()) {
					int returnCode = openSaveDialog(dialog, "Your SampleConfiguration has been changed.\n"
							+ "Would you like to save your changes to the SampleConfiguration?");
					if(returnCode == SWT.YES) {
						status = Window.CANCEL;
						editorWidget.doSave();
					}
				}
			}
		});
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
	}

	private void createButtons(Composite parent) {
		new Label(parent, SWT.NONE);
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		buttonComposite.setLayout(new GridLayout(3, true));
		
		GridData buttonLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		buttonLayoutData.widthHint = 80;
		
		runButton = new Button(buttonComposite, SWT.PUSH);
		runButton.setLayoutData(buttonLayoutData);
		runButton.setText("Run");
		runButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedConfig = selectorWidget.getSelectedConfig();
				if(editorWidget.wasChanged()) {
					int returnCode = openSaveDialog(parent, "Your SampleConfiguration has been changed.\n"
							+ "Would you like to save your changes to the SampleConfiguration?");
					if(returnCode == SWT.YES) {
						status = Window.OK;
						editorWidget.doSave();
						dialog.close();
					}
				} else {
					status = Window.OK;
					dialog.close();
				}
			}
		});
		
		saveButton = new Button(buttonComposite, SWT.PUSH);
		saveButton.setLayoutData(buttonLayoutData);
		saveButton.setText("Save");
		saveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int returnCode = openSaveDialog(parent, "Would you like to save your changes to the SampleConfiguration?");
				if(returnCode == SWT.YES) {
					editorWidget.doSave();
				}
			}
		});
		
		cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setLayoutData(buttonLayoutData);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
	}
	
	private int openSaveDialog(Composite parent, String message) {
		MessageBox mb = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setText("Save SampleConfiguration");
		mb.setMessage(message);
		return mb.open();
	}
	
	/**
	 * Update the buttons and editor widget
	 */
	private void update(){
		selectedConfig = selectorWidget.getSelectedConfig();
		if (getSelectedSampleConfig() == null){
			runButton.setEnabled(false);
			saveButton.setEnabled(false);
		} else {
			runButton.setEnabled(true);
			saveButton.setEnabled(true);
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
	
	public void setValid(boolean b) {
        boolean wasValid = isValid;
        isValid = b;
        if (wasValid != isValid) {
            update();
        }
    }
}
