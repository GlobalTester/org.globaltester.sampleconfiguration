package org.globaltester.sampleconfiguration.ui.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.globaltester.logging.logger.GtErrorLogger;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.ui.Activator;
import org.globaltester.sampleconfiguration.ui.UiImages;
import org.globaltester.sampleconfiguration.GtSampleConfigProject;

public class NewSampleConfigWizard extends Wizard implements INewWizard, IPageChangingListener {
	
	private WizardNewProjectCreationPage projectCreationPage;
	private WizardNewSampleConfigInitPage sampleConfigInitPage;

	public NewSampleConfigWizard() {
		setWindowTitle("GlobalTester SampleConfiguration Wizard");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setDefaultPageImageDescriptor(UiImages.SAMPLECONFIG_BANNER
				.getImageDescriptor());
	}

	@Override
	public boolean performFinish() {
		String name = projectCreationPage.getProjectName();
	    URI location = (projectCreationPage.useDefaults()) ? null : projectCreationPage.getLocationURI();
	    IProject createdProject = GtSampleConfigProject.createProject(name, location);
	    
	    SampleConfig createdSampleConfig = sampleConfigInitPage.getSampleConfig();
	    createdSampleConfig.setProject(createdProject);
	    try {
			createdSampleConfig.saveToProject();
		} catch (CoreException e) {
			GtErrorLogger.log(Activator.PLUGIN_ID, e);
		}

	    return true;
	}
	
	@Override
	public void addPages() {
	    super.addPages();

	    projectCreationPage = new WizardNewProjectCreationPage("GlobalTester SampleConfiguration Project Wizard");
	    projectCreationPage.setTitle("GlobalTester SampleConfiguration Project");
	    projectCreationPage.setDescription("Create a new GlobalTester SampleConfiguration");
	    addPage(projectCreationPage);
	    
	    sampleConfigInitPage = new WizardNewSampleConfigInitPage("GlobalTester SampleConfiguration Project Wizard");
	    sampleConfigInitPage.setTitle("GlobalTester SampleConfiguration Project");
	    sampleConfigInitPage.setDescription("Create a new GlobalTester SampleConfiguration");
	    sampleConfigInitPage.setProjectCreationPage(projectCreationPage);
	    addPage(sampleConfigInitPage);

	}
	
	@Override
	public void handlePageChanging(PageChangingEvent event) {
		
		if (event.getTargetPage().equals(sampleConfigInitPage)){
			sampleConfigInitPage.setSampleConfigName(projectCreationPage.getProjectName());
		}
	}

	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);
		if (wizardContainer instanceof WizardDialog){
			((WizardDialog)wizardContainer).addPageChangingListener(this);
		}
	}

}
