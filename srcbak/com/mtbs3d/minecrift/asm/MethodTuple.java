package com.mtbs3d.minecrift.asm;

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
	
	public MethodTuple(String methodName, String methodDesc) {
		this(methodName, methodDesc, null, null);
	}
}
