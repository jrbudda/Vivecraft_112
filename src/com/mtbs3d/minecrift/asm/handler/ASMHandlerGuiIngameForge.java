package com.mtbs3d.minecrift.asm.handler;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mtbs3d.minecrift.asm.ASMClassHandler;
import com.mtbs3d.minecrift.asm.ASMMethodHandler;
import com.mtbs3d.minecrift.asm.ASMUtil;
import com.mtbs3d.minecrift.asm.ClassTuple;
import com.mtbs3d.minecrift.asm.MethodTuple;

public class ASMHandlerGuiIngameForge extends ASMClassHandler {
	@Override
	public ClassTuple getDesiredClass() {
		return new ClassTuple("net.minecraftforge.client.GuiIngameForge");
	}

	@Override
	public ASMMethodHandler[] getMethodHandlers() {
		return new ASMMethodHandler[]{new RemoveCrosshairMethodHandler()};
	}

	@Override
	public boolean getComputeFrames() {
		return false;
	}

	public static class RemoveCrosshairMethodHandler implements ASMMethodHandler {
		@Override
		public MethodTuple getDesiredMethod() {
			return new MethodTuple("func_175180_a", "(F)V"); //renderGameOverlay
		}

		@Override
		public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated) {
			MethodInsnNode insn = (MethodInsnNode)ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/GuiIngameForge", "renderCrosshairs", "(F)V", false);
			ASMUtil.deleteInstructions(methodNode, methodNode.instructions.indexOf(insn) - 2, 3);
			System.out.println("Deleted renderCrosshairs call");
			
		}
	}
}
