package org.globaltester.sampleconfiguration.profiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.globaltester.sampleconfiguration.profiles.expressions.ProfileExpression;
import org.globaltester.sampleconfiguration.profiles.expressions.ValueProfileExpression;
import org.globaltester.sampleconfiguration.profiles.parser.ProfileExpressionParser;

public class ProfileMapper {

	public static final String MAPPING_FILE_SUFFIX = "gtmapping";
	public static final String MAPPING_FILE_NAME = "profiles." + MAPPING_FILE_SUFFIX;

	public static ProfileExpression parse(String profiles, IFile ... propertyFiles){
		for (int i = propertyFiles.length-1; i >=0; i--){
			try {
				BufferedReader reader = createReaderForFile(propertyFiles[i]);
				String line;
				while ((line = reader.readLine()) != null){
					if (line.isEmpty()){
						continue;
					}
					if (! line.startsWith("#")){
						if (line.equals(profiles)){
							try{
								return ProfileExpressionParser.parse(reader.readLine());
							}catch(IllegalArgumentException e){
								return new ValueProfileExpression(false);
							}
						}
					}
				}
			} catch (IOException e) {
				throw new IllegalArgumentException("The given mapping " + propertyFiles[i] + " file is not readable");
			}
		}
		
		return ProfileExpressionParser.parse(profiles);
	}

	private static BufferedReader createReaderForFile(IFile file) throws FileNotFoundException{
		if (!file.exists()){
			throw new IllegalArgumentException("Can not create reader for not existing file");
		}
		return new BufferedReader(new FileReader(file.getLocation().toOSString()));
	}
}
