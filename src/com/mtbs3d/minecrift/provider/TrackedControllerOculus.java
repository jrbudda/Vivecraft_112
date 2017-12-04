package com.mtbs3d.minecrift.provider;

import jopenvr.*;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import com.mtbs3d.minecrift.control.AxisInfo;
import com.mtbs3d.minecrift.control.AxisType;
import com.mtbs3d.minecrift.control.ButtonType;
import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.utils.Quaternion;
import com.mtbs3d.minecrift.utils.Vector2;
import com.mtbs3d.minecrift.utils.Vector3;

public class TrackedControllerOculus extends TrackedController {
	private boolean stickButtonsEnabled = true;
	
	private static final long k_buttonIndexTrigger = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Trigger);
	private static final long k_buttonHandTrigger =  (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_Axis2);
	private static final long k_buttonA =  (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_A);
	private static final long k_buttonB = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_ApplicationMenu);
	private static final long k_buttonStick = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Touchpad);
	private static final int k_axisStick = 0;
	private static final int k_axisIndexTrigger = 1;
	private static final int k_axisHandTrigger = 2;

	public TrackedControllerOculus(ControllerType type) {
		super(type);
	}

	@Override
	void processInput() {
		// axis direction "buttons"
		if (stickButtonsEnabled) {
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].x > 0.5F) != (lastState.rAxis[k_axisStick].x > 0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_STICK_RIGHT, null, state.rAxis[k_axisStick].x > 0.5F, true, null);
			}
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].x < -0.5F) != (lastState.rAxis[k_axisStick].x < -0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_STICK_LEFT, null, state.rAxis[k_axisStick].x < -0.5F, true, null);
			}
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].y > 0.5F) != (lastState.rAxis[k_axisStick].y > 0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_STICK_UP, null, state.rAxis[k_axisStick].y > 0.5F, true, null);
			}
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].y < -0.5F) != (lastState.rAxis[k_axisStick].y < -0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_STICK_DOWN, null, state.rAxis[k_axisStick].y < -0.5F, true, null);
			}
		}
		

		// axis change
		if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].x != lastState.rAxis[k_axisStick].x || state.rAxis[k_axisStick].y != lastState.rAxis[k_axisStick].y)) {
			Vector2 deltaVec = new Vector2(state.rAxis[k_axisStick].x - lastState.rAxis[k_axisStick].x, state.rAxis[k_axisStick].y - lastState.rAxis[k_axisStick].y);
			MCOpenVR.queueInputEvent(this, null, AxisType.OCULUS_STICK, false, false, deltaVec);
		}
		if (state.rAxis[k_axisIndexTrigger] != null && state.rAxis[k_axisIndexTrigger].x != lastState.rAxis[k_axisIndexTrigger].x) {
			Vector2 deltaVec = new Vector2(state.rAxis[k_axisIndexTrigger].x - lastState.rAxis[k_axisIndexTrigger].x, 0);
			MCOpenVR.queueInputEvent(this, null, AxisType.OCULUS_INDEX_TRIGGER, false, false, deltaVec);
		}
		if (state.rAxis[k_axisHandTrigger] != null && state.rAxis[k_axisHandTrigger].x != lastState.rAxis[k_axisHandTrigger].x) {
			Vector2 deltaVec = new Vector2(state.rAxis[k_axisHandTrigger].x - lastState.rAxis[k_axisHandTrigger].x, 0);
			MCOpenVR.queueInputEvent(this, null, AxisType.OCULUS_HAND_TRIGGER, false, false, deltaVec);
		}
	}
	
	@Override
	void processButtonEvent(int button, boolean state, boolean press) {
		switch (button) {
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Trigger:
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_INDEX_TRIGGER, null, state, press, null);
				break;
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_Axis2:
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_HAND_TRIGGER, null, state, press, null);
				break;
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_A:
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_AX, null, state, press, null);
				break;
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_ApplicationMenu:
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_BY, null, state, press, null);
				break;
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Touchpad:
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_STICK, null, state, press, null);
				break;
		}
	}

	@Override
	public List<ButtonType> getActiveButtons() {
		List<ButtonType> list = new ArrayList<ButtonType>();
		list.add(ButtonType.OCULUS_INDEX_TRIGGER);
		list.add(ButtonType.OCULUS_HAND_TRIGGER);
		list.add(ButtonType.OCULUS_AX);
		list.add(ButtonType.OCULUS_BY);
		list.add(ButtonType.OCULUS_STICK);
		if (stickButtonsEnabled) {
			list.add(ButtonType.OCULUS_STICK_UP);
			list.add(ButtonType.OCULUS_STICK_DOWN);
			list.add(ButtonType.OCULUS_STICK_LEFT);
			list.add(ButtonType.OCULUS_STICK_RIGHT);
		}
		return list;
	}

	@Override
	public boolean isButtonTouched(ButtonType button) {
		switch (button) {
			case OCULUS_AX:
				return (state.ulButtonTouched & k_buttonA) > 0;
			case OCULUS_BY:
				return (state.ulButtonTouched & k_buttonB) > 0;
			case OCULUS_INDEX_TRIGGER:
				return (state.ulButtonTouched & k_buttonIndexTrigger) > 0;
			case OCULUS_STICK:
				return (state.ulButtonTouched & k_buttonStick) > 0;
			default:
				return false;
		}
	}

	@Override
	public boolean isButtonPressed(ButtonType button) {
		switch (button) {
			case OCULUS_AX:
				return (state.ulButtonPressed & k_buttonA) > 0;
			case OCULUS_BY:
				return (state.ulButtonPressed & k_buttonB) > 0;
			case OCULUS_INDEX_TRIGGER:
				return (state.ulButtonPressed & k_buttonIndexTrigger) > 0;
			case OCULUS_HAND_TRIGGER:
				return (state.ulButtonPressed & k_buttonHandTrigger) > 0;
			case OCULUS_STICK:
				return (state.ulButtonPressed & k_buttonStick) > 0;
			case OCULUS_STICK_RIGHT:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y > 0.5F;
			case OCULUS_STICK_LEFT:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y < -0.5F;
			case OCULUS_STICK_UP:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y > 0.5F;
			case OCULUS_STICK_DOWN:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y < -0.5F;
			default:
				return false;
		}
	}

	@Override
	public boolean canButtonBeTouched(ButtonType button) {
		switch (button) {
			case OCULUS_AX:
			case OCULUS_BY:
			case OCULUS_INDEX_TRIGGER:
			case OCULUS_STICK:
				return true;
			default:
				return false;
		}
	}

	@Override
	public Vector2 getAxis(AxisType axis) {
		switch (axis) {
			case OCULUS_STICK:
				if (state.rAxis[k_axisStick] != null)
					return new Vector2(state.rAxis[k_axisStick].x, state.rAxis[k_axisStick].y);
				return new Vector2();
			case OCULUS_INDEX_TRIGGER:
				if (state.rAxis[k_axisIndexTrigger] != null)
					return new Vector2(state.rAxis[k_axisIndexTrigger].x, 0);
				return new Vector2();
			case OCULUS_HAND_TRIGGER:
				if (state.rAxis[k_axisHandTrigger] != null)
					return new Vector2(state.rAxis[k_axisHandTrigger].x, 0);
				return new Vector2();
			default:
				return new Vector2();
		}
	}
	
	@Override
	public AxisInfo getButtonAxis(ButtonType button) {
		switch (button) {
			case OCULUS_STICK_UP:
				return new AxisInfo(AxisType.OCULUS_STICK, true, false);
			case OCULUS_STICK_DOWN:
				return new AxisInfo(AxisType.OCULUS_STICK, true, true);
			case OCULUS_STICK_LEFT:
				return new AxisInfo(AxisType.OCULUS_STICK, false, true);
			case OCULUS_STICK_RIGHT:
				return new AxisInfo(AxisType.OCULUS_STICK, false, false);
			case OCULUS_INDEX_TRIGGER:
				return new AxisInfo(AxisType.OCULUS_INDEX_TRIGGER, false, false);
			case OCULUS_HAND_TRIGGER:
				return new AxisInfo(AxisType.OCULUS_HAND_TRIGGER, false, false);
			default:
				return null;
		}
	}

	public boolean isStickButtonsEnabled() {
		return stickButtonsEnabled;
	}

	public void setStickButtonsEnabled(boolean stickButtonsEnabled) {
		this.stickButtonsEnabled = stickButtonsEnabled;
	}
}
