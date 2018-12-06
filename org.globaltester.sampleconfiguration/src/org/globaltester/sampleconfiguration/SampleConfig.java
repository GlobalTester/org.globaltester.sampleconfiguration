package org.globaltester.sampleconfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.globaltester.base.resources.GtResourceHelper;
import org.globaltester.base.xml.XMLHelper;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.sampleconfiguration.category.CategoryFactory;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.DataConversionException;
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
	private static final String XML_ATTRIB_PARAM_GENERATED = "generated";

	private static final String UNKNOWN = "_unknown_";
	
	private static HashMap<IProject, SampleConfig> projectMap = new HashMap<>();

	public static synchronized SampleConfig getSampleConfigForProject(IProject project) {
		if (!projectMap.containsKey(project)) {
			projectMap.put(project, new SampleConfig(project));
		}
		return projectMap.get(project);
	}

	public static SampleConfig getSampleConfigForProject(String projectName) {
		if (projectName == null) return null;
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project == null) return null;
		
		return getSampleConfigForProject(project);
	}

	public static Set<String> getAvailableConfigNames() {
		return GtResourceHelper
				.getProjectNamesWithNature(GtSampleConfigNature.NATURE_ID);
	}
	
	

	private HashMap<String, HashMap<String, SampleConfigParameterValue>> configParams = new HashMap<>();
	private IProject project;
	private String originalName;
	private String platformId;
	private String sampleId;
	private String descr;

	/**
	 * Creates a new instance which is linked to and persisted in the given {@link IProject}
	 */
	private SampleConfig(IProject proj) {
		this.project = proj;
		this.descr = "";
		this.sampleId = "00";
		this.platformId = "12345";

		if (getSampleConfigIfile().exists()) {
			initFromIFile();
		} else {
				saveToProject();
		}

		ResourcesPlugin.getWorkspace().addResourceChangeListener(
			      this, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Create a new instance, which is not linked to an {@link IProject} and thus read only
	 * @param sampleConfigElement
	 */
	public SampleConfig(Element sampleConfigElement) {
		extractFromXml(sampleConfigElement);
	}

	private synchronized void initFromIFile() {
		if (!isModificationAllowed()) throw new IllegalStateException();
		
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
		SampleConfigParameterValue paramValue = getParameterValue(category, key);
		
		return paramValue == null ? null : paramValue.getValue();
	}

	/**
	 * Returns a single parameter from this {@link SampleConfig}
	 * 
	 * @param category
	 * @param key
	 * @return
	 */
	public SampleConfigParameterValue getParameterValue(String category, String key) {
		checkFactories(category, key);
		
		if (!contains(category, key)){
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
		if (!contains(category, key)) {
			BasicLogger.log(getClass(), "No factory available for SampleConfig entry: " + category + "_" + key, LogLevel.TRACE);	
		}	
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
		put(category, key, new SampleConfigParameterValue(value));
	}

	public void put(String category, String key, boolean value) {
		put(category, key, new SampleConfigParameterValue(Boolean.toString(value)));
	}
	
	public void put(String category, String key, SampleConfigParameterValue value) {
		if (!configParams.containsKey(category)){
			configParams.put(category, new HashMap<>());
		}
		
		configParams.get(category).put(key, value);
		saveToProject();
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
		
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			BasicLogger.logException("Unable to refresh SampleConfig project. Not all resources may be up to date.", e, LogLevel.DEBUG);
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
				SampleConfigParameterValue curParamValue = configParams.get(curCategory).get(curParam);
				if (curParamValue != null) {
					Element curParamElem = new Element(XML_TAG_PARAMETER);
					curParamElem.setAttribute(XML_ATTRIB_PARAM_NAME, curCategory + "_" + curParam);
					
					if (curParamValue.isGenerated()) {
						curParamElem.setAttribute(XML_ATTRIB_PARAM_GENERATED, "true");	
					}
					
					String valueString = curParamValue.getValue();
					if (valueString.contains("<")){
						curParamElem.addContent(new CDATA(valueString));
					} else {
						curParamElem.addContent(valueString);	
					}
					configParamsElem.addContent(curParamElem);
				}
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
		configParams.clear();
		Element paramsElem = root.getChild(XML_TAG_CONFIG_PARAMS);
		if (paramsElem != null) {
			for (Object curParamObj : paramsElem.getChildren(XML_TAG_PARAMETER)) {
				if (curParamObj instanceof Element) {
					Element curParamElem = (Element) curParamObj;
					
					SampleConfigParameterValue curParamValue = new SampleConfigParameterValue(curParamElem.getTextTrim());
					
					Attribute attribGenerated = curParamElem.getAttribute(XML_ATTRIB_PARAM_GENERATED);
					try {
						if (attribGenerated != null && attribGenerated.getBooleanValue()) {
							curParamValue.setGenerated(true);
						}
					} catch (DataConversionException e) {
						BasicLogger.logException("Unable to decode attribute generated as boolean", e, LogLevel.DEBUG);
					}
					
					String curParamName = curParamElem
							.getAttributeValue(XML_ATTRIB_PARAM_NAME);
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
		if (!isModificationAllowed()) throw new IllegalStateException();
		this.platformId = platformId;
		saveToProject();
	}

	public void setSampleId(String sampleId) {
		if (!isModificationAllowed()) throw new IllegalStateException();
		this.sampleId = sampleId;
		saveToProject();
	}

	public void setProject(IProject newProject) {
		if (!isModificationAllowed()) throw new IllegalStateException();
		this.project = newProject;
		saveToProject();
	}

	public void setDescription(String newDescr) {
		if (!isModificationAllowed()) throw new IllegalStateException();
		this.descr = newDescr;
		saveToProject();
	}

	public boolean isStoredAsProject() {
		return project != null;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IFile file = getSampleConfigIfile();
		if (file != null) {
			IResourceDelta eventDelta = event.getDelta()
					.findMember(file.getFullPath());

			if (eventDelta != null) {
				if (eventDelta.getKind() == IResourceDelta.REMOVED) {
					// remove this form SampleConfig from project mapping
					projectMap.remove(this.getProject());
					
					//remove this listener from Workspace
					ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
					
				} else {
					if (isModificationAllowed()) {
						// update the editor to reflect resource changes (if not locked by another ThreadGroup)
						initFromIFile();
					} 
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
	 * Store binaryData within the SampleConfig
	 * @param fileName
	 * @param value
	 * @throws IOException 
	 */
	public void storeBinaryData(String fileName, byte[] bytes) throws IOException {
		if (project == null) return;
		
		IFile iFile = project.getFile(fileName);
		Path file = iFile.getLocation().toFile().toPath();
		
		storeBinaryData(file, bytes);
	}

	/**
	 * Store binaryData within the SampleConfig
	 * @param fileName
	 * @param value
	 * @throws IOException 
	 */
	public void storeBinaryData(Path path, byte[] bytes) throws IOException {
		if (project == null) return;
		
		Path file = path;
		if (!path.isAbsolute()) {
			IFile iFile = project.getFile(path.toString());
			file = iFile.getLocation().toFile().toPath(); 
		}
		
		Files.createDirectories(file.getParent());
		Files.write(file, bytes);
		
	}
	
	public byte[] getBinaryData(String category, String key) throws IOException {
		return Files.readAllBytes(Paths.get(getAbsolutePath(category, key)));
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
		HashMap<String, SampleConfigParameterValue> hashMap = configParams.get(category);
		if (hashMap == null) {
			return false;
		} else {
			return hashMap.containsKey(key);
		}
	}

	public void putPath(String category, String key, Path path) {
		String value = path.toString();
		
		if (project != null) {
			Path projectPath = project.getLocation().toFile().toPath();
			
			if (path.startsWith(projectPath)) {
				value = projectPath.relativize(path).toString();
			}
		} 
				
		put(category, key, value);
	}
	
	ThreadGroup lockedThreadGroup = null;

	public synchronized void lock() {
		lockedThreadGroup = Thread.currentThread().getThreadGroup();
	}

	public synchronized void unlock() {
		if (lockedThreadGroup == Thread.currentThread().getThreadGroup()) {
			lockedThreadGroup = null;
		}
	}
	
	private boolean isModificationAllowed() {
		return lockedThreadGroup == null ? true : lockedThreadGroup == Thread.currentThread().getThreadGroup();
	}
}
