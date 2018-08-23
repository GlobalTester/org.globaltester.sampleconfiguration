package org.globaltester.sampleconfiguration.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public class DirectoryCategoryParameterEditor extends AbstractCategoryParameterEditor{
	
	Label lbl;
	Text valueField;
	Button btnFileDialog;
	Composite certComposite;
	
	public DirectoryCategoryParameterEditor(Composite tabItemComp, CategoryParameterDescription curParamDescriptor) {
		super(curParamDescriptor);
		
		certComposite = new Composite(tabItemComp, SWT.NONE);
		GridData gdCertComp = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		GridLayout certLayout = new GridLayout(3, false);
		certComposite.setLayout(certLayout);
		certComposite.setLayoutData(gdCertComp);
		
		lbl = new Label(certComposite, SWT.NONE);
		lbl.setText(curParamDescriptor.getDescription());
		GridData gdLbl = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gdLbl.widthHint = 110;
		lbl.setLayoutData(gdLbl);
		
		valueField = new Text(certComposite, SWT.BORDER);
		GridData gdValueField = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		valueField.setLayoutData(gdValueField);
		
		btnFileDialog = new Button(certComposite, SWT.PUSH);
		btnFileDialog.setText("Browse...");
		
		btnFileDialog.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				DirectoryDialog directoryDialog = new DirectoryDialog(certComposite.getShell());
				
				directoryDialog.setFilterPath(valueField.getText());
				directoryDialog.setMessage("Please select a certificate and click OK");
				
				String dir = directoryDialog.open();
				if (dir != null) {
					valueField.setText(dir);
				}
			}
		});
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
		btnFileDialog.setEnabled(active);
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		valueField.addListener(eventType, listener);
	}
}
