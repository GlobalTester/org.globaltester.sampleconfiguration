package org.globaltester.sampleconfiguration.profiles.expressions;

import org.globaltester.sampleconfiguration.SampleConfig;

/**
 * This represents a logical OR between given profiles.
 * @author mboonk
 *
 */
public class OrProfileExpression extends AbstractProfileExpression {

	ProfileExpression [] expressions;
	
	public OrProfileExpression(ProfileExpression ... expressions) {
		if (expressions.length == 0){
			throw new IllegalArgumentException("OR expressions need at least one subexpression");
		}
		this.expressions = expressions;
	}
	
	@Override
	public boolean evaluate(SampleConfig config) {
		for (ProfileExpression current : expressions){
			if (current.evaluate(config) == true){
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String result = "OR(";
		for (ProfileExpression current : expressions){
			result += current.toString();
			if (!(current == expressions[expressions.length - 1])){
				result += ",";
			}
		}
		return result + ")";
	}
}
