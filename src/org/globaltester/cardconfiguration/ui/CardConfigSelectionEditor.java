package org.globaltester.cardconfiguration.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.globaltester.cardconfiguration.CardConfig;
import org.globaltester.cardconfiguration.CardConfigManager;

public class CardConfigSelectionEditor {

	private ICardSelectionListener editor;

	private Composite parent;
	private Combo configSelection;
	private TabFolder tabFolder;
	private Text name;
	private Text mrz1;
	private Text mrz2;
	private Text mrz3;

	private boolean dirty = false;

	public CardConfigSelectionEditor(Composite parent,
			ICardSelectionListener editor) {
		this.parent = parent;
		this.editor = editor;

		this.createPartControl();
	}

	private void createPartControl() {
		configSelection = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		configSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		configSelection.setItems(CardConfigManager.getAvailableConfigNames()
				.toArray(new String[] {}));
		configSelection.select(0);
		configSelection.setVisibleItemCount(5);
		configSelection.addSelectionListener(new SelectionAdapter() {
			
		      public void widgetSelected(SelectionEvent e) {
		          cardConfigSelectionChanged();
		        }
		      });

		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		addTabItemGeneral(tabFolder);
		addTabItemCardReader(tabFolder);
		addTabItemsForProtocols(tabFolder);
		
		//update all widgets with content from selected CardConfig
		cardConfigSelectionChanged();

	}

	protected void cardConfigSelectionChanged() {
		updateTabItemGeneral();
		updateTabItemProtocols();
		
		//inform the editor about that change
		editor.cardConfigSelectionChanged();
		
	}

	private void updateTabItemGeneral() {
		name.setText(getSelectedConfig().getName());
	}
	
	private void updateTabItemProtocols() {
		String mrzString = (String) getSelectedConfig().get("ICAO9303", "MRZ");
		//use methods from MRZ class after refactoring to protocol
		mrz1.setText(mrzString.substring(0, 40));
		mrz2.setText(mrzString.substring(41));
		mrz3.setText("");
	}

	private void addTabItemGeneral(TabFolder tabFolder) {
		// TODO Auto-generated method stub
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("General");

		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));

		Label lblName = new Label(tabItemComp, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblName.setText("Name:");
		name = new Text(tabItemComp, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	private void addTabItemCardReader(TabFolder tabFolder) {
		// TODO Auto-generated method stub
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Reader selection");
	}

	private void addTabItemsForProtocols(TabFolder tabFolder) {
		// TODO extract TabFolder for different protocols
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("ICAO9303");

		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));

		Label lblMrz1 = new Label(tabItemComp, SWT.NONE);
		lblMrz1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblMrz1.setText("MRZ (line 1):");
		mrz1 = new Text(tabItemComp, SWT.BORDER);
		mrz1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label lblMrz2 = new Label(tabItemComp, SWT.NONE);
		lblMrz2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblMrz2.setText("MRZ (line 2):");
		mrz2 = new Text(tabItemComp, SWT.BORDER);
		mrz2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label lblMrz3 = new Label(tabItemComp, SWT.NONE);
		lblMrz3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblMrz3.setText("MRZ (line 3):");
		mrz3 = new Text(tabItemComp, SWT.BORDER);
		mrz3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	public CardConfig getSelectedConfig() {
		return CardConfigManager.get(configSelection.getText());
	}

	public void doSave() {
		CardConfig curConfig = getSelectedConfig();

		// flush all changes to the CardConfig object
		// curConfig.put("ICAO9303", "MRZ",
		// mrz1.getText()+mrz2.getText()+mrz3.getText());

		// save the CardConfig
		curConfig.doSave();

	}

	public boolean isDirty() {
		return dirty ;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		editor.selectedCardConfigDirty(dirty);
	}

}
