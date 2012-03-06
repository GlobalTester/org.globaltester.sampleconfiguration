package org.globaltester.cardconfiguration;

public class CardConfigManager {

	//FIXME AMY replace the following stubs with a logic that stores/retrieves CardConfigs from workspace
	public static String DEFAULT_CARD_CONFIG = "nPA mustermann Erika";
	private static CardConfig mustermannErika = new CardConfig(DEFAULT_CARD_CONFIG);
	static {
		mustermannErika.put("ICAO9303", "MRZ", "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6");
	}
	
	/**
	 * Return the card config with the given name. creates it if it does not
	 * exist yet
	 * 
	 * @param cardConfigName
	 * @return
	 */
	public static CardConfig get(String cardConfigName) {
		return mustermannErika;
	}

}
