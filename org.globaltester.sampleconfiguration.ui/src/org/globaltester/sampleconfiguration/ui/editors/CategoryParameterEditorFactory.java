package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.globaltester.sampleconfiguration.category.parameter.BooleanCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.BooleanTableCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.DirectoryCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.FileCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.HiddenCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.ListCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.MultilineStringCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.ProfileCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;
import org.globaltester.sampleconfiguration.category.parameter.SeparatorCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.SpacerCategoryParameter;
import org.globaltester.sampleconfiguration.category.parameter.StringCategoryParameter;

public class CategoryParameterEditorFactory {
	public static final Font FONT_MONOSPACE = JFaceResources.getFont(JFaceResources.TEXT_FONT);
	
	public static CategoryParameterEditor createEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		
		if (curParamDescriptor instanceof HiddenCategoryParameter) return new DefaultHiddenParameterEditor(curParamDescriptor);
		if (curParamDescriptor instanceof SeparatorCategoryParameter) return new SeparatorCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof SpacerCategoryParameter) return new SpacerCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof ProfileCategoryParameter) return new ProfileCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof BooleanCategoryParameter) return new BooleanCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof FileCategoryParameter) return new FileCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof DirectoryCategoryParameter) return new DirectoryCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof MultilineStringCategoryParameter) return new MultilineStringCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof StringCategoryParameter) return new StringCategoryParameterEditor(tabItemComp, curParamDescriptor);
		if (curParamDescriptor instanceof ListCategoryParameter) return new ListCategoryParameterEditor(tabItemComp, curParamDescriptor, ((ListCategoryParameter) curParamDescriptor).getMapping());
		if (curParamDescriptor instanceof BooleanTableCategoryParameter) return new BooleanTableCategoryParameterEditor(tabItemComp, curParamDescriptor, ((BooleanTableCategoryParameter) curParamDescriptor).getColumnDescription(), ((BooleanTableCategoryParameter) curParamDescriptor).getLineDescription(), ((BooleanTableCategoryParameter) curParamDescriptor).getTable());
		
		return new UnknownCategoryParameterEditor(tabItemComp, curParamDescriptor);
		
	}

}
