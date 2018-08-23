package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public class ProfileCategoryParameterEditor extends BooleanCategoryParameterEditor {

	public ProfileCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super(tabItemComp, curParamDescriptor);

		checkbox.setText(paramDescr.getName());
		checkbox.setToolTipText(paramDescr.getDescription());
		
	}
}
