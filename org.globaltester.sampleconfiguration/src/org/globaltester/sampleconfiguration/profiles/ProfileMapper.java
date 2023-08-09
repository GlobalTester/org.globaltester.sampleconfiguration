package org.globaltester.sampleconfiguration.profiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.globaltester.logging.BasicLogger;
import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;
import org.globaltester.sampleconfiguration.profiles.parser.ProfileExpressionParser;
import org.globaltester.sampleconfiguration.profiles.parser.UnparseableProfileExpression;

public class ProfileMapper {

	public static final String MAPPING_FILE_SUFFIX = "gtmapping";
	public static final String MAPPING_FILE_NAME = "profiles." + MAPPING_FILE_SUFFIX;

	/**
	 * This parses a profile expression from a profile string using the given property files.
	 * @param profiles the profile string to parse
	 * @param propertyFiles the property files to use
	 * @return the resulting {@link ProfileExpression}
	 * @throws IOException when the property files are not readable
	 */
	public static ProfileExpression parse(String profiles, IFile ... propertyFiles){
		
		for (int i = propertyFiles.length-1; i >=0; i--){
			try (BufferedReader reader = createReaderForFile(propertyFiles[i])){
				String line;
				while ((line = reader.readLine()) != null){
					if (line.isEmpty() || line.startsWith("#")){
						continue;
					}
				
					String mappedExpression = reader.readLine();
					if (line.equals(profiles)){
							return ProfileExpressionParser.parse(mappedExpression);
					}
				}
			} catch (IOException e) {
				BasicLogger.logException(ProfileMapper.class, "Error while parsing property file " + propertyFiles[i], e);
				return new UnparseableProfileExpression(e.getMessage());
			}
		}
		if (propertyFiles.length > 0 && !profiles.isEmpty()) {
			return new UnparseableProfileExpression("Parsing profile \"" + profiles + "\" without a mapping is not allowed when mapping files exist");
		} else {
			return ProfileExpressionParser.parse(profiles);
		}
	}

	private static BufferedReader createReaderForFile(IFile file) throws FileNotFoundException{
		if (!file.exists()){
			System.gc();
			throw new FileNotFoundException("Can not create reader for not existing file");
		}
		return new BufferedReader(new FileReader(file.getLocation().toOSString()));
	}
}
