package com.mtbs3d.minecrift.gameplay.screenhandlers;

import java.util.function.Predicate;

import com.mtbs3d.minecrift.control.ButtonTuple;
import com.mtbs3d.minecrift.control.ButtonType;
import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.control.TrackedController;
import com.mtbs3d.minecrift.control.VRButtonMapping;
import com.mtbs3d.minecrift.control.VRInputEvent;
import com.mtbs3d.minecrift.gui.GuiKeyboard;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.Utils;

import de.fruitfly.ovr.structs.Matrix4f;
import de.fruitfly.ovr.structs.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.Main;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.Vec3d;

public class KeyboardHandler {
	//keyboard
	public static Minecraft mc = Minecraft.getMinecraft();
	public static boolean Showing = false;
	public static GuiKeyboard UI = new GuiKeyboard();
	public static Vec3d Pos_room = new Vec3d(0,0,0);
	public static Matrix4f Rotation_room = new Matrix4f();
	private static boolean lpl, lps, PointedL, PointedR;
	public static boolean keyboardForGui; 
	public static Framebuffer Framebuffer = null;

	public static boolean setOverlayShowing(boolean showingState) {
		if (Main.kiosk) return false;
		if(mc.vrSettings.seated) showingState = false;
		int ret = 1;
		if (showingState) {		
            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            UI.setWorldAndResolution(Minecraft.getMinecraft(), i, j);
			Showing = true;
      		orientOverlay(mc.currentScreen!=null);
      		RadialHandler.setOverlayShowing(false, null);
		} else {
			Showing = false;
		}
		return Showing;
	}

