package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public class SeparatorCategoryParameterEditor extends AbstractCategoryParameterEditor {

	public SeparatorCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super (curParamDescriptor);
		
		Label separator = new Label(tabItemComp, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gdSep = new GridData(GridData.FILL_HORIZONTAL);
		gdSep.grabExcessHorizontalSpace = true;
		gdSep.horizontalSpan = 2;
		separator.setLayoutData(gdSep);

		if (curParamDescriptor.getDescription() != null) {
			Label description = new Label(tabItemComp, SWT.NONE);
			description.setText(curParamDescriptor.getDescription());
			GridData gdDescr = new GridData(GridData.FILL_HORIZONTAL);
			gdDescr.grabExcessHorizontalSpace = true;
			gdDescr.horizontalSpan = 2;
			description.setLayoutData(gdDescr);
		}

	}

	@Override
	public void setValue(String newValue) {
		// no value handled for a separator
	}

	@Override
	public String getValue() {
		return null; // no value handled for a separator
	}

	@Override
	public void setActive(boolean active) {
		// separator is never editable
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		// separator needs no listener
	}
	

}
