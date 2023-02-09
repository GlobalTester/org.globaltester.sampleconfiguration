package org.globaltester.sampleconfiguration.category.parameter;

import org.globaltester.sampleconfiguration.SampleConfig;

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
			try {
				sampleConfig.put(category, key, input.generate(sampleConfig));
			} catch (Exception e) {
				throw new RuntimeException("Generation of value for " + category + "_" + key + " failed", e);
			}
		}
	}

}
