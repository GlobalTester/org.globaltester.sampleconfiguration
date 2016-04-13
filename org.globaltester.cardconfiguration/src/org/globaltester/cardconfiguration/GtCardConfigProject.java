package org.globaltester.cardconfiguration;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.base.resources.GtResourceHelper;
import org.globaltester.logging.legacy.logger.GtErrorLogger;


/**
 * Represents and handles the workspace representation of a TestCampaign
 * 
 * @author amay
 * 
 */
public class GtCardConfigProject {
	
	/**
	 * Create a GlobalTester TestCampaign Project. This includes creation
	 * of the Eclipse project, adding the according nature and creating the
	 * initial folder structure.
	 * 
	 * @param projectName
	 *            name of the project to be created
	 * @param location
	 *            location where the project shall be created. If empty the
	 *            default workspace location will be used.
	 * @return the created project
	 */
	public static IProject createProject(String projectName, URI location) {
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);

		IProject project = GtResourceHelper.createEmptyProject(projectName, location);
		try {
			GtResourceHelper.addNature(project, GtCardConfigNature.NATURE_ID);

		} catch (CoreException e) {
			e.printStackTrace();
			project = null;
		}
		
		// refresh the workspace
		try {
			ResourcesPlugin.getWorkspace().getRoot()
					.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			// refresh workspace failed
			// log CoreException to eclipse log
			GtErrorLogger.log(Activator.PLUGIN_ID, e);

			// users most probably will ignore this behavior and refresh
			// manually, so do not open annoying dialog
		}

		return project;
	}

}
