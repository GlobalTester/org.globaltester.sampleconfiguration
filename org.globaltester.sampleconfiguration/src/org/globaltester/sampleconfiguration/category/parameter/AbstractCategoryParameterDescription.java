package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Abstract super implementation for {@link CategoryParameterDescription}
 * <p/>
 * This class handles the basic requirements of a CategoryParameterDescription
 * to remove that burden from implementations. It is not meant to be
 * instantiated directly.
 * 
 * @author amay
 *
 */
public abstract class AbstractCategoryParameterDescription implements CategoryParameterDescription {

	protected String categoryName = "";
	protected String name = "";
	protected String description = "";

	public AbstractCategoryParameterDescription(String categoryName, String name) {
		this.categoryName = categoryName;
		this.name = name;
	}

	public AbstractCategoryParameterDescription(String categoryName, String name, String description) {
		this(categoryName, name);
		this.description = description;
	}

	@Override
	public String getCategoryName() {
		return categoryName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public ParameterGenerator getGenerator() {
		return null;
	}

	@Override
	public String toString() {
		return "AbstractCategoryParameterDescription [categoryName=" + categoryName + ", name=" + name
				+ ", description=" + description + "]";
	}
	
}