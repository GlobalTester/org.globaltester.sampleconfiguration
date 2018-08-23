package org.globaltester.sampleconfiguration.ui.editors;

import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

/**
 * Abstract super implementation of a {@link CategoryParameterEditor} that
 * handles registering and caching a {@link CategoryParameterDescription}
 * 
 * @author amay
 *
 */
public abstract class AbstractCategoryParameterEditor implements CategoryParameterEditor {

	CategoryParameterDescription paramDescr;

	public AbstractCategoryParameterEditor(CategoryParameterDescription categoryParameterDescription) {
		paramDescr = categoryParameterDescription;
	}

	@Override
	public CategoryParameterDescription getCategoryParameterDescription() {
		return paramDescr;
	}

}
