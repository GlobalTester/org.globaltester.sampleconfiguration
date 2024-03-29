package org.globaltester.sampleconfiguration.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.category.CategoryFactory;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;
import org.globaltester.sampleconfiguration.category.parameter.HiddenCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.StringCategoryParameter;
import org.globaltester.sampleconfiguration.ui.editors.CategoryParameterEditor;
import org.globaltester.sampleconfiguration.ui.editors.CategoryParameterEditorFactory;
import org.globaltester.sampleconfiguration.ui.editors.DefaultHiddenParameterEditor;
import org.globaltester.sampleconfiguration.ui.editors.HiddenCategoryParameterEditor;
import org.globaltester.sampleconfiguration.ui.editors.StringCategoryParameterEditor;

public class SampleConfigEditorWidget {
	
	private Composite mainComp;
	private SampleConfig sampleConfig;
	private TabFolder tabFolder;
	Label lblName;
	private Text name;
	private Text txtPlatformId;
	private Text txtSampleId;
	private Text txtTestSetup;
	private Text descr;

	private List<CategoryParameterEditor> paramEditors = new ArrayList<>();
	private List<CategoryParameterEditor> unsupportedTabEditors = new LinkedList<>();
	private Map<String, TabItem> protocolTabItems = new HashMap<>();

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
		addTabItemsForNewProtocols(tabFolder);
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
		if (getSampleConfig().getTestSetup() != null)
			txtTestSetup.setText(getSampleConfig().getTestSetup());
	}

	private void updateProtocolParameterEditors() {
		unsupportedTabEditors.clear();
		
		addTabItemsForNewProtocols(tabFolder);
		
		HashSet<String> parameterNames = new HashSet<>();
		for (CategoryParameterEditor curParamEditor : paramEditors) {
			String protocolName = curParamEditor.getCategoryParameterDescription().getCategoryName();
			String paramName = curParamEditor.getCategoryParameterDescription().getName();
			
			parameterNames.add(protocolName + "_" + paramName);

			if (curParamEditor instanceof HiddenCategoryParameterEditor) {
				continue;
			}
			
			String newValue = sampleConfig.get(protocolName, paramName);
			if (newValue != null) {
				curParamEditor.setValue(newValue);
			} else {
				BasicLogger.log(getClass(), "Could not udpate editor sample config entry for " + protocolName + "_" + paramName, LogLevel.WARN);
			}
		}
		

		HashMap<String, HashMap<String, CategoryParameterDescription>> unsupportedParamEditors = new HashMap<>();
		
		for (String protocol : sampleConfig.getStoredCategories()){
			for (String parameter : sampleConfig.getStoredParameters(protocol)){
				if (parameterNames.contains(protocol + "_" + parameter)){
					continue;
				}
				if (!unsupportedParamEditors.containsKey(protocol)){
					unsupportedParamEditors.put(protocol, new HashMap<>());
				}
				if (!unsupportedParamEditors.get(protocol).containsKey(parameter)){
					unsupportedParamEditors.get(protocol).put(parameter, new StringCategoryParameter(protocol, parameter, protocol + "_" + parameter));
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
					if (unsupportedTabItemScroller!= null && !unsupportedTabItemScroller.isDisposed()){
						unsupportedTabItemScroller.setFocus();	
					}
				}
			});
			
			for (String protocol : unsupportedParamEditors.keySet()){
				for (String parameter : unsupportedParamEditors.get(protocol).keySet()){
					CategoryParameterEditor editor = new StringCategoryParameterEditor(unsupportedTabItemComp, unsupportedParamEditors.get(protocol).get(parameter));
					editor.setActive(active);
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
		lblPlatformId.setText("Platform ID:");
		txtPlatformId = new Text(tabItemComp, SWT.BORDER);
		txtPlatformId.setFont(CategoryParameterEditorFactory.FONT_MONOSPACE);
		txtPlatformId.setLayoutData(gdReport);
		if(listener != null) {
			txtPlatformId.addListener(SWT.Modify, listener);
		}
		
		Label lblSampleId = new Label(tabItemComp, SWT.NONE);
		lblSampleId.setText("Sample ID:");
		txtSampleId = new Text(tabItemComp, SWT.BORDER);
		txtSampleId.setFont(CategoryParameterEditorFactory.FONT_MONOSPACE);
		txtSampleId.setLayoutData(gdReport);
		if(listener != null) {
			txtSampleId.addListener(SWT.Modify, listener);
		}
		
		Label lblTestSetup = new Label(tabItemComp, SWT.NONE);
		lblTestSetup.setLayoutData(new GridData(SWT.TOP, SWT.LEFT, false, false, 1, 1));
		lblTestSetup.setText("Test setup:");
		txtTestSetup = new Text(tabItemComp, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		txtTestSetup.setFont(CategoryParameterEditorFactory.FONT_MONOSPACE);
		GridData txtTestSetupGd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		txtTestSetupGd.heightHint = 50;
		txtTestSetup.setLayoutData(txtTestSetupGd);
		if(listener != null) {
			txtTestSetup.addListener(SWT.Modify, listener);
		}
		
		Label lblDescription = new Label(tabItemComp, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.TOP, SWT.LEFT, false, false, 1, 1));
		lblDescription.setText("Description:");
		
		descr = new Text(tabItemComp, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		GridData txtDescrGd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		txtDescrGd.heightHint = 50;
		descr.setLayoutData(txtDescrGd);
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

	private void addTabItemsForNewProtocols(TabFolder tabFolder) {
		CategoryFactory[] pFactories = org.globaltester.sampleconfiguration.Activator.getAvailableCategoryFactories();
		
		//sort protocol factories by name
		Arrays.sort(pFactories, new Comparator<CategoryFactory>() {
			@Override
			public int compare(CategoryFactory factory1, CategoryFactory factory2) {
				return factory1.getName().compareToIgnoreCase(factory2.getName());
			}
		});
		
		for (CategoryFactory curProtocolFactory : pFactories) {
			if (curProtocolFactory == null) continue;
			getProtocolTabItem(tabFolder, curProtocolFactory);
		}
	}

	private synchronized TabItem getProtocolTabItem(TabFolder tabFolder, CategoryFactory curProtocolFactory) {
		TabItem curTabItem = protocolTabItems.get(curProtocolFactory.getName()); 
		if ( curTabItem != null) return curTabItem;

		List<CategoryParameterDescription> parameterDescriptors = curProtocolFactory.getParameterDescriptors();
				
		if (!createHiddenEditors(parameterDescriptors)) {
			return null;
		}
		
		curTabItem = new TabItem(tabFolder, SWT.NONE);
		curTabItem.setText(curProtocolFactory.getUiName());
		
		ScrolledComposite scroller = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		Composite tabItemComp = new Composite(scroller, SWT.NONE);
		scroller.setContent(tabItemComp);
		scroller.setExpandVertical(true);
		scroller.setExpandHorizontal(true);
		scroller.setLayout(new FillLayout());
		curTabItem.setControl(scroller);
		tabItemComp.setLayout(new GridLayout(2, false));
			
		createVisibleEditors(parameterDescriptors, tabItemComp);
		
		scroller.setMinHeight(tabItemComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scroller.setFocus();
			}
		});

		tabItemComp.pack();

		protocolTabItems.put(curProtocolFactory.getName(), curTabItem);
		
		return curTabItem;
	}

	/**
	 * Creates editors that are visible to the user
	 * @param parameterDescriptors
	 * @param tabItemComp
	 */
	private void createVisibleEditors(List<CategoryParameterDescription> parameterDescriptors, Composite tabItemComp) {
		for (CategoryParameterDescription curParamDescriptor : parameterDescriptors) {
			if (curParamDescriptor != null && !(curParamDescriptor instanceof HiddenCategoryParameter)) {
				CategoryParameterEditor editor = CategoryParameterEditorFactory.createEditor(tabItemComp, curParamDescriptor);
				if(listener != null) {
					editor.addListener(SWT.Selection, listener);
					editor.addListener(SWT.Modify, listener);
				}
				paramEditors.add(editor);
			}
		}
	}

	/**
	 * @param parameterDescriptors
	 * @return true, iff a descriptor was found that is not hidden from the user
	 */
	private boolean createHiddenEditors(List<CategoryParameterDescription> parameterDescriptors) {
		boolean foundNotHiddenDescriptor = false;
		for (CategoryParameterDescription curParamDescriptor : parameterDescriptors) {
			if (curParamDescriptor != null) {
				if (curParamDescriptor instanceof HiddenCategoryParameter) {
					paramEditors.add(new DefaultHiddenParameterEditor(curParamDescriptor));
				} else {
					foundNotHiddenDescriptor = true;
				}
			}
		}
		return foundNotHiddenDescriptor;
	}

	public SampleConfig getSampleConfig() {
		return sampleConfig;
	}

	public void doSave() {
		if (sampleConfig == null){
			return;
		}
		sampleConfig.setTestSetup(txtTestSetup.getText());
		sampleConfig.setDescription(descr.getText());
		sampleConfig.setSampleId(txtSampleId.getText());
		sampleConfig.setPlatformId(txtPlatformId.getText());
		
		//flush all ProtocolParameter values to the SampleConfig object
		for (CategoryParameterEditor curParam : paramEditors) {
			String protocolName = curParam.getCategoryParameterDescription().getCategoryName();
			String paramName = curParam.getCategoryParameterDescription().getName();
			String paramValue = curParam.getValue();
			if (paramValue != null) {
				sampleConfig.put(protocolName, paramName, paramValue);
			}
		}
		

		// save the SampleConfig
		sampleConfig.saveToProject();

	}
	
	private void setActive(boolean active, Text ... texts){
		for (Text text : texts){
			text.setEnabled(active);
			text.setEditable(active);
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
		setActive(active, txtTestSetup,descr, name, txtPlatformId, txtSampleId);
		
		for (CategoryParameterEditor curParam : paramEditors) {
			curParam.setActive(active);
		}
		
		for (CategoryParameterEditor curParam : unsupportedTabEditors) {
			curParam.setActive(active);
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
