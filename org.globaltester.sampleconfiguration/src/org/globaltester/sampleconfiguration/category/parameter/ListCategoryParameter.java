package org.globaltester.sampleconfiguration.category.parameter;

import java.util.AbstractMap;

/**
 * Generic category parameter that defines a list of values.
 * 
 * @author cstroh
 *
 */
public class ListCategoryParameter extends AbstractCategoryParameterDescription
		implements CategoryParameterDescription {
	
	private AbstractMap<String, String> mapping;

	public ListCategoryParameter(String categoryName, String name, String description, AbstractMap<String, String> mapping) {
		super(categoryName, name, description);
		this.mapping = mapping;
	}

	public AbstractMap<String, String> getMapping() {
		return mapping;
	}

}
