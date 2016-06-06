package org.globaltester.sampleconfiguration.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.globaltester.logging.legacy.logger.GtErrorLogger;
import org.globaltester.protocol.ProtocolFactory;
import org.globaltester.protocol.parameter.ProtocolParameterDescription;
import org.globaltester.protocol.parameter.StringProtocolParameter;
import org.globaltester.protocol.ui.ProtocolParameterEditor;
import org.globaltester.protocol.ui.ProtocolParameterEditorFactory;
import org.globaltester.protocol.ui.StringProtocolParameterEditor;
import org.globaltester.sampleconfiguration.SampleConfig;

public class SampleConfigEditorWidget {

	private static final Font monospacedFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
	
	private Composite mainComp;
	private SampleConfig sampleConfig;
	private TabFolder tabFolder;
	Label lblName;
	private Text name;
	private Text txtPlatformId;
	private Text txtSampleId;
	private Text descr;
	private Text mrz1;
	private Text mrz2;
	private Text mrz3;

	private List<ProtocolParameterEditor> paramEditors = new ArrayList<>();
	private HashMap<String, HashMap<String, ProtocolParameterEditor>> unsupportedParamEditors = new HashMap<>();

	private Listener listener;

	private TabItem unsupportedTabItem;
	private Composite unsupportedTabItemComp;
	private ScrolledComposite unsupportedTabItemScroller;
	
	
	public SampleConfigEditorWidget(Composite parent) {
		this.createPartControl(parent);
	}
	
	public SampleConfigEditorWidget(Composite parent, Listener listener) {
		this.listener = listener;
		this.createPartControl(parent);
	}

	private void createPartControl(Composite parent) {
		mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout(2, false));
		
		tabFolder = new TabFolder(mainComp, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
//				if (e.widget == tabFolder) {
					tabFolder.redraw();
//				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		addTabItemGeneral(tabFolder);
		addTabItemMrz(tabFolder);
		addTabItemsForProtocols(tabFolder);
		new Label(mainComp, SWT.NONE);
	}

	public void updateContents() {
		updateTabItemGeneral();
		updateTabItemMrz();
		updateProtocolParameterEditors();
	}

	private void updateTabItemGeneral() {
		if (!name.isDisposed()) {
		name.setText(getSampleConfig().getName());
		}
		descr.setText(getSampleConfig().getDescription());
		if (getSampleConfig().getPlatformId() != null)
			txtPlatformId.setText(getSampleConfig().getPlatformId());
		if (getSampleConfig().getSampleId() != null)
			txtSampleId.setText(getSampleConfig().getSampleId());
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
		HashSet<String> parameterNames = new HashSet<>();
		for (ProtocolParameterEditor curParamEditor : paramEditors) {
			String protocolName = curParamEditor.getProtocolParameterDescription().getProtocolName();
			String paramName = curParamEditor.getProtocolParameterDescription().getName();
			
			parameterNames.add(protocolName + "_" + paramName);
			
			String newValue = sampleConfig.get(protocolName, paramName);
			if (newValue != null) {
				curParamEditor.setValue(newValue);
			}
		}
		
		for (String protocol : sampleConfig.getStoredProtocols()){
			for (String parameter : sampleConfig.getStoredParameters(protocol)){
				if (parameterNames.contains(protocol + "_" + parameter)){
					continue;
				}
				if (!unsupportedParamEditors.containsKey(protocol)){
					unsupportedParamEditors.put(protocol, new HashMap<>());
				}
				if (!unsupportedParamEditors.get(protocol).containsKey(parameter)){
					unsupportedParamEditors.get(protocol).put(parameter, new StringProtocolParameterEditor(unsupportedTabItemComp, new StringProtocolParameter(protocol, parameter, protocol + "_" + parameter)));
				}
				ProtocolParameterEditor editor = unsupportedParamEditors.get(protocol).get(parameter);
				if(listener != null) {
					editor.addListener(SWT.Selection, listener);
					editor.addListener(SWT.Modify, listener);
				}
				
				String newValue = sampleConfig.get(protocol, parameter);
				if (newValue != null) {
					editor.setValue(newValue);
				}
			}
		}
		
		unsupportedTabItemScroller.setMinHeight(unsupportedTabItemComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		unsupportedTabItemComp.pack();
	}

	private void addTabItemGeneral(TabFolder tabFolder) {
		// TODO Auto-generated method stub
		TabItem curTabItem = new TabItem(tabFolder, SWT.NONE);
		curTabItem.setText("General");
		
		ScrolledComposite scroller = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		Composite tabItemComp = new Composite(scroller, SWT.NONE);
		scroller.setContent(tabItemComp);
		scroller.setExpandVertical(true);
		scroller.setExpandHorizontal(true);
		curTabItem.setControl(scroller);
		tabItemComp.setLayout(new GridLayout(2, false));
		
		lblName = new Label(tabItemComp, SWT.NONE);
		lblName.setText("Name:");
		name = new Text(tabItemComp, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		name.setEditable(false);
		
		GridData gdReport = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		Label lblPlatformId = new Label(tabItemComp, SWT.NONE);
		lblPlatformId.setText("Platform ID");
		txtPlatformId = new Text(tabItemComp, SWT.BORDER);
		txtPlatformId.setFont(monospacedFont);
		txtPlatformId.setLayoutData(gdReport);
		if(listener != null) {
			txtPlatformId.addListener(SWT.Modify, listener);
		}
		
		Label lblSampleId = new Label(tabItemComp, SWT.NONE);
		lblSampleId.setText("Sample ID");
		txtSampleId = new Text(tabItemComp, SWT.BORDER);
		txtSampleId.setFont(monospacedFont);
		txtSampleId.setLayoutData(gdReport);
		if(listener != null) {
			txtSampleId.addListener(SWT.Modify, listener);
		}
		
		Label lblDescription = new Label(tabItemComp, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.TOP, SWT.LEFT, false, false, 1, 1));
		lblDescription.setText("Description:");
		
		descr = new Text(tabItemComp, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		GridData lblDescrGd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		lblDescrGd.heightHint = 50;
		descr.setLayoutData(lblDescrGd);
		if(listener != null) {
			descr.addListener(SWT.Modify, listener);
		}
		
		scroller.setMinSize(tabItemComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scroller.setFocus();
			}
		});
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
		if(listener != null) {
			mrz1.addListener(SWT.Modify, listener);
		}
		Label lblMrz2 = new Label(tabItemComp, SWT.NONE);
		lblMrz2.setText("MRZ (line 2):");
		mrz2 = new Text(tabItemComp, SWT.BORDER);
		mrz2.setFont(monospacedFont);
		GridData gdMrz2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		mrz2.setLayoutData(gdMrz2);
		if(listener != null) {
			mrz2.addListener(SWT.Modify, listener);
		}
		Label lblMrz3 = new Label(tabItemComp, SWT.NONE);
		lblMrz3.setText("MRZ (line 3):");
		mrz3 = new Text(tabItemComp, SWT.BORDER);
		mrz3.setFont(monospacedFont);
		GridData gdMrz3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		mrz3.setLayoutData(gdMrz3);
		if(listener != null) {
			mrz3.addListener(SWT.Modify, listener);
		}
		
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
		
		Collection<ProtocolParameterDescription> parameters = new HashSet<>();
		
		for (ProtocolFactory curProtocolFactory : pFactories) {
			if (curProtocolFactory == null) continue;
			parameters.addAll(curProtocolFactory.getParameterDescriptors());
			createProtcolTabItem(tabFolder, curProtocolFactory);
		}
		
		createRemainingTabItem(tabFolder, parameters);
	}

