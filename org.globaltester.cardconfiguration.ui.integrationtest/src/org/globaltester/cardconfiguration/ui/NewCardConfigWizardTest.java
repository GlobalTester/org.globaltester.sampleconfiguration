package org.globaltester.cardconfiguration.ui;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.cardconfiguration.GtCardConfigNature;
import org.globaltester.swtbot.Strings;
import org.globaltester.swtbot.uihelper.CardConfigWizardUiHelper;
import org.globaltester.swtbot.uihelper.GlobalTesterUiHelper;
import org.junit.Before;
import org.junit.Test;

public class NewCardConfigWizardTest {
	
	@Before
	public void setUpClass() throws CoreException {
		GlobalTesterUiHelper.init();
	}

	@Test
	public void testCreateNewDefaultCardConfig() throws CoreException {
		String cardConfigName = "MustermannErika";
		
		CardConfigWizardUiHelper wizard = GlobalTesterUiHelper.openNewWizardByMenu().selectCardConfiguration();
		wizard.setProjectName(cardConfigName);
		wizard.captureScreenshot(Strings.FILE_SCREENSHOTS_SUBFOLDER + File.separator + "newCardConfigWizard.png");
		wizard.finish();
		
		IProject createdProject = ResourcesPlugin.getWorkspace().getRoot()
		.getProject(cardConfigName);
		
		assertTrue("Project was not created", createdProject.exists());
		assertTrue("Project does not contain nature", createdProject.hasNature(GtCardConfigNature.NATURE_ID));
	}
}