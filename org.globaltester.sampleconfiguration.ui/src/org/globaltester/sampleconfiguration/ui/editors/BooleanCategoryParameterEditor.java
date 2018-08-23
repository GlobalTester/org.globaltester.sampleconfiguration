package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public class BooleanCategoryParameterEditor extends AbstractCategoryParameterEditor {
	
	Button checkbox;

	public BooleanCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super(curParamDescriptor);
		
		checkbox = new Button(tabItemComp, SWT.CHECK);
		checkbox.setText(paramDescr.getDescription());
		
	}

	@Override
	public void setValue(String newValue) {
		checkbox.setSelection(Boolean.parseBoolean(newValue));
	}

	@Override
	public String getValue() {
		return Boolean.toString(checkbox.getSelection());
	}

	@Override
	public void setActive(boolean active) {
		checkbox.setEnabled(active);
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		checkbox.addListener(eventType, listener);
	}
}
