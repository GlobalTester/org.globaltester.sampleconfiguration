package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Generic category parameter that defines a String value.
 * 
 * @author amay
 *
 */
public class StringCategoryParameter extends AbstractCategoryParameterDescription
		implements CategoryParameterDescription {

	public StringCategoryParameter(String categoryName, String name, String description) {
		super(categoryName, name, description);
	}

}
