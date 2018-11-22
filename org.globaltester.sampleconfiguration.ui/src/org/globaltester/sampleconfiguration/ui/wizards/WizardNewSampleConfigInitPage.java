package org.globaltester.sampleconfiguration.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.ui.SampleConfigEditorWidget;

public class WizardNewSampleConfigInitPage extends WizardPage {

	private SampleConfigEditorWidget editorWidget;

	public WizardNewSampleConfigInitPage(String pageName) {
		super(pageName);

	}

	@Override
	public void createControl(Composite parent) {
		
		// create main composite for this page
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new FillLayout());

		editorWidget = new SampleConfigEditorWidget(container);
		editorWidget.setActive(true);
		editorWidget.hideName();
	}
	
	@Override
	public IWizardPage getPreviousPage() {
		//do not allow 
		return null;
	}
	
	public void doSave() {
		editorWidget.doSave();
	}

	public void setProject(IProject project) {
		editorWidget.setInput(SampleConfig.getSampleConfigForProject(project));
		
	}

}
