package com.mtbs3d.minecrift.utils;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;

public class ASMDelegator {
	public static boolean containerCreativeMouseDown() {
		return Mouse.isButtonDown(0) || GuiScreen.mouseDown;
	}
	
	public static void dummy(float f) {
		// does nothing
	}
}
