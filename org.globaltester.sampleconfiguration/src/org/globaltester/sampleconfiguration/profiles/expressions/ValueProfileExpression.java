package org.globaltester.sampleconfiguration.profiles.expressions;

import org.globaltester.sampleconfiguration.SampleConfig;

public class ValueProfileExpression extends AbstractProfileExpression {

	private boolean value;

	public ValueProfileExpression(boolean value) {
		this.value = value;
	}
	
	@Override
	public boolean evaluate(SampleConfig config) {
		return value;
	}

	@Override
	public String toString() {
		return Boolean.toString(value);
	}
}
