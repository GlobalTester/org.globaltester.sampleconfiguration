package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Profile that defines whether testcases are executable on the given sample.
 * 
 * @author amay
 *
 */
public class ProfileCategoryParameter extends BooleanCategoryParameter {

	public ProfileCategoryParameter(String categoryName, String name, String description) {
		super(categoryName, name, description);
	}
	
}
