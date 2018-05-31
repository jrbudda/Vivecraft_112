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

public class ASMHandlerGuiContainer extends ASMClassHandler {
	@Override
	public ClassTuple getDesiredClass() {
		return new ClassTuple("net.minecraft.client.gui.inventory.GuiContainer");
	}

	@Override
	public ASMMethodHandler[] getMethodHandlers() {
		return new ASMMethodHandler[]{new FakeShiftMethodHandler(), new ColorMaskMethodHandler()};
	}

	@Override
	public boolean getComputeFrames() {
		return false;
	}

	public static class FakeShiftMethodHandler implements ASMMethodHandler {
		@Override
		public MethodTuple getDesiredMethod() {
			return new MethodTuple("mouseClicked", "(III)V", "a", "(III)V");
		}

		@Override
		public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated) {
			MethodInsnNode insn = (MethodInsnNode)ASMUtil.findFirstInstructionPattern(methodNode, new Object[]{Opcodes.BIPUSH, 54}, new Object[]{Opcodes.INVOKESTATIC, "org/lwjgl/input/Keyboard", "isKeyDown", "(I)Z", false});
			JumpInsnNode jumpInsn = (JumpInsnNode)methodNode.instructions.get(methodNode.instructions.indexOf(insn) - 2);
			InsnList insnList = new InsnList();
			insnList.add(new JumpInsnNode(Opcodes.IFNE, jumpInsn.label));
			insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ObfNames.resolve("net/minecraft/client/gui/inventory/GuiContainer", obfuscated), "isFakeShift", "()Z", false));
			methodNode.instructions.insert(insn, insnList);
			System.out.println("Inserted pressShiftFake check");
		}
	}

	public static class ColorMaskMethodHandler implements ASMMethodHandler {
		@Override
		public MethodTuple getDesiredMethod() {
			return new MethodTuple("drawScreen", "(IIF)V", "a", "(IIF)V");
		}

		@Override
		public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated) {
			AbstractInsnNode findInsn = ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKEVIRTUAL, ObfNames.resolve("net/minecraft/inventory/Slot", obfuscated), obfuscated ? "b" : "canBeHovered", "()Z", false);
			AbstractInsnNode insn = methodNode.instructions.get(methodNode.instructions.indexOf(findInsn) + 1);
			InsnList insnList = new InsnList();
			insnList.add(new InsnNode(Opcodes.ICONST_1));
			insnList.add(new InsnNode(Opcodes.ICONST_1));
			insnList.add(new InsnNode(Opcodes.ICONST_1));
			insnList.add(new InsnNode(Opcodes.ICONST_0));
			insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ObfNames.resolve("net/minecraft/client/renderer/GlStateManager", obfuscated), obfuscated ? "a" : "colorMask", "(ZZZZ)V", false));
			methodNode.instructions.insert(insn, insnList);
			insn = (MethodInsnNode)ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKESPECIAL, ObfNames.resolve("net/minecraft/client/gui/inventory/GuiContainer", obfuscated), obfuscated ? "a" : "drawSlot", ObfNames.resolveDescriptor("(Lnet/minecraft/inventory/Slot;)V", obfuscated), false);
			insnList.clear();
			insnList.add(new InsnNode(Opcodes.ICONST_1));
			insnList.add(new InsnNode(Opcodes.ICONST_1));
			insnList.add(new InsnNode(Opcodes.ICONST_1));
			insnList.add(new InsnNode(Opcodes.ICONST_1));
			insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ObfNames.resolve("net/minecraft/client/renderer/GlStateManager", obfuscated), obfuscated ? "a" : "colorMask", "(ZZZZ)V", false));
			insn = ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKESTATIC, ObfNames.resolve("net/minecraft/inventory/GlStateManager", obfuscated), obfuscated ? "H" : "popMatrix", "()V", false);
			methodNode.instructions.insert(insn, insnList); // same call
			System.out.println("Inserted colorMask calls");
		}
	}
}
