package org.globaltester.sampleconfiguration;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.globaltester.base.resources.GtResourceHelper;

/**
 * This class manages all mutable  {@link SampleConfig}s stored in projects within
 * the workspace.
 * 
 * @author amay
 * 
 */
public class SampleConfigManager {

//	private static HashMap<String, SampleConfig> configs = new HashMap<String, SampleConfig>();
	static {
		for (String curConfigName : getAvailableConfigNames()) {
			IProject curProject = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(curConfigName);
			SampleConfig curConfig = new SampleConfig(curProject);
//			configs.put(curConfigName, curConfig);
		}
	}

	/**
	 * Return the {@link SampleConfig} with the given name, or null if this does not exists in the current
	 * workspace.
	 * 
	 * @param sampleConfigName
	 * @return
	 */
	public static SampleConfig get(String sampleConfigName) {
//		if (!configs.containsKey(sampleConfigName)) {
			if (getAvailableConfigNames().contains(sampleConfigName)) {
				SampleConfig newConfig = new SampleConfig(ResourcesPlugin
						.getWorkspace().getRoot().getProject(sampleConfigName));
//				configs.put(sampleConfigName, newConfig);
				return newConfig;
			} 
			return null;
//		}
//		return configs.get(sampleConfigName);
	}

	public static Set<String> getAvailableConfigNames() {
		return GtResourceHelper
				.getProjectNamesWithNature(GtSampleConfigNature.NATURE_ID);
	}

	public static void register(SampleConfig sampleConfig) {
//		if (sampleConfig.isStoredAsProject()) {
//			configs.put(sampleConfig.getName(), sampleConfig);
//		}
	}

	public static void remove(SampleConfig sampleConfig) {
//		if (sampleConfig.equals(configs.get(sampleConfig.getName()))) {
//			configs.remove(sampleConfig.getName());
//		}
	}

	public static boolean isAvailableAsProject(String sampleConfigName) {
		return getAvailableConfigNames().contains(sampleConfigName);
	}

}
