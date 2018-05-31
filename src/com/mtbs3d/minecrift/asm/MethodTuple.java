package com.mtbs3d.minecrift.asm;

import java.util.Map;

public class MethodTuple {
	public final String methodName;
	public final String methodDesc;
	public final String methodNameObf;
	public final String methodDescObf;
	
	public MethodTuple(String methodName, String methodDesc, String methodNameObf, String methodDescObf) {
		this.methodName = methodName;
		this.methodDesc = methodDesc;
		this.methodNameObf = methodNameObf;
		this.methodDescObf = methodDescObf;
	}

	/**
	 * This constructor will automatically resolve the obfuscated descriptor.
	 */
	public MethodTuple(String methodName, String methodDesc, String methodNameObf) {
		this(methodName, methodDesc, methodNameObf, ObfNames.resolveDescriptor(methodDesc));
	}
	
	public MethodTuple(String methodName, String methodDesc) {
		this(methodName, methodDesc, null, null);
	}
}
