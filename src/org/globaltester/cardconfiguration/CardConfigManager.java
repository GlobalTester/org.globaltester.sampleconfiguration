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

	private static String DEFAULT_CARD_CONFIG = "Mustermann Erika";

	/**
	 * Return the card config with the given name. 
	 * Returns DEFAULT_CARD_CONFIG if not present.
	 * exist yet
	 * 
	 * @param cardConfigName
	 * @return
	 */
	public static CardConfig get(String cardConfigName) {
		if (!configs.containsKey(cardConfigName)) {
			return getDefaultConfig();
		}
		return configs.get(cardConfigName);
	}

	private static CardConfig createNewCardConfig(String cardConfigName) {
		// FIXME AMY CardConfig remove the method CreateNewCardConfig and migrate it to a Wizard
		IProject project = GtCardConfigProject.createProject(cardConfigName,
				null);
		CardConfig newConfig = new CardConfig(project);
		return newConfig;
	}

	public static Set<String> getAvailableConfigNames() {
		return GtResourceHelper
				.getProjectNamesWithNature(GtCardConfigNature.NATURE_ID);
	}

	public static CardConfig getDefaultConfig() {
		if (!configs.containsKey(DEFAULT_CARD_CONFIG)) {
			CardConfig newConfig = createNewCardConfig(DEFAULT_CARD_CONFIG);
			// init the default card config with default values
			newConfig
					.put("ICAO9303",
							"MRZ",
							"P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6");

		}
		return get(DEFAULT_CARD_CONFIG);
	}

	public static void register(CardConfig cardConfig) {
		configs.put(cardConfig.getName(), cardConfig);
	}

}
