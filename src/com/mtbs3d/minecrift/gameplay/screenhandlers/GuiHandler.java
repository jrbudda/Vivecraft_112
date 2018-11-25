package com.mtbs3d.minecrift.gameplay.screenhandlers;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.mtbs3d.minecrift.control.VRButtonMapping;
import com.mtbs3d.minecrift.control.VRInputEvent;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.InputInjector;
import com.mtbs3d.minecrift.utils.KeyboardSimulator;

import de.fruitfly.ovr.structs.Matrix4f;
import de.fruitfly.ovr.structs.Quatf;
import de.fruitfly.ovr.structs.Vector3f;
import jopenvr.OpenVRUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class GuiHandler {
	public static Minecraft mc = Minecraft.getMinecraft();
	//TODO: to hell with all these conversions.
	//sets mouse position for currentscreen
	
	static boolean lastPressedLeftClick;
	static boolean lastPressedRightClick;
	static boolean lastPressedMiddleClick;
	static boolean lastPressedShift;
	static boolean lastPressedCtrl;
	static boolean lastPressedAlt;
	
	// For mouse menu emulation
	private static float controllerMouseX = -1.0f;
	private static float controllerMouseY = -1.0f;
	public static boolean controllerMouseValid;
	public static int controllerMouseTicks;

	public static float guiScale = 1.0f;
	public static Vec3d guiPos_room = new Vec3d(0,0,0);
	public static Matrix4f guiRotation_room = new Matrix4f();
	
	public static final KeyBinding guiMenuButton = new KeyBinding("GUI Menu Button", 0, "Vivecraft GUI");
	public static final KeyBinding guiLeftClick = new KeyBinding("GUI Left Click", 0, "Vivecraft GUI");
	public static final KeyBinding guiRightClick = new KeyBinding("GUI Right Click", 0, "Vivecraft GUI");
	public static final KeyBinding guiMiddleClick = new KeyBinding("GUI Middle Click", 0, "Vivecraft GUI");
	public static final KeyBinding guiShift = new KeyBinding("GUI Shift", 0, "Vivecraft GUI");
	public static final KeyBinding guiCtrl = new KeyBinding("GUI Ctrl", 0, "Vivecraft GUI");
	public static final KeyBinding guiAlt = new KeyBinding("GUI Alt", 0, "Vivecraft GUI");
	public static final KeyBinding guiScrollUp = new KeyBinding("GUI Scroll Up", 0, "Vivecraft GUI");
	public static final KeyBinding guiScrollDown = new KeyBinding("GUI Scroll Down", 0, "Vivecraft GUI");
	public static Framebuffer guiFramebuffer = null;


	public static void processGui() {
		if(mc.currentScreen == null)return;
		if(mc.vrSettings.seated) return;
		if(guiRotation_room == null) return;

		Vector3f controllerPos = new Vector3f();
		//OpenVRUtil.convertMatrix4ftoTranslationVector(controllerPose[0]);
		Vec3d con = mc.vrPlayer.vrdata_room_pre.getController(0).getPosition();
		controllerPos.x	= (float) con.x;
		controllerPos.y	= (float) con.y;
		controllerPos.z	= (float) con.z;

		Vec3d controllerdir = mc.vrPlayer.vrdata_room_pre.getController(0).getDirection();
		Vector3f cdir = new Vector3f((float)controllerdir.x,(float) controllerdir.y,(float) controllerdir.z);
		Vector3f forward = new Vector3f(0,0,1);

		Vector3f guiNormal = guiRotation_room.transform(forward);
		Vector3f guiRight = guiRotation_room.transform(new Vector3f(1,0,0));
		Vector3f guiUp = guiRotation_room.transform(new Vector3f(0,1,0));

		float guiWidth = 1.0f;		
		float guiHalfWidth = guiWidth * 0.5f;		
		float guiHeight = 1.0f;	
		float guiHalfHeight = guiHeight * 0.5f;

		Vector3f gp = new Vector3f();

		gp.x = (float) (guiPos_room.x);// + interPolatedRoomOrigin.x ) ;
		gp.y = (float) (guiPos_room.y);// + interPolatedRoomOrigin.y ) ;
		gp.z = (float) (guiPos_room.z);// + interPolatedRoomOrigin.z ) ;

		Vector3f guiTopLeft = gp.subtract(guiUp.divide(1.0f / guiHalfHeight)).subtract(guiRight.divide(1.0f/guiHalfWidth));
		Vector3f guiTopRight = gp.subtract(guiUp.divide(1.0f / guiHalfHeight)).add(guiRight.divide(1.0f / guiHalfWidth));

		//Vector3f guiBottomLeft = guiPos.add(guiUp.divide(1.0f / guiHalfHeight)).subtract(guiRight.divide(1.0f/guiHalfWidth));
		//Vector3f guiBottomRight = guiPos.add(guiUp.divide(1.0f / guiHalfHeight)).add(guiRight.divide(1.0f/guiHalfWidth));

		float guiNormalDotControllerDirection = guiNormal.dot(cdir);
		if (Math.abs(guiNormalDotControllerDirection) > 0.00001f)
		{//pointed normal to the GUI
			float intersectDist = -guiNormal.dot(controllerPos.subtract(guiTopLeft)) / guiNormalDotControllerDirection;
			Vector3f pointOnPlane = controllerPos.add(cdir.divide(1.0f/intersectDist));

			Vector3f relativePoint = pointOnPlane.subtract(guiTopLeft);
			float u = relativePoint.dot(guiRight.divide(1.0f/guiWidth));
			float v = relativePoint.dot(guiUp.divide(1.0f/guiWidth));


			// adjust vertical for aspect ratio
			v = ( (v - 0.5f) * ((float)1280 / (float)720) ) + 0.5f;

			// TODO: Figure out where this magic 0.68f comes from. Probably related to Minecraft window size.
			//JRBUDDA: It's probbably 1/defaulthudscale (1.5)

			u = ( u - 0.5f ) * 0.68f / guiScale + 0.5f;
			v = ( v - 0.5f ) * 0.68f / guiScale + 0.5f;

			if (u<0 || v<0 || u>1 || v>1)
			{
				// offscreen
				controllerMouseX = -1.0f;
				controllerMouseY = -1.0f;
			}
			else if (controllerMouseX == -1.0f)
			{
				controllerMouseX = (int) (u * mc.displayWidth);
				controllerMouseY = (int) (v * mc.displayHeight);
			}
			else
			{
				// apply some smoothing between mouse positions
				float newX = (int) (u * mc.displayWidth);
				float newY = (int) (v * mc.displayHeight);
				controllerMouseX = controllerMouseX * 0.7f + newX * 0.3f;
				controllerMouseY = controllerMouseY * 0.7f + newY * 0.3f;
			}

			// copy to mc for debugging
			//			mc.guiU = u;
			//			mc.guiV = v;
			//			mc.intersectDist = intersectDist;
			//			mc.pointOnPlaneX = pointOnPlane.x;
			//			mc.pointOnPlaneY = pointOnPlane.y;
			//			mc.pointOnPlaneZ = pointOnPlane.z;
			//			mc.guiTopLeftX = guiTopLeft.x;
			//			mc.guiTopLeftY = guiTopLeft.y;
			//			mc.guiTopLeftZ = guiTopLeft.z;
			//			mc.guiTopRightX = guiTopRight.x;
			//			mc.guiTopRightY = guiTopRight.y;
			//			mc.guiTopRightZ = guiTopRight.z;
			//			mc.controllerPosX = controllerPos.x;
			//			mc.controllerPosY = controllerPos.y;
			//			mc.controllerPosZ = controllerPos.z;
		}

		if (controllerMouseX >= 0 && controllerMouseX < mc.displayWidth
				&& controllerMouseY >=0 && controllerMouseY < mc.displayHeight)
		{
			// mouse on screen
			int mouseX = Math.min(Math.max((int) controllerMouseX, 0), mc.displayWidth);
			int mouseY = Math.min(Math.max((int) controllerMouseY, 0), mc.displayHeight);

			if (MCOpenVR.controllerDeviceIndex[MCOpenVR.RIGHT_CONTROLLER] != -1)
			{
				InputInjector.mouseMoveEvent(mouseX, mouseY); // Needs to be called first, since it only puts an event if delta != 0
				Mouse.setCursorPosition(mouseX, mouseY);
				controllerMouseValid = true;

			}
		} else { //mouse off screen
			if(controllerMouseTicks == 0)
				controllerMouseValid = false;

			if(controllerMouseTicks>0)controllerMouseTicks--;
		}
	}

	public static void processBindingsGui() {
		if (controllerMouseX >= 0 && controllerMouseX < mc.displayWidth
				&& controllerMouseY >=0 && controllerMouseY < mc.displayHeight)
		{
			// mouse on screen
			int mouseX = Math.min(Math.max((int) controllerMouseX, 0), mc.displayWidth);
			int mouseY = Math.min(Math.max((int) controllerMouseY, 0), mc.displayHeight);

			if (MCOpenVR.controllerDeviceIndex[MCOpenVR.RIGHT_CONTROLLER] != -1)
			{
				//LMB
				if (guiLeftClick.isPressed() && mc.currentScreen != null)
				{ //press left mouse button
					if (Display.isActive()) 
						KeyboardSimulator.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					else mc.currentScreen.mouseDown(mouseX, mouseY, 0, true);
					lastPressedLeftClick = true;
				}	

				if (guiLeftClick.isKeyDown() && mc.currentScreen != null)				
					mc.currentScreen.mouseDrag(mouseX, mouseY);//Signals mouse move


				if (!guiLeftClick.isKeyDown() && lastPressedLeftClick && mc.currentScreen != null) {
					//release left mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					else mc.currentScreen.mouseUp(mouseX, mouseY, 0, true);
					lastPressedLeftClick = false;
				}
				//end LMB

				//RMB
				if (guiRightClick.isPressed() && mc.currentScreen != null) {
					//press right mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
					else mc.currentScreen.mouseDown(mouseX, mouseY, 1, true);
					lastPressedRightClick = true;
				}	

				if (guiRightClick.isKeyDown() && mc.currentScreen != null)
					mc.currentScreen.mouseDrag(mouseX, mouseY);//Signals mouse move

				if (!guiRightClick.isKeyDown() && lastPressedRightClick && mc.currentScreen != null) {
					//release right mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
					else mc.currentScreen.mouseUp(mouseX, mouseY, 1, true);
					lastPressedRightClick = false;
				}	
				//end RMB	

				//MMB
				if (guiMiddleClick.isPressed() && mc.currentScreen != null) {
					//press middle mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
					else mc.currentScreen.mouseDown(mouseX, mouseY, 2, true);
					lastPressedMiddleClick = true;
				}	

				if (guiMiddleClick.isKeyDown() && mc.currentScreen != null) 
					mc.currentScreen.mouseDrag(mouseX, mouseY);//Signals mouse move

				if (!guiMiddleClick.isKeyDown() && lastPressedMiddleClick && mc.currentScreen != null) {
					//release middle mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
					else mc.currentScreen.mouseUp(mouseX, mouseY, 2, true);
					lastPressedMiddleClick = false;
				}	
				//end MMB

			}
		}

		if (MCOpenVR.controllerDeviceIndex[MCOpenVR.LEFT_CONTROLLER] != -1) {
			//Shift
			if (guiShift.isPressed())				
			{
				//press Shift
				if (mc.currentScreen != null) mc.currentScreen.pressShiftFake = true;
				if (Display.isActive()) KeyboardSimulator.robot.keyPress(KeyEvent.VK_SHIFT);
				lastPressedShift = true;
			}


			if (!guiShift.isKeyDown() && lastPressedShift)			
			{
				//release Shift
				if (mc.currentScreen != null) mc.currentScreen.pressShiftFake = false;
				if (Display.isActive()) KeyboardSimulator.robot.keyRelease(KeyEvent.VK_SHIFT);
				lastPressedShift = false;
			}	
			//end Shift

			//Ctrl
			if (guiCtrl.isPressed())				
			{
				//press Ctrl
				if (Display.isActive()) KeyboardSimulator.robot.keyPress(KeyEvent.VK_CONTROL);
				lastPressedCtrl = true;
			}


			if (!guiCtrl.isKeyDown() && lastPressedCtrl)			
			{
				//release Ctrl
				if (Display.isActive()) KeyboardSimulator.robot.keyRelease(KeyEvent.VK_CONTROL);
				lastPressedCtrl = false;
			}	
			//end Ctrl

			//Alt
			if (guiAlt.isPressed())				
			{
				//press Alt
				if (Display.isActive()) KeyboardSimulator.robot.keyPress(KeyEvent.VK_ALT);
				lastPressedAlt = true;
			}


			if (!guiAlt.isKeyDown() && lastPressedAlt)			
			{
				//release Alt
				if (Display.isActive()) KeyboardSimulator.robot.keyRelease(KeyEvent.VK_ALT);
				lastPressedAlt = false;
			}	
			//end Alt

		}

		if (guiScrollUp.isPressed()) {
			MCOpenVR.triggerBindingHapticPulse(guiScrollUp, 400);
			KeyboardSimulator.robot.mouseWheel(-120);
		}

		if (guiScrollDown.isPressed()) {
			MCOpenVR.triggerBindingHapticPulse(guiScrollDown, 400);
			KeyboardSimulator.robot.mouseWheel(120);
		}

		if(guiMenuButton.isPressed()) { //handle esc		
			if(Display.isActive()){
				KeyboardSimulator.robot.keyPress(KeyEvent.VK_ESCAPE); //window focus... yadda yadda
				KeyboardSimulator.robot.keyRelease(KeyEvent.VK_ESCAPE); //window focus... yadda yadda
			}
			else {
				if (mc.player != null) mc.player.closeScreen();
				else mc.displayGuiScreen((GuiScreen)null);
			}

			KeyboardHandler.setOverlayShowing(false);	
		}
	}

	public static void onGuiScreenChanged(GuiScreen previousScreen, GuiScreen newScreen, boolean unpressKeys)
	{
		if(unpressKeys){
			if(Display.isActive()){ //why do we do this again? something about awt.robot keys getting stuck?
				KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
				KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				KeyboardSimulator.robot.keyRelease(KeyEvent.VK_SHIFT);
				for (VRButtonMapping mapping : mc.vrSettings.buttonMappings.values()) {
					if(newScreen!=null) {
						if(mapping.isGUIBinding() && mapping.keyBinding != mc.gameSettings.keyBindInventory)
							mapping.actuallyUnpress();
					} else
						mapping.actuallyUnpress();
				}
			}
		}

		if(newScreen == null) {
			//just insurance
			guiPos_room = null;
			guiRotation_room = null;
			guiScale=1;
			if(KeyboardHandler.keyboardForGui)
				KeyboardHandler.setOverlayShowing(false);
		} else {
			RadialHandler.setOverlayShowing(false, null);
		}

		// static main menu/win game/
		if (!mc.vrSettings.seated && !mc.vrSettings.menuAlwaysFollowFace && (mc.world==null || newScreen instanceof GuiWinGame)) {
			//TODO reset scale things
			guiScale = 2.0f;
			mc.vrSettings.vrWorldRotationCached = mc.vrSettings.vrWorldRotation;
			mc.vrSettings.vrWorldRotation = 0;
			float[] playArea = MCOpenVR.getPlayAreaSize();
			guiPos_room = new Vec3d(
					(float) (0),
					(float) (1.3f),
					(float) (playArea != null ? -playArea[1] / 2 : -1.5f) - 0.3f);			

			guiRotation_room = new Matrix4f();
			guiRotation_room.M[0][0] = guiRotation_room.M[1][1] = guiRotation_room.M[2][2] = guiRotation_room.M[3][3] = 1.0F;
			guiRotation_room.M[0][1] = guiRotation_room.M[1][0] = guiRotation_room.M[2][3] = guiRotation_room.M[3][1] = 0.0F;
			guiRotation_room.M[0][2] = guiRotation_room.M[1][2] = guiRotation_room.M[2][0] = guiRotation_room.M[3][2] = 0.0F;
			guiRotation_room.M[0][3] = guiRotation_room.M[1][3] = guiRotation_room.M[2][1] = guiRotation_room.M[3][0] = 0.0F;

			return;
		} else { //these dont update when screen open.

			if (mc.vrSettings.vrWorldRotationCached != 0) {
				mc.vrSettings.vrWorldRotation = mc.vrSettings.vrWorldRotationCached;
				mc.vrSettings.vrWorldRotationCached = 0;
			}

		}		

		if((previousScreen==null && newScreen != null) || (newScreen instanceof GuiChat || newScreen instanceof GuiScreenBook || newScreen instanceof GuiEditSign))		
		{
			Quatf controllerOrientationQuat;
			boolean appearOverBlock = (newScreen instanceof GuiCrafting)
					|| (newScreen instanceof GuiChest)
					|| (newScreen instanceof GuiShulkerBox)
					|| (newScreen instanceof GuiHopper)
					|| (newScreen instanceof GuiFurnace)
					|| (newScreen instanceof GuiBrewingStand)
					|| (newScreen instanceof GuiBeacon)
					|| (newScreen instanceof GuiDispenser)
					|| (newScreen instanceof GuiEnchantment)
					|| (newScreen instanceof GuiRepair)
					;

			if(appearOverBlock && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK){	
				//appear over block.
				Vec3d temp =new Vec3d(mc.objectMouseOver.getBlockPos().getX() + 0.5f,
						mc.objectMouseOver.getBlockPos().getY(),
						mc.objectMouseOver.getBlockPos().getZ() + 0.5f);

				Vec3d temp_room = mc.vrPlayer.world_to_room_pos(temp, mc.vrPlayer.vrdata_world_pre);			
				Vec3d pos = mc.vrPlayer.vrdata_room_pre.hmd.getPosition();

				double dist = temp_room.subtract(pos).lengthVector();
				guiScale = (float) Math.sqrt(dist);


				//idk it works.
				Vec3d guiPosWorld = new Vec3d(temp.x, mc.objectMouseOver.getBlockPos().getY() + 1.1 + (0.5f * guiScale/2), temp.z);

				guiPos_room = mc.vrPlayer.world_to_room_pos(guiPosWorld, mc.vrPlayer.vrdata_world_pre);	

				Vector3f look = new Vector3f();
				look.x = (float) (guiPos_room.x - pos.x);
				look.y = (float) (guiPos_room.y - pos.y);
				look.z = (float) (guiPos_room.z - pos.z);

				float pitch = (float) Math.asin(look.y/look.length());
				float yaw = (float) ((float) Math.PI + Math.atan2(look.x, look.z));    
				guiRotation_room = Matrix4f.rotationY((float) yaw);
				Matrix4f tilt = OpenVRUtil.rotationXMatrix(pitch);	
				guiRotation_room = Matrix4f.multiply(guiRotation_room,tilt);		

			}				
			else{
				//static screens like menu, inventory, and dead.
				Vec3d adj = new Vec3d(0,0,-2);
				if (newScreen instanceof GuiChat){
					adj = new Vec3d(0.3,0.75,-2);
				} else if (newScreen instanceof GuiScreenBook || newScreen instanceof GuiEditSign) {
					adj = new Vec3d(0,1,-2);
				}

				Vec3d v = mc.vrPlayer.vrdata_room_pre.hmd.getPosition();
				Vec3d e = mc.vrPlayer.vrdata_room_pre.hmd.getCustomVector(adj);
				guiPos_room = new Vec3d(
						(e.x  / 2 + v.x),
						(e.y / 2 + v.y),
						(e.z / 2 + v.z));

				Vec3d pos = mc.vrPlayer.vrdata_room_pre.hmd.getPosition();
				Vector3f look = new Vector3f();
				look.x = (float) (guiPos_room.x - pos.x);
				look.y = (float) (guiPos_room.y - pos.y);
				look.z = (float) (guiPos_room.z - pos.z);

				float pitch = (float) Math.asin(look.y/look.length());
				float yaw = (float) ((float) Math.PI + Math.atan2(look.x, look.z));    
				guiRotation_room = Matrix4f.rotationY((float) yaw);
				Matrix4f tilt = OpenVRUtil.rotationXMatrix(pitch);	
				guiRotation_room = Matrix4f.multiply(guiRotation_room,tilt);		

			}
		}

		KeyboardHandler.orientOverlay(newScreen!=null);

	}


}
