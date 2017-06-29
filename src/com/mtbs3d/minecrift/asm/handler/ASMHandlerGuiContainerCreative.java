package com.mtbs3d.minecrift.asm.handler;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mtbs3d.minecrift.asm.ASMClassHandler;
import com.mtbs3d.minecrift.asm.ASMMethodHandler;
import com.mtbs3d.minecrift.asm.ASMUtil;
import com.mtbs3d.minecrift.asm.ClassTuple;
import com.mtbs3d.minecrift.asm.MethodTuple;
import com.mtbs3d.minecrift.asm.ObfNames;

public class ASMHandlerGuiContainerCreative extends ASMClassHandler {
	@Override
	public ClassTuple getDesiredClass() {
		return new ClassTuple("net.minecraft.client.gui.inventory.GuiContainerCreative", ObfNames.GUICONTAINERCREATIVE);
	}

	@Override
	public ASMMethodHandler[] getMethodHandlers() {
		return new ASMMethodHandler[]{new MouseDownMethodHandler()};
	}

	@Override
	public boolean getComputeFrames() {
		return false;
	}

	public static class MouseDownMethodHandler implements ASMMethodHandler {
		@Override
		public MethodTuple getDesiredMethod() {
			return new MethodTuple("drawScreen", "(IIF)V", "a", "(IIF)V");
		}

		@Override
		public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated) {
			MethodInsnNode insn = (MethodInsnNode)ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKESTATIC, "org/lwjgl/input/Mouse", "isButtonDown", "(I)Z", false);
			insn.owner = "com/mtbs3d/minecrift/utils/ASMDelegator";
			insn.name = "containerCreativeMouseDown";
			insn.desc = "()Z";
			System.out.println("Redirected method call to delegator");
		}
	}
}
