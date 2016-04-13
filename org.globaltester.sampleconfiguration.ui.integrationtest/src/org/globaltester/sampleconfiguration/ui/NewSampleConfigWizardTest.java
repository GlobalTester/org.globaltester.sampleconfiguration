package org.globaltester.sampleconfiguration.ui;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.sampleconfiguration.GtSampleConfigNature;
import org.globaltester.swtbot.Strings;
import org.globaltester.swtbot.uihelper.SampleConfigWizardUiHelper;
import org.globaltester.swtbot.uihelper.GlobalTesterUiHelper;
import org.junit.Before;
import org.junit.Test;

public class NewSampleConfigWizardTest {
	
	@Before
	public void setUpClass() throws CoreException {
		GlobalTesterUiHelper.init();
	}

	@Test
	public void testCreateNewDefaultSampleConfig() throws CoreException {
		String sampleConfigName = "MustermannErika";
		
		SampleConfigWizardUiHelper wizard = GlobalTesterUiHelper.openNewWizardByMenu().selectSampleConfiguration();
		wizard.setProjectName(sampleConfigName);
		wizard.captureScreenshot(Strings.FILE_SCREENSHOTS_SUBFOLDER + File.separator + "newSampleConfigWizard.png");
		wizard.finish();
		
		IProject createdProject = ResourcesPlugin.getWorkspace().getRoot()
		.getProject(sampleConfigName);
		
		assertTrue("Project was not created", createdProject.exists());
		assertTrue("Project does not contain nature", createdProject.hasNature(GtSampleConfigNature.NATURE_ID));
	}
}
