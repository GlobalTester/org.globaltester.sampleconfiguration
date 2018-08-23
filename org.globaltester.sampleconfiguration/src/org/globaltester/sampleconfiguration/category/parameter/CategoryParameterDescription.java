package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Describes a parameter that may be used by a category.
 * <p/>
 * A category parameter itself is essentially a String variable which may be
 * evaluated by the category to whatever is needed in the category context.
 * <p/>
 * This interface is intentionally small so that implementing classes can handle
 * their specific type of data matching their needs. E.g. restricting values or
 * providing default values it completely out of scope as not all category
 * parameters do need this.
 * 
 * @author amay
 *
 */
public interface CategoryParameterDescription {

	/**
	 * Return the name of this parameter.
	 * <p/>
	 * This is used together with the name of the defining category to
	 * explicitly reference this parameter.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Return the name of the category this parameter is associated with.
	 * <p/>
	 * This is used together with the name of the parameter to
	 * explicitly reference this parameter.
	 * 
	 * @return
	 */
	public String getCategoryName();

	/**
	 * Return the user description of this parameter.
	 * <p/>
	 * This will be used within the UI for example to label input elements.
	 * 
	 * @return
	 */
	public String getDescription();

	public ParameterGenerator getGenerator();
	
}
