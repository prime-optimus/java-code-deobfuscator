package com.deobfuscation.config;

import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigurationManager {
	private PropertiesConfiguration propertiesConfiguration;
	private static ConfigurationManager config;
	private ConfigurationManager(){}
	
	public String getDebfuscationRegex(){
		return propertiesConfiguration.getString("regex");
	}
	
	public String getInputFolder(){
		return propertiesConfiguration.getString("folder.input");
	}
	
	public String getVariableNamePattern(){
		return propertiesConfiguration.getString("name.variable");
	}

	public String getMethodNamePattern(){
		return propertiesConfiguration.getString("name.method");
	}
	public String getTypeNamePattern(){
		return propertiesConfiguration.getString("name.type");
	}
	
	public String getFinalIdentifierPattern(){
		return propertiesConfiguration.getString("name.variable.final");
	}
	
	public String getGlobalVariablePrefix(){
		return propertiesConfiguration.getString("prefix.variable.global");
	}
	
	public String getParameterVariablePrefix(){
		return propertiesConfiguration.getString("prefix.variable.parameter");
	}
	public String getEnumVariablePrefix(){
		return propertiesConfiguration.getString("prefix.variable.enum");
	}
	public String getOtherVariablePrefix(){
		return propertiesConfiguration.getString("prefix.variable.others");
	}
	
	public String getTypePrefix(){
		return propertiesConfiguration.getString("prefix.type");
	}
	
	public static ConfigurationManager getConfigurationManager(String filePath) throws ConfigurationException, IOException{
		ConfigurationManager manager = getConfigurationManager();
		manager.propertiesConfiguration = new PropertiesConfiguration();
		manager.propertiesConfiguration.read(new FileReader(filePath));
		return manager;
	}
	
	public static ConfigurationManager getConfigurationManager(){
		if(config == null){
			config = new  ConfigurationManager();
		}
		return config;
	}
	

}
