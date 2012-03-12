package org.globaltester.cardconfiguration.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.globaltester.cardconfiguration.CardConfig;
import org.globaltester.cardconfiguration.CardConfigManager;

public class CardConfigSelector {
	
	//TODO update contents of selection Combo on workspace refresh

	private Composite parent;
	private Combo configSelection;
	private String selectedConfigName;

	public CardConfigSelector(Composite parent) {
		this.parent = parent;
		this.createPartControl();
	}

	private void createPartControl() {
		parent.setLayout(new GridLayout(4, false));
		
		Label lblSelectCardconfigTo = new Label(parent, SWT.NONE);
		lblSelectCardconfigTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSelectCardconfigTo.setText("Select CardConfig to use:");
		
		configSelection = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		configSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		configSelection.setItems(CardConfigManager.getAvailableConfigNames()
				.toArray(new String[] {}));
		configSelection.select(0);
		configSelection.setVisibleItemCount(5);

		Button btnDetails = new Button(parent, SWT.NONE);
		btnDetails.setText("Details");
		//FIXME AMY CardConfig implement viewer for CardConfig here
		
		Button btnEdit = new Button(parent, SWT.NONE);
		btnEdit.setText("Edit");
		//FIXME AMY CardConfig implement editor for CardConfig here
		

	}

	public CardConfig getSelectedConfig() {
		configSelection.getDisplay().syncExec(new Runnable() {
            public void run() {
            	selectedConfigName = configSelection.getText();
            }
         });
		return CardConfigManager.get(selectedConfigName);
	}

}
