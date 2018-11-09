package org.globaltester.sampleconfiguration.category.parameter;


public class HiddenStringCategoryParameter extends AbstractCategoryParameterDescription implements HiddenCategoryParameter{

	public HiddenStringCategoryParameter(String categoryName, String name, ParameterGenerator generator) {
		super(categoryName, name, "", generator);
	}

}
