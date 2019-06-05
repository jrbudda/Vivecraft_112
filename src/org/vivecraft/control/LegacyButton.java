package org.vivecraft.control;

/**
 * This now exists just to convert from old binding configs to new
 */
public enum LegacyButton {
	BUTTON_LEFT_TRIGGER(ButtonType.VIVE_TRIGGER, ControllerType.LEFT),
	BUTTON_LEFT_TRIGGER_FULLCLICK(ButtonType.VIVE_TRIGGER_CLICK, ControllerType.LEFT),
	BUTTON_LEFT_APPMENU(ButtonType.VIVE_APPMENU, ControllerType.LEFT),
	BUTTON_LEFT_TOUCHPAD_UL(ButtonType.VIVE_TOUCHPAD_UL, ControllerType.LEFT),
	BUTTON_LEFT_TOUCHPAD_UR(ButtonType.VIVE_TOUCHPAD_UR, ControllerType.LEFT),
	BUTTON_LEFT_TOUCHPAD_BL(ButtonType.VIVE_TOUCHPAD_LL, ControllerType.LEFT),
	BUTTON_LEFT_TOUCHPAD_BR(ButtonType.VIVE_TOUCHPAD_LR, ControllerType.LEFT),
	BUTTON_LEFT_GRIP(ButtonType.VIVE_GRIP, ControllerType.LEFT),
	BUTTON_RIGHT_TRIGGER(ButtonType.VIVE_TRIGGER, ControllerType.RIGHT),
	BUTTON_RIGHT_TRIGGER_FULLCLICK(ButtonType.VIVE_TRIGGER_CLICK, ControllerType.RIGHT),
	BUTTON_RIGHT_APPMENU(ButtonType.VIVE_APPMENU, ControllerType.RIGHT),
	BUTTON_RIGHT_TOUCHPAD_UL(ButtonType.VIVE_TOUCHPAD_UL, ControllerType.RIGHT),
	BUTTON_RIGHT_TOUCHPAD_UR(ButtonType.VIVE_TOUCHPAD_UR, ControllerType.RIGHT),
	BUTTON_RIGHT_TOUCHPAD_BL(ButtonType.VIVE_TOUCHPAD_LL, ControllerType.RIGHT),
	BUTTON_RIGHT_TOUCHPAD_BR(ButtonType.VIVE_TOUCHPAD_LR, ControllerType.RIGHT),
	BUTTON_RIGHT_GRIP(ButtonType.VIVE_GRIP, ControllerType.RIGHT),
	BUTTON_LEFT_TOUCHPAD_SWIPE_UP(ButtonType.VIVE_TOUCHPAD_SWIPE_UP, ControllerType.LEFT),
	BUTTON_LEFT_TOUCHPAD_SWIPE_DOWN(ButtonType.VIVE_TOUCHPAD_SWIPE_DOWN, ControllerType.LEFT),
	BUTTON_LEFT_TOUCHPAD_SWIPE_LEFT(ButtonType.VIVE_TOUCHPAD_SWIPE_LEFT, ControllerType.LEFT),
	BUTTON_LEFT_TOUCHPAD_SWIPE_RIGHT(ButtonType.VIVE_TOUCHPAD_SWIPE_RIGHT, ControllerType.LEFT),
	BUTTON_RIGHT_TOUCHPAD_SWIPE_UP(ButtonType.VIVE_TOUCHPAD_SWIPE_UP, ControllerType.RIGHT),
	BUTTON_RIGHT_TOUCHPAD_SWIPE_DOWN(ButtonType.VIVE_TOUCHPAD_SWIPE_DOWN, ControllerType.RIGHT),
	BUTTON_RIGHT_TOUCHPAD_SWIPE_LEFT(ButtonType.VIVE_TOUCHPAD_SWIPE_LEFT, ControllerType.RIGHT),
	BUTTON_RIGHT_TOUCHPAD_SWIPE_RIGHT(ButtonType.VIVE_TOUCHPAD_SWIPE_RIGHT, ControllerType.RIGHT),
	OCULUS_LEFT_INDEX_TRIGGER_TOUCH(ButtonType.OCULUS_INDEX_TRIGGER, ControllerType.LEFT, true),
	OCULUS_LEFT_INDEX_TRIGGER_PRESS(ButtonType.OCULUS_INDEX_TRIGGER, ControllerType.LEFT),
	OCULUS_LEFT_HAND_TRIGGER_TOUCH(ButtonType.OCULUS_HAND_TRIGGER, ControllerType.LEFT, true),
	OCULUS_LEFT_HAND_TRIGGER_PRESS(ButtonType.OCULUS_HAND_TRIGGER, ControllerType.LEFT),
	OCULUS_LEFT_Y_PRESS(ButtonType.OCULUS_BY, ControllerType.LEFT),
	OCULUS_LEFT_Y_TOUCH(ButtonType.OCULUS_BY, ControllerType.LEFT, true),
	OCULUS_LEFT_STICK_PRESS(ButtonType.OCULUS_STICK, ControllerType.LEFT),
	OCULUS_LEFT_STICK_TOUCH(ButtonType.OCULUS_STICK, ControllerType.LEFT, true),
	OCULUS_LEFT_X_PRESS(ButtonType.OCULUS_AX, ControllerType.LEFT),
	OCULUS_LEFT_X_TOUCH(ButtonType.OCULUS_AX, ControllerType.LEFT, true),
	OCULUS_LEFT_STICK_LEFT(ButtonType.OCULUS_STICK_LEFT, ControllerType.LEFT),
	OCULUS_LEFT_STICK_RIGHT(ButtonType.OCULUS_STICK_RIGHT, ControllerType.LEFT),
	OCULUS_LEFT_STICK_UP(ButtonType.OCULUS_STICK_UP, ControllerType.LEFT),
	OCULUS_LEFT_STICK_DOWN(ButtonType.OCULUS_STICK_DOWN, ControllerType.LEFT),
	OCULUS_RIGHT_INDEX_TRIGGER_TOUCH(ButtonType.OCULUS_INDEX_TRIGGER, ControllerType.RIGHT, true),
	OCULUS_RIGHT_INDEX_TRIGGER_PRESS(ButtonType.OCULUS_INDEX_TRIGGER, ControllerType.RIGHT),
	OCULUS_RIGHT_HAND_TRIGGER_TOUCH(ButtonType.OCULUS_HAND_TRIGGER, ControllerType.RIGHT, true),
	OCULUS_RIGHT_HAND_TRIGGER_PRESS(ButtonType.OCULUS_HAND_TRIGGER, ControllerType.RIGHT),
	OCULUS_RIGHT_B_PRESS(ButtonType.OCULUS_BY, ControllerType.RIGHT),
	OCULUS_RIGHT_B_TOUCH(ButtonType.OCULUS_BY, ControllerType.RIGHT, true),
	OCULUS_RIGHT_STICK_PRESS(ButtonType.OCULUS_STICK, ControllerType.RIGHT),
	OCULUS_RIGHT_STICK_TOUCH(ButtonType.OCULUS_STICK, ControllerType.RIGHT, true),
	OCULUS_RIGHT_A_PRESS(ButtonType.OCULUS_AX, ControllerType.RIGHT),
	OCULUS_RIGHT_A_TOUCH(ButtonType.OCULUS_AX, ControllerType.RIGHT, true),
	OCULUS_RIGHT_STICK_LEFT(ButtonType.OCULUS_STICK_LEFT, ControllerType.RIGHT),
	OCULUS_RIGHT_STICK_RIGHT(ButtonType.OCULUS_STICK_RIGHT, ControllerType.RIGHT),
	OCULUS_RIGHT_STICK_UP(ButtonType.OCULUS_STICK_UP, ControllerType.RIGHT),
	OCULUS_RIGHT_STICK_DOWN(ButtonType.OCULUS_STICK_DOWN, ControllerType.RIGHT);
	
	public final ButtonType button;
	public final ControllerType controller;
	public final boolean isTouch;
	
	private LegacyButton(ButtonType button, ControllerType controller, boolean isTouch) {
		this.button = button;
		this.controller = controller;
		this.isTouch = isTouch;
	}
	
	private LegacyButton(ButtonType button, ControllerType controller) {
		this(button, controller, false);
	}
}
