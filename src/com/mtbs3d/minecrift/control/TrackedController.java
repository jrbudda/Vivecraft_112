package com.mtbs3d.minecrift.control;

import java.util.List;

import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.Vector2;
import com.mtbs3d.minecrift.utils.Vector3;

import jopenvr.VRControllerAxis_t;
import jopenvr.VRControllerState_t;
import net.minecraft.client.Minecraft;

public abstract class TrackedController {
	protected VRControllerState_t.ByReference state;
	protected VRControllerState_t lastState;
	public int deviceIndex = -1;
	final ControllerType type;
	public boolean tracking;
	
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
		if(Minecraft.getMinecraft().vrSettings.seated) return;
		if (deviceIndex == -1) return;
		if (duration < 0) return;
		if (duration > 3999) duration = 3999;
		MCOpenVR.vrsystem.TriggerHapticPulse.apply(deviceIndex, 0, (short)duration);
	}
	
	public boolean isButtonActive(ButtonType button) {
		return getActiveButtons().contains(button);
	}

	public float getButtonAxisValue(ButtonType button) {
		AxisInfo ai = getButtonAxis(button);
		if (ai == null) return isButtonPressed(button) ? 1 : 0;
		Vector2 axis = this.getAxis(ai.getAxis());
		float x = axis.getX();
		float y = axis.getY();
		if (ai.isX()) {
			if (ai.isNegative()) return x < 0 ? -x : 0;
			else return x > 0 ? x : 0;
		} else {
			if (ai.isNegative()) return y < 0 ? -y : 0;
			else return y > 0 ? y : 0;
		}
	}
	
	public abstract Vector3 getButtonLocation(ButtonType button);
	public abstract void processInput();
	public abstract void processButtonEvent(int button, boolean state, boolean press);
	public abstract List<ButtonType> getActiveButtons();
	public abstract boolean isButtonTouched(ButtonType button);
	public abstract boolean isButtonPressed(ButtonType button);
	public abstract boolean canButtonBeTouched(ButtonType button);
	public abstract Vector2 getAxis(AxisType axis);
	public abstract AxisInfo getButtonAxis(ButtonType button);
}
