package com.deobfuscation.visitors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class PublicClassNameVisitor extends ASTVisitor {
	private String className;
	
	@Override
	public void endVisit(TypeDeclaration node) {
		if(StringUtils.isEmpty(className)){
			className = node.getName().getIdentifier();
		}
		
		if(Modifier.isPublic(node.getModifiers())){
			className = node.getName().getIdentifier();
		}
		super.endVisit(node);
	}

	public String getClassName() {
		return className + ".java";
	}
	
}
