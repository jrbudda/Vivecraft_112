/**
* Copyright 2013 Mark Browning, StellaArtois
* Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
*/
package com.mtbs3d.minecrift.settings;

import com.google.common.util.concurrent.Runnables;
import com.mtbs3d.minecrift.gameplay.OpenVRPlayer;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;
import com.mtbs3d.minecrift.settings.VRSettings.VrOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

import org.lwjgl.input.Keyboard;

public class VRHotkeys {

	static long nextRead = 0;
	static final long COOLOFF_PERIOD_MILLIS = 500;

	public static boolean handleKeyboardInputs(Minecraft mc)
	{
		// Support cool-off period for key presses - otherwise keys can get spammed...
		if (nextRead != 0 && System.currentTimeMillis() < nextRead)
		return false;

		// Capture Minecrift key events
		boolean gotKey = false;

		// Debug aim
		if (Keyboard.getEventKey() == Keyboard.KEY_RSHIFT && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		{
			mc.vrSettings.storeDebugAim = true;
			mc.printChatMessage("Show aim (RCTRL+RSHIFT): done");
			gotKey = true;
		}

		// Walk up blocks
		if (Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		{
			mc.vrSettings.walkUpBlocks = !mc.vrSettings.walkUpBlocks;
			mc.printChatMessage("Walk up blocks (RCTRL+B): " + (mc.vrSettings.walkUpBlocks ? "YES" : "NO"));
			gotKey = true;
		}

		// Player inertia
		if (Keyboard.getEventKey() == Keyboard.KEY_I && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
		{
			mc.vrSettings.inertiaFactor += 1;
			if (mc.vrSettings.inertiaFactor > VRSettings.INERTIA_MASSIVE)
			mc.vrSettings.inertiaFactor = VRSettings.INERTIA_NONE;
			switch (mc.vrSettings.inertiaFactor)
			{
			case VRSettings.INERTIA_NONE:
				mc.printChatMessage("Player player movement inertia (LCTRL-I): None");
				break;
			case VRSettings.INERTIA_NORMAL:
				mc.printChatMessage("Player player movement inertia (LCTRL-I): Normal");
				break;
			case VRSettings.INERTIA_LARGE:
				mc.printChatMessage("Player player movement inertia (LCTRL-I): Large");
				break;
			case VRSettings.INERTIA_MASSIVE:
				mc.printChatMessage("Player player movement inertia (LCTRL-I): Massive");
				break;
			}
			gotKey = true;
		}

		// Render full player model or just an disembodied hand...
		if (Keyboard.getEventKey() == Keyboard.KEY_H && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		{
			mc.vrSettings.renderFullFirstPersonModelMode++;
			if (mc.vrSettings.renderFullFirstPersonModelMode > VRSettings.RENDER_FIRST_PERSON_NONE)
			mc.vrSettings.renderFullFirstPersonModelMode = VRSettings.RENDER_FIRST_PERSON_FULL;

				switch (mc.vrSettings.renderFullFirstPersonModelMode)
			{
			case VRSettings.RENDER_FIRST_PERSON_FULL:
				mc.printChatMessage("First person model (RCTRL-H): Full");
				break;
			case VRSettings.RENDER_FIRST_PERSON_HAND:
				mc.printChatMessage("First person model (RCTRL-H): Hand");
				break;
			case VRSettings.RENDER_FIRST_PERSON_NONE:
				mc.printChatMessage("First person model (RCTRL-H): None");
				break;
			}
			gotKey = true;
		}
		// VIVE START - hotkeys

		// Testing different movement styles
//		if (Keyboard.getEventKey() == Keyboard.KEY_M && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
//		{
//			// JRBUDDA ADDED all dis.
//			if (mc.vrPlayer.getFreeMoveMode()) {
//				//cycle restricted movement styles
//				if (mc.vrPlayer.useLControllerForRestricedMovement) {
//					mc.vrPlayer.useLControllerForRestricedMovement = false;
//					mc.printChatMessage("Restricted movement mode set to gaze");
//				} else {
//					mc.vrPlayer.useLControllerForRestricedMovement = true;
//					mc.printChatMessage("Restricted movement mode set to left controller");
//				}
//			} else {				
//				OpenVRPlayer vrp = mc.vrPlayer;				
//				// cycle VR movement styles
//				if (vrp.vrMovementStyle.name == "Minimal") vrp.vrMovementStyle.setStyle("Beam");
//				else if (vrp.vrMovementStyle.name == "Beam") vrp.vrMovementStyle.setStyle("Tunnel");
//				else if (vrp.vrMovementStyle.name == "Tunnel") vrp.vrMovementStyle.setStyle("Grapple");
//				else if (vrp.vrMovementStyle.name == "Grapple") vrp.vrMovementStyle.setStyle("Arc");
//				else vrp.vrMovementStyle.setStyle("Minimal");			
//			}
//					
//			gotKey = true;
//		}

		if (Keyboard.getEventKey() == Keyboard.KEY_R && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		{
			// for testing restricted client mode
			
			if (mc.vrPlayer.getFreeMove()) {
				mc.vrPlayer.setFreeMove(false);
							mc.printChatMessage("Restricted movement disabled (teleporting allowed)");
				} else {
				mc.vrPlayer.setFreeMove(true);
				mc.printChatMessage("Restricted movement enabled (no teleporting)");
			}
			
			gotKey = true;
		}
		
		
		if (Keyboard.getEventKey() == Keyboard.KEY_HOME && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		{
			snapMRCam(mc, 0);
			gotKey = true;
		}
		if(Keyboard.getEventKey() == Keyboard.KEY_F12){
            //mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
			gotKey = true;
		}

		// VIVE END - hotkeys

		if (gotKey) {
			mc.vrSettings.saveOptions();
		}

		return gotKey;
	}

	public static void handleMRKeys() {
		Minecraft mc = Minecraft.getMinecraft();
		
		boolean gotKey = false;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.vrFixedCamposX -= 0.01;
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.vrFixedCamposX += 0.01;
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.vrFixedCamposZ -= 0.01;
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.vrFixedCamposZ += 0.01;
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.vrFixedCamposY += 0.01;
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.vrFixedCamposY -= 0.01;
			gotKey = true;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			if(MCOpenVR.mrMovingCamActive) {
				mc.vrSettings.mrMovingCamOffsetPitch -= 90;
			}else {
				mc.vrSettings.vrFixedCamrotPitch -= 0.5;
			}
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			if(MCOpenVR.mrMovingCamActive) {
				mc.vrSettings.mrMovingCamOffsetPitch += 90;
			}else {
				mc.vrSettings.vrFixedCamrotPitch += 0.5;	
			}
			gotKey = true;

		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			if(MCOpenVR.mrMovingCamActive) {
				mc.vrSettings.mrMovingCamOffsetYaw -= 90;
			}else {
				mc.vrSettings.vrFixedCamrotYaw -= 0.5;
			}
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			if(MCOpenVR.mrMovingCamActive) {
				mc.vrSettings.mrMovingCamOffsetYaw += 90;
			}else {
				mc.vrSettings.vrFixedCamrotYaw += 0.5;
			}
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			if(MCOpenVR.mrMovingCamActive) {
				mc.vrSettings.mrMovingCamOffsetRoll -= 90;
			}else {
				mc.vrSettings.vrFixedCamrotRoll -= 0.05;
			}
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			if(MCOpenVR.mrMovingCamActive) {
				mc.vrSettings.mrMovingCamOffsetRoll += 90;
			}else {
				mc.vrSettings.vrFixedCamrotRoll += 0.05;	
			}
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_INSERT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		{
			mc.gameSettings.fovSetting +=1 ;
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DELETE) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		{
			mc.gameSettings.fovSetting -=1 ;
			gotKey = true;
		}
		
		if(gotKey) {
			mc.vrSettings.saveOptions();
			if(MCOpenVR.mrMovingCamActive) { //todo print offsets
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("pitch: " + mc.vrSettings.mrMovingCamOffsetPitch + " yaw: " + mc.vrSettings.mrMovingCamOffsetYaw + " roll: " + mc.vrSettings.mrMovingCamOffsetRoll));            
			} else {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("pitch: " + mc.vrSettings.vrFixedCamrotPitch + " yaw: " + mc.vrSettings.vrFixedCamrotYaw + " roll: " + mc.vrSettings.vrFixedCamrotRoll));            
			}
		}
	}
	
	public static void snapMRCam(Minecraft mc, int controller) {
		Vec3d c = mc.vrPlayer.vrdata_room_pre.getController(controller).getPosition();
		mc.vrSettings.vrFixedCamposX =(float) c.x;
		mc.vrSettings.vrFixedCamposY =(float) c.y;
		mc.vrSettings.vrFixedCamposZ =(float) c.z;	
		
		mc.vrSettings.vrFixedCamrotPitch = mc.vrPlayer.vrdata_room_pre.getController(controller).getPitch();
		mc.vrSettings.vrFixedCamrotYaw = mc.vrPlayer.vrdata_room_pre.getController(controller).getYaw();
		mc.vrSettings.vrFixedCamrotRoll = mc.vrPlayer.vrdata_room_pre.getController(controller).getRoll();
	}
}
