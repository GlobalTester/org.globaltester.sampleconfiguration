package org.globaltester.sampleconfiguration.profiles.expressions;

import org.globaltester.sampleconfiguration.SampleConfig;

public interface ProfileExpression {
	public boolean evaluate(SampleConfig config);
}
