package org.globaltester.sampleconfiguration.profiles;

import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.profiles.expressions.AbstractProfileExpression;
import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;

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
	private String protocol;
	
	public Profile(String profile) {
		int protocolDividerPosition = profile.indexOf('_');
		
		if (protocolDividerPosition < 0){
			throw new IllegalArgumentException("Profiles need to contain a '_' to divide the protocol name");
		}
		
		this.protocol = profile.substring(0, protocolDividerPosition);
		this.name = profile.substring(protocolDividerPosition + 1);
	}
	
	@Override
	public boolean evaluate(SampleConfig config){
		return Boolean.parseBoolean(config.get(protocol, name));
	}
	
	@Override
	public String toString() {
		return protocol + "_" + name;
	}
}
