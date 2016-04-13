package com.deobfuscation.beans;

import org.eclipse.jdt.core.dom.SimpleName;

public class IdentifierNameManager {
	private boolean isNameUpdated = false;
	private SimpleName simpleName;
	
	public IdentifierNameManager(SimpleName simpleName) {
		this.simpleName = simpleName;
	}

	public String getSimpleName() {
		return isNameUpdated ? simpleName.getIdentifier(): updateSimpleName();
	}

	private String updateSimpleName() {
		simpleName.setIdentifier(SimpleNameUpdater.updateSimpleName(simpleName));
		isNameUpdated = true;
		return simpleName.getIdentifier();
	}
}
