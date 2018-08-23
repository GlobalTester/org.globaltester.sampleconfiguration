package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Generic category parameter that defines a path to a file.
 * 
 * @author jgoeke
 *
 */
public class FileCategoryParameter extends StringCategoryParameter {

	public FileCategoryParameter(String categoryName, String name, String description) {
		super(categoryName, name, description);
	}

}
