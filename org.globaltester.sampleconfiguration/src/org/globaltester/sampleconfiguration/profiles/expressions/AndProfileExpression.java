package org.globaltester.sampleconfiguration.profiles.expressions;

import org.globaltester.sampleconfiguration.SampleConfig;

public class AndProfileExpression extends AbstractProfileExpression{

	ProfileExpression [] expressions;
	
	public AndProfileExpression(ProfileExpression ... expressions) {
		if (expressions.length == 0){
			throw new IllegalArgumentException("AND expressions need at least one subexpression");
		}
		this.expressions = expressions;
	}
	
	@Override
	public boolean evaluate(SampleConfig config) {
		for (ProfileExpression current : expressions){
			if (current.evaluate(config) == false){
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String result = "AND(";
		for (ProfileExpression current : expressions){
			result += current.toString();
			if (!(current == expressions[expressions.length - 1])){
				result += ",";
			}
		}
		return result + ")";
	}
}
