package com.mtbs3d.minecrift.control;

import jopenvr.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.Quaternion;
import com.mtbs3d.minecrift.utils.Vector2;
import com.mtbs3d.minecrift.utils.Vector3;

import de.fruitfly.ovr.structs.Vector3f;

public class TrackedControllerVive extends TrackedController {
	private TouchpadMode touchpadMode = TouchpadMode.SINGLE;
	private boolean swipeEnabled = true;
	private Vector2f[] touchpadSampleBuffer = new Vector2f[5];
	private int touchpadSampleIndex;
	private long touchpadSampleCount;
	private ButtonType lastPressedTouchpadButton;
	private ButtonType lastTouchedTouchpadButton;
	private float swipeAccumX = 0;
	private float swipeAccumY = 0;

	private static final long k_buttonAppMenu = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_ApplicationMenu);
	private static final long k_buttonGrip =  (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_Grip);
	private static final long k_buttonTouchpad = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Touchpad);
	private static final long k_buttonTrigger = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Trigger);
	private static final int k_axisTouchpad = 0;
	private static final int k_axisTrigger = 1;

	public TrackedControllerVive(ControllerType type) {
		super(type);
		for (int i = 0; i < touchpadSampleBuffer.length; i++)
			touchpadSampleBuffer[i] = new Vector2f();
	}
	
	@Override
	public void updateState() {
		super.updateState();
		updateTouchpadSampleBuffer();
	}

	private void updateTouchpadSampleBuffer() {
		if (state.rAxis[k_axisTouchpad] != null && (state.ulButtonTouched & k_buttonTouchpad) > 0) {
			touchpadSampleBuffer[touchpadSampleIndex].x = state.rAxis[k_axisTouchpad].x;
			touchpadSampleBuffer[touchpadSampleIndex].y = state.rAxis[k_axisTouchpad].y;
			if (++touchpadSampleIndex >= touchpadSampleBuffer.length) touchpadSampleIndex = 0;
			touchpadSampleCount++;
		} else {
			for (int i = 0; i < touchpadSampleBuffer.length; i++) {
				touchpadSampleBuffer[touchpadSampleIndex].x = 0;
				touchpadSampleBuffer[touchpadSampleIndex].y = 0;
			}
			touchpadSampleCount = 0;
		}
	}

	@Override
	public void processInput() {
		// button touch
		if ((state.ulButtonTouched & k_buttonTouchpad) > 0 && getTouchpadButton() != lastTouchedTouchpadButton && lastTouchedTouchpadButton != null) {
			MCOpenVR.queueInputEvent(this, lastTouchedTouchpadButton, null, false, false, null);
			MCOpenVR.queueInputEvent(this, getTouchpadButton(), null, true, false, null);
			lastTouchedTouchpadButton = getTouchpadButton();
		}

		// button press
		if ((state.ulButtonPressed & k_buttonTouchpad) > 0 && getTouchpadButton() != lastPressedTouchpadButton && lastPressedTouchpadButton != null) {
			MCOpenVR.queueInputEvent(this, lastPressedTouchpadButton, null, false, true, null);
			MCOpenVR.queueInputEvent(this, getTouchpadButton(), null, true, true, null);
			lastPressedTouchpadButton = getTouchpadButton();
		}
		if (state.rAxis[k_axisTrigger] != null && (state.rAxis[k_axisTrigger].x > 0.99F) != (lastState.rAxis[k_axisTrigger].x > 0.99F)) {
			// ulButtonPressed returns true on partial press for some reason, but we want actual click
			MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TRIGGER_CLICK, null, state.rAxis[k_axisTrigger].x > 0.99F, true, null);
		}
		
		// touchpad swipe "buttons"
		if (swipeEnabled && touchpadSampleCount >= touchpadSampleBuffer.length && (state.ulButtonPressed & k_buttonTouchpad) == 0) {
			float swipeThreshold = 0.5F;
			int nextSampleIndex = (touchpadSampleIndex + 1) % touchpadSampleBuffer.length;
			swipeAccumX += touchpadSampleBuffer[nextSampleIndex].x - touchpadSampleBuffer[touchpadSampleIndex].x;
			swipeAccumY += touchpadSampleBuffer[nextSampleIndex].y - touchpadSampleBuffer[touchpadSampleIndex].y;

			if (swipeAccumX >= swipeThreshold) {
				swipeAccumX -= swipeThreshold;
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_RIGHT, null, true, true, null);
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_RIGHT, null, false, true, null);
			}
			if (swipeAccumX <= -swipeThreshold) {
				swipeAccumX += swipeThreshold;
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_LEFT, null, true, true, null);
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_LEFT, null, false, true, null);
			}
			if (swipeAccumY >= swipeThreshold) {
				swipeAccumY -= swipeThreshold;
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_UP, null, true, true, null);
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_UP, null, false, true, null);
			}
			if (swipeAccumY <= -swipeThreshold) {
				swipeAccumY += swipeThreshold;
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_DOWN, null, true, true, null);
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TOUCHPAD_SWIPE_DOWN, null, false, true, null);
			}
		}

		// axis change
		if (state.rAxis[k_axisTouchpad] != null && (state.rAxis[k_axisTouchpad].x != lastState.rAxis[k_axisTouchpad].x || state.rAxis[k_axisTouchpad].y != lastState.rAxis[k_axisTouchpad].y)) {
			Vector2 deltaVec = new Vector2(state.rAxis[k_axisTouchpad].x - lastState.rAxis[k_axisTouchpad].x, state.rAxis[k_axisTouchpad].y - lastState.rAxis[k_axisTouchpad].y);
			MCOpenVR.queueInputEvent(this, null, AxisType.VIVE_TOUCHPAD, false, false, deltaVec);
		}
		if (state.rAxis[k_axisTrigger] != null && state.rAxis[k_axisTrigger].x != lastState.rAxis[k_axisTrigger].x) {
			Vector2 deltaVec = new Vector2(state.rAxis[k_axisTrigger].x - lastState.rAxis[k_axisTrigger].x, 0);
			MCOpenVR.queueInputEvent(this, null, AxisType.VIVE_TRIGGER, false, false, deltaVec);
		}
	}
	
	@Override
	public void processButtonEvent(int button, boolean state, boolean press) {
		switch (button) {
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_ApplicationMenu:
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_APPMENU, null, state, press, null);
				break;
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_Grip:
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_GRIP, null, state, press, null);
				break;
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Touchpad:
				if (press) {
					if (state) {
						MCOpenVR.queueInputEvent(this, getTouchpadButton(), null, true, true, null);
						lastPressedTouchpadButton = getTouchpadButton();
					} else {
						MCOpenVR.queueInputEvent(this, lastPressedTouchpadButton, null, false, true, null);
						lastPressedTouchpadButton = null;
					}
				} else {
					if (state) {
						MCOpenVR.queueInputEvent(this, getTouchpadButton(), null, true, false, null);
						lastTouchedTouchpadButton = getTouchpadButton();
					} else {
						MCOpenVR.queueInputEvent(this, lastTouchedTouchpadButton, null, false, false, null);
						lastTouchedTouchpadButton = null;
					}
				}
				break;
			case JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Trigger:
				MCOpenVR.queueInputEvent(this, ButtonType.VIVE_TRIGGER, null, state, press, null);
				break;
		}
	}

	protected ButtonType getTouchpadButton() {
		float centerThreshold = 0.3F;
		Vector2 axis = getAxis(AxisType.VIVE_TOUCHPAD);
		switch (touchpadMode) {
			case SINGLE:
				return ButtonType.VIVE_TOUCHPAD;
			case SPLIT_UD:
				if (axis.getY() > 0) return ButtonType.VIVE_TOUCHPAD_U;
				else return ButtonType.VIVE_TOUCHPAD_D;
			case SPLIT_LR:
				if (axis.getX() > 0) return ButtonType.VIVE_TOUCHPAD_R;
				else return ButtonType.VIVE_TOUCHPAD_L;
			case SPLIT_QUAD_CENTER:
				if (Math.abs(axis.getX()) < centerThreshold && Math.abs(axis.getY()) < centerThreshold)
					return ButtonType.VIVE_TOUCHPAD_C;
				// fall-through case
			case SPLIT_QUAD:
				if (axis.getX() > 0) {
					if (axis.getY() > 0) return ButtonType.VIVE_TOUCHPAD_UR;
					else return ButtonType.VIVE_TOUCHPAD_LR;
				} else {
					if (axis.getY() > 0) return ButtonType.VIVE_TOUCHPAD_UL;
					else return ButtonType.VIVE_TOUCHPAD_LL;
				}
			case SPLIT_CROSS_CENTER:
				if (Math.abs(axis.getX()) < centerThreshold && Math.abs(axis.getY()) < centerThreshold)
					return ButtonType.VIVE_TOUCHPAD_C;
				// fall-through case
			case SPLIT_CROSS:
				float angle = new Vector2(0, 0).angle(axis);
				if (angle == Float.NaN) angle = 0;
				if (angle >= 45 && angle <= 135)
					return ButtonType.VIVE_TOUCHPAD_U;
				if (angle <= -45 && angle >= -135)
					return ButtonType.VIVE_TOUCHPAD_D;
				if (angle >= 135 || angle <= -135)
					return ButtonType.VIVE_TOUCHPAD_L;
				if (angle <= 45 && angle >= -45)
					return ButtonType.VIVE_TOUCHPAD_R;
			case SPLIT_HEX_CENTER:
				if (Math.abs(axis.getX()) < centerThreshold && Math.abs(axis.getY()) < centerThreshold)
					return ButtonType.VIVE_TOUCHPAD_C;
				// fall-through case
			case SPLIT_HEX:
				angle = new Vector2(0, 0).angle(axis);
				if (angle == Float.NaN) angle = 0;
				if (angle >= 30 && angle <= 90)
					return ButtonType.VIVE_TOUCHPAD_S1;
				if (angle >= -30 && angle <= 30)
					return ButtonType.VIVE_TOUCHPAD_S2;
				if (angle >= -90 && angle <= -30)
					return ButtonType.VIVE_TOUCHPAD_S3;
				if (angle >= -150 && angle <= -90)
					return ButtonType.VIVE_TOUCHPAD_S4;
				if (angle >= 150 && angle <= -150)
					return ButtonType.VIVE_TOUCHPAD_S5;
				if (angle >= 90 && angle <= 150)
					return ButtonType.VIVE_TOUCHPAD_S6;
			case SPLIT_OCT_CENTER:
				if (Math.abs(axis.getX()) < centerThreshold && Math.abs(axis.getY()) < centerThreshold)
					return ButtonType.VIVE_TOUCHPAD_C;
				// fall-through case
			case SPLIT_OCT:
				angle = new Vector2(0, 0).angle(axis);
				if (angle == Float.NaN) angle = 0;
				if (angle >= 45 && angle <= 90)
					return ButtonType.VIVE_TOUCHPAD_S1;
				if (angle >= 0 && angle <= 45)
					return ButtonType.VIVE_TOUCHPAD_S2;
				if (angle >= -45 && angle <= 0)
					return ButtonType.VIVE_TOUCHPAD_S3;
				if (angle >= -90 && angle <= -45)
					return ButtonType.VIVE_TOUCHPAD_S4;
				if (angle >= -135 && angle <= -90)
					return ButtonType.VIVE_TOUCHPAD_S5;
				if (angle >= -180 && angle <= -135)
					return ButtonType.VIVE_TOUCHPAD_S6;
				if (angle >= 135 && angle <= 180)
					return ButtonType.VIVE_TOUCHPAD_S7;
				if (angle >= 90 && angle <= 135)
					return ButtonType.VIVE_TOUCHPAD_S8;
			default:
				return ButtonType.VIVE_TOUCHPAD; // we should never get here
		}
	}

	@Override
	public List<ButtonType> getActiveButtons() {
		List<ButtonType> list = new ArrayList<ButtonType>();
		list.add(ButtonType.VIVE_APPMENU);
		list.add(ButtonType.VIVE_GRIP);
		list.add(ButtonType.VIVE_TRIGGER);
		list.add(ButtonType.VIVE_TRIGGER_CLICK);
		switch (touchpadMode) {
			case SINGLE:
				list.add(ButtonType.VIVE_TOUCHPAD);
				break;
			case SPLIT_UD:
				list.add(ButtonType.VIVE_TOUCHPAD_U);
				list.add(ButtonType.VIVE_TOUCHPAD_D);
				break;
			case SPLIT_LR:
				list.add(ButtonType.VIVE_TOUCHPAD_L);
				list.add(ButtonType.VIVE_TOUCHPAD_R);
				break;
			case SPLIT_QUAD_CENTER:
				list.add(ButtonType.VIVE_TOUCHPAD_C);
				// fall-through case
			case SPLIT_QUAD:
				list.add(ButtonType.VIVE_TOUCHPAD_UL);
				list.add(ButtonType.VIVE_TOUCHPAD_UR);
				list.add(ButtonType.VIVE_TOUCHPAD_LL);
				list.add(ButtonType.VIVE_TOUCHPAD_LR);
				break;
			case SPLIT_CROSS_CENTER:
				list.add(ButtonType.VIVE_TOUCHPAD_C);
				// fall-through case
			case SPLIT_CROSS:
				list.add(ButtonType.VIVE_TOUCHPAD_U);
				list.add(ButtonType.VIVE_TOUCHPAD_D);
				list.add(ButtonType.VIVE_TOUCHPAD_L);
				list.add(ButtonType.VIVE_TOUCHPAD_R);
				break;
			case SPLIT_HEX_CENTER:
				list.add(ButtonType.VIVE_TOUCHPAD_C);
				// fall-through case
			case SPLIT_HEX:
				list.add(ButtonType.VIVE_TOUCHPAD_S1);
				list.add(ButtonType.VIVE_TOUCHPAD_S2);
				list.add(ButtonType.VIVE_TOUCHPAD_S3);
				list.add(ButtonType.VIVE_TOUCHPAD_S4);
				list.add(ButtonType.VIVE_TOUCHPAD_S5);
				list.add(ButtonType.VIVE_TOUCHPAD_S6);
				break;
			case SPLIT_OCT_CENTER:
				list.add(ButtonType.VIVE_TOUCHPAD_C);
				// fall-through case
			case SPLIT_OCT:
				list.add(ButtonType.VIVE_TOUCHPAD_S1);
				list.add(ButtonType.VIVE_TOUCHPAD_S2);
				list.add(ButtonType.VIVE_TOUCHPAD_S3);
				list.add(ButtonType.VIVE_TOUCHPAD_S4);
				list.add(ButtonType.VIVE_TOUCHPAD_S5);
				list.add(ButtonType.VIVE_TOUCHPAD_S6);
				list.add(ButtonType.VIVE_TOUCHPAD_S7);
				list.add(ButtonType.VIVE_TOUCHPAD_S8);
				break;
		}
		if (swipeEnabled) {
			list.add(ButtonType.VIVE_TOUCHPAD_SWIPE_UP);
			list.add(ButtonType.VIVE_TOUCHPAD_SWIPE_DOWN);
			list.add(ButtonType.VIVE_TOUCHPAD_SWIPE_LEFT);
			list.add(ButtonType.VIVE_TOUCHPAD_SWIPE_RIGHT);
		}
		return list;
	}

	@Override
	public boolean isButtonTouched(ButtonType button) {
		switch (button) {
			case VIVE_TOUCHPAD:
				return (state.ulButtonTouched & k_buttonTouchpad) > 0;
			case VIVE_TOUCHPAD_U:
			case VIVE_TOUCHPAD_D:
			case VIVE_TOUCHPAD_L:
			case VIVE_TOUCHPAD_R:
			case VIVE_TOUCHPAD_UL:
			case VIVE_TOUCHPAD_UR:
			case VIVE_TOUCHPAD_LR:
			case VIVE_TOUCHPAD_LL:
			case VIVE_TOUCHPAD_S1:
			case VIVE_TOUCHPAD_S2:
			case VIVE_TOUCHPAD_S3:
			case VIVE_TOUCHPAD_S4:
			case VIVE_TOUCHPAD_S5:
			case VIVE_TOUCHPAD_S6:
			case VIVE_TOUCHPAD_S7:
			case VIVE_TOUCHPAD_S8:
			case VIVE_TOUCHPAD_C:
				return (state.ulButtonTouched & k_buttonTouchpad) > 0 && button == getTouchpadButton();
			default:
				return false;
		}
	}

	@Override
	public boolean isButtonPressed(ButtonType button) {
		switch (button) {
			case VIVE_APPMENU:
				return (state.ulButtonPressed & k_buttonAppMenu) > 0;
			case VIVE_GRIP:
				return (state.ulButtonPressed & k_buttonGrip) > 0;
			case VIVE_TOUCHPAD:
				return (state.ulButtonPressed & k_buttonTouchpad) > 0;
			case VIVE_TRIGGER:
				return (state.ulButtonPressed & k_buttonTrigger) > 0;
			case VIVE_TRIGGER_CLICK:
				return state.rAxis[k_axisTrigger] != null && state.rAxis[k_axisTrigger].x > 0.99F;
			case VIVE_TOUCHPAD_U:
			case VIVE_TOUCHPAD_D:
			case VIVE_TOUCHPAD_L:
			case VIVE_TOUCHPAD_R:
			case VIVE_TOUCHPAD_UL:
			case VIVE_TOUCHPAD_UR:
			case VIVE_TOUCHPAD_LR:
			case VIVE_TOUCHPAD_LL:
			case VIVE_TOUCHPAD_S1:
			case VIVE_TOUCHPAD_S2:
			case VIVE_TOUCHPAD_S3:
			case VIVE_TOUCHPAD_S4:
			case VIVE_TOUCHPAD_S5:
			case VIVE_TOUCHPAD_S6:
			case VIVE_TOUCHPAD_S7:
			case VIVE_TOUCHPAD_S8:
			case VIVE_TOUCHPAD_C:
				return (state.ulButtonPressed & k_buttonTouchpad) > 0 && button == getTouchpadButton();
			default:
				return false;
		}
	}

	@Override
	public boolean canButtonBeTouched(ButtonType button) {
		return false;
		// nah
		/*switch (button) {
			case VIVE_TOUCHPAD:
			case VIVE_TOUCHPAD_U:
			case VIVE_TOUCHPAD_D:
			case VIVE_TOUCHPAD_L:
			case VIVE_TOUCHPAD_R:
			case VIVE_TOUCHPAD_UL:
			case VIVE_TOUCHPAD_UR:
			case VIVE_TOUCHPAD_LR:
			case VIVE_TOUCHPAD_LL:
			case VIVE_TOUCHPAD_S1:
			case VIVE_TOUCHPAD_S2:
			case VIVE_TOUCHPAD_S3:
			case VIVE_TOUCHPAD_S4:
			case VIVE_TOUCHPAD_S5:
			case VIVE_TOUCHPAD_S6:
			case VIVE_TOUCHPAD_S7:
			case VIVE_TOUCHPAD_S8:
			case VIVE_TOUCHPAD_C:
				return true;
			default:
				return false;
		}*/
	}

	@Override
	public Vector2 getAxis(AxisType axis) {
		switch (axis) {
			case VIVE_TOUCHPAD:
				if (state.rAxis[k_axisTouchpad] != null)
					return new Vector2(state.rAxis[k_axisTouchpad].x, state.rAxis[k_axisTouchpad].y);
				return new Vector2();
			case VIVE_TRIGGER:
				if (state.rAxis[k_axisTrigger] != null)
					return new Vector2(state.rAxis[k_axisTrigger].x, 0);
				return new Vector2();
			default:
				return new Vector2();
		}
	}
	
	@Override
	public AxisInfo getButtonAxis(ButtonType button) {
		switch (button) {
			// Technically not wrong, but doesn't make much sense, so removed
			/*case VIVE_TOUCHPAD_SWIPE_UP:
				return new AxisInfo(AxisType.VIVE_TOUCHPAD, true, false);
			case VIVE_TOUCHPAD_SWIPE_DOWN:
				return new AxisInfo(AxisType.VIVE_TOUCHPAD, true, true);
			case VIVE_TOUCHPAD_SWIPE_LEFT:
				return new AxisInfo(AxisType.VIVE_TOUCHPAD, false, true);
			case VIVE_TOUCHPAD_SWIPE_RIGHT:
				return new AxisInfo(AxisType.VIVE_TOUCHPAD, false, false);*/
			case VIVE_TRIGGER:
				return new AxisInfo(AxisType.VIVE_TRIGGER, false, false);
			default:
				return null;
		}
	}
	
	public TouchpadMode getTouchpadMode() {
		return touchpadMode;
	}
	
	public void setTouchpadMode(TouchpadMode touchpadMode) {
		this.touchpadMode = touchpadMode;
	}
	
	public boolean isSwipeEnabled() {
		return swipeEnabled;
	}
	
	public void setSwipeEnabled(boolean swipeEnabled) {
		this.swipeEnabled = swipeEnabled;
	}

	public static enum TouchpadMode {
		SINGLE,
		SPLIT_UD,
		SPLIT_LR,
		SPLIT_QUAD,
		SPLIT_QUAD_CENTER,
		SPLIT_CROSS,
		SPLIT_CROSS_CENTER,
		SPLIT_HEX,
		SPLIT_HEX_CENTER,
		SPLIT_OCT,
		SPLIT_OCT_CENTER
	}

	@Override
	public Vector3 getButtonLocation(ButtonType button) {
		int i = this.type == ControllerType.RIGHT ? 0 : 1;
		long butt = -1;
		switch (button) {
		case VIVE_APPMENU:
			butt = k_buttonAppMenu;
			break;
		case VIVE_GRIP:
			butt = k_buttonGrip;
			break;
		case VIVE_TOUCHPAD:
		case VIVE_TOUCHPAD_U:
		case VIVE_TOUCHPAD_D:
		case VIVE_TOUCHPAD_L:
		case VIVE_TOUCHPAD_R:
		case VIVE_TOUCHPAD_UL:
		case VIVE_TOUCHPAD_UR:
		case VIVE_TOUCHPAD_LR:
		case VIVE_TOUCHPAD_LL:
		case VIVE_TOUCHPAD_S1:
		case VIVE_TOUCHPAD_S2:
		case VIVE_TOUCHPAD_S3:
		case VIVE_TOUCHPAD_S4:
		case VIVE_TOUCHPAD_S5:
		case VIVE_TOUCHPAD_S6:
		case VIVE_TOUCHPAD_S7:
		case VIVE_TOUCHPAD_S8:
		case VIVE_TOUCHPAD_C:
			butt = k_buttonTouchpad;
			break;
		case VIVE_TRIGGER:
		case VIVE_TRIGGER_CLICK:
			butt = k_buttonTrigger;
			break;
		default:
			break;
		}
		de.fruitfly.ovr.structs.Matrix4f mat = MCOpenVR.getControllerComponentTransformFromButton(i, butt);
		Vector3f v = mat.transform(MCOpenVR.forward);
		return new Vector3(v.x, v.y, v.z);
	}
}
