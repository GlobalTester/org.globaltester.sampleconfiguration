package org.globaltester.cardconfiguration.ui;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.globaltester.cardconfiguration.GtCardConfigNature;
import org.globaltester.rcp.ApplicationWorkbenchWindowAdvisor;
import org.junit.Before;
import org.junit.Test;

public class NewCardConfigWizardTest {

	private SWTWorkbenchBot bot = new SWTWorkbenchBot();

	@Before
	public void setUpClass() {
		bot.waitUntil(Conditions
				.shellIsActive(ApplicationWorkbenchWindowAdvisor.gtMainWindowTitle));

		SWTBotView activeView = bot.activeView();

		// close the welcome page, if it is shown
		if ("Welcome".equals(activeView.getTitle())) {
			bot.viewByTitle("Welcome").close();
		}
	}

	public void openNewCardConfigWizard() {
		bot.menu("File").menu("New").click();

		SWTBotShell newDialogBot = bot.shell("New");
		SWTBotTree newDialogTree = newDialogBot.bot().tree();

		newDialogTree.expandNode("GlobalTester", true).select("CardConfiguration");

		bot.button("Next >").click();

	}

	@Test
	public void testCreateNewDefaultCardConfig() throws CoreException {
		String cardConfigName = "MustermannErika";

		openNewCardConfigWizard();

		bot.captureScreenshot("screenshots" + File.separator
				+ "newCardConfigWizard.png");

		bot.text().setText(cardConfigName);
		bot.button("Next >").click();
		
		bot.button("Finish").click();
		
		IProject createdProject = ResourcesPlugin.getWorkspace().getRoot()
		.getProject(cardConfigName);
		
		assertTrue("Project was not created", createdProject.exists());
		assertTrue("Project does not contain nature", createdProject.hasNature(GtCardConfigNature.NATURE_ID));

	}
}