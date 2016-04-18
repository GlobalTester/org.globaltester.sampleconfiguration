package org.globaltester.sampleconfiguration.profiles.parser;

import java.util.LinkedList;
import java.util.List;

import org.globaltester.sampleconfiguration.profiles.Profile;
import org.globaltester.sampleconfiguration.profiles.expressions.AndProfileExpression;
import org.globaltester.sampleconfiguration.profiles.expressions.NotProfileExpression;
import org.globaltester.sampleconfiguration.profiles.expressions.OrProfileExpression;
import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;
import org.globaltester.sampleconfiguration.profiles.expressions.ValueProfileExpression;

public class ProfileExpressionParser {
	
	public static ProfileExpression parse(String expressionString){
		return parseRecursive(expressionString);
	}
	
	protected static int getGroupLength(String expressionString, int initialOffset){
		int offset = initialOffset;
		if (expressionString.charAt(offset) != '('){
			throw new IllegalArgumentException("This method needs to be called with the starting offset of a group");
		}
		
		int openedGroups = 0;
		
		char currentChar;
		do{
			currentChar = expressionString.charAt(offset++);
			switch (currentChar) {
			case '(':
				openedGroups++;
				break;
			case ')':
				openedGroups--;
				break;
			}
		} while (openedGroups > 0 && offset < expressionString.length());
		return offset - initialOffset;
	}
	
	private static ProfileExpression [] parseContent(String content){
		List<ProfileExpression> expressions = new LinkedList<>();
		while (content.length() > 0){
			int tokenEnd = content.indexOf(',');
			
			if (tokenEnd == 0){
				content = content.substring(1);
				tokenEnd = content.indexOf(',');
			}
			
			int groupBegin = content.indexOf("(");
			
			if ((tokenEnd != -1 && groupBegin != -1 && tokenEnd < groupBegin) || groupBegin == -1 ){
				if (tokenEnd == -1){
					tokenEnd = content.length();
				}
				
				expressions.add(parseRecursive(content.substring(0, tokenEnd)));
				content = content.substring(tokenEnd);
			} else {
				int length = getGroupLength(content, groupBegin);
				expressions.add(parseRecursive(content.substring(0, groupBegin+length)));
				content = content.substring(groupBegin + length);
			}
		}
		
		return expressions.toArray(new ProfileExpression[expressions.size()]);
	}
	
	private static ProfileExpression parseRecursive(String expressionString){
		expressionString = expressionString.trim();
		if (expressionString.isEmpty()){
			return new ValueProfileExpression(true);
		}
		if (! expressionString.endsWith(")")){
			if (expressionString.contains("(")){
				throw new IllegalArgumentException("Found unexpected brace in expression leaf");
			}
			return new Profile(expressionString);
		}
		int firstBrace = expressionString.indexOf('(');
		
		String operator = expressionString.substring(0, firstBrace);
		String content = expressionString.substring(firstBrace + 1, expressionString.length() - 1);
		
		switch (operator) {
		case "AND":
			return new AndProfileExpression(parseContent(content));
		case "OR":
			return new OrProfileExpression(parseContent(content));
		case "NOT":
			return new NotProfileExpression(parseRecursive(content));
		case "VALUE":
			return new ValueProfileExpression(Boolean.parseBoolean(content));
		default:
			return null;
		}
	}

}
