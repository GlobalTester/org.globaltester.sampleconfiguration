package org.globaltester.sampleconfiguration;

import static org.junit.Assert.assertEquals;

import org.globaltester.sampleconfiguration.SampleConfig;
import org.junit.Test;

public class SampleConfigTest {
	
	private final String DEFAULT_MRZ = "P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F2310314<<<<<<<<<<<<<<<4";

	@Test
	public void testGetMrz() {
		assertEquals(DEFAULT_MRZ, new SampleConfig().get("ICAO9303", "MRZ"));
	}

}
