package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.widgets.Listener;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public class DefaultHiddenParameterEditor implements HiddenCategoryParameterEditor {

	private CategoryParameterDescription description;

	public DefaultHiddenParameterEditor(CategoryParameterDescription description) {
		this.description = description;
	}

	@Override
	public CategoryParameterDescription getCategoryParameterDescription() {
		return description;
	}

	@Override
	public void setValue(String newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		// TODO Auto-generated method stub
		
	}

}
