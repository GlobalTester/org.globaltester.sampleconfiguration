package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Not an actual category parameter but a means to separate different category
 * parameters semantically. This might be mapped to a visual divider within UI
 * usage.
 * 
 * @author amay
 *
 */
public class SeparatorCategoryParameter extends AbstractCategoryParameterDescription
		implements CategoryParameterDescription {

	public SeparatorCategoryParameter(String categoryName,  String name, String description) {
		super(categoryName, name, description);
	}

}
