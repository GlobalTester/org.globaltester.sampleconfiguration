package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

/**
 * Allows setting {@link String} values for {@link SampleConfig} parameters.
 * @author amay
 *
 */
public class StringCategoryParameterEditor extends AbstractCategoryParameterEditor {
	
	CategoryParameterDescription paramDescriptor;
	
	Label lbl;
	Text valueField;

	public StringCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super(curParamDescriptor);

		lbl = new Label(tabItemComp, SWT.NONE);
		lbl.setText(curParamDescriptor.getDescription());

		valueField = new Text(tabItemComp, getStyle());
		valueField.setFont(CategoryParameterEditorFactory.FONT_MONOSPACE);
		valueField.setLayoutData(getLayoutData());
	}

	/**
	 * @return the {@link GridData} to be used for laouting the the value field of this editor
	 */
	protected GridData getLayoutData() {
		return new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
	}

	/**
	 * @see Text
	 * @return the style value to be used for the value field
	 */
	protected int getStyle() {
		return SWT.BORDER;
	}

	@Override
	public void setValue(String newValue) {
		valueField.setText(newValue);
	}

	@Override
	public String getValue() {
		return valueField.getText();
	}

	@Override
	public void setActive(boolean active) {
		valueField.setEditable(active);
		valueField.setEnabled(active);
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		valueField.addListener(eventType, listener);
	}

}
