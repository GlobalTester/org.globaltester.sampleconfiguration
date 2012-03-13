package org.globaltester.cardconfiguration;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.core.xml.XMLHelper;
import org.globaltester.logging.logger.GtErrorLogger;
import org.jdom.Document;
import org.jdom.Element;

public class CardConfig {

	private static final String XML_TAG_NAME = "Name";
	private static final String XML_TAG_CONFIG_PARAMS = "ConfigurationParams";
	private static final String XML_TAG_PARAMETER = "Parameter";
	private static final String XML_ATTRIB_PARAM_NAME = "paramName";

	private HashMap<String, Object> configParams = new HashMap<String, Object>();
	private IProject project;
	private String name;

	public CardConfig(IProject proj) {
		this.project = proj;
		this.name = proj.getName();
		
		if (getCardConfigIfile().exists()){
			initFromIFile();
		} else {
			try {
				saveToProject();
			} catch (CoreException e) {
				GtErrorLogger.log(Activator.PLUGIN_ID, e);
			}
		}
		
		CardConfigManager.register(this);
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
		return name;
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

	private IFile getCardConfigIfile() {
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
		Element specNameElem = new Element(XML_TAG_NAME);
		specNameElem.addContent(name);
		root.addContent(specNameElem);

		// add configParams
		Element configParamsElem = new Element(XML_TAG_CONFIG_PARAMS);
		for (String curParam : configParams.keySet()) {
			Element curParamElem = new Element(XML_TAG_PARAMETER);
			curParamElem.setAttribute(XML_ATTRIB_PARAM_NAME, curParam);
			curParamElem.addContent((String) configParams.get(curParam));
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
		
		//extract configParams
		Element paramsElem = root.getChild(XML_TAG_CONFIG_PARAMS);
		if (paramsElem != null) {
			for (Object curParamObj : paramsElem.getChildren(XML_TAG_PARAMETER)) {
				if (curParamObj instanceof Element){
					Element curParamElem = (Element) curParamObj;
					String curParamName = curParamElem.getAttributeValue(XML_ATTRIB_PARAM_NAME);
					String curParamValue = curParamElem.getTextTrim();
					configParams.put(curParamName, curParamValue);
				}
			}
		}
	}

}
