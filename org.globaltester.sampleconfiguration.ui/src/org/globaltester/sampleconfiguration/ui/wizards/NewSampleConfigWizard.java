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
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.sampleconfiguration.GtSampleConfigProject;
import org.globaltester.sampleconfiguration.ui.UiImages;

public class NewSampleConfigWizard extends Wizard implements INewWizard, IPageChangingListener {
	
	private WizardNewProjectCreationPage projectCreationPage;
	private WizardNewSampleConfigInitPage sampleConfigInitPage;
	private IProject project = null;

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
	    sampleConfigInitPage.doSave();
	    
	    return true;
	}
	
	@Override
	public boolean performCancel() {
		if (project != null) {
			try {
				project.delete(true, true, null);
			} catch (CoreException e) {
				BasicLogger.logException("unable to delete temporarily created SampleConfig project", e, LogLevel.DEBUG);
			}
		}
		return super.performCancel();
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
	    addPage(sampleConfigInitPage);

	}
	
	@Override
	public void handlePageChanging(PageChangingEvent event) {
		if (project == null) {
			String name = projectCreationPage.getProjectName();
		    URI location = (projectCreationPage.useDefaults()) ? null : projectCreationPage.getLocationURI();
	    	project = GtSampleConfigProject.createProject(name, location);
	    	sampleConfigInitPage.setProject(project);
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
