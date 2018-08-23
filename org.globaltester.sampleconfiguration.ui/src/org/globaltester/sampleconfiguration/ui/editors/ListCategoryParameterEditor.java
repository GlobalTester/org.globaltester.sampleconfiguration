package org.globaltester.sampleconfiguration.ui.editors;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public class ListCategoryParameterEditor extends AbstractCategoryParameterEditor {
	
	Label lbl;
	Combo valueField;
	AbstractMap<String, String> mapping;

	public ListCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor, AbstractMap<String, String> mapping) {
		super(curParamDescriptor);
		
		lbl = new Label(tabItemComp, SWT.NONE);
		lbl.setText(curParamDescriptor.getDescription());
		
		valueField = new Combo(tabItemComp, SWT.READ_ONLY);
		GridData gdData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		valueField.setLayoutData(gdData);
		valueField.setItems(mapping.keySet().toArray(new String[]{}));
		valueField.select(0);
		this.mapping = mapping;
	}

	@Override
	public void setValue(String newValue) {
		for(Entry<String, String> entry : mapping.entrySet()) {
			if(newValue.equals(entry.getValue())) {
				valueField.select(valueField.indexOf(entry.getKey()));
			}
		}
		
	}

	@Override
	public String getValue() {
		return mapping.get(valueField.getItem(valueField.getSelectionIndex()));
	}

	@Override
	public void setActive(boolean active) {
		valueField.setEnabled(active);
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		valueField.addListener(eventType, listener);
	}
	

}
