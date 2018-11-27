package org.globaltester.sampleconfiguration.profiles.parser;

import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.profiles.expressions.ProfileEvaluationException;
import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;

public class UnparseableProfileExpression implements ProfileExpression {

	private String reason;

	public UnparseableProfileExpression(String reason) {
		this.reason = reason;
	}
	
	@Override
	public boolean evaluate(SampleConfig config) throws ProfileEvaluationException {
		throw new ProfileEvaluationException("UnparseableProfileExpression can not be evaluated: "+reason);
	}
	
	@Override
	public String toString() {
		return "UNPARSEABLE(" + reason + ")";
	}

}
