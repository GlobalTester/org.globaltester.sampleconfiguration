package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Generic category parameter that defines a String value.
 * 
 * @author amay
 *
 */
public class MultilineStringCategoryParameter extends StringCategoryParameter {

	private int numberOfLines;

	public MultilineStringCategoryParameter(String categoryName, String name, String description, int numberOfLines) {
		super(categoryName, name, description);
		this.numberOfLines = numberOfLines;
	}

	public int getNumberOfLines(){
		return numberOfLines;
	}
	
}
