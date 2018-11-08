package org.globaltester.sampleconfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.sampleconfiguration.category.CategoryFactory;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;
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
	 * category implementations.
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
				saveToProject();
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
	 * @param category
	 * @param key
	 * @return
	 */
	public String get(String category, String key) {
		checkFactories(category, key);
		
		if (!configParams.containsKey(category)){
			BasicLogger.log(getClass(), "Requested SampleConfig entry could neither be found nor generated: " + category + "_" + key, LogLevel.WARN);
			return null;
		}
		return configParams.get(category).get(key);
	}

	private void checkFactories(String category, String key) {
		CategoryFactory[] pFactories = org.globaltester.sampleconfiguration.Activator.getAvailableCategoryFactories();
		
		for (CategoryFactory curCategoryFactory : pFactories) {
			if (curCategoryFactory == null || !curCategoryFactory.getName().equals(category)) continue;
			for (CategoryParameterDescription description : curCategoryFactory.getParameterDescriptors()) {
				if (category.equals(description.getCategoryName()) && key.equals(description.getName()) && description.getGenerator() != null) {
					description.getGenerator().generate(category, key, this);
					return;
				}
			}
		}

		BasicLogger.log(getClass(), "No factory available for SampleConfig entry: " + category + "_" + key, LogLevel.TRACE);	
	}

	/**
	 * Returns a single parameter from this {@link SampleConfig} converted to boolean.
	 * 
	 * @param category
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String category, String key) {
		return Boolean.parseBoolean(get(category, key));
	}
	
	public Set<String> getStored(){
		return configParams.keySet();
	}

	/**
	 * Add a single parameter in this {@link SampleConfig}
	 * 
	 * @param category
	 * @param key
	 * @param value
	 */
	public void put(String category, String key, String value) {
		if (!configParams.containsKey(category)){
			configParams.put(category, new HashMap<>());
		}
		configParams.get(category).put(key, value);
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

	public IProject getProject() {
		return project;
	}

	public void saveToProject() {
		// do not save if project is not defined
		if (project == null)
			return;

		IFile iFile = getSampleConfigIfile();
		try {
		if (!iFile.exists()) {
			iFile.create(null, false, null);
		}

			Element root;
			root = getXmlRepresentation();

		// write to file
		XMLHelper.saveDoc(iFile, root);
		} catch (CoreException e) {
			BasicLogger.logException(getClass(),  "Saving the sample config failed", e);
		}
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
		for (String curCategory : configParams.keySet()) {
			for (String curParam : configParams.get(curCategory).keySet()){
				Element curParamElem = new Element(XML_TAG_PARAMETER);
				curParamElem.setAttribute(XML_ATTRIB_PARAM_NAME, curCategory + "_" + curParam);
				String curParamValue = (String) configParams.get(curCategory).get(curParam);
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
					String categoryName = curParamName.substring(0, divider);
					String parameterName = curParamName.substring(divider + 1);
					if (!configParams.containsKey(categoryName)){
						configParams.put(categoryName, new HashMap<>());
					}
					configParams.get(categoryName).put(parameterName, curParamValue);
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

	public Set<String> getStoredCategories() {
		return configParams.keySet();
	}

	public Set<String> getStoredParameters(String category) {
		if (!configParams.containsKey(category)){
			throw new IllegalArgumentException("This category is not part of this sample config");
		}
		return configParams.get(category).keySet();
	}

	/**
	 * Store binaryData within the 
	 * @param fileName
	 * @param value
	 * @throws IOException 
	 */
	public void storeBinaryData(String fileName, byte[] bytes) throws IOException {
		if (project == null) return;
		
		IFile iFile = project.getFile(fileName);
		Path file = iFile.getLocation().toFile().toPath();
		Files.createDirectories(file.getParent());
		Files.write(file, bytes);
		
	}
	
	/**
	 * Returns a single parameter from this {@link SampleConfig} converted as
	 * absolute path.
	 * 
	 * @param category
	 * @param key
	 * @return the absolute path as string for this category and key or null if not available
	 */
	public String getAbsolutePath(String category, String key) {
		String configValue = get(category, key);
		if (configValue == null) {
			return null;
		}
		
		Path path = Paths.get(configValue); 
		if (!path.isAbsolute()){
			//resolve against sample config project directory if relative path given
			IProject sampleConfigProject = getSampleConfigIfile().getProject();
			path = Paths.get(sampleConfigProject.getLocation().append(path.toString()).toOSString());
		}
		return path.toFile().getAbsolutePath();
	}

	public boolean contains(String category, String key) {
		HashMap<String, String> hashMap = configParams.get(category);
		if (hashMap == null) {
			return false;
		} else {
			return hashMap.containsKey(key);
		}
	}
	
	
}
