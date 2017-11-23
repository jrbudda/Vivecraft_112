package com.mtbs3d.minecrift.provider;

import java.util.List;

import com.mtbs3d.minecrift.control.AxisType;
import com.mtbs3d.minecrift.control.ButtonType;
import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.utils.Vector2;

import jopenvr.VRControllerAxis_t;
import jopenvr.VRControllerState_t;

public abstract class TrackedController {
	protected VRControllerState_t.ByReference state;
	protected VRControllerState_t lastState;
	int deviceIndex = -1;
	final ControllerType type;
	boolean tracking;
	
	public TrackedController(ControllerType type) {
		this.type = type;
		state = new VRControllerState_t.ByReference();
		lastState = new VRControllerState_t();
		for (int i = 0; i < lastState.rAxis.length; i++)
			lastState.rAxis[i] = new VRControllerAxis_t();
	}
	
	public void updateState() {
		lastState.unPacketNum = state.unPacketNum;
		lastState.ulButtonPressed = state.ulButtonPressed;
		lastState.ulButtonTouched = state.ulButtonTouched;
		for (int i = 0; i < 5; i++) {
			if (state.rAxis[i] != null) {
				lastState.rAxis[i].x = state.rAxis[i].x;
				lastState.rAxis[i].y = state.rAxis[i].y;
			}
		}

		if (deviceIndex != -1) {
			MCOpenVR.vrsystem.GetControllerState.apply(deviceIndex, state, state.size());
			state.read();
		} else {
			state.ulButtonPressed = 0;
			state.ulButtonTouched = 0;
			for (int i = 0; i < 5; i++) {
				if (state.rAxis[i] != null) {
					state.rAxis[i].x = 0;
					state.rAxis[i].y = 0;
				}
			}
		}
	}
	
	public int getDeviceIndex() {
		return deviceIndex;
	}
	
	public ControllerType getType() {
		return type;
	}
	
	public boolean isTracking() {
		return tracking;
	}

	public void triggerHapticPulse(int duration) {
		if (deviceIndex == -1) return;
		if (duration < 0) return;
		if (duration > 3999) duration = 3999;
		MCOpenVR.vrsystem.TriggerHapticPulse.apply(deviceIndex, 0, (short)duration);
	}

	abstract void processInput();
	abstract void processButtonEvent(int button, boolean state, boolean press);
	public abstract List<ButtonType> getActiveButtons();
	public abstract boolean isButtonTouched(ButtonType button);
	public abstract boolean isButtonPressed(ButtonType button);
	public abstract boolean canButtonBeTouched(ButtonType button);
	public abstract Vector2 getAxis(AxisType axis);
	public abstract AxisType getButtonAxis(ButtonType button);
}
