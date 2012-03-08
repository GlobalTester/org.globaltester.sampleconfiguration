package org.globaltester.cardconfiguration;

import java.util.HashMap;
import java.util.Set;

public class CardConfigManager {

	private static HashMap<String, CardConfig> configs = new HashMap<String, CardConfig>();
	
	//FIXME AMY replace the following stubs with a logic that stores/retrieves CardConfigs from workspace
	public static String DEFAULT_CARD_CONFIG = "Mustermann Erika";
	static {
		CardConfig mustermannErika = createNewCardConfig(DEFAULT_CARD_CONFIG);
		mustermannErika.put("ICAO9303", "MRZ", "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6");
		
		
		CardConfig rusoran = createNewCardConfig("ePassport DEUMAY MSQ244225");
		rusoran.put("ICAO9303", "MRZ", "P<DEUMAY<<ALEXANDER<<<<<<<<<<<<<<<<<<<<<<<<<MSQ2442259DEU7001017M2111153123456789<<<<<79");
		
		CardConfig hunRusoran = createNewCardConfig("ePassport HUNRUSORAN 0002179");
		hunRusoran.put("ICAO9303", "MRZ", "P<HUNRUSORAN<<GABRIELLA<<<<<<<<<<<<<<<<<<<<<0002179<<7HUN6506146F1602065<<<<<<<<<<<<<<02");

	}
	
	
	
	/**
	 * Return the card config with the given name. creates it if it does not
	 * exist yet
	 * 
	 * @param cardConfigName
	 * @return
	 */
	public static CardConfig get(String cardConfigName) {
		if (configs.containsKey(cardConfigName)) {
			return configs.get(cardConfigName);
		} else {
			return createNewCardConfig(cardConfigName);
		}
	}

	private static CardConfig createNewCardConfig(String cardConfigName) {
		CardConfig newConfig = new CardConfig(cardConfigName);
		configs.put(cardConfigName, newConfig);
		return newConfig;
	}

	public static Set<String> getAvailableConfigNames() {
		return configs.keySet();
	}

}
