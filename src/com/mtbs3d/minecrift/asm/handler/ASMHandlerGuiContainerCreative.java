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
		return new ClassTuple("net.minecraft.client.gui.inventory.GuiContainerCreative");
	}

	@Override
	public ASMMethodHandler[] getMethodHandlers() {
		return new ASMMethodHandler[]{new MouseDownMethodHandler(), new AddItemsMethodHandler(), new AddSearchMethodHandler()};
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
			insn.desc = "(I)Z";
			System.out.println("Redirected Mouse.isButtonDown() to delegator");
		}
	}

	public static class AddItemsMethodHandler implements ASMMethodHandler {
		@Override
		public MethodTuple getDesiredMethod() {
			return new MethodTuple("setCurrentCreativeTab", "(Lnet/minecraft/creativetab/CreativeTabs;)V", "b");
		}

		@Override
		public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated) {
			InsnList newInsns = new InsnList();
			newInsns.add(new VarInsnNode(Opcodes.ALOAD, 1));
			newInsns.add(new VarInsnNode(Opcodes.ALOAD, 3));
			newInsns.add(new FieldInsnNode(Opcodes.GETFIELD, ObfNames.resolve("net/minecraft/client/gui/inventory/GuiContainerCreative$ContainerCreative", obfuscated), obfuscated ? "a" : "itemList", ObfNames.resolveDescriptor("Lnet/minecraft/util/NonNullList;", obfuscated)));
			newInsns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mtbs3d/minecrift/utils/ASMDelegator", "addCreativeItems", ObfNames.resolveDescriptor("(Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/util/NonNullList;)V", obfuscated), false));
			AbstractInsnNode insn = ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKEVIRTUAL, ObfNames.resolve("net/minecraft/creativetab/CreativeTabs", obfuscated), obfuscated ? "a" : "displayAllRelevantItems", ObfNames.resolveDescriptor("(Lnet/minecraft/util/NonNullList;)V", obfuscated), false);
			methodNode.instructions.insert(insn, newInsns);
			System.out.println("Inserted call to delegator");
		}
	}

	public static class AddSearchMethodHandler implements ASMMethodHandler {
		@Override
		public MethodTuple getDesiredMethod() {
			return new MethodTuple("updateCreativeSearch", "()V", "h", "()V");
		}

		@Override
		public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated) {
			InsnList newInsns = new InsnList();
			newInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
			newInsns.add(new FieldInsnNode(Opcodes.GETFIELD, ObfNames.resolve("net/minecraft/client/gui/inventory/GuiContainerCreative", obfuscated), obfuscated ? "C" : "searchField", ObfNames.resolveDescriptor("Lnet/minecraft/client/gui/GuiTextField;", obfuscated)));
			newInsns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ObfNames.resolve("net/minecraft/client/gui/GuiTextField", obfuscated), obfuscated ? "b" : "getText", "()Ljava/lang/String;", false));
			newInsns.add(new VarInsnNode(Opcodes.ALOAD, 1));
			newInsns.add(new FieldInsnNode(Opcodes.GETFIELD, ObfNames.resolve("net/minecraft/client/gui/inventory/GuiContainerCreative$ContainerCreative", obfuscated), obfuscated ? "a" : "itemList", ObfNames.resolveDescriptor("Lnet/minecraft/util/NonNullList;", obfuscated)));
			newInsns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mtbs3d/minecrift/utils/ASMDelegator", "addCreativeSearch", ObfNames.resolveDescriptor("(Ljava/lang/String;Lnet/minecraft/util/NonNullList;)V", obfuscated), false));
			AbstractInsnNode insn = ASMUtil.findFirstInstruction(methodNode, Opcodes.PUTFIELD, ObfNames.resolve("net/minecraft/client/gui/inventory/GuiContainerCreative", obfuscated), obfuscated ? "z" : "currentScroll", "F");
			ASMUtil.insertInstructionsRelative(methodNode, insn, -3, newInsns);
			System.out.println("Inserted call to delegator");
		}
	}
}
