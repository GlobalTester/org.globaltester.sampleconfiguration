package org.globaltester.sampleconfiguration.category.parameter;

import org.globaltester.base.PreferenceHelper;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.category.parameter.ParameterGenerator;

public class PreferenceGenerator implements ParameterGenerator {

	private String bundle;
	private String preference;
	private boolean generateAlways;

	public PreferenceGenerator(String bundle, String preference) {
		this(bundle, preference, false);
	}

	public PreferenceGenerator(String bundle, String preference, boolean generateAlways) {
		this.bundle = bundle;
		this.preference = preference;
		this.generateAlways = generateAlways;
	}
	
	@Override
	public void generate(String category, String key, SampleConfig sampleConfig) {
		if (generateAlways || !sampleConfig.contains(category, key)) {
			sampleConfig.put(category, key, PreferenceHelper.getPreferenceValue(bundle, preference));
		}
	}

}
