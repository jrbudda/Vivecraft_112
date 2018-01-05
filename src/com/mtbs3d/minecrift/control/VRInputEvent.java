package com.mtbs3d.minecrift.control;

import com.mtbs3d.minecrift.utils.Vector2;

public class VRInputEvent {
	private final TrackedController controller;
	private final ButtonType button;
	private final AxisType axis;
	private final boolean buttonState;
	private final boolean buttonPress;
	private final Vector2 axisDelta;

	public VRInputEvent(TrackedController controller, ButtonType button, AxisType axis, boolean buttonState, boolean buttonPress, Vector2 axisDelta) {
		this.controller = controller;
		this.button = button;
		this.axis = axis;
		this.buttonState = buttonState;
		this.buttonPress = buttonPress;
		this.axisDelta = axisDelta;
	}

	public TrackedController getController() {
		return controller;
	}

	public ButtonType getButton() {
		return button;
	}

	public AxisType getAxis() {
		return axis;
	}

	public boolean isButtonTouchEvent() {
		return button != null && !buttonPress;
	}

	public boolean isButtonPressEvent() {
		return button != null && buttonPress;
	}

	public boolean isAxisEvent() {
		return axis != null;
	}

	public boolean getButtonState() {
		return buttonState;
	}

	public Vector2 getAxisDelta() {
		return axisDelta.copy();
	}

	@Override
	public String toString() {
		return "VRInputEvent [controller=" + controller.getType() + ", button=" + button + ", axis=" + axis + ", buttonState=" + buttonState + ", buttonPress=" + buttonPress + ", axisDelta=" + axisDelta + "]";
	}
}
