package org.globaltester.cardconfiguration;

import static org.junit.Assert.*;
import static org.globaltester.cardconfiguration.CardConfigManager.DEFAULT_CARD_CONFIG;

import org.junit.Test;

public class CardConfigTest {
	
	private final String DEFAULT_MRZ = "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6";

	@Test
	public void testGetMrz() {
		assertEquals(DEFAULT_MRZ, CardConfigManager.get(DEFAULT_CARD_CONFIG).get("ICAO9303", "MRZ"));
	}

}
