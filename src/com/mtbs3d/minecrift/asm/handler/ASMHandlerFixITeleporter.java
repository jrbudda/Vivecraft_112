package com.mtbs3d.minecrift.asm.handler;

import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mtbs3d.minecrift.asm.ASMClassHandler;
import com.mtbs3d.minecrift.asm.ASMMethodHandler;
import com.mtbs3d.minecrift.asm.ASMUtil;
import com.mtbs3d.minecrift.asm.ClassTuple;
import com.mtbs3d.minecrift.asm.MethodTuple;
import com.mtbs3d.minecrift.asm.ObfNames;

public class ASMHandlerFixITeleporter extends ASMClassHandler {
	@Override
	public ClassTuple getDesiredClass() {
		return new ClassTuple("net.minecraft.entity.player.EntityPlayerMP");
	}

	@Override
	public ASMMethodHandler[] getMethodHandlers() {
		return new ASMMethodHandler[]{new ChangeClassRefMethodHandler()};
	}

	@Override
	public boolean getComputeFrames() {
		return false;
	}

	public static class ChangeClassRefMethodHandler implements ASMMethodHandler {
		@Override
		public MethodTuple getDesiredMethod() {
			return new MethodTuple("changeDimension", "(ILcom/mtbs3d/minecrift/utils/ITeleporterDummy;)Lnet/minecraft/entity/Entity;");
		}

		@Override
		public void patchMethod(MethodNode methodNode, ClassNode classNode, boolean obfuscated) {
			String oldClass = "com/mtbs3d/minecrift/utils/ITeleporterDummy";
			String newClass = "net/minecraftforge/common/util/ITeleporter";
			System.out.println("Applying ITeleporterDummy -> ITeleporter replacements...");
			methodNode.desc = methodNode.desc.replace(oldClass, newClass);
			System.out.println("Replaced in method descriptor");
			for (Iterator<AbstractInsnNode> insns = methodNode.instructions.iterator(); insns.hasNext();) {
				AbstractInsnNode insn = insns.next();
				if (insn instanceof MethodInsnNode) {
					MethodInsnNode methodInsn = (MethodInsnNode)insn;
					methodInsn.owner = methodInsn.owner.replace(oldClass, newClass);
					System.out.println("Replaced in method call owner");
				} else if (insn instanceof FrameNode) {
					FrameNode frame = (FrameNode)insn;
					if (frame.local != null) {
						frame.local.replaceAll(l -> {
							if (l instanceof String)
								return ((String)l).replace(oldClass, newClass);
							return l;
						});
						System.out.println("Replaced in frame locals");
					}
				}
			}
			for (Iterator<LocalVariableNode> locals = methodNode.localVariables.iterator(); locals.hasNext();) {
				LocalVariableNode local = locals.next();
				local.desc = local.desc.replace(oldClass, newClass);
			}
			System.out.println("Replaced in method locals");
		}
	}
}
