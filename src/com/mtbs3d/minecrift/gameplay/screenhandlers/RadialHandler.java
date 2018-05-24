package com.mtbs3d.minecrift.gameplay.screenhandlers;

import java.util.function.Predicate;

import com.mtbs3d.minecrift.api.VRData.VRDevicePose;
import com.mtbs3d.minecrift.control.ButtonTuple;
import com.mtbs3d.minecrift.control.ButtonType;
import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.control.TrackedController;
import com.mtbs3d.minecrift.control.VRButtonMapping;
import com.mtbs3d.minecrift.control.VRInputEvent;
import com.mtbs3d.minecrift.gui.GuiRadial;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.Utils;

import de.fruitfly.ovr.structs.Matrix4f;
import de.fruitfly.ovr.structs.Vector3f;
import jopenvr.OpenVRUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.Main;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.Vec3d;

public class RadialHandler {
	//
	public static Minecraft mc = Minecraft.getMinecraft();
	public static boolean Showing = false;
	public static GuiRadial UI = new GuiRadial();
	public static Vec3d Pos_room = new Vec3d(0,0,0);
	public static Matrix4f Rotation_room = new Matrix4f();
	private static boolean lpl, lps, PointedL, PointedR;

	public static Framebuffer Framebuffer = null;

	private static ControllerType activecontroller;
	private static ButtonTuple activeButton;
	
	public static boolean setOverlayShowing(boolean showingState, ButtonTuple button) {
		if (Main.kiosk) return false;
		if(mc.vrSettings.seated) showingState = false;
		int ret = 1;
		if (showingState) {		
			ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			UI.setWorldAndResolution(Minecraft.getMinecraft(), i, j);
			Showing = true;
			activecontroller = button.controller;
			orientOverlay(activecontroller);
			activeButton = button; 
		} else {
			Showing = false;
			activecontroller = null;
			activeButton = null;
		}

		return Showing;
	}

	public static void processGui() {

		PointedL = false;
		PointedR = false;

		if(!Showing) {
			return;
		}

		if(activecontroller == null) //impossibru
			return;

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

			if(mc.vrSettings.radialModeHold && activecontroller == ControllerType.LEFT) {
				u = -1;
				v = -1;
			} //lazy

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

			if(mc.vrSettings.radialModeHold && activecontroller == ControllerType.RIGHT) {
				u = -1;
				v = -1;
			} //lazy

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


	public static void orientOverlay(ControllerType controller) {
		if (!Showing) return;

		VRDevicePose pose = mc.vrPlayer.vrdata_room_pre.hmd; //normal menu.
		float dist = 2;
		
		int id=0;
		if(controller == ControllerType.LEFT)
			id=1;

		if(mc.vrSettings.radialModeHold) { //open with controller centered, consistent motions.
			pose = mc.vrPlayer.vrdata_room_pre.getController(id);
			dist = 1.2f;
		}

		org.lwjgl.util.vector.Matrix4f matrix = new org.lwjgl.util.vector.Matrix4f();

		Vec3d v = pose.getPosition();
		Vec3d adj = new Vec3d(0,0,-dist);
		Vec3d e = pose.getCustomVector(adj);
		Pos_room = new Vec3d(
				(e.x / 2 + v.x),
				(e.y / 2 + v.y),
				(e.z / 2 + v.z));

		Vector3f look = new Vector3f();
		look.x = (float) (Pos_room.x - v.x);
		look.y = (float) (Pos_room.y - v.y);
		look.z = (float) (Pos_room.z - v.z);

		float pitch = (float) Math.asin(look.y/look.length());
		float yaw = (float) ((float) Math.PI + Math.atan2(look.x, look.z));    
		Rotation_room = Matrix4f.rotationY((float) yaw);
		Matrix4f tilt = OpenVRUtil.rotationXMatrix(pitch);	
		Rotation_room = Matrix4f.multiply(Rotation_room, tilt);	

	}

	public static boolean handleInputEvent(VRInputEvent event) {

		if(!Showing) return false;

		Predicate<ButtonTuple> predicate = b -> b.button.equals(event.getButton()) && b.isTouch == event.isButtonTouchEvent();
		
		VRButtonMapping shift = mc.vrSettings.buttonMappings.get(MCOpenVR.guiShift.getKeyDescription());

		if((PointedL || PointedR) && shift.buttons.stream().anyMatch( b -> b.button.equals(event.getButton()) && b.isTouch == event.isButtonTouchEvent() && b.controller == event.getController().getType())) {
			if (event.getButtonState())
				UI.setShift(true);
			else
				UI.setShift(false);
			return true;
		}

		if(mc.vrSettings.radialModeHold) {
			
			if(activeButton == null || activecontroller == null) 
				return false;

			boolean ismeUp = event.getButtonState() == false &&  activeButton.button == event.getButton() && activecontroller == event.getController().getType();
		
			if(ismeUp) {
				if (activecontroller == ControllerType.LEFT) {
					UI.mouseDown((int)UI.cursorX1, (int)UI.cursorY1, 0, false);
				} else {
					UI.mouseDown((int)UI.cursorX2, (int)UI.cursorY2, 0, false);
				}
				RadialHandler.setOverlayShowing(false, null);
				return true;
			}
			
		} else {
			VRButtonMapping leftClick = mc.vrSettings.buttonMappings.get(MCOpenVR.guiLeftClick.getKeyDescription());		
			VRButtonMapping rightClick = mc.vrSettings.buttonMappings.get(MCOpenVR.guiRightClick.getKeyDescription());
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
		}
		return false;
	}	
	
}