	private void createRemainingTabItem(TabFolder tabFolder, Collection<ProtocolParameterDescription> parameters) {
		unsupportedTabItem = new TabItem(tabFolder, SWT.NONE);
		unsupportedTabItem.setText("Unsupported");
		
		unsupportedTabItemScroller = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		unsupportedTabItemComp = new Composite(unsupportedTabItemScroller, SWT.NONE);
		unsupportedTabItemScroller.setContent(unsupportedTabItemComp);
		unsupportedTabItemScroller.setExpandVertical(true);
		unsupportedTabItemScroller.setExpandHorizontal(true);
		unsupportedTabItemScroller.setLayout(new FillLayout());
		unsupportedTabItem.setControl(unsupportedTabItemScroller);
		unsupportedTabItemComp.setLayout(new GridLayout(2, false));
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				unsupportedTabItemScroller.setFocus();
			}
		});
	}

	private TabItem createProtcolTabItem(TabFolder tabFolder, ProtocolFactory curProtocolFactory) {
		TabItem curTabItem = new TabItem(tabFolder, SWT.NONE);
		curTabItem.setText(curProtocolFactory.getName());
		
		ScrolledComposite scroller = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		Composite tabItemComp = new Composite(scroller, SWT.NONE);
		scroller.setContent(tabItemComp);
		scroller.setExpandVertical(true);
		scroller.setExpandHorizontal(true);
		scroller.setLayout(new FillLayout());
		curTabItem.setControl(scroller);
		tabItemComp.setLayout(new GridLayout(2, false));
			
		for (ProtocolParameterDescription curParamDescriptor : curProtocolFactory.getParameterDescriptors()) {
			if (curParamDescriptor != null) {
				ProtocolParameterEditor editor = ProtocolParameterEditorFactory.createEditor(tabItemComp, curParamDescriptor);
				if(listener != null) {
					editor.addListener(SWT.Selection, listener);
					editor.addListener(SWT.Modify, listener);
				}
				paramEditors.add(editor);
			}
		}
		
		scroller.setMinHeight(tabItemComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scroller.setFocus();
			}
		});

		tabItemComp.pack();

		return curTabItem;
	}

	public SampleConfig getSampleConfig() {
		return sampleConfig;
	}

	public void doSave() {
		sampleConfig.setDescription(descr.getText());
		sampleConfig.setSampleId(txtSampleId.getText());
		sampleConfig.setPlatformId(txtPlatformId.getText());
		
		// flush all changes to the SampleConfig object
		
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
	
	public void setEditable(boolean editable) {
		descr.setEditable(editable);
		mrz1.setEditable(editable);
		mrz2.setEditable(editable);
		mrz3.setEditable(editable);
		txtPlatformId.setEditable(editable);
		txtSampleId.setEditable(editable);
		for (ProtocolParameterEditor curParam : paramEditors) {
			curParam.setEditable(editable);
		}
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

	public void hideName() {
		lblName.dispose();
		name.dispose();
	}

}
