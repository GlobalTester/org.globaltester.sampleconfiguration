package org.globaltester.cardconfiguration;

import java.util.HashMap;

public class CardConfig {

	private HashMap<String, Object> configParams;
	private String name;

	public CardConfig(String name) {
		this.name = name;
		
		configParams = new HashMap<String, Object>();
	}

	/**
	 * Returns a single parameter from this CardConfiguration
	 * 
	 * @param protocol
	 * @param key
	 * @return
	 */
	public Object get(String protocol, String key) {
		return configParams.get(protocol + "_" + key);
	}

	/**
	 * Add a single parameter in this CardConfig
	 * @param protocol
	 * @param key
	 * @param value
	 */
	public void put(String protocol, String key, Object value) {
		configParams.put(protocol + "_" + key, value);
	}

	public String getName() {
		return name;
	}

}
