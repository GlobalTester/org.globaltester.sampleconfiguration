package org.globaltester.sampleconfiguration.category.parameter;

import org.globaltester.sampleconfiguration.SampleConfig;

public interface ParameterGenerator {

	public void generate(String category, String key, SampleConfig sampleConfig);

}
