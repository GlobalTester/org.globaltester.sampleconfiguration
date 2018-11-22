package org.globaltester.sampleconfiguration.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.globaltester.base.ui.GtUiHelper;
import org.globaltester.logging.legacy.logger.GtErrorLogger;
import org.globaltester.sampleconfiguration.SampleConfig;

public class SampleConfigSelector {

	public static final int BTN_DETAILS = 1;
	public static final int BTN_EDIT = 2;
	public static final int BTN_NEW = 4;
	public static final int ALL_BUTTONS = 7;
	private List<INewConfigWizardClosedListener> configWizardDoneListener;

	private Composite mainComp;
	private Combo configSelection;
	private String selectedConfigName;

	public SampleConfigSelector(Composite parent, int availableButtons) {
		this.createPartControl(parent, availableButtons);
		configWizardDoneListener = new ArrayList<INewConfigWizardClosedListener>();
	}

	private void createPartControl(Composite parent, int availableButtons) {
		mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout(5, false));

		Label lblSelectSampleConfigTo = new Label(mainComp, SWT.NONE);
		lblSelectSampleConfigTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblSelectSampleConfigTo.setText("Select SampleConfig to use:");

		configSelection = new Combo(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		configSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		String[] configs = SampleConfig.getAvailableConfigNames()
				.toArray(new String[] {});
		Arrays.sort(configs);
		configSelection.setItems(configs);
		configSelection.select(0);
		configSelection.setVisibleItemCount(5);

		addButtons(availableButtons);

	}

	private void addButtons(int availableButtons) {

		if ((availableButtons & BTN_DETAILS) == BTN_DETAILS) {
			Button btnDetails = new Button(mainComp, SWT.NONE);
			btnDetails.setText("Details");
			btnDetails.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					SampleConfigViewerDialog dialog = new SampleConfigViewerDialog(
							mainComp.getShell(), getSelectedConfig());
					dialog.open();
				}
			});
		}

		if ((availableButtons & BTN_EDIT) == BTN_EDIT) {
			Button btnEdit = new Button(mainComp, SWT.NONE);
			btnEdit.setText("Edit");
			btnEdit.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IFile fileToBeOpened = getSelectedConfig().getSampleConfigIfile();
					IEditorInput editorInput = new FileEditorInput(fileToBeOpened);
					IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					try {
						page.openEditor(editorInput, SampleConfigEditor.ID);
					} catch (PartInitException ex) {
						GtErrorLogger.log(Activator.PLUGIN_ID, ex);
					}
					
				}
			});
			       
		}

		if ((availableButtons & BTN_NEW) == BTN_NEW) {
			Button btnNew = new Button(mainComp, SWT.NONE);
			btnNew.setText("New");
			btnNew.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Set<String> configs = SampleConfig.getAvailableConfigNames();
					try {
						GtUiHelper
								.openWizard(Activator.NEW_SAMPLECONFIG_WIAZRD_ID);
					} catch (CoreException ex) {
						GtErrorLogger.log(Activator.PLUGIN_ID, ex);
					}
					Set<String> configsWithNewElement = SampleConfig.getAvailableConfigNames();
					String[] configNames = configsWithNewElement.toArray(new String[] {});
					
					configsWithNewElement.removeAll(configs);
					if (configNames.length > 0) {
						configSelection.setItems(configNames);
						setSelection(configsWithNewElement.iterator().next());
						informListeners();
					}
				}
			});
		}
	}

	public SampleConfig getSelectedConfig() {
		configSelection.getDisplay().syncExec(new Runnable() {
			public void run() {
				selectedConfigName = configSelection.getText();
			}
		});
		return SampleConfig.getSampleConfigForProject(selectedConfigName);
	}

	public void setLayoutData(Object layoutData) {
		mainComp.setLayoutData(layoutData);
	}

	public void addSelectionListener(SelectionListener selectionListener) {
		configSelection.addSelectionListener(selectionListener);
	}

	private void informListeners(){
		for (INewConfigWizardClosedListener listener: configWizardDoneListener){
			listener.wizardClosed();
		}
	}
	
	public void addNewSampleConfigDoneListener(INewConfigWizardClosedListener listener){
		configWizardDoneListener.add(listener);
	}

	public void setSelection(SampleConfig sampleConfig) {
		setSelection(sampleConfig.getName());
	}
	
	public void setSelection(String sampleConfigName) {
		int index = configSelection.indexOf(sampleConfigName);
		if (index >= 0) {
			configSelection.select(index);
		}
	}
	
}
