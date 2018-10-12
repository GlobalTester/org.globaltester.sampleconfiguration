package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Category parameter that defines a boolean parameter.
 * 
 * @author cstroh
 *
 */
public class BooleanCategoryParameter extends AbstractCategoryParameterDescription
		implements CategoryParameterDescription {
	
	public BooleanCategoryParameter(String categoryName, String name, String description) {
		super(categoryName, name, description);
	}

	public BooleanCategoryParameter(String categoryName, String name, String description,
			ParameterGenerator generator) {
		super(categoryName, name, description, generator);
	}

}
