package org.globaltester.sampleconfiguration.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.globaltester.logging.legacy.logger.GtErrorLogger;
import org.globaltester.protocol.ProtocolFactory;
import org.globaltester.protocol.parameter.ProtocolParameterDescription;
import org.globaltester.protocol.ui.ProtocolParameterEditor;
import org.globaltester.protocol.ui.ProtocolParameterEditorFactory;
import org.globaltester.sampleconfiguration.SampleConfig;

public class SampleConfigEditorWidget {

	private static final Font monospacedFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
	
	private Composite mainComp;
	private SampleConfig sampleConfig;
	private TabFolder tabFolder;
	private Text name;
	private Text txtPlatformId;
	private Text txtSampleId;
	private Text descr;
	private Text pin;
	private Text puk;
	private Text mrz1;
	private Text mrz2;
	private Text mrz3;

	private List<ProtocolParameterEditor> paramEditors = new ArrayList<>();
	

	public SampleConfigEditorWidget(Composite parent) {
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
		addTabItemMrz(tabFolder);
		addTabItemsForProtocols(tabFolder);
		new Label(mainComp, SWT.NONE);
	}

	public void updateContents() {
		updateTabItemGeneral();
		updateTabItemReader();
		updateTabItemPasswords();
		updateTabItemMrz();
		updateProtocolParameterEditors();
	}

	private void updateTabItemGeneral() {
		name.setText(getSampleConfig().getName());
		descr.setText(getSampleConfig().getDescription());
		if (getSampleConfig().getPlatformId() != null)
			txtPlatformId.setText(getSampleConfig().getPlatformId());
		if (getSampleConfig().getSampleId() != null)
			txtSampleId.setText(getSampleConfig().getSampleId());
	}

	private void updateTabItemReader() {

	}

	private void updateTabItemPasswords() {
		String pinString = getSampleConfig().get("PASSWORDS", "PIN");
		if (pinString != null) {
			pin.setText(pinString);
		}
		
		String pukString = getSampleConfig().get("PASSWORDS", "PUK");
		if (pukString != null) {
			puk.setText(pukString);
		}
	}

	private void updateTabItemMrz() {
		String mrzString = getSampleConfig().get("MRZ", "MRZ");
		if (mrzString != null) {

			// TODO use methods from MRZ class after refactoring to protocol
			switch (mrzString.length()) {
			case 90: //ID-1 / TD-1
				mrz1.setText(mrzString.substring(0, 30));
				mrz2.setText(mrzString.substring(30, 60));
				mrz3.setText(mrzString.substring(60));
				break;
			case 72: //ID-2 / TD-2
				mrz1.setText(mrzString.substring(0, 36));
				mrz2.setText(mrzString.substring(36));
				mrz3.setText("");
				break;
			case 88: //ID-3 / TD-3
				mrz1.setText(mrzString.substring(0, 44));
				mrz2.setText(mrzString.substring(44));
				mrz3.setText("");
				break;

			default: //unkown format
				mrz1.setText(mrzString);
				mrz2.setText("");
				mrz3.setText("");
				break;
			}
		} else {
			mrz1.setText("");
			mrz2.setText("");
			mrz3.setText("");
		}
	}

