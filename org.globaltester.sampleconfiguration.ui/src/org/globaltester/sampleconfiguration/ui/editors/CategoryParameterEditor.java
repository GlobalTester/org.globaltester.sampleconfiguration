package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.widgets.Listener;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

/**
 * Defines SWT widgets that allow editing of a specific CategoryParameter.
 * <p/>
 * Implementing classes define the actual widgets and their behavior (e.g.
 * condition checking).
 * <p/>
 * {@link CategoryParameterEditor}s are created for every available
 * CategoryParamter once the {@link SampleConfigEditorWidget} is created. They can
 * be initialized with given values from an existing {@link SampleConfig}
 * and allow retrieving all relevant informations to store their value.
 * 
 * @author amay
 *
 */
public interface CategoryParameterEditor {
	
	/**
	 * @return the {@link CategoryParameterDescription} this editor is associated with
	 */
	public CategoryParameterDescription getCategoryParameterDescription();

	/**
	 * Set the value this editor displays based on the given parameter
	 */
	public void setValue(String newValue);

	/**
	 * Retrieve the currently displayed value from this editor.
	 */
	public String getValue();
	
	/**
	 * Sets the active state. This determines if the editor enabled for editing
	 * and modifies the visual representation to indicate the newly set state.
	 * 
	 * @param active
	 *            the new editable state
	 */
	public void setActive(boolean active);
	
	public void addListener(int eventType, Listener listener);

}
