package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

/**
 * Default Editor for CategoryParameter that no known Editor is associated.
 * <p/>
 * This is essentially a {@link StringCategoryParameterEditor} with an
 * obviously wrong background.
 * 
 * @author amay
 *
 */
public class UnknownCategoryParameterEditor extends StringCategoryParameterEditor {

	public UnknownCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super(tabItemComp, curParamDescriptor);

		Display display = Display.getCurrent();
		Color bgColor = display.getSystemColor(SWT.COLOR_YELLOW);

		lbl.setBackground(bgColor);
	}

}