	private void updateProtocolParameterEditors() {
		
		for (ProtocolParameterEditor curParamEditor : paramEditors) {
			String protocolName = curParamEditor.getProtocolParameterDescription().getProtocolName();
			String paramName = curParamEditor.getProtocolParameterDescription().getName();
			
			String newValue = sampleConfig.get(protocolName, paramName);
			if (newValue != null) {
				curParamEditor.setValue(newValue);
			}
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
		
		GridData gdReport = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		Label lblPlatformId = new Label(tabItemComp, SWT.NONE);
		lblPlatformId.setText("Platform ID");
		txtPlatformId = new Text(tabItemComp, SWT.BORDER);
		txtPlatformId.setFont(monospacedFont);
		txtPlatformId.setLayoutData(gdReport);
		
		Label lblSampleId = new Label(tabItemComp, SWT.NONE);
		lblSampleId.setText("Sample ID");
		txtSampleId = new Text(tabItemComp, SWT.BORDER);
		txtSampleId.setFont(monospacedFont);
		txtSampleId.setLayoutData(gdReport);
		
		Label lblDescription = new Label(tabItemComp, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.TOP, SWT.LEFT, false, false, 1, 1));
		lblDescription.setText("Description:");
		
		descr = new Text(tabItemComp, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		GridData lblDescrGd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		lblDescrGd.heightHint = 50;
		descr.setLayoutData(lblDescrGd);
	}

//	private void addTabItemCardReader(TabFolder tabFolder) {
//		// TODO Auto-generated method stub
//		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
//		tbtmNewItem.setText("Reader selection");
//	}

	private void addTabItemPasswords(TabFolder tabFolder) {
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Passwords");
		
		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));
		GridData gdPassword = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);

		Label lblPin = new Label(tabItemComp, SWT.NONE);
		lblPin.setText("PIN:");
		pin = new Text(tabItemComp, SWT.BORDER);
		pin.setFont(monospacedFont);
		pin.setLayoutData(gdPassword);
		
		Label lblPuk = new Label(tabItemComp, SWT.NONE);
		lblPuk.setText("PUK:");
		puk = new Text(tabItemComp, SWT.BORDER);
		puk.setFont(monospacedFont);
		puk.setLayoutData(gdPassword);
	}

	private void addTabItemMrz(TabFolder tabFolder) {
		// TODO extract TabFolder for different protocols
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("MRZ");

		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));
		
		Label lblMrz1 = new Label(tabItemComp, SWT.NONE);
		lblMrz1.setText("MRZ (line 1):");
		mrz1 = new Text(tabItemComp, SWT.BORDER);
		mrz1.setFont(monospacedFont);
		GridData gdMrz1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		mrz1.setLayoutData(gdMrz1);
		Label lblMrz2 = new Label(tabItemComp, SWT.NONE);
		lblMrz2.setText("MRZ (line 2):");
		mrz2 = new Text(tabItemComp, SWT.BORDER);
		mrz2.setFont(monospacedFont);
		GridData gdMrz2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		mrz2.setLayoutData(gdMrz2);
		Label lblMrz3 = new Label(tabItemComp, SWT.NONE);
		lblMrz3.setText("MRZ (line 3):");
		mrz3 = new Text(tabItemComp, SWT.BORDER);
		mrz3.setFont(monospacedFont);
		GridData gdMrz3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		mrz3.setLayoutData(gdMrz3);
		
		//calculate width hint
		GC gc = new GC(mrz1);
	    FontMetrics fm = gc.getFontMetrics();
	    int charWidth = fm.getAverageCharWidth();
	    gc.dispose();
		gdMrz1.widthHint = charWidth * 45;
		gdMrz2.widthHint = gdMrz1.widthHint;
		gdMrz3.widthHint = gdMrz1.widthHint;
	}

	private void addTabItemsForProtocols(TabFolder tabFolder) {
		ProtocolFactory[] pFactories = org.globaltester.protocol.Activator.getAvailableProtocolFactories();
		
		//sort protocol factories by name
		Arrays.sort(pFactories, new Comparator<ProtocolFactory>() {
			@Override
			public int compare(ProtocolFactory factory1, ProtocolFactory factory2) {
				return factory1.getName().compareToIgnoreCase(factory2.getName());
			}
		});
		
		for (ProtocolFactory curProtocolFactory : pFactories) {
			if (curProtocolFactory == null) continue;
			
			createProtcolTabItem(tabFolder, curProtocolFactory);
		}		
	}

	private TabItem createProtcolTabItem(TabFolder tabFolder, ProtocolFactory curProtocolFactory) {
		TabItem curTabItem = new TabItem(tabFolder, SWT.NONE);
		curTabItem.setText(curProtocolFactory.getName());
		
		Composite tabItemComp = new Composite(tabFolder, SWT.NONE);
		curTabItem.setControl(tabItemComp);
		tabItemComp.setLayout(new GridLayout(2, false));
			
		for (ProtocolParameterDescription curParamDescriptor : curProtocolFactory.getParameterDescriptors()) {
			if (curParamDescriptor != null) {
				paramEditors.add(ProtocolParameterEditorFactory.createEditor(tabItemComp, curParamDescriptor));
			}
		}
		
		return curTabItem;
	}

	public SampleConfig getSampleConfig() {
		return sampleConfig;
	}

	public void doSave() {
		sampleConfig.setDescription(descr.getText());
		sampleConfig.setName(name.getText());
		sampleConfig.setSampleId(txtSampleId.getText());
		sampleConfig.setPlatformId(txtPlatformId.getText());
		
		// flush all changes to the SampleConfig object
		sampleConfig.put("PASSWORDS", "PIN", pin.getText());
		sampleConfig.put("PASSWORDS", "PUK", puk.getText());
		
		sampleConfig.put("MRZ", "MRZ", mrz1.getText() + mrz2.getText()
				+ mrz3.getText());
		
		//flush all ProtocolParameter values to the SampleConfig object
		for (ProtocolParameterEditor curParam : paramEditors) {
			String protocolName = curParam.getProtocolParameterDescription().getProtocolName();
			String paramName = curParam.getProtocolParameterDescription().getName();
			String paramValue = curParam.getValue();
			if (paramValue != null) {
				sampleConfig.put(protocolName, paramName, paramValue);
			}
		}
		

		// save the SampleConfig
		try {
			sampleConfig.saveToProject();
		} catch (CoreException e) {
			GtErrorLogger.log(Activator.PLUGIN_ID, e);
		}

	}
	
	public boolean wasChanged(){
		if(!sampleConfig.getDescription().equals(descr.getText())) {
			return true;
		}
		if(!sampleConfig.get("PASSWORDS", "PIN").equals(pin.getText())) {
			return true;
		}
		if(!sampleConfig.get("PASSWORDS", "PUK").equals(puk.getText())) {
			return true;
		}
		if(!sampleConfig.get("MRZ", "MRZ").equals(mrz1.getText() + mrz2.getText() + mrz3.getText())) {
			return true;
		}
		if(!sampleConfig.getSampleId().equals(txtSampleId.getText())) {
			return true;
		}
		if(!sampleConfig.getPlatformId().equals(txtPlatformId.getText())) {
			return true;
		}
		if(!sampleConfig.getName().equals(name.getText())) {
			return true;
		}
		for (ProtocolParameterEditor curParam : paramEditors) {
			String protocolName = curParam.getProtocolParameterDescription().getProtocolName();
			String paramName = curParam.getProtocolParameterDescription().getName();
			String paramValue = curParam.getValue();
			if (paramValue != null) {
				String sampleConfigParameter = sampleConfig.get(protocolName, paramName);
				
				if(sampleConfigParameter != null && !sampleConfigParameter.equals(paramValue)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public void setEditable(boolean editable) {
		name.setEditable(editable);
		descr.setEditable(editable);
		pin.setEditable(editable);
		puk.setEditable(editable);
		mrz1.setEditable(editable);
		mrz2.setEditable(editable);
		mrz3.setEditable(editable);
		txtPlatformId.setEditable(editable);
		txtSampleId.setEditable(editable);
		for (ProtocolParameterEditor curParam : paramEditors) {
			curParam.setEditable(editable);
		}
	}

	public void setNameEditable(boolean editable) {
		name.setEditable(editable);
	}

	public void setInput(SampleConfig newInput) {
		this.sampleConfig = newInput;
		if (sampleConfig != null){
			updateContents();
		}
	}

	public void setLayoutData(Object layoutData) {
		mainComp.setLayoutData(layoutData);
	}

}
