package org.globaltester.cardconfiguration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Assert;
import org.junit.Test;

public class GtCardConfigProjectTest {

	@Test(expected = AssertionFailedException.class)
	public void testCreateProjectWithEmptyNameArg() {
		String projectName = " "; //$NON-NLS-1$
		GtCardConfigProject.createProject(projectName, null);
	}

	@Test(expected = AssertionFailedException.class)
	public void testCreateProjectWithNullNameArg() {
		String projectName = null;
		GtCardConfigProject.createProject(projectName, null);
	}

	@Test
	public void testCreateProjectWithGoodArgs() throws Exception {
		String projectName = "junitTestProject-deleteMe";

		IProject project = GtCardConfigProject.createProject(projectName, null);

		// check nature is added
		Assert.assertTrue("GtCardConfigNature is not correctly added",
				project.hasNature(GtCardConfigNature.NATURE_ID));

		// delete the project after the test
		project.delete(true, null);
	}

}
