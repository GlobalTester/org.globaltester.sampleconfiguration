package org.globaltester.sampleconfiguration.category.parameter.generator;

import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.SampleConfigParameterValue;
import org.globaltester.sampleconfiguration.category.parameter.ParameterGenerator;

public class ProfileParameterGenerator implements ParameterGenerator {

	private boolean value;
	
	public ProfileParameterGenerator() {
		this(true);
	}
	
	public ProfileParameterGenerator(boolean defaultValue) {
		this.value = defaultValue;
	}

	@Override
	public void generate(String category, String key, SampleConfig sampleConfig) {
		if (!sampleConfig.contains(category, key)) {
			SampleConfigParameterValue generatedValue = new SampleConfigParameterValue(Boolean.toString(value), true);
			sampleConfig.put(category, key, generatedValue);
		}
	}

}
