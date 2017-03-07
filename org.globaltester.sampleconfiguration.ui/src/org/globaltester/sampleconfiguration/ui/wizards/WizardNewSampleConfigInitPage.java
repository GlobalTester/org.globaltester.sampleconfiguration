package org.globaltester.sampleconfiguration.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.ui.SampleConfigEditorWidget;

public class WizardNewSampleConfigInitPage extends WizardPage {

	private SampleConfig sampleConfig;
	private SampleConfigEditorWidget editorWidget;

	public WizardNewSampleConfigInitPage(String pageName) {
		super(pageName);

		sampleConfig = new SampleConfig();
	}

	@Override
	public void createControl(Composite parent) {
		
		// create main composite for this page
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new FillLayout());

		editorWidget = new SampleConfigEditorWidget(container);
		editorWidget.hideName();
		editorWidget.setActive(true);
		editorWidget.setInput(sampleConfig);
	}
	
	

	public SampleConfig getSampleConfig() {
		editorWidget.doSave();
		return sampleConfig;
	}

}
