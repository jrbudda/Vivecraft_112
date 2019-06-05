package org.vivecraft.gameplay.screenhandlers;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.vivecraft.control.VRButtonMapping;
import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.render.RenderPass;
import org.vivecraft.utils.InputInjector;
import org.vivecraft.utils.KeyboardSimulator;

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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.src.Config;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.optifine.reflect.Reflector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

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
	public static float controllerMouseX = -1.0f;
	public static float controllerMouseY = -1.0f;
	public static boolean controllerMouseValid;
	public static int controllerMouseTicks;

	public static float guiScale = 1.0f;
	public static Vec3d guiPos_room = new Vec3d(0,0,0);
	public static Matrix4f guiRotation_room = new Matrix4f();
	
	public static float hudScale = 1.0f;
	public static Vec3d hudPos_room = new Vec3d(0,0,0);
	public static Matrix4f hudRotation_room = new Matrix4f();
	
	public static final KeyBinding keyMenuButton = new KeyBinding("GUI Menu Button", 0, "Vivecraft GUI");
	public static final KeyBinding keyLeftClick = new KeyBinding("GUI Left Click", 0, "Vivecraft GUI");
	public static final KeyBinding keyRightClick = new KeyBinding("GUI Right Click", 0, "Vivecraft GUI");
	public static final KeyBinding keyMiddleClick = new KeyBinding("GUI Middle Click", 0, "Vivecraft GUI");
	public static final KeyBinding keyShift = new KeyBinding("GUI Shift", 0, "Vivecraft GUI");
	public static final KeyBinding keyCtrl = new KeyBinding("GUI Ctrl", 0, "Vivecraft GUI");
	public static final KeyBinding keyAlt = new KeyBinding("GUI Alt", 0, "Vivecraft GUI");
	public static final KeyBinding keyScrollUp = new KeyBinding("GUI Scroll Up", 0, "Vivecraft GUI");
	public static final KeyBinding keyScrollDown = new KeyBinding("GUI Scroll Down", 0, "Vivecraft GUI");

	public static Framebuffer guiFramebuffer = null;


	public static void processGui() {
		controllerMouseValid = false;
		if(mc.currentScreen == null)return;
		if(mc.vrSettings.seated) return;
		if(guiRotation_room == null && hudRotation_room == null) return;

		Matrix4f rot = guiRotation_room == null ? hudRotation_room : guiRotation_room;
		Vec3d pos = guiPos_room == null ? hudPos_room : guiPos_room;
		float scale =  guiPos_room == null ? hudScale : guiScale;
		
		Vector3f controllerPos = new Vector3f();
		//OpenVRUtil.convertMatrix4ftoTranslationVector(controllerPose[0]);
		Vec3d con = mc.vrPlayer.vrdata_room_pre.getController(0).getPosition();
		controllerPos.x	= (float) con.x;
		controllerPos.y	= (float) con.y;
		controllerPos.z	= (float) con.z;

		Vec3d controllerdir = mc.vrPlayer.vrdata_room_pre.getController(0).getDirection();
		Vector3f cdir = new Vector3f((float)controllerdir.x,(float) controllerdir.y,(float) controllerdir.z);
		Vector3f forward = new Vector3f(0,0,1);

		Vector3f guiNormal = rot.transform(forward);
		Vector3f guiRight = rot.transform(new Vector3f(1,0,0));
		Vector3f guiUp = rot.transform(new Vector3f(0,1,0));

		float guiWidth = 1.0f;		
		float guiHalfWidth = guiWidth * 0.5f;		
		float guiHeight = 1.0f;	
		float guiHalfHeight = guiHeight * 0.5f;

		Vector3f gp = new Vector3f();

		gp.x = (float) (pos.x);// + interPolatedRoomOrigin.x ) ;
		gp.y = (float) (pos.y);// + interPolatedRoomOrigin.y ) ;
		gp.z = (float) (pos.z);// + interPolatedRoomOrigin.z ) ;

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

			u = ( u - 0.5f ) * 0.68f / scale + 0.5f;
			v = ( v - 0.5f ) * 0.68f / scale + 0.5f;

			
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
			System.out.println(controllerMouseX);
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
				
				if(Display.isActive()){
					InputInjector.mouseMoveEvent(mouseX, mouseY); // Needs to be called first, since it only puts an event if delta != 0
					 Mouse.setCursorPosition(mouseX, mouseY);
					 }
		
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
				if (keyLeftClick.isPressed())
				{ //press left mouse button
					if (Display.isActive()) 
						KeyboardSimulator.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					else if (mc.currentScreen != null) 
						mc.currentScreen.mouseDown(mouseX, mouseY, 0, true);
					lastPressedLeftClick = true;
				}	

				if (keyLeftClick.isKeyDown() && mc.currentScreen != null)				
					mc.currentScreen.mouseDrag(mouseX, mouseY);//Signals mouse move


				if (!keyLeftClick.isKeyDown() && lastPressedLeftClick) {
					//release left mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					else if (mc.currentScreen != null) 
						mc.currentScreen.mouseUp(mouseX, mouseY, 0, true);
					lastPressedLeftClick = false;
				}
				//end LMB

				//RMB
				if (keyRightClick.isPressed()) {
					//press right mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
					else if (mc.currentScreen != null) 
						mc.currentScreen.mouseDown(mouseX, mouseY, 1, true);
					lastPressedRightClick = true;
				}	

				if (keyRightClick.isKeyDown() && mc.currentScreen != null)
					mc.currentScreen.mouseDrag(mouseX, mouseY);//Signals mouse move

				if (!keyRightClick.isKeyDown() && lastPressedRightClick) {
					//release right mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
					else if (mc.currentScreen != null) 
						mc.currentScreen.mouseUp(mouseX, mouseY, 1, true);
					lastPressedRightClick = false;
				}	
				//end RMB	

				//MMB
				if (keyMiddleClick.isPressed() && mc.currentScreen != null) {
					//press middle mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
					else if (mc.currentScreen != null) 
						mc.currentScreen.mouseDown(mouseX, mouseY, 2, true);
					lastPressedMiddleClick = true;
				}	

				if (keyMiddleClick.isKeyDown() && mc.currentScreen != null) 
					mc.currentScreen.mouseDrag(mouseX, mouseY);//Signals mouse move

				if (!keyMiddleClick.isKeyDown() && lastPressedMiddleClick) {
					//release middle mouse button
					if (Display.isActive()) KeyboardSimulator.robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
					else if (mc.currentScreen != null) 
						mc.currentScreen.mouseUp(mouseX, mouseY, 2, true);
					lastPressedMiddleClick = false;
				}	
				//end MMB
			}
		}

		if (MCOpenVR.controllerDeviceIndex[MCOpenVR.LEFT_CONTROLLER] != -1) {
			//Shift
			if (keyShift.isPressed())				
			{
				//press Shift
				if (mc.currentScreen != null) mc.currentScreen.pressShiftFake = true;
				if (Display.isActive()) KeyboardSimulator.robot.keyPress(KeyEvent.VK_SHIFT);
				lastPressedShift = true;
			}


			if (!keyShift.isKeyDown() && lastPressedShift)			
			{
				//release Shift
				if (mc.currentScreen != null) mc.currentScreen.pressShiftFake = false;
				if (Display.isActive()) KeyboardSimulator.robot.keyRelease(KeyEvent.VK_SHIFT);
				lastPressedShift = false;
			}	
			//end Shift

			//Ctrl
			if (keyCtrl.isPressed())				
			{
				//press Ctrl
				if (Display.isActive()) KeyboardSimulator.robot.keyPress(KeyEvent.VK_CONTROL);
				lastPressedCtrl = true;
			}


			if (!keyCtrl.isKeyDown() && lastPressedCtrl)			
			{
				//release Ctrl
				if (Display.isActive()) KeyboardSimulator.robot.keyRelease(KeyEvent.VK_CONTROL);
				lastPressedCtrl = false;
			}	
			//end Ctrl

			//Alt
			if (keyAlt.isPressed())				
			{
				//press Alt
				if (Display.isActive()) KeyboardSimulator.robot.keyPress(KeyEvent.VK_ALT);
				lastPressedAlt = true;
			}


			if (!keyAlt.isKeyDown() && lastPressedAlt)			
			{
				//release Alt
				if (Display.isActive()) KeyboardSimulator.robot.keyRelease(KeyEvent.VK_ALT);
				lastPressedAlt = false;
			}	
			//end Alt

		}

		if (keyScrollUp.isPressed()) {
			MCOpenVR.triggerBindingHapticPulse(keyScrollUp, 400);
			KeyboardSimulator.robot.mouseWheel(-120);
		}

		if (keyScrollDown.isPressed()) {
			MCOpenVR.triggerBindingHapticPulse(keyScrollDown, 400);
			KeyboardSimulator.robot.mouseWheel(120);
		}

		if(keyMenuButton.isPressed()) { //handle esc		
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
					adj = new Vec3d(0,0.5,-2);
				} else if (newScreen instanceof GuiScreenBook || newScreen instanceof GuiEditSign) {
					adj = new Vec3d(0,0.25,-2);
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
	
	
    public static void applyGUIModelView(RenderPass currentPass)
	{
   		mc.mcProfiler.startSection("applyGUIModelView");
	
			Vec3d eye =mc.vrPlayer.vrdata_world_render.getEye(currentPass).getPosition();
			
			if(mc.currentScreen != null && GuiHandler.guiPos_room == null){
				//naughty mods!
				GuiHandler.onGuiScreenChanged(null, mc.currentScreen, false);			
			}
			
			Vec3d guipos = GuiHandler.guiPos_room;
			Matrix4f guirot = GuiHandler.guiRotation_room;
			Vec3d guiLocal = new Vec3d(0, 0, 0);		
			float scale = GuiHandler.guiScale;
			
			if(guipos == null){
				guirot = null;
				scale = 1;
				if (mc.world!=null && (mc.currentScreen==null || mc.vrSettings.floatInventory == false))
				// HUD view - attach to head or controller
				{
					int i = 1;
					if (mc.vrSettings.vrReverseHands) i = -1;

					//					if(currentPass != RenderPass.Third)
					//						eye = mc.vrPlayer.getEyePos_World(currentPass); //dont need interpolation.
					//					else {
					//						mc.getMRTransform(false);
					//						eye = mc.vrPlayer.vrdata_world_render.getEye(mc.currentPass).getPosition();
					//					}
					if (mc.vrSettings.seated || mc.vrSettings.vrHudLockMode == mc.vrSettings.HUD_LOCK_HEAD)
					{
						Matrix4f rot = Matrix4f.rotationY((float)mc.vrPlayer.vrdata_world_render.rotation_radians);
						Matrix4f max = Matrix4f.multiply(rot, MCOpenVR.hmdRotation);

						Vec3d v = mc.vrPlayer.vrdata_world_render.hmd.getPosition();
						Vec3d d = mc.vrPlayer.vrdata_world_render.hmd.getDirection();

						if(mc.vrSettings.seated && mc.vrSettings.seatedHudAltMode){
							d = mc.vrPlayer.vrdata_world_render.getController(0).getDirection();
							max = Matrix4f.multiply(rot, MCOpenVR.getAimRotation(0)); 
						}

						guipos = new Vec3d((v.x + d.x*mc.vrPlayer.vrdata_world_render.worldScale), 
								(v.y + d.y*mc.vrPlayer.vrdata_world_render.worldScale), 
								(v.z + d.z*mc.vrPlayer.vrdata_world_render.worldScale));


						Quatf orientationQuat = OpenVRUtil.convertMatrix4ftoRotationQuat(max);

						guirot = new Matrix4f(orientationQuat);

						//float pitchOffset = (float) Math.toRadians( -mc.vrSettings.hudPitchOffset );
						//float yawOffset = (float) Math.toRadians( -mc.vrSettings.hudYawOffset );
						//guiRotationPose = Matrix4f.multiply(guiRotationPose, OpenVRUtil.rotationXMatrix(yawOffset));
						//guiRotationPose = Matrix4f.multiply(guiRotationPose, Matrix4f.rotationY(pitchOffset));
						//guirot.M[3][3] = 1.0f;

					}else if (mc.vrSettings.vrHudLockMode == mc.vrSettings.HUD_LOCK_HAND)//hud on hand
					{
						Matrix4f out = MCOpenVR.getAimRotation(1);
						Matrix4f rot = Matrix4f.rotationY((float) mc.vrPlayer.vrdata_world_render.rotation_radians);
						Matrix4f MguiRotationPose =  Matrix4f.multiply(rot,out);
						guirot = Matrix4f.multiply(MguiRotationPose, OpenVRUtil.rotationXMatrix((float) Math.PI * -0.2F));
						guirot = Matrix4f.multiply(guirot, Matrix4f.rotationY((float) Math.PI * 0.1F * i));
						scale = 1/1.7f;

						guiLocal = new Vec3d(guiLocal.x, 0.32*mc.vrPlayer.vrdata_world_render.worldScale,guiLocal.z);

						guipos = mc.entityRenderer.getControllerRenderPos(1);

						MCOpenVR.hudPopup = true;
					}
					else if (mc.vrSettings.vrHudLockMode == mc.vrSettings.HUD_LOCK_WRIST)//hud on wrist
					{

						Matrix4f out = MCOpenVR.getAimRotation(1);
						Matrix4f rot = Matrix4f.rotationY((float) mc.vrPlayer.vrdata_world_render.rotation_radians);
						guirot =  Matrix4f.multiply(rot,out);

						guirot = Matrix4f.multiply(guirot, Matrix4f.rotationY((float) Math.PI * 0.3f *i));				

						Vector3f forward = new Vector3f(0,0,1);
						Vector3f guiNormal = guirot.transform(forward);

						Vec3d facev = mc.vrPlayer.vrdata_world_render.hmd.getDirection();
						Vector3f face = new Vector3f((float)facev.x, (float)facev.y, (float)facev.z);

						float dot = face.dot(guiNormal);

						guipos = mc.entityRenderer.getControllerRenderPos(1);

						Vec3d head = mc.vrPlayer.vrdata_world_render.hmd.getPosition();

						Vector3f headv = new Vector3f((float)guipos.x, (float)guipos.y, (float)guipos.z).subtract(new Vector3f((float)head.x, (float)head.y, (float)head.z)).normalised();
						if(headv == null) return;
						float dot2 = (float) headv.dot(guiNormal);

						if(MCOpenVR.hudPopup){
							MCOpenVR.hudPopup = Math.abs(dot2) > 0.5 &&  dot < -0.7;
						}else {
							MCOpenVR.hudPopup = Math.abs(dot2) > 0.9 &&  dot < -0.97;	
						}

						if(MCOpenVR.hudPopup){
							scale = .5f;
							guiLocal = new Vec3d(
									-0.005*mc.vrPlayer.vrdata_world_render.worldScale,
									0.16*mc.vrPlayer.vrdata_world_render.worldScale,
									0.19*mc.vrPlayer.vrdata_world_render.worldScale);
						}else {
							scale = 0.33f;
							guiLocal = new Vec3d(
									i*-0.12f*mc.vrPlayer.vrdata_world_render.worldScale,
									0.1*mc.vrPlayer.vrdata_world_render.worldScale,
									0.06*mc.vrPlayer.vrdata_world_render.worldScale);
							guirot = Matrix4f.multiply(guirot, Matrix4f.rotationY((float) Math.PI * 0.2f*i ));		
						}
					}
				} 	

				Matrix4f rot = Matrix4f.rotationY(-mc.vrPlayer.vrdata_world_render.rotation_radians);
				GuiHandler.hudRotation_room = Matrix4f.multiply(guirot, rot);			
			
				Vector3f temp = guirot.transform(new Vector3f((float)guiLocal.x,(float)guiLocal.y, (float)guiLocal.z));				
				Vec3d hudpos_World = new Vec3d(guipos.x + temp.x, guipos.y + temp.y, guipos.z + temp.z);		
				GuiHandler.hudPos_room = mc.vrPlayer.world_to_room_pos(hudpos_World, mc.vrPlayer.vrdata_world_render);
				
				GuiHandler.hudScale = scale;			
				
			} else {
				//convert previously calculated coords to world coords
				guipos = mc.vrPlayer.room_to_world_pos(guipos, mc.vrPlayer.vrdata_world_render); 
				Matrix4f rot = Matrix4f.rotationY(mc.vrPlayer.vrdata_world_render.rotation_radians);
				guirot = Matrix4f.multiply(rot, guirot);
			}
			
						
			if (((mc.vrSettings.seated || mc.vrSettings.menuAlwaysFollowFace) && (mc.world == null || mc.currentScreen instanceof GuiWinGame))){ //|| mc.vrSettings.vrHudLockMode == VRSettings.HUD_LOCK_BODY) {
				
				//main menu slow yaw tracking thing
				scale = 2;
				
				Vec3d posAvg = new Vec3d(0, 0, 0);
				for (Vec3d vec : mc.hmdPosSamples) {
					posAvg = new Vec3d(posAvg.x + vec.x, posAvg.y + vec.y, posAvg.z + vec.z);
				}
				posAvg = new Vec3d(posAvg.x / mc.hmdPosSamples.size(), posAvg.y / mc.hmdPosSamples.size(), posAvg.z / mc.hmdPosSamples.size());
				
				float yawAvg = 0;
				for (float f : mc.hmdYawSamples) {
					yawAvg += f;
				}
				yawAvg /= mc.hmdYawSamples.size();
				yawAvg = (float)Math.toRadians(yawAvg);
							
				Vec3d dir = new Vec3d(-Math.sin(yawAvg), 0, Math.cos(yawAvg));
				float dist = mc.world == null || mc.currentScreen instanceof GuiWinGame ? 2.5F*mc.vrPlayer.vrdata_world_render.worldScale: mc.vrSettings.hudDistance;
				Vec3d pos = posAvg.add(new Vec3d(dir.x * dist, dir.y * dist, dir.z * dist));
				Vec3d gpr = new Vec3d(pos.x, pos.y, pos.z);
				
				Matrix4f gr = Matrix4f.rotationY(135-yawAvg); // don't ask
				
				guirot = Matrix4f.multiply(gr, Matrix4f.rotationY(mc.vrPlayer.vrdata_world_render.rotation_radians));
				guipos = mc.vrPlayer.room_to_world_pos(gpr, mc.vrPlayer.vrdata_world_render); 
				
				//for mouse control
				GuiHandler.guiRotation_room = gr;
				GuiHandler.guiScale=2;
				GuiHandler.guiPos_room = gpr;
				//
			}
			
			// counter head rotation
			if (currentPass != RenderPass.THIRD) {
				GL11.glMultMatrix(mc.vrPlayer.vrdata_world_render.hmd.getMatrix().toFloatBuffer());
			} else {
				mc.entityRenderer.applyMRCameraRotation(false);			
			}

			
			GL11.glTranslatef((float) (guipos.x - eye.x), (float)(guipos.y - eye.y), (float)(guipos.z - eye.z));
//		
//			// offset from eye to gui pos
			GL11.glMultMatrix(guirot.transposed().toFloatBuffer());
			GL11.glTranslatef((float)guiLocal.x, (float) guiLocal.y, (float)guiLocal.z);
		
			float thescale = scale * mc.vrPlayer.vrdata_world_render.worldScale; // * this.mc.vroptions.hudscale
			GlStateManager.scale(thescale, thescale, thescale);

			int minLight = Config.isShaders() ? 8 : 4; 
			if(mc.world != null){
				if (mc.getItemRenderer().isInsideOpaqueBlock(guipos, false))
					guipos = mc.vrPlayer.vrdata_world_render.hmd.getPosition();
				
				int i = mc.world.getCombinedLight(new BlockPos(guipos), minLight);
				int j = i % 65536;
				int k = i / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);

				if(!Config.isShaders()){ 
					float b = ((float)k) / 255;
					if (j>k) b = ((float)j) / 255;
					GlStateManager.color(b, b, b); // \_(oo)_/
				}  
				
			}
				//double timeOpen = getCurrentTimeSecs() - startedOpeningInventory;
	
	
				//		if (timeOpen < 1.5) {
				//			scale = (float)(Math.sin(Math.PI*0.5*timeOpen/1.5));
				//		}
	
			mc.mcProfiler.endSection();
	
	} 
    

}
