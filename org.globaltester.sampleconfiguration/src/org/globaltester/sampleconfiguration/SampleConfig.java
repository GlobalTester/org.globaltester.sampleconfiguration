package org.globaltester.sampleconfiguration;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.base.xml.XMLHelper;
import org.globaltester.logging.legacy.logger.GtErrorLogger;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

public class SampleConfig implements IResourceChangeListener {

	private static final String XML_ATTRIB_ORIGINAL_NAME = "originalName";
	private static final String XML_TAG_DESCRIPTION = "Description";
	private static final String XML_TAG_PLATFORM_ID = "PlatformId";
	private static final String XML_TAG_SAMPLE_ID = "SampleId";
	private static final String XML_TAG_CONFIG_PARAMS = "ConfigurationParams";
	private static final String XML_TAG_PARAMETER = "Parameter";
	private static final String XML_ATTRIB_PARAM_NAME = "paramName";

	private static final String UNKNOWN = "_unknown_";

	private HashMap<String, HashMap<String, String>> configParams = new HashMap<>();
	private IProject project;
	private String originalName;
	private String platformId;
	private String sampleId;
	private String descr;

	/**
	 * Creates a new instance which is populated with default values provided by
	 * protocol implementations.
	 */
	public SampleConfig() {
		this.project = null;
		initWithDefaulValues();
	}

	private void initWithDefaulValues() {
		descr = "Default configuration";
		platformId = "00";
		sampleId = "12345";
	}

	public SampleConfig(IProject proj) {
		this.project = proj;
		this.descr = "";
		this.sampleId = "";
		this.platformId = "";

		if (getSampleConfigIfile().exists()) {
			initFromIFile();
		} else {
			try {
				saveToProject();
			} catch (CoreException e) {
				GtErrorLogger.log(Activator.PLUGIN_ID, e);
			}
		}

		SampleConfigManager.register(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
			      this, IResourceChangeEvent.POST_CHANGE);
	}

	public SampleConfig(Element sampleConfigElement) {
		this();

		extractFromXml(sampleConfigElement);
	}

	private void initFromIFile() {
		IFile iFile = getSampleConfigIfile();
		if (iFile.exists()) {
			Document doc = XMLHelper.readDocument(iFile);
			Element root = doc.getRootElement();
			extractFromXml(root);
		}
	}

	/**
	 * Returns a single parameter from this {@link SampleConfig}
	 * 
	 * @param protocol
	 * @param key
	 * @return
	 */
	public String get(String protocol, String key) {
		if (!configParams.containsKey(protocol)){
			return null;
		}
		return configParams.get(protocol).get(key);
	}

	/**
	 * Returns a single parameter from this {@link SampleConfig} converted to boolean.
	 * 
	 * @param protocol
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String protocol, String key) {
		return Boolean.parseBoolean(get(protocol, key));
	}
	
	public Set<String> getStored(){
		return configParams.keySet();
	}

	/**
	 * Add a single parameter in this {@link SampleConfig}
	 * 
	 * @param protocol
	 * @param key
	 * @param value
	 */
	public void put(String protocol, String key, String value) {
		if (!configParams.containsKey(protocol)){
			configParams.put(protocol, new HashMap<>());
		}
		configParams.get(protocol).put(key, value);
	}
	
	public String getName() {
		return project != null ? project.getName() : originalName;
	}

	public String getDescription() {
		if (descr == null) {
			descr = "";
		}
		return descr;
	}
	
	public String getSampleId() {
		if (sampleId == null) {
			sampleId = "";
		}
		return sampleId;
	}
	
	public String getPlatformId() {
		if (platformId == null) {
			platformId = "";
		}
		return platformId;
	}

	public void saveToProject() throws CoreException {
		// do not save if project is not defined
		if (project == null)
			return;

		IFile iFile = getSampleConfigIfile();
		if (!iFile.exists()) {
			iFile.create(null, false, null);
		}

		Element root = getXmlRepresentation();

		// write to file
		XMLHelper.saveDoc(iFile, root);
	}

