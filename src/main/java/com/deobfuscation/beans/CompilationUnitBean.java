package com.deobfuscation.beans;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.deobfuscation.visitors.PublicClassNameVisitor;

public class CompilationUnitBean {
	private CompilationUnit compilationUnit;
	private File javaFile;
	private IDocument document;
	
	
	public CompilationUnitBean(File rootDirectory, File javaFile) throws IOException {
		String javaCode = FileUtils.readFileToString(javaFile);
		
		this.javaFile = javaFile;
		this.compilationUnit = parseSourceFile(javaFile, rootDirectory, javaCode);
		this.compilationUnit.recordModifications();
		this.document = new Document(javaCode);
	}

	public void saveModifications(String newName){
		try {
			TextEdit rewrite = compilationUnit.rewrite(document, null);
			rewrite.apply(document);
			FileUtils.write(javaFile, document.get());
			
			if(!StringUtils.equals(newName, javaFile.getName())){
				File file = new File(javaFile.getParent() + "/" + newName);
				FileUtils.moveFile(javaFile, file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private CompilationUnit parseSourceFile(File javaFile, File rootDirectory, String javaCode) throws IOException {
		String sourcePathEntries[] = {rootDirectory.getAbsolutePath()};
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setUnitName(javaFile.getAbsolutePath());
		parser.setEnvironment(null, sourcePathEntries, null, true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(javaCode.toCharArray());
		parser.setResolveBindings(true); 
		return (CompilationUnit) parser.createAST(null);
	}
	
	public void accept(ASTVisitor visitor) {
		this.compilationUnit.accept(visitor);
	}
	
	@Override
	public int hashCode() {
		return javaFile.getAbsolutePath().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompilationUnitBean other = (CompilationUnitBean) obj;
		if (javaFile == null) {
			if (other.javaFile != null)
				return false;
		} else if (!javaFile.getAbsolutePath().equals(other.javaFile.getAbsolutePath()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CompilationUnitBean [javaFile=" + javaFile.getAbsolutePath() + "]";
	}
	
	public String getRootClassName(){
		PublicClassNameVisitor classNameVisitor = new PublicClassNameVisitor();
		this.compilationUnit.accept(classNameVisitor);
		return classNameVisitor.getClassName();
	}
	
}
