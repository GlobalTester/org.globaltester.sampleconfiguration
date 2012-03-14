package org.globaltester.cardconfiguration.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.globaltester.cardconfiguration.CardConfig;
import org.globaltester.cardconfiguration.CardConfigManager;

public class CardConfigSelector {

	// TODO update contents of selection Combo on workspace refresh

	private Composite mainComp;
	private Combo configSelection;
	private String selectedConfigName;

	public CardConfigSelector(Composite parent) {
		this.createPartControl(parent);
	}

	private void createPartControl(Composite parent) {
		mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout(4, false));
		
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

		Button btnDetails = new Button(mainComp, SWT.NONE);
		btnDetails.setText("Details");
		btnDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CardConfigViewerDialog dialog = new CardConfigViewerDialog(mainComp.getShell(), getSelectedConfig());
				dialog.open();
			}
		});

		Button btnEdit = new Button(mainComp, SWT.NONE);
		btnEdit.setText("Edit");
		// FIXME AMY CardConfig implement editor for CardConfig here

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

}
