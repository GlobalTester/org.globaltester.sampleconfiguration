package org.globaltester.sampleconfiguration.category.parameter;

import org.globaltester.logging.BasicLogger;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.SampleConfigParameterValue;

public class ParameterGeneratorCustom implements ParameterGenerator {

	private CustomGenerator input;
	private boolean generateAlways;
	
	public ParameterGeneratorCustom(CustomGenerator input) {
		this(input, false);
	}
	
	public ParameterGeneratorCustom(CustomGenerator input, boolean generateAlways) {
		this.input = input;
		this.generateAlways = generateAlways;
	}

	@Override
	public void generate(String category, String key, SampleConfig sampleConfig) {
		if (generateAlways || (!sampleConfig.contains(category, key))) {
			String value = input.generate(sampleConfig);
			if (value != null) {
				SampleConfigParameterValue generatedValue = new SampleConfigParameterValue(value, true);
				sampleConfig.put(category, key, generatedValue);
			} else {
				BasicLogger.log(this.getClass(), "Failed to generate value for " + category + "_" + key);
			}
		}
	}

}
