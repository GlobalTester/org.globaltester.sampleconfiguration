package org.globaltester.sampleconfiguration.profiles;

import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.profiles.expressions.AbstractProfileExpression;
import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;
import org.globaltester.sampleconfiguration.profiles.parser.ParseException;

/**
 * This is the leaf {@link ProfileExpression} that represents a single profile
 * that is directly matched to configuration values.
 * 
 * Non-existing information evaluates to false.
 * 
 * @author mboonk
 *
 */
public class Profile extends AbstractProfileExpression{
	private String name;
	private String category;
	
	public Profile(String profile) throws ParseException {
		int categoryDividerPosition = profile.indexOf('_');
		
		if (categoryDividerPosition < 0){
			throw new ParseException("Profiles need to contain a '_' to divide the category name, offending profile string: " + profile);
		}
		
		this.category = profile.substring(0, categoryDividerPosition);
		this.name = profile.substring(categoryDividerPosition + 1);
	}
	
	@Override
	public boolean evaluate(SampleConfig config){
		if (config == null){
			return false;
		}
		return Boolean.parseBoolean(config.get(category, name));
	}
	
	@Override
	public String toString() {
		return category + "_" + name;
	}
}