	public static void processGui() {
	
		PointedL = false;
		PointedR = false;
		
		if(!Showing) {
			return;
		}
		if(mc.vrSettings.seated) return;
		if(Rotation_room == null) return;

		Vector3f forward = new Vector3f(0,0,1);

		Vector3f guiNormal = Rotation_room.transform(forward);
		Vector3f guiRight = Rotation_room.transform(new Vector3f(1,0,0));
		Vector3f guiUp = Rotation_room.transform(new Vector3f(0,1,0));

		float guiWidth = 1.0f;		
		float guiHalfWidth = guiWidth * 0.5f;		
		float guiHeight = 1.0f;	
		float guiHalfHeight = guiHeight * 0.5f;

		Vector3f gp = new Vector3f();

		gp.x = (float) (Pos_room.x);// + mc.vrPlayer.interPolatedRoomOrigin.x ) ;
		gp.y = (float) (Pos_room.y);// + mc.vrPlayer.interPolatedRoomOrigin.y ) ;
		gp.z = (float) (Pos_room.z);// + mc.vrPlayer.interPolatedRoomOrigin.z ) ;

		Vector3f guiTopLeft = gp.subtract(guiUp.divide(1.0f / guiHalfHeight)).subtract(guiRight.divide(1.0f/guiHalfWidth));
		Vector3f guiTopRight = gp.subtract(guiUp.divide(1.0f / guiHalfHeight)).add(guiRight.divide(1.0f / guiHalfWidth));


		Vector3f controllerPos = new Vector3f();
		Vec3d con = mc.vrPlayer.vrdata_room_pre.getController(0).getPosition();
		controllerPos.x	= (float) con.x;
		controllerPos.y	= (float) con.y;
		controllerPos.z	= (float) con.z;
		Vec3d controllerdir = mc.vrPlayer.vrdata_room_pre.getController(0).getDirection();
		Vector3f cdir = new Vector3f((float)controllerdir.x,(float) controllerdir.y,(float) controllerdir.z);
		float guiNormalDotControllerDirection = guiNormal.dot(cdir);
		if (Math.abs(guiNormalDotControllerDirection) > 0.00001f)
		{//pointed normal to the GUI
			float intersectDist = -guiNormal.dot(controllerPos.subtract(guiTopLeft)) / guiNormalDotControllerDirection;
			Vector3f pointOnPlane = controllerPos.add(cdir.divide(1.0f/intersectDist));

			Vector3f relativePoint = pointOnPlane.subtract(guiTopLeft);
			float u = relativePoint.dot(guiRight.divide(1.0f/guiWidth));
			float v = relativePoint.dot(guiUp.divide(1.0f/guiWidth));

			v = ( (v - 0.5f) * ((float)1280 / (float)720) ) + 0.5f;
			u = ( u - 0.5f ) * 0.68f / GuiHandler.guiScale + 0.5f;
			v = ( v - 0.5f ) * 0.68f / GuiHandler.guiScale + 0.5f;

			if (u<0 || v<0 || u>1 || v>1)
			{
				// offscreen
				UI.cursorX2 = -1.0f;
				UI.cursorY2 = -1.0f;
			}
			else if (UI.cursorX2 == -1.0f)
			{
				UI.cursorX2 = (int) (u * mc.displayWidth);
				UI.cursorY2 = (int) ((1-v) * mc.displayHeight);
				PointedR = true;
			}
			else
			{
				// apply some smoothing between mouse positions
				float newX = (int) (u * mc.displayWidth);
				float newY = (int) ((1-v) * mc.displayHeight);
				UI.cursorX2 = UI.cursorX2 * 0.7f + newX * 0.3f;
				UI.cursorY2 = UI.cursorY2 * 0.7f + newY * 0.3f;
				PointedR = true;
			}
		}

		con = mc.vrPlayer.vrdata_room_pre.getController(1).getPosition();
		controllerPos.x	= (float) con.x;
		controllerPos.y	= (float) con.y;
		controllerPos.z	= (float) con.z;
		controllerdir = mc.vrPlayer.vrdata_room_pre.getController(1).getDirection();
		cdir = new Vector3f((float)controllerdir.x,(float) controllerdir.y,(float) controllerdir.z);
		guiNormalDotControllerDirection = guiNormal.dot(cdir);
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

			u = ( u - 0.5f ) * 0.68f / GuiHandler.guiScale + 0.5f;
			v = ( v - 0.5f ) * 0.68f / GuiHandler.guiScale + 0.5f;

			if (u<0 || v<0 || u>1 || v>1)
			{
				// offscreen
				UI.cursorX1 = -1.0f;
				UI.cursorY1 = -1.0f;
			}
			else if (UI.cursorX1 == -1.0f)
			{
				UI.cursorX1 = (int) (u * mc.displayWidth);
				UI.cursorY1 = (int) ((1-v) * mc.displayHeight);
				PointedL = true;
			}
			else
			{
				// apply some smoothing between mouse positions
				float newX = (int) (u * mc.displayWidth);
				float newY = (int) ((1-v) * mc.displayHeight);
				UI.cursorX1 = UI.cursorX1 * 0.7f + newX * 0.3f;
				UI.cursorY1 = UI.cursorY1 * 0.7f + newY * 0.3f;
				PointedL = true;
			}
		}
	}

	
	public static void orientOverlay(boolean guiRelative) {
		
		keyboardForGui = false;
		if (!Showing) return;
		keyboardForGui = guiRelative;
		
		org.lwjgl.util.vector.Matrix4f matrix = new org.lwjgl.util.vector.Matrix4f();
		if (guiRelative && GuiHandler.guiRotation_room != null) {
			org.lwjgl.util.vector.Matrix4f guiRot = Utils.convertOVRMatrix(GuiHandler.guiRotation_room);
			Vec3d guiUp = new Vec3d(guiRot.m10, guiRot.m11, guiRot.m12);
			Vec3d guiFwd = new Vec3d(guiRot.m20, guiRot.m21, guiRot.m22).scale(0.25f);
			guiUp = guiUp.scale(0.80f);
			matrix.translate(new org.lwjgl.util.vector.Vector3f((float)(GuiHandler.guiPos_room.x - guiUp.x), (float)(GuiHandler.guiPos_room.y - guiUp.y), (float)(GuiHandler.guiPos_room.z - guiUp.z)));
			matrix.translate(new org.lwjgl.util.vector.Vector3f((float)(guiFwd.x), (float)(guiFwd.y), (float)(guiFwd.z)));
			org.lwjgl.util.vector.Matrix4f.mul(matrix, guiRot, matrix);
			matrix.rotate((float)Math.toRadians(30), new org.lwjgl.util.vector.Vector3f(-1, 0, 0)); // tilt it a bit	
			Rotation_room =   Utils.convertToOVRMatrix(matrix);
			Pos_room = new Vec3d(Rotation_room.M[0][3],Rotation_room.M[1][3],Rotation_room.M[2][3]);
			Rotation_room.M[0][3] = 0;
			Rotation_room.M[1][3] = 0;
			Rotation_room.M[2][3] = 0;

		} else { //copied from vrplayer.onguiuscreenchanged
			Vec3d v = mc.vrPlayer.vrdata_room_pre.hmd.getPosition();
			Vec3d adj = new Vec3d(0,-0.5,-2);
			Vec3d e = mc.vrPlayer.vrdata_room_pre.hmd.getCustomVector(adj);
			Pos_room = new Vec3d(
					(e.x  / 2 + v.x),
					(e.y / 2 + v.y),
					(e.z / 2 + v.z));

			Vec3d pos = mc.vrPlayer.vrdata_room_pre.hmd.getPosition();
			Vector3f look = new Vector3f();
			look.x = (float) (Pos_room.x - pos.x);
			look.y = (float) (Pos_room.y - pos.y);
			look.z = (float) (Pos_room.z - pos.z);

			float pitch = (float) Math.asin(look.y/look.length());
			float yaw = (float) ((float) Math.PI + Math.atan2(look.x, look.z));    
			Rotation_room = Matrix4f.rotationY((float) yaw);
		}
	}
	
	public static boolean handleInputEvent(VRInputEvent event) {
	
		if(event.getButtonState() && event.getController().getType() == ControllerType.LEFT && (event.getButton() == ButtonType.VIVE_APPMENU || event.getButton() == ButtonType.OCULUS_BY)) {
			if (MCOpenVR.controllers[MCOpenVR.LEFT_CONTROLLER].isButtonPressed(ButtonType.VIVE_GRIP) || MCOpenVR.controllers[MCOpenVR.LEFT_CONTROLLER].isButtonPressed(ButtonType.OCULUS_HAND_TRIGGER)) {
				setOverlayShowing(!Showing);
				return true;
			}
		}

		if(Showing) { // Left click, right click and shift bindings will work on keyboard, ignoring left/right controller designation.
			VRButtonMapping leftClick = mc.vrSettings.buttonMappings.get(MCOpenVR.guiLeftClick.getKeyDescription());
			VRButtonMapping rightClick = mc.vrSettings.buttonMappings.get(MCOpenVR.guiRightClick.getKeyDescription());
			VRButtonMapping shift = mc.vrSettings.buttonMappings.get(MCOpenVR.guiShift.getKeyDescription());
			Predicate<ButtonTuple> predicate = b -> b.button.equals(event.getButton()) && b.isTouch == event.isButtonTouchEvent();
			boolean isClick = leftClick.buttons.stream().anyMatch(predicate) || rightClick.buttons.stream().anyMatch(predicate);

			if(PointedL && event.getController().getType() == ControllerType.LEFT && isClick) {
				if(event.getButtonState()) {
					UI.mouseDown((int)UI.cursorX1, (int)UI.cursorY1, 0, false);
				} else {
					UI.mouseUp((int)UI.cursorX1, (int)UI.cursorY1, 0, false);
				}
				return true;
			}

			if(PointedR && event.getController().getType() == ControllerType.RIGHT && isClick) {
				if(event.getButtonState()) {
					UI.mouseDown((int)UI.cursorX2, (int)UI.cursorY2, 0, false);
				} else  {
					UI.mouseUp((int)UI.cursorX2, (int)UI.cursorY2, 0, false);
				}
				return true;
			}
			
			if((PointedL || PointedR) && shift.buttons.stream().anyMatch(predicate)) {
				if (event.getButtonState())
					UI.setShift(true);
				else
					UI.setShift(false);
				return true;
			}
		}

		return false;
	}

	
}
