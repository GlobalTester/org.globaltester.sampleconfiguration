package org.globaltester.sampleconfiguration.category.parameter;

public class CertificateCategoryParameter extends AbstractCategoryParameterDescription implements HiddenCategoryParameter{

	public CertificateCategoryParameter(String categoryName, String name, ParameterGenerator generator) {
		super(categoryName, name, "", generator);
	}

}
