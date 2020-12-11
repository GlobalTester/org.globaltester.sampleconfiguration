package org.globaltester.sampleconfiguration;

/**
 * Data structure that represents a single parameter value stored in {@link SampleConfig}
 */
public class SampleConfigParameterValue {

	String value;
	boolean generated;

	public SampleConfigParameterValue(String value, boolean generated) {
		this.value = value;
		this.generated = generated;
	}

	public SampleConfigParameterValue(String value) {
		this(value, false);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}
	
	@Override
	public String toString() {
		return "SampleConfigParameterValue [value=" + value + ", generated=" + generated + "]";
	}

}
