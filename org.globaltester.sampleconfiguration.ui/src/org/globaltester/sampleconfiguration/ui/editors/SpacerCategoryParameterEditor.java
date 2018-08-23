package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public class SpacerCategoryParameterEditor extends AbstractCategoryParameterEditor {

	public SpacerCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super (curParamDescriptor);
		
		new Label(tabItemComp, SWT.NONE);
	}

	@Override
	public void setValue(String newValue) {
		// no value handled for a spacer
	}

	@Override
	public String getValue() {
		return null; // no value handled for a spacer
	}

	@Override
	public void setActive(boolean active) {
		// spacer is never editable
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		// spacer needs no listener
	}
}
