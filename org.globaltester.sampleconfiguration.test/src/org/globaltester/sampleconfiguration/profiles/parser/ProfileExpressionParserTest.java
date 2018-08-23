package org.globaltester.sampleconfiguration.profiles.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;
import org.junit.Test;

public class ProfileExpressionParserTest {
	@Test
	public void testParsingEmpty(){
		String testString = "";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals("true", parsed.toString());
	}
	@Test
	public void testParsingSimpleToken(){
		String testString = "protocol_token";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}
	
	@Test
	public void testParsingInvalidProfile(){
		String testString = "protocoltoken";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertTrue(parsed instanceof UnparseableProfileExpression);
	}
	
	@Test
	public void testParsingInvalidOperation(){
		String testString = "INVALIDOP(true)";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertTrue(parsed instanceof UnparseableProfileExpression);
	}
	
	@Test
	public void testParsingInvalidBracesClosing(){
		String testString = "AND(true))";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals("AND(UNPARSEABLE(true)))", parsed.toString());
	}
	
	@Test
	public void testParsingInvalidBracesOpening(){
		String testString = "AND((true)";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals("AND(UNPARSEABLE((true))", parsed.toString());
	}
	
	@Test
	public void testParsingFlippedBraces(){
		String testString = "AND)(";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertTrue(parsed instanceof UnparseableProfileExpression);
	}
	
	@Test
	public void testParsingCorrectBraces(){
		String testString = "AND(true)";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}

	@Test
	public void testParsingAndExpression(){
		String testString = "AND(protocol_token,protocol_token2)";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}
	
	@Test
	public void testParsingOrExpression(){
		String testString = "OR(protocol_token,protocol_token2)";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}
	
	@Test
	public void testParsingTrue(){
		String testString = "true";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}
	
	@Test
	public void testParsingFalse(){
		String testString = "false";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}
	
	@Test
	public void testParsingComplexExpression(){
		String testString = "OR(AND(protocol_token,NOT(protocol_token2)),AND(protocol2_token,protocol3_token))";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}
	
	@Test
	public void testGetGroupLength() throws Exception{
		String testString = "((x(xx))x(x))xx";
		assertEquals(testString.length()-2, ProfileExpressionParser.getGroupLength(testString, 0));
	}
}
