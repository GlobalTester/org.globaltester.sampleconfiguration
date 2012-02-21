package org.globaltester.cardconfiguration;

import java.util.HashMap;

public class CardConfig {

	private HashMap<String, Object> configParams;

	public CardConfig() {
		configParams = new HashMap<String, Object>();

		// FIXME remove default MRZ
		configParams
				.put("ICAO9303_MRZ",
						"P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6");
	}

	/**
	 * Returns a CardConfiguration parameter from the default scope.
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String protocol, String key) {
		return configParams.get(protocol + "_" + key);
	}

}
