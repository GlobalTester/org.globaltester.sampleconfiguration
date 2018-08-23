package org.globaltester.sampleconfiguration.category.parameter;

public class CertificateCategoryParameter extends AbstractCategoryParameterDescription implements HiddenCategoryParameter{

	private ParameterGenerator generator;

	public CertificateCategoryParameter(String categoryName, String name, ParameterGenerator generator) {
		super(categoryName, name);
		this.generator = generator;
	}

	@Override
	public ParameterGenerator getGenerator() {
		return this.generator;
	}
}
