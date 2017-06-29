package com.mtbs3d.minecrift.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface ASMMethodHandler {
	public MethodTuple getDesiredMethod();
	public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated);
}
