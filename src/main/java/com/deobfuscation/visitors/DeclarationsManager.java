package com.deobfuscation.visitors;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import com.deobfuscation.beans.IdentifierNameManager;
import com.deobfuscation.config.ConfigurationManager;
import com.deobfuscation.utils.Utils;

public class DeclarationsManager extends ASTVisitor{
	
	private Map<String, IdentifierNameManager> declarations = new HashMap<>();
	//private Map<String, List<IdentifierNameManager>> constructorDeclarations = new HashMap<>();

	@Override
	public boolean visit(SimpleName node) {
		if(isValidDeclaration(node)){
			System.out.println("Declaration: " + node.getIdentifier());
			IBinding binding = node.resolveBinding();
			IdentifierNameManager manager = new IdentifierNameManager(node);
			node.setIdentifier(manager.getSimpleName());
			declarations.put(binding.getKey(), manager);
			
			/*if(isConstructor(node)){
				ITypeBinding typeBinding = node.resolveTypeBinding();
				
				if(constructorDeclarations.containsKey(typeBinding.getKey())){
					List<IdentifierNameManager> list = constructorDeclarations.get(typeBinding.getKey());
					list.add(manager);
				} else {
					List<IdentifierNameManager> list = new ArrayList<>();
					list.add(manager);
					constructorDeclarations.put(typeBinding.getKey(), list);
				}
				System.out.println("Constructor: " + binding.getKey());
			}*/
		}
		return super.visit(node);
	}

	private boolean isValidDeclaration(SimpleName node) {
		return matchesRegex(node) && (node.isDeclaration() || isConstructor(node)) && node.resolveBinding() != null 
				&& node.resolveBinding().getKey() != null && !isOverriddenMethod(node);
	}
	
	private boolean matchesRegex(SimpleName node) {
		ConfigurationManager manager = ConfigurationManager.getConfigurationManager();
		String pattern = StringUtils.trim(manager.getDebfuscationRegex());
		return node.getIdentifier().matches(pattern);
	}

	private boolean isOverriddenMethod(SimpleName node) {
		boolean result = false;
		IBinding binding = node.resolveBinding();
		if(binding instanceof IMethodBinding){
			result = Utils.isOverriddenMethod((IMethodBinding)binding);
		}
		return result;
	}

	private boolean isConstructor(SimpleName node) {
		return node.getParent() != null && node.getParent() instanceof MethodDeclaration 
				&& ((MethodDeclaration)node.getParent()).isConstructor();
	}

	public Map<String, IdentifierNameManager> getDeclarations() {
		return declarations;
	}

	/*public Map<String, List<IdentifierNameManager>> getConstructorDeclarations() {
		return constructorDeclarations;
	}*/
	
}
