package org.globaltester.sampleconfiguration.profiles.parser;

import static org.junit.Assert.assertEquals;

import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;
import org.junit.Test;

public class ProfileExpressionParserTest {
	@Test
	public void testParsingSimpleToken(){
		String testString = "protocol_token";
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
	public void testParsingComplexExpression(){
		String testString = "OR(AND(protocol_token,NOT(protocol_token2)),AND(protocol2_token,protocol3_token))";
		ProfileExpression parsed = ProfileExpressionParser.parse(testString);
		assertEquals(testString, parsed.toString());
	}
	
	@Test
	public void testGetGroupLength(){
		String testString = "((x(xx))x(x))xx";
		assertEquals(testString.length()-2, ProfileExpressionParser.getGroupLength(testString, 0));
	}
}
