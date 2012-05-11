package org.globaltester.cardconfiguration.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.globaltester.cardconfiguration.CardConfig;
import org.globaltester.logging.logger.GtErrorLogger;

public class CardConfigEditorWidget {

	private Composite mainComp;
	private CardConfig cardConfig;
	private TabFolder tabFolder;
	private Text name;
	private Text descr;
	private Text pin;
	private Text mrz1;
	private Text mrz2;
	private Text mrz3;
	

	public CardConfigEditorWidget(Composite parent) {
		this.createPartControl(parent);
	}

	private void createPartControl(Composite parent) {
		mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout(2, false));
		
		tabFolder = new TabFolder(mainComp, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		addTabItemGeneral(tabFolder);
//		addTabItemCardReader(tabFolder);
		addTabItemPasswords(tabFolder);
		addTabItemsForProtocols(tabFolder);
		new Label(mainComp, SWT.NONE);

	}

	public void updateContents() {
		updateTabItemGeneral();
		updateTabItemReader();
		updateTabItemProtocols();
	}

	private void updateTabItemGeneral() {
		name.setText(getCardConfig().getName());
		descr.setText(getCardConfig().getDescription());
	}

	private void updateTabItemReader() {

	}

	private void updateTabItemProtocols() {
		String mrzString = (String) getCardConfig().get("ICAO9303", "MRZ");
		if (mrzString != null) {
			// use methods from MRZ class after refactoring to protocol
			mrz1.setText(mrzString.substring(0, 43));
			mrz2.setText(mrzString.substring(44));
			mrz3.setText("");
		}
	}

	private void addTabItemGeneral(TabFolder tabFolder) {
		// TODO Auto-generated method stub
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("General");

		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));

		Label lblName = new Label(tabItemComp, SWT.NONE);
		lblName.setText("Name:");
		name = new Text(tabItemComp, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDescription = new Label(tabItemComp, SWT.NONE);
		lblDescription.setText("Description:");
		
		descr = new Text(tabItemComp, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		descr.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

//	private void addTabItemCardReader(TabFolder tabFolder) {
//		// TODO Auto-generated method stub
//		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
//		tbtmNewItem.setText("Reader selection");
//	}

	private void addTabItemPasswords(TabFolder tabFolder) {
		// TODO Auto-generated method stub
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Passwords");
		
		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));

		Label lblPin = new Label(tabItemComp, SWT.NONE);
		lblPin.setText("PIN:");
		pin = new Text(tabItemComp, SWT.BORDER);
		GridData gdPin = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdPin.widthHint = 350; //TODO AMY: calculate this required width based on font width
		pin.setLayoutData(gdPin);
		
	}

	private void addTabItemsForProtocols(TabFolder tabFolder) {
		// TODO extract TabFolder for different protocols
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("ICAO9303");

		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));

		Label lblMrz1 = new Label(tabItemComp, SWT.NONE);
		lblMrz1.setText("MRZ (line 1):");
		mrz1 = new Text(tabItemComp, SWT.BORDER);
		GridData gdMrz1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdMrz1.widthHint = 350; //TODO AMY: calculate this required width based on font width
		mrz1.setLayoutData(gdMrz1);
		Label lblMrz2 = new Label(tabItemComp, SWT.NONE);
		lblMrz2.setText("MRZ (line 2):");
		mrz2 = new Text(tabItemComp, SWT.BORDER);
		GridData gdMrz2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdMrz2.widthHint = gdMrz1.widthHint;
		mrz2.setLayoutData(gdMrz2);
		Label lblMrz3 = new Label(tabItemComp, SWT.NONE);
		lblMrz3.setText("MRZ (line 3):");
		mrz3 = new Text(tabItemComp, SWT.BORDER);
		GridData gdMrz3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdMrz3.widthHint = gdMrz1.widthHint;
		mrz3.setLayoutData(gdMrz3);
		
	}

	public CardConfig getCardConfig() {
		return cardConfig;
	}

	public void doSave() {
		cardConfig.setDescription(descr.getText());
		
		// flush all changes to the CardConfig object
		cardConfig.put("ICAO9303", "MRZ", mrz1.getText() + mrz2.getText()
				+ mrz3.getText());

		// save the CardConfig
		try {
			cardConfig.saveToProject();
		} catch (CoreException e) {
			GtErrorLogger.log(Activator.PLUGIN_ID, e);
		}

	}

	public void setEditable(boolean editable) {
		name.setEditable(editable);
		descr.setEditable(editable);
		mrz1.setEditable(editable);
		mrz2.setEditable(editable);
		mrz3.setEditable(editable);
	}

	public void setNameEditable(boolean editable) {
		name.setEditable(editable);
	}

	public void setInput(CardConfig newInput) {
		this.cardConfig = newInput;
		if (cardConfig != null){
			updateContents();
		}
	}

	public void setLayoutData(Object layoutData) {
		mainComp.setLayoutData(layoutData);
	}

}
