package com.deobfuscation.utils;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class Utils {
	
	public static boolean isOverriddenMethod(IMethodBinding methodBinding) {
		boolean result = false;
		
		ITypeBinding declaringClass = methodBinding.getDeclaringClass();
		
		String methodName = methodBinding.getName();
		ITypeBinding superclass= declaringClass.getSuperclass();
		result = isOverriddenMethod(methodName, superclass);
		
		if(!result){
			ITypeBinding[] interfaces = declaringClass.getInterfaces();
			for (ITypeBinding iTypeBinding : interfaces) {
				result = isOverriddenMethod(methodName, iTypeBinding);
			}
		}
		return result;
	}
	
	private static boolean isOverriddenMethod(String methodName, ITypeBinding declaringClass) {
		boolean result = false;
		
		if(declaringClass != null){
			result = containsMethodName(methodName, declaringClass);
			if(!result){
				ITypeBinding superclass= declaringClass.getSuperclass();
				result = isOverriddenMethod(methodName, superclass);
			}
			
			if(!result){
				ITypeBinding[] interfaces = declaringClass.getInterfaces();
				for (ITypeBinding iTypeBinding : interfaces) {
					result = isOverriddenMethod(methodName, iTypeBinding);
				}
			}
		}
		return result;
	}

	private static boolean containsMethodName(String methodName, ITypeBinding declaringClass) {
		boolean result = false;
		IMethodBinding[] declaredMethods = declaringClass.getDeclaredMethods();
		
		for (IMethodBinding iMethodBinding : declaredMethods) {
			if(StringUtils.equals(iMethodBinding.getName(), methodName)){
				result = true;
				break;
			}
		}
		return result;
	}
	
	

}
