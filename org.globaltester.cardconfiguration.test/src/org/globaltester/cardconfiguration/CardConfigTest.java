package org.globaltester.cardconfiguration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CardConfigTest {
	
	private final String DEFAULT_MRZ = "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6";

	@Test
	public void testGetMrz() {
		assertEquals(DEFAULT_MRZ, new CardConfig().get("ICAO9303", "MRZ"));
	}

}
