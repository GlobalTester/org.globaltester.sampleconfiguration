package org.globaltester.sampleconfiguration.profiles.expressions;

import org.globaltester.sampleconfiguration.SampleConfig;

/**
 * Implementations of this interface are used to represent test case profiles.
 * They are used to evaluate the applicability of test cases given a
 * {@link SampleConfig}. </br>
 * 
 * @author mboonk
 *
 */
public interface ProfileExpression {
	/**
	 * Evaluates if the profile expression can be fulfilled by the given
	 * {@link SampleConfig}.
	 * 
	 * @param config
	 *            The {@link SampleConfig} to be used
	 * @return true, if the expression evaluates to true
	 */
	public boolean evaluate(SampleConfig config) throws ProfileEvaluationException;
}
