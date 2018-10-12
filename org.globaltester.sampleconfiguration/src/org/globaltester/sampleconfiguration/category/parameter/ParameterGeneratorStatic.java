package org.globaltester.sampleconfiguration.category.parameter;

import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.category.parameter.ParameterGenerator;

public class ParameterGeneratorStatic implements ParameterGenerator {

	private String value;
	private boolean generateAlways;
	
	public ParameterGeneratorStatic(String string) {
		this(string, false);
	}
	
	public ParameterGeneratorStatic(String string, boolean generateAlways) {
		this.value = string;
		this.generateAlways = generateAlways;
	}

	@Override
	public void generate(String category, String key, SampleConfig sampleConfig) {
		if (generateAlways || (!sampleConfig.contains(category, key))) {
			sampleConfig.put(category, key, value);
		}
	}

}
