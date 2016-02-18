package org.globaltester.cardconfiguration;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.globaltester.core.resources.GtResourceHelper;

/**
 * This class manages all mutable CardConfigurations stored in projects within
 * the workspace.
 * 
 * @author amay
 * 
 */
public class CardConfigManager {

	private static HashMap<String, CardConfig> configs = new HashMap<String, CardConfig>();
	static {
		for (String curConfigName : getAvailableConfigNames()) {
			IProject curProject = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(curConfigName);
			CardConfig curConfig = new CardConfig(curProject);
			configs.put(curConfigName, curConfig);
		}
	}

	/**
	 * Return the CardConfig with the given name, or null if this does not exists in the current
	 * workspace.
	 * 
	 * @param cardConfigName
	 * @return
	 */
	public static CardConfig get(String cardConfigName) {
		if (!configs.containsKey(cardConfigName)) {
			if (getAvailableConfigNames().contains(cardConfigName)) {
				CardConfig newConfig = new CardConfig(ResourcesPlugin
						.getWorkspace().getRoot().getProject(cardConfigName));
				configs.put(cardConfigName, newConfig);
			} 
		}
		return configs.get(cardConfigName);
	}

	public static Set<String> getAvailableConfigNames() {
		return GtResourceHelper
				.getProjectNamesWithNature(GtCardConfigNature.NATURE_ID);
	}

	public static void register(CardConfig cardConfig) {
		if (cardConfig.isStoredAsProject()) {
			configs.put(cardConfig.getName(), cardConfig);
		}
	}

	public static void remove(CardConfig cardConfig) {
		if (cardConfig.equals(configs.get(cardConfig.getName()))) {
			configs.remove(cardConfig.getName());
		}
	}

	public static boolean isAvailableAsProject(String cardConfigName) {
		return getAvailableConfigNames().contains(cardConfigName);
	}

}