	public IFile getSampleConfigIfile() {
		if (project != null) {
			return project.getFile("sample.gtconfig");
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
		root.setAttribute(XML_ATTRIB_ORIGINAL_NAME, getName());
		Element descrElem = new Element(XML_TAG_DESCRIPTION);
		descrElem.addContent(descr);
		root.addContent(descrElem);
		Element platformElem = new Element(XML_TAG_PLATFORM_ID);
		platformElem.addContent(platformId);
		root.addContent(platformElem);
		Element sampleElem = new Element(XML_TAG_SAMPLE_ID);
		sampleElem.addContent(sampleId);
		root.addContent(sampleElem);

		// add configParams
		Element configParamsElem = new Element(XML_TAG_CONFIG_PARAMS);
		for (String curProtocol : configParams.keySet()) {
			for (String curParam : configParams.get(curProtocol).keySet()){
				Element curParamElem = new Element(XML_TAG_PARAMETER);
				curParamElem.setAttribute(XML_ATTRIB_PARAM_NAME, curProtocol + "_" + curParam);
				String curParamValue = (String) configParams.get(curProtocol).get(curParam);
				if (curParamValue.contains("<")){
					curParamElem.addContent(new CDATA(curParamValue));
				} else {
					curParamElem.addContent(curParamValue);	
				}
				configParamsElem.addContent(curParamElem);	
			}
		}
		root.addContent(configParamsElem);

	}

	protected String getXmlRootElementName() {
		return "SampleConfig";
	}

	void extractFromXml(Element root) {
		// extract meta data
		originalName = root.getAttributeValue(XML_ATTRIB_ORIGINAL_NAME);
		originalName = originalName == null ? UNKNOWN : originalName;
		descr = root.getChildTextTrim(XML_TAG_DESCRIPTION);
		platformId = root.getChildTextTrim(XML_TAG_PLATFORM_ID);
		sampleId = root.getChildTextTrim(XML_TAG_SAMPLE_ID);
		
		// extract configParams
		Element paramsElem = root.getChild(XML_TAG_CONFIG_PARAMS);
		if (paramsElem != null) {
			for (Object curParamObj : paramsElem.getChildren(XML_TAG_PARAMETER)) {
				if (curParamObj instanceof Element) {
					Element curParamElem = (Element) curParamObj;
					String curParamName = curParamElem
							.getAttributeValue(XML_ATTRIB_PARAM_NAME);
					String curParamValue = curParamElem.getTextTrim();
					
					int divider = curParamName.indexOf("_");
					String protocolName = curParamName.substring(0, divider);
					String parameterName = curParamName.substring(divider + 1);
					if (!configParams.containsKey(protocolName)){
						configParams.put(protocolName, new HashMap<>());
					}
					configParams.get(protocolName).put(parameterName, curParamValue);
				}
			}
		}
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public void setProject(IProject newProject) {
		this.project = newProject;
	}

	public void setDescription(String newDescr) {
		this.descr = newDescr;
	}

	public boolean isStoredAsProject() {
		return project != null;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IFile file = getSampleConfigIfile();
		if (file != null) {
			IResourceDelta campaignExecutionDelta = event.getDelta()
					.findMember(file.getFullPath());

			if (campaignExecutionDelta != null) {
				if (campaignExecutionDelta.getKind() == IResourceDelta.REMOVED) {
					// remove this form SampleConfigManager
					SampleConfigManager.remove(this);
					
					//remove this from Workspace
					ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
					
				} else {
					// update the editor to reflect resource changes
					initFromIFile();
				}
			}
		}
		
	}

	@Override
	public String toString() {
		return "SampleConfig [configParams=" + configParams + ", project=" + project 
				+ ", platformId=" + platformId + ", sampleId=" + sampleId + ", descr=" + descr + "]";
	}

	public Set<String> getStoredProtocols() {
		return configParams.keySet();
	}

	public Set<String> getStoredParameters(String protocol) {
		if (!configParams.containsKey(protocol)){
			throw new IllegalArgumentException("This protocol is not part of this sample config");
		}
		return configParams.get(protocol).keySet();
	}
	
	
}
