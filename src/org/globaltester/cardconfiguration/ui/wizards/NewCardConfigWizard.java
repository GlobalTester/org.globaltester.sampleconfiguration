package org.globaltester.cardconfiguration.ui.wizards;

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
import org.globaltester.cardconfiguration.CardConfig;
import org.globaltester.cardconfiguration.GtCardConfigProject;
import org.globaltester.cardconfiguration.ui.Activator;
import org.globaltester.logging.logger.GtErrorLogger;

public class NewCardConfigWizard extends Wizard implements INewWizard, IPageChangingListener {
	
	private WizardNewProjectCreationPage projectCreationPage;
	private WizardNewCardConfigInitPage cardConfigInitPage;

	public NewCardConfigWizard() {
		setWindowTitle("GlobalTester CardConfiguration Wizard");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	@Override
	public boolean performFinish() {
		String name = projectCreationPage.getProjectName();
	    URI location = (projectCreationPage.useDefaults()) ? null : projectCreationPage.getLocationURI();
	    IProject createdProject = GtCardConfigProject.createProject(name, location);
	    
	    CardConfig createdCardConfig = cardConfigInitPage.getCardConfig();
	    createdCardConfig.setProject(createdProject);
	    try {
			createdCardConfig.saveToProject();
		} catch (CoreException e) {
			GtErrorLogger.log(Activator.PLUGIN_ID, e);
		}

	    return true;
	}
	
	@Override
	public void addPages() {
	    super.addPages();

	    projectCreationPage = new WizardNewProjectCreationPage("GlobalTester CardConfiguration Project Wizard");
	    projectCreationPage.setTitle("GlobalTester CardConfiguration Project");
	    projectCreationPage.setDescription("Create a new GlobalTester CardConfiguration");
	    addPage(projectCreationPage);
	    
	    cardConfigInitPage = new WizardNewCardConfigInitPage("GlobalTester CardConfiguration Project Wizard");
	    cardConfigInitPage.setTitle("GlobalTester CardConfiguration Project");
	    cardConfigInitPage.setDescription("Create a new GlobalTester CardConfiguration");
	    cardConfigInitPage.setProjectCreationPage(projectCreationPage);
	    addPage(cardConfigInitPage);

	}
	
	@Override
	public void handlePageChanging(PageChangingEvent event) {
		
		if (event.getTargetPage().equals(cardConfigInitPage)){
			cardConfigInitPage.setCardConfigName(projectCreationPage.getProjectName());
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
