package com.mtbs3d.minecrift.control;

import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.provider.TrackedController;

public enum ControllerType {
	RIGHT,
	LEFT;
	
	public TrackedController getController() {
		return MCOpenVR.controllers[this.ordinal()];
	}
}
