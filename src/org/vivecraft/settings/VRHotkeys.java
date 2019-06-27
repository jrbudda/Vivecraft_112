/**
* Copyright 2013 Mark Browning, StellaArtois
* Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
*/
package org.vivecraft.settings;

import org.vivecraft.api.VRData;
import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.utils.Angle;
import org.vivecraft.utils.Axis;
import org.vivecraft.utils.Quaternion;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.Vector3;

import de.fruitfly.ovr.structs.Matrix4f;
import de.fruitfly.ovr.structs.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class VRHotkeys {

	static long nextRead = 0;
	static final long COOLOFF_PERIOD_MILLIS = 500;
	static boolean debug = false;

	private static int startController;
	private static VRData.VRDevicePose startControllerPose;
	private static float startCamposX;
	private static float startCamposY;
	private static float startCamposZ;
	private static Quaternion startCamrotQuat;

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
			snapMRCam(0);
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
			adjustCamPos(new Vector3(-0.01F, 0, 0));
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamPos(new Vector3(0.01F, 0, 0));
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamPos(new Vector3(0, 0, -0.01F));
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamPos(new Vector3(0, 0, 0.01F));
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamPos(new Vector3(0, 0.01F, 0));
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamPos(new Vector3(0, -0.01F, 0));
			gotKey = true;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamRot(Axis.PITCH, 0.5F);
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamRot(Axis.PITCH, -0.5F);
			gotKey = true;

		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamRot(Axis.YAW, 0.5F);
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamRot(Axis.YAW, -0.5F);
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamRot(Axis.ROLL, 0.5F);
			gotKey = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			adjustCamRot(Axis.ROLL, -0.5F);
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
		
		if (Keyboard.isKeyDown(Keyboard.KEY_INSERT) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.mixedRealityFov +=1 ;
			gotKey = true;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DELETE) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)&& Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			mc.vrSettings.mixedRealityFov -=1 ;
			gotKey = true;
		}
		
		if(gotKey) {
			mc.vrSettings.saveOptions();
			if (MCOpenVR.mrMovingCamActive) {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("X: " + mc.vrSettings.mrMovingCamOffsetX + " Y: " + mc.vrSettings.mrMovingCamOffsetY + " Z: " + mc.vrSettings.mrMovingCamOffsetZ));
				Angle angle = mc.vrSettings.mrMovingCamOffsetRotQuat.toEuler();
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Pitch: " + angle.getPitch() + " Yaw: " + angle.getYaw() + " Roll: " + angle.getRoll()));
			} else {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("X: " + mc.vrSettings.vrFixedCamposX + " Y: " + mc.vrSettings.vrFixedCamposY + " Z: " + mc.vrSettings.vrFixedCamposZ));
				Angle angle = mc.vrSettings.vrFixedCamrotQuat.toEuler();
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Pitch: " + angle.getPitch() + " Yaw: " + angle.getYaw() + " Roll: " + angle.getRoll()));
			}
		}
	}
	
	private static void adjustCamPos(Vector3 offset) {
		Minecraft mc = Minecraft.getMinecraft();
		if (MCOpenVR.mrMovingCamActive) {
			offset = mc.vrSettings.mrMovingCamOffsetRotQuat.multiply(offset);
			mc.vrSettings.mrMovingCamOffsetX += offset.getX();
			mc.vrSettings.mrMovingCamOffsetY += offset.getY();
			mc.vrSettings.mrMovingCamOffsetZ += offset.getZ();
		} else {
			offset = mc.vrSettings.vrFixedCamrotQuat.inverse().multiply(offset);
			mc.vrSettings.vrFixedCamposX += offset.getX();
			mc.vrSettings.vrFixedCamposY += offset.getY();
			mc.vrSettings.vrFixedCamposZ += offset.getZ();
		}
	}

	private static void adjustCamRot(Axis axis, float degrees) {
		Minecraft mc = Minecraft.getMinecraft();
		if (MCOpenVR.mrMovingCamActive) {
			mc.vrSettings.mrMovingCamOffsetRotQuat.set(mc.vrSettings.mrMovingCamOffsetRotQuat.rotate(axis, degrees, true));
		} else {
			mc.vrSettings.vrFixedCamrotQuat.set(mc.vrSettings.vrFixedCamrotQuat.rotate(axis, degrees, false));
		}
	}
	
	public static void snapMRCam(int controller) {
		Minecraft mc = Minecraft.getMinecraft();
		Vec3d c = mc.vrPlayer.vrdata_room_pre.getController(controller).getPosition();
		mc.vrSettings.vrFixedCamposX =(float) c.x;
		mc.vrSettings.vrFixedCamposY =(float) c.y;
		mc.vrSettings.vrFixedCamposZ =(float) c.z;	
		
		Quaternion quat = new Quaternion(Utils.convertOVRMatrix(mc.vrPlayer.vrdata_room_pre.getController(controller).getMatrix()));
		mc.vrSettings.vrFixedCamrotQuat.set(quat);
	}

	public static void updateMovingThirdPersonCam() {
		Minecraft mc = Minecraft.getMinecraft();

		if (startControllerPose != null) {
			VRData.VRDevicePose controllerPose = mc.vrPlayer.vrdata_room_pre.getController(startController);
			Vec3d startPos = startControllerPose.getPosition();
			Vec3d deltaPos = controllerPose.getPosition().subtract(startPos);

			Matrix4f deltaMatrix = Matrix4f.multiply(controllerPose.getMatrix(), startControllerPose.getMatrix().inverted());
			Vector3f offset = new Vector3f(startCamposX - (float)startPos.x, startCamposY - (float)startPos.y, startCamposZ - (float)startPos.z);
			Vector3f offsetRotated = deltaMatrix.transform(offset);

			mc.vrSettings.vrFixedCamposX = startCamposX + (float)deltaPos.x + (offsetRotated.x - offset.x);
			mc.vrSettings.vrFixedCamposY = startCamposY + (float)deltaPos.y + (offsetRotated.y - offset.y);
			mc.vrSettings.vrFixedCamposZ = startCamposZ + (float)deltaPos.z + (offsetRotated.z - offset.z);
			mc.vrSettings.vrFixedCamrotQuat.set(startCamrotQuat.multiply(new Quaternion(Utils.convertOVRMatrix(deltaMatrix))));
		}
	}

	public static void startMovingThirdPersonCam(int controller) {
		Minecraft mc = Minecraft.getMinecraft();
		startController = controller;
		startControllerPose = mc.vrPlayer.vrdata_room_pre.getController(controller);
		startCamposX = mc.vrSettings.vrFixedCamposX;
		startCamposY = mc.vrSettings.vrFixedCamposY;
		startCamposZ = mc.vrSettings.vrFixedCamposZ;
		startCamrotQuat = mc.vrSettings.vrFixedCamrotQuat.copy();
	}

	public static void stopMovingThirdPersonCam() {
		startControllerPose = null;
	}

	public static boolean isMovingThirdPersonCam() {
		return startControllerPose != null;
	}
}
