package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.globaltester.sampleconfiguration.category.parameter.MultilineStringCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

/**
 * Allows use of multiple line strings as parameters.
 * @author mboonk
 *
 */
public class MultilineStringCategoryParameterEditor extends StringCategoryParameterEditor {
	
	CategoryParameterDescription paramDescriptor;

	public MultilineStringCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super(tabItemComp, curParamDescriptor);
	}
	
	@Override
	public GridData getLayoutData() {
		GridData data = super.getLayoutData();
		if (paramDescriptor instanceof MultilineStringCategoryParameter){
			data.heightHint = ((MultilineStringCategoryParameter)paramDescriptor).getNumberOfLines();
		} else {
			data.heightHint =  3 * valueField.getLineHeight();
		}
		return data;
	}
	
	@Override
	public int getStyle() {
		return super.getStyle() | SWT.MULTI;
	}

}
