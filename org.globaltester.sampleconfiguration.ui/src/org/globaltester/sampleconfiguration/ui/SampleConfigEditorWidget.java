package org.globaltester.sampleconfiguration.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
	
	private Composite mainComp;
	private SampleConfig sampleConfig;
	private TabFolder tabFolder;
	Label lblName;
	private Text name;
	private Text txtPlatformId;
	private Text txtSampleId;
	private Text descr;

	private List<ProtocolParameterEditor> paramEditors = new ArrayList<>();
	private List<ProtocolParameterEditor> unsupportedTabEditors = new LinkedList<>();

	private Listener listener;
	private TabItem unsupportedTabItem;
	private boolean active = true;
	
	
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
				tabFolder.redraw();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
		
		addTabItemGeneral(tabFolder);
		addTabItemsForProtocols(tabFolder);
		new Label(mainComp, SWT.NONE);
	}

	public void updateContents() {
		updateTabItemGeneral();
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

	private void updateProtocolParameterEditors() {
		unsupportedTabEditors.clear();
		
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
		

		HashMap<String, HashMap<String, ProtocolParameterDescription>> unsupportedParamEditors = new HashMap<>();
		
		for (String protocol : sampleConfig.getStoredProtocols()){
			for (String parameter : sampleConfig.getStoredParameters(protocol)){
				if (parameterNames.contains(protocol + "_" + parameter)){
					continue;
				}
				if (!unsupportedParamEditors.containsKey(protocol)){
					unsupportedParamEditors.put(protocol, new HashMap<>());
				}
				if (!unsupportedParamEditors.get(protocol).containsKey(parameter)){
					unsupportedParamEditors.get(protocol).put(parameter, new StringProtocolParameter(protocol, parameter, protocol + "_" + parameter));
				}
			}
		}	

		
		if (unsupportedTabItem != null){
			unsupportedTabItem.dispose();
			unsupportedTabItem = null;
		}
		
		if (!unsupportedParamEditors.isEmpty()){
			Composite unsupportedTabItemComp;
			ScrolledComposite unsupportedTabItemScroller;
			

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
			
			for (String protocol : unsupportedParamEditors.keySet()){
				for (String parameter : unsupportedParamEditors.get(protocol).keySet()){
					ProtocolParameterEditor editor = new StringProtocolParameterEditor(unsupportedTabItemComp, unsupportedParamEditors.get(protocol).get(parameter));
					editor.setEditable(active);
					if(listener != null) {
						editor.addListener(SWT.Selection, listener);
						editor.addListener(SWT.Modify, listener);
					}
					String newValue = sampleConfig.get(protocol, parameter);
					if (newValue != null) {
						editor.setValue(newValue);
					}
					unsupportedTabEditors.add(editor);
				}
			}
			
			unsupportedTabItemScroller.setMinHeight(unsupportedTabItemComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			unsupportedTabItemComp.pack();
		}
	}

	private void addTabItemGeneral(TabFolder tabFolder) {
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
		txtPlatformId.setFont(ProtocolParameterEditorFactory.FONT_MONOSPACE);
		txtPlatformId.setLayoutData(gdReport);
		if(listener != null) {
			txtPlatformId.addListener(SWT.Modify, listener);
		}
		
		Label lblSampleId = new Label(tabItemComp, SWT.NONE);
		lblSampleId.setText("Sample ID");
		txtSampleId = new Text(tabItemComp, SWT.BORDER);
		txtSampleId.setFont(ProtocolParameterEditorFactory.FONT_MONOSPACE);
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
			createProtocolTabItem(tabFolder, curProtocolFactory);
		}
	}

	private TabItem createProtocolTabItem(TabFolder tabFolder, ProtocolFactory curProtocolFactory) {
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
		if (sampleConfig == null){
			return;
		}
		sampleConfig.setDescription(descr.getText());
		sampleConfig.setSampleId(txtSampleId.getText());
		sampleConfig.setPlatformId(txtPlatformId.getText());
		
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
	
	private void setActive(boolean editable, Text ... texts){
		for (Text text : texts){
			text.setEnabled(editable);
			text.setEditable(editable);
		}
	}
	
	/**
	 * Sets the active state. This controls if the receiver is able to be edited
	 * and potentially visual representation.
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
		setActive(active, descr, name, txtPlatformId, txtSampleId);
		
		for (ProtocolParameterEditor curParam : paramEditors) {
			curParam.setEditable(active);
		}
		
		for (ProtocolParameterEditor curParam : unsupportedTabEditors) {
			curParam.setEditable(active);
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
