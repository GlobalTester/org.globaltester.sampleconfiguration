package org.globaltester.sampleconfiguration.profiles.expressions;

import org.globaltester.sampleconfiguration.SampleConfig;

/**
 * This represents logical inversion of a given profile
 * @author mboonk
 *
 */
public class NotProfileExpression extends AbstractProfileExpression {

	private ProfileExpression expression;

	public NotProfileExpression(ProfileExpression expression) {
		this.expression = expression;
	}
	
	@Override
	public boolean evaluate(SampleConfig config) throws ProfileEvaluationException {
		return ! expression.evaluate(config);
	}

	@Override
	public String toString() {
		return "NOT(" + expression + ")";
	}
}
