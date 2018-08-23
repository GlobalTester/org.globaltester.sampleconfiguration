package org.globaltester.sampleconfiguration.category;

import java.util.List;

import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

/**
 * OSGi-Service that describes the category (its parameters and configuration)
 * and allows instantiation.
 * <p/>
 * This Service is used to find available categorys, provide the relevant
 * configuration parameter descriptions and allow instantiation w/o problems
 * with OSGi related class loading issues.
 * 
 * @author amay
 *
 */
public interface CategoryFactory {

	/**
	 * The human readable category name
	 * @return the name of the category
	 */
	public String getName();

	/**
	 * Provide information about the available parameters that the created
	 * category will be able to handle.
	 * <p/>
	 * The order of the returned List will be used to order/group the mentioned
	 * parameters within automatically generated editors. Thus special
	 * {@link CategoryParameterDescriptors} are allowed that allow formatting.
	 * 
	 * @return
	 */
	public List<CategoryParameterDescription> getParameterDescriptors();

}
