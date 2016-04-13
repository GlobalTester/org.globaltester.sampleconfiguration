package org.globaltester.sampleconfiguration;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.base.xml.XMLHelper;
import org.globaltester.logging.logger.GtErrorLogger;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

public class SampleConfig implements IResourceChangeListener {

	public static final String DEFAULT_NAME = "Mustermann Erika";

	private static final String XML_TAG_NAME = "Name";
	private static final String XML_TAG_DESCRIPTION = "Description";
	private static final String XML_TAG_CONFIG_PARAMS = "ConfigurationParams";
	private static final String XML_TAG_PARAMETER = "Parameter";
	private static final String XML_ATTRIB_PARAM_NAME = "paramName";

	private HashMap<String, String> configParams = new HashMap<String, String>();
	private IProject project;
	private String name;
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
		name = DEFAULT_NAME;
		descr = "Default configuration";

		configParams
				.put("ICAO9303_MRZ",
						"P<D<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<<<<<<<<<C11T002JM4D<<9608122F1310317<<<<<<<<<<<<<<<6");
	}

	public SampleConfig(IProject proj) {
		this.project = proj;
		this.name = proj.getName();
		this.descr = "";

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
		return configParams.get(protocol + "_" + key);
	}

	/**
	 * Add a single parameter in this {@link SampleConfig}
	 * 
	 * @param protocol
	 * @param key
	 * @param value
	 */
	public void put(String protocol, String key, String value) {
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

		IFile iFile = getSampleConfigIfile();
		if (!iFile.exists()) {
			iFile.create(null, false, null);
		}

		Element root = getXmlRepresentation();

		// write to file
		XMLHelper.saveDoc(iFile, root);

		// TODO extract/display contained data to individual files (for user
		// convenience)
	}

	public IFile getSampleConfigIfile() {
		//TODO make this private once it is no longer needed for TextEditorInput
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
		return "SampleConfig";
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

	public SampleConfig getCloneForExecution() {
		Element xmlRepresentation = new Element("SampleConfiguration");
		dumpToXml(xmlRepresentation);
		return new SampleConfig(xmlRepresentation);
	}

	public void setProject(IProject newProject) {
		this.project = newProject;

		setName(project.getName());
	}

	public void setName(String newName) {
		if (!name.equals(newName)) {
			SampleConfigManager.remove(this);
			this.name = newName;
			SampleConfigManager.register(this);
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
}
