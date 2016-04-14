package com.deobfuscation.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

import com.deobfuscation.config.ConfigurationManager;

public class SimpleNameUpdater {
	
	public static String updateSimpleName(SimpleName simpleName){
		IBinding binding = simpleName.resolveBinding();
		
		String updatedName = simpleName.getIdentifier();
		switch(binding.getKind()){
			case IBinding.VARIABLE: {
				updatedName = getUpdatedVariableName(binding);
				break;
			}
			case IBinding.METHOD: {
				updatedName = getUpdatedMethodName(binding);
				break;
			}
			case IBinding.TYPE: {
				updatedName = getUpdatedTypeName(binding);
				break;
			}
		}
		return updatedName;
	}

	private static String getUpdatedTypeName(IBinding binding) {
		Map<String, String> valueMap = getIntialValueMap(binding);
		
		ConfigurationManager manager = ConfigurationManager.getConfigurationManager();
		valueMap.put("prefix", manager.getTypePrefix());
		return updateSimpleName(valueMap, manager.getTypeNamePattern());
	}

	private static String getUpdatedMethodName(IBinding binding) {
		String updatedName = StringUtils.EMPTY;
		
		Map<String, String> valueMap = getIntialValueMap(binding);
		IMethodBinding methodBinding = (IMethodBinding) binding;
		if(methodBinding.isConstructor()){
			updatedName = getUpdatedTypeName(methodBinding.getDeclaringClass());
		} else {
			valueMap.put("prefix", "m");
			valueMap.put("type", sanitizeTypeName(methodBinding.getReturnType().getName()));
			
			ConfigurationManager manager = ConfigurationManager.getConfigurationManager();
			updatedName = updateSimpleName(valueMap, manager.getMethodNamePattern());
		}
		return updatedName;
	}

	private static String getUpdatedVariableName(IBinding binding) {
		ConfigurationManager manager = ConfigurationManager.getConfigurationManager();
		
		Map<String, String> valueMap = getIntialValueMap(binding);
		IVariableBinding variableBinding = (IVariableBinding) binding;
		valueMap.put("prefix", getVariablePrefix(variableBinding));
		valueMap.put("type", sanitizeTypeName(variableBinding.getType().getName()));
		
		if(variableBinding.isEffectivelyFinal()){
			valueMap.put("final", manager.getFinalIdentifierPattern());
		}
		return updateSimpleName(valueMap, manager.getVariableNamePattern());
	}

	private static String getVariablePrefix(IVariableBinding variableBinding) {
		String prefix = "";
		
		ConfigurationManager manager = ConfigurationManager.getConfigurationManager();
		if(variableBinding.isField()){
			prefix = manager.getGlobalVariablePrefix();
		} else if(variableBinding.isParameter()){
			prefix = manager.getParameterVariablePrefix();
		} else if (variableBinding.isEnumConstant()){
			prefix = manager.getEnumVariablePrefix();
		} else {
			prefix = manager.getOtherVariablePrefix();
		}
		return prefix;
	}
	
	private static String sanitizeTypeName(String name) {
		return name.replace("[]", "Array");
	}
	
	private static String updateSimpleName(Map<String, String> valueMap, String pattern) {
		StrSubstitutor sub = new StrSubstitutor(valueMap);
		return sub.replace(pattern);
	}
	
	private static Map<String, String> getIntialValueMap(IBinding binding) {
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("name", binding.getName());
		valueMap.put("prefix", "");
		valueMap.put("type", "");
		valueMap.put("final", "");
		return valueMap;
	}
}
