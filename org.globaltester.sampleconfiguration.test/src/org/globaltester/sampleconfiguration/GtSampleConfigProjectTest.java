package org.globaltester.sampleconfiguration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.AssertionFailedException;
import org.globaltester.sampleconfiguration.GtSampleConfigNature;
import org.globaltester.sampleconfiguration.GtSampleConfigProject;
import org.junit.Assert;
import org.junit.Test;

public class GtSampleConfigProjectTest {

	@Test(expected = AssertionFailedException.class)
	public void testCreateProjectWithEmptyNameArg() {
		String projectName = " "; //$NON-NLS-1$
		GtSampleConfigProject.createProject(projectName, null);
	}

	@Test(expected = AssertionFailedException.class)
	public void testCreateProjectWithNullNameArg() {
		String projectName = null;
		GtSampleConfigProject.createProject(projectName, null);
	}

	@Test
	public void testCreateProjectWithGoodArgs() throws Exception {
		String projectName = "junitTestProject-deleteMe";

		IProject project = GtSampleConfigProject.createProject(projectName, null);

		// check nature is added
		Assert.assertTrue("GtSampleConfigNature is not correctly added",
				project.hasNature(GtSampleConfigNature.NATURE_ID));

		// delete the project after the test
		project.delete(true, null);
	}

}
