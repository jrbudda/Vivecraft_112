package org.vivecraft.control;

import java.util.ArrayList;
import java.util.List;

import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.utils.Vector2;
import org.vivecraft.utils.Vector3;

import de.fruitfly.ovr.structs.Vector3f;
import jopenvr.JOpenCompositeLibrary;
import jopenvr.JOpenVRLibrary;

public class TrackedControllerOculus extends TrackedController {
	private boolean stickButtonsEnabled = true;
	private long extendedButtonState = 0;
	private long lastExtendedButtonState = 0;
	
	private static final long k_buttonIndexTrigger = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Trigger);
	private static final long k_buttonHandTrigger =  (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_Axis2);
	private static final long k_buttonA =  (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_A);
	private static final long k_buttonB = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_ApplicationMenu);
	private static final long k_buttonStick = (1L << JOpenVRLibrary.EVRButtonId.EVRButtonId_k_EButton_SteamVR_Touchpad);
	private static final int k_axisStick = 0;
	private static final int k_axisIndexTrigger = 1;
	private static final int k_axisHandTrigger = 2;

	// OpenComposite extended buttons
	private static final long k_buttonMenu = (1L << JOpenCompositeLibrary.EVRExtendedButtonId.EVRExtendedButtonId_k_EButton_OVRMenu);

	public TrackedControllerOculus(ControllerType type) {
		super(type);
	}

	@Override
	public void updateState() {
		super.updateState();

		if (MCOpenVR.hasOpenComposite()) {
			lastExtendedButtonState = extendedButtonState;

			if (deviceIndex != -1) {
				extendedButtonState = MCOpenVR.vrOpenComposite.GetExtendedButtonStatus.apply();
			}
			else {
				extendedButtonState = 0;
			}
		}
	}

	@Override
	public void processInput() {
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

		// OpenComposite extended buttons (may miss very fast inputs due to lack of event-based polling)
		if (MCOpenVR.hasOpenComposite() && this.type == ControllerType.LEFT) {
			if ((extendedButtonState & k_buttonMenu) != (lastExtendedButtonState & k_buttonMenu)) {
				MCOpenVR.queueInputEvent(this, ButtonType.OCULUS_MENU, null, (extendedButtonState & k_buttonMenu) > 0, true, null);
			}
		}
	}
	
	@Override
	public void processButtonEvent(int button, boolean state, boolean press) {
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
		if (MCOpenVR.hasOpenComposite() && this.type == ControllerType.LEFT)
			list.add(ButtonType.OCULUS_MENU);
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
		}

		if (MCOpenVR.hasOpenComposite() && this.type == ControllerType.LEFT) {
			switch (button) {
				case OCULUS_MENU:
					return (extendedButtonState & k_buttonMenu) > 0;
			}
		}

		return false;
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

	@Override
	public Vector3 getButtonLocation(ButtonType button) {
		int i = this.type == ControllerType.RIGHT ? 0 : 1;
		long butt = -1;
		switch (button) {
			case OCULUS_AX:
				butt = k_buttonA;
				break;
			case OCULUS_BY:
				butt = k_buttonB;
				break;
			case OCULUS_INDEX_TRIGGER:
				butt = k_buttonIndexTrigger;
				break;
			case OCULUS_HAND_TRIGGER:
				butt = k_buttonHandTrigger;
				break;
			case OCULUS_STICK:
				butt = k_buttonStick;
				break;
			case OCULUS_STICK_RIGHT:
				butt = k_axisStick;
				break;
			case OCULUS_STICK_LEFT:
				butt = k_axisStick;
				break;
			case OCULUS_STICK_UP:
				butt = k_axisStick;
				break;
			case OCULUS_STICK_DOWN:
				butt = k_axisStick;
				break;
			case OCULUS_MENU:
				return new Vector3(); // no way to get this button's position
			default:
				break;
		}
		de.fruitfly.ovr.structs.Matrix4f mat = MCOpenVR.getControllerComponentTransformFromButton(i, butt);
		Vector3f v = mat.transform(MCOpenVR.forward);
		return new Vector3(v.x, v.y, v.z);
	}
}
