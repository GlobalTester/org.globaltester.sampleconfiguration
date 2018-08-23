package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Not an actual category parameter but a means add a spacer. This might be
 * mapped to an empty label within UI usage.
 * 
 * @author cstroh
 *
 */
public class SpacerCategoryParameter extends AbstractCategoryParameterDescription
		implements CategoryParameterDescription {

	public SpacerCategoryParameter(String categoryName,  String name, String description) {
		super(categoryName, name, description);
	}

}
