package com.deobfuscation.visitors;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;

import com.deobfuscation.beans.IdentifierNameManager;

public class ReferencesUpdater extends ASTVisitor {
	private DeclarationsManager declarationsManager;

	public ReferencesUpdater(DeclarationsManager declarationsManager) {
		this.declarationsManager = declarationsManager;
	}

	public boolean visit(SimpleName node) {
		Map<String, IdentifierNameManager> declarations = declarationsManager.getDeclarations();
		
		if(isValidReference(declarations, node)){
			IBinding binding = node.resolveBinding();
			String simpleName = declarations.get(binding.getKey()).getSimpleName();
			System.out.println("Updated: " + simpleName + " : " + binding.getName());
			node.setIdentifier(simpleName);
		}
		return super.visit(node);
	}

	private boolean isValidReference(Map<String, IdentifierNameManager> declarations, SimpleName node) {
		return !node.isDeclaration() && node.resolveBinding() !=null 
				&& node.resolveBinding().getKey() != null
				&& declarations.containsKey(node.resolveBinding().getKey());
	}
}
