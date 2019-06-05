package org.vivecraft.control;

import java.util.List;

import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.utils.Vector2;

public class TrackedControllerWindowsMR extends TrackedControllerVive {
	private boolean stickButtonsEnabled = true;
	
	private static final int k_axisStick = 2;
	
	public TrackedControllerWindowsMR(ControllerType type) {
		super(type);
	}

	@Override
	public void processInput() {
		// axis direction "buttons"
		if (stickButtonsEnabled) {
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].x > 0.5F) != (lastState.rAxis[k_axisStick].x > 0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.WMR_STICK_RIGHT, null, state.rAxis[k_axisStick].x > 0.5F, true, null);
			}
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].x < -0.5F) != (lastState.rAxis[k_axisStick].x < -0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.WMR_STICK_LEFT, null, state.rAxis[k_axisStick].x < -0.5F, true, null);
			}
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].y > 0.5F) != (lastState.rAxis[k_axisStick].y > 0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.WMR_STICK_UP, null, state.rAxis[k_axisStick].y > 0.5F, true, null);
			}
			if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].y < -0.5F) != (lastState.rAxis[k_axisStick].y < -0.5F)) {
				MCOpenVR.queueInputEvent(this, ButtonType.WMR_STICK_DOWN, null, state.rAxis[k_axisStick].y < -0.5F, true, null);
			}
		}

		// axis change
		if (state.rAxis[k_axisStick] != null && (state.rAxis[k_axisStick].x != lastState.rAxis[k_axisStick].x || state.rAxis[k_axisStick].y != lastState.rAxis[k_axisStick].y)) {
			Vector2 deltaVec = new Vector2(state.rAxis[k_axisStick].x - lastState.rAxis[k_axisStick].x, state.rAxis[k_axisStick].y - lastState.rAxis[k_axisStick].y);
			MCOpenVR.queueInputEvent(this, null, AxisType.WMR_STICK, false, false, deltaVec);
		}

		super.processInput();
	}

	@Override
	public List<ButtonType> getActiveButtons() {
		List<ButtonType> list = super.getActiveButtons();
		if (stickButtonsEnabled) {
			list.add(ButtonType.WMR_STICK_UP);
			list.add(ButtonType.WMR_STICK_DOWN);
			list.add(ButtonType.WMR_STICK_LEFT);
			list.add(ButtonType.WMR_STICK_RIGHT);
		}
		return list;
	}

	@Override
	public boolean isButtonPressed(ButtonType button) {
		switch (button) {
			case WMR_STICK_RIGHT:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y > 0.5F;
			case WMR_STICK_LEFT:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y < -0.5F;
			case WMR_STICK_UP:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y > 0.5F;
			case WMR_STICK_DOWN:
				return state.rAxis[k_axisStick] != null && state.rAxis[k_axisStick].y < -0.5F;
			default:
				return super.isButtonPressed(button);
		}
	}

	
	
	@Override
	public Vector2 getAxis(AxisType axis) {
		switch (axis) {
			case WMR_STICK:
				if (state.rAxis[k_axisStick] != null)
					return new Vector2(state.rAxis[k_axisStick].x, state.rAxis[k_axisStick].y);
				return new Vector2();
			default:
				return super.getAxis(axis);
		}
	}
	
	@Override
	public AxisInfo getButtonAxis(ButtonType button) {
		switch (button) {
			case WMR_STICK_UP:
				return new AxisInfo(AxisType.WMR_STICK, true, false);
			case WMR_STICK_DOWN:
				return new AxisInfo(AxisType.WMR_STICK, true, true);
			case WMR_STICK_LEFT:
				return new AxisInfo(AxisType.WMR_STICK, false, true);
			case WMR_STICK_RIGHT:
				return new AxisInfo(AxisType.WMR_STICK, false, false);
			default:
				return super.getButtonAxis(button);
		}
	}

	public boolean isStickButtonsEnabled() {
		return stickButtonsEnabled;
	}

	public void setStickButtonsEnabled(boolean stickButtonsEnabled) {
		this.stickButtonsEnabled = stickButtonsEnabled;
	}
}
