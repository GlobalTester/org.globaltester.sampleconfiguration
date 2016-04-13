package org.globaltester.cardconfiguration.ui;

import java.util.ArrayList;
import java.util.List;

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
import org.globaltester.cardconfiguration.CardConfig;
import org.globaltester.cardconfiguration.CardConfigManager;
import org.globaltester.logging.legacy.logger.GtErrorLogger;

public class CardConfigSelector {

	public static final int BTN_DETAILS = 1;
	public static final int BTN_EDIT = 2;
	public static final int BTN_NEW = 4;
	public static final int ALL_BUTTONS = 7;
	private List<INewConfigWizardClosedListener> configWizardDoneListener;

	// TODO update contents of selection Combo on workspace refresh

	private Composite mainComp;
	private Combo configSelection;
	private String selectedConfigName;

	public CardConfigSelector(Composite parent, int availableButtons) {
		this.createPartControl(parent, availableButtons);
		configWizardDoneListener = new ArrayList<INewConfigWizardClosedListener>();
	}

	private void createPartControl(Composite parent, int availableButtons) {
		mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout(5, false));

		Label lblSelectCardconfigTo = new Label(mainComp, SWT.NONE);
		lblSelectCardconfigTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblSelectCardconfigTo.setText("Select CardConfig to use:");

		configSelection = new Combo(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		configSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		configSelection.setItems(CardConfigManager.getAvailableConfigNames()
				.toArray(new String[] {}));
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
					CardConfigViewerDialog dialog = new CardConfigViewerDialog(
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
					IFile fileToBeOpened = getSelectedConfig().getCardConfigIfile();
					IEditorInput editorInput = new FileEditorInput(fileToBeOpened);
					IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					try {
						page.openEditor(editorInput, CardConfigEditor.ID);
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
					try {
						GtUiHelper
								.openWizard(Activator.NEW_CARDCONFIG_WIAZRD_ID);
					} catch (CoreException ex) {
						GtErrorLogger.log(Activator.PLUGIN_ID, ex);
					}
					String[] configNames = CardConfigManager
							.getAvailableConfigNames().toArray(new String[] {});
					if (configNames.length > 0) {
						configSelection.setItems(configNames);
						configSelection.select(0);
						informListeners();
					}
				}
			});
		}
	}

	public CardConfig getSelectedConfig() {
		configSelection.getDisplay().syncExec(new Runnable() {
			public void run() {
				selectedConfigName = configSelection.getText();
			}
		});
		return CardConfigManager.get(selectedConfigName);
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
	
	public void addNewCardConfigDoneListener(INewConfigWizardClosedListener listener){
		configWizardDoneListener.add(listener);
	}

	/**
	 * If a cardConfig wit
	 * @param cardConfig
	 */
	public void setSelection(CardConfig cardConfig) {
		int index = configSelection.indexOf(cardConfig.getName());
		if (index >= 0) {
			configSelection.select(index);
		}
	}
	
}
