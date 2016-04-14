package com.deobfuscation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;

import com.deobfuscation.beans.CompilationUnitBean;
import com.deobfuscation.config.ConfigurationManager;
import com.deobfuscation.visitors.DeclarationsManager;
import com.deobfuscation.visitors.ReferencesVisitor;

public class DeobfuscationDriver {

	public static void main(String args[]) throws IOException, CoreException, ConfigurationException {
		ConfigurationManager manager = ConfigurationManager.getConfigurationManager(args[0]);
		File rootDirectory = new File(manager.getInputFolder());
		
		List<CompilationUnitBean> compilationUnitBeans = new ArrayList<>();
		for(File currentFile: getSourceFiles(rootDirectory)){
			CompilationUnitBean compilationUnitBean = new CompilationUnitBean(rootDirectory, currentFile);
			compilationUnitBeans.add(compilationUnitBean);
		}
		
		DeclarationsManager declarationsManager = new DeclarationsManager();
		for (CompilationUnitBean compilationUnitBean : compilationUnitBeans) {
			compilationUnitBean.accept(declarationsManager);
		}
		
		ReferencesVisitor invokationManager = new ReferencesVisitor(declarationsManager);
		for (CompilationUnitBean compilationUnitBean : compilationUnitBeans) {
			System.out.println("Updating " + compilationUnitBean);
			compilationUnitBean.accept(invokationManager);
		}
		
		for (CompilationUnitBean compilationUnitBean : compilationUnitBeans) {
			System.out.println("Saving: " + compilationUnitBean.getRootClassName());
			compilationUnitBean.saveModifications(compilationUnitBean.getRootClassName());
		}
	}

	private static Collection<File> getSourceFiles(File rootDirectory){
		String extensions[] = {"java"};
		return FileUtils.listFiles(rootDirectory, extensions, true);
	}
	
}
