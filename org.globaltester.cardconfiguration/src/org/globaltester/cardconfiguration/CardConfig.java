package org.globaltester.cardconfiguration;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.core.xml.XMLHelper;
import org.globaltester.logging.logger.GtErrorLogger;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

public class CardConfig implements IResourceChangeListener {

	public static final String DEFAULT_NAME = "Mustermann Erika";

	private static final String XML_TAG_NAME = "Name";
	private static final String XML_TAG_DESCRIPTION = "Description";
	private static final String XML_TAG_CONFIG_PARAMS = "ConfigurationParams";
	private static final String XML_TAG_PARAMETER = "Parameter";
	private static final String XML_ATTRIB_PARAM_NAME = "paramName";

	private HashMap<String, Object> configParams = new HashMap<String, Object>();
	private IProject project;
	private String name;
	private String descr;

	/**
	 * Creates a new instance which is populated with default values provided by
	 * protocol implementations.
	 */
	public CardConfig() {
		this.project = null;
		initWithDefaulValues();
	}

	private void initWithDefaulValues() {
		name = DEFAULT_NAME;
		descr = "Default configuration";

		configParams
				.put("ICAO9303_MRZ",
						"P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6");
	}

	public CardConfig(IProject proj) {
		this.project = proj;
		this.name = proj.getName();
		this.descr = "";

		if (getCardConfigIfile().exists()) {
			initFromIFile();
		} else {
			try {
				saveToProject();
			} catch (CoreException e) {
				GtErrorLogger.log(Activator.PLUGIN_ID, e);
			}
		}

		CardConfigManager.register(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
			      this, IResourceChangeEvent.POST_CHANGE);
	}

	public CardConfig(Element cardConfigElement) {
		this();

		extractFromXml(cardConfigElement);
	}

	private void initFromIFile() {
		IFile iFile = getCardConfigIfile();
		if (iFile.exists()) {
			Document doc = XMLHelper.readDocument(iFile);
			Element root = doc.getRootElement();
			extractFromXml(root);
		}
	}

	/**
	 * Returns a single parameter from this CardConfiguration
	 * 
	 * @param protocol
	 * @param key
	 * @return
	 */
	public Object get(String protocol, String key) {
		return configParams.get(protocol + "_" + key);
	}

	/**
	 * Add a single parameter in this CardConfig
	 * 
	 * @param protocol
	 * @param key
	 * @param value
	 */
	public void put(String protocol, String key, Object value) {
		configParams.put(protocol + "_" + key, value);
	}

	public String getName() {
		if (name == null){
			name = "";
		}
		return name;
	}

	public String getDescription() {
		if (descr == null) {
			descr = "";
		}
		return descr;
	}

	public void saveToProject() throws CoreException {
		// do not save if project is not defined
		if (project == null)
			return;

		IFile iFile = getCardConfigIfile();
		if (!iFile.exists()) {
			iFile.create(null, false, null);
		}

		Element root = getXmlRepresentation();

		// write to file
		XMLHelper.saveDoc(iFile, root);

		// TODO extract/display contained data to individual files (for user
		// convenience)
	}

	public IFile getCardConfigIfile() {
		//TODO make this private once it is no longer needed for TextEditorInput
		if (project != null) {
			return project.getFile("cardconfig.xml");
		} else {
			return null;
		}
	}

	private Element getXmlRepresentation() throws CoreException {
		Element root = new Element(getXmlRootElementName());
		dumpToXml(root);

		return root;
	}

	/**
	 * 
	 * @param root
	 */
	public void dumpToXml(Element root) {
		// add meta data
		Element nameElem = new Element(XML_TAG_NAME);
		nameElem.addContent(name);
		root.addContent(nameElem);
		Element descrElem = new Element(XML_TAG_DESCRIPTION);
		descrElem.addContent(descr);
		root.addContent(descrElem);

		// add configParams
		Element configParamsElem = new Element(XML_TAG_CONFIG_PARAMS);
		for (String curParam : configParams.keySet()) {
			Element curParamElem = new Element(XML_TAG_PARAMETER);
			curParamElem.setAttribute(XML_ATTRIB_PARAM_NAME, curParam);
			String curParamValue = (String) configParams.get(curParam);
			if (curParamValue.contains("<")){
				curParamElem.addContent(new CDATA(curParamValue));
			} else {
				curParamElem.addContent(curParamValue);	
			}
			configParamsElem.addContent(curParamElem);
		}
		root.addContent(configParamsElem);

	}

	protected String getXmlRootElementName() {
		return "CardConfig";
	}

	void extractFromXml(Element root) {
		// extract name
		this.name = root.getChildTextTrim(XML_TAG_NAME);
		this.descr = root.getChildTextTrim(XML_TAG_DESCRIPTION);

		// extract configParams
		Element paramsElem = root.getChild(XML_TAG_CONFIG_PARAMS);
		if (paramsElem != null) {
			for (Object curParamObj : paramsElem.getChildren(XML_TAG_PARAMETER)) {
				if (curParamObj instanceof Element) {
					Element curParamElem = (Element) curParamObj;
					String curParamName = curParamElem
							.getAttributeValue(XML_ATTRIB_PARAM_NAME);
					String curParamValue = curParamElem.getTextTrim();
					configParams.put(curParamName, curParamValue);
				}
			}
		}
	}

	public CardConfig getCloneForExecution() {
		Element xmlRepresentation = new Element("CardConfiguration");
		dumpToXml(xmlRepresentation);
		return new CardConfig(xmlRepresentation);
	}

	public void setProject(IProject newProject) {
		this.project = newProject;

		setName(project.getName());
	}

	public void setName(String newName) {
		if (!name.equals(newName)) {
			CardConfigManager.remove(this);
			this.name = newName;
			CardConfigManager.register(this);
		}
	}

	public void setDescription(String newDescr) {
		this.descr = newDescr;
	}

	public boolean isStoredAsProject() {
		return project != null;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IFile file = getCardConfigIfile();
		if (file != null) {
			IResourceDelta campaignExecutionDelta = event.getDelta()
					.findMember(file.getFullPath());

			if (campaignExecutionDelta != null) {
				if (campaignExecutionDelta.getKind() == IResourceDelta.REMOVED) {
					// remove this form CardConfigManager
					CardConfigManager.remove(this);
					
					//remove this from Workspace
					ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
					
				} else {
					// update the editor to reflect resource changes
					initFromIFile();
				}
			}
		}
		
	}
}
