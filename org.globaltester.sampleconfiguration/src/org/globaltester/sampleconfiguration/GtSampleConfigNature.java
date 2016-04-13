package org.globaltester.sampleconfiguration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class GtSampleConfigNature implements IProjectNature {
	
	public static final String NATURE_ID = "org.globaltester.sampleconfiguration.gtSampleConfigNature"; //$NON-NLS-1$
	private IProject project = null;

	@Override
	public void configure() throws CoreException {
		// currently no action needed here
	}

	@Override
	public void deconfigure() throws CoreException {
		// currently no action needed here		
	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
