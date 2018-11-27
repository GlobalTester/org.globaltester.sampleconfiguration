package org.globaltester.sampleconfiguration.category.parameter;

import org.globaltester.sampleconfiguration.category.parameter.generator.ProfileParameterGenerator;

/**
 * Profile that defines whether testcases are executable on the given sample.
 * 
 * @author amay
 *
 */
public class ProfileCategoryParameter extends BooleanCategoryParameter {

	public ProfileCategoryParameter(String categoryName, String name, String description) {
		this(categoryName, name, description, new ProfileParameterGenerator());
	}

	public ProfileCategoryParameter(String categoryName, String name, String description,
			ParameterGenerator generator) {
		super(categoryName, name, description, generator);
	}
	
}
