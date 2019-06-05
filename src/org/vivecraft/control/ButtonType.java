package org.vivecraft.control;

public enum ButtonType {
	VIVE_APPMENU("App Menu"),
	VIVE_GRIP("Grip"),
	VIVE_TRIGGER("Trigger"),
	VIVE_TRIGGER_CLICK("Trigger Click"),
	VIVE_TOUCHPAD("Touchpad"),
	VIVE_TOUCHPAD_C("Touchpad Center"),
	VIVE_TOUCHPAD_U("Touchpad Top"),
	VIVE_TOUCHPAD_D("Touchpad Bottom"),
	VIVE_TOUCHPAD_L("Touchpad Left"),
	VIVE_TOUCHPAD_R("Touchpad Right"),
	VIVE_TOUCHPAD_UL("Touchpad Upper Left"),
	VIVE_TOUCHPAD_UR("Touchpad Upper Right"),
	VIVE_TOUCHPAD_LR("Touchpad Lower Right"),
	VIVE_TOUCHPAD_LL("Touchpad Lower Left"),
	VIVE_TOUCHPAD_S1("Touchpad Segment 1"),
	VIVE_TOUCHPAD_S2("Touchpad Segment 2"),
	VIVE_TOUCHPAD_S3("Touchpad Segment 3"),
	VIVE_TOUCHPAD_S4("Touchpad Segment 4"),
	VIVE_TOUCHPAD_S5("Touchpad Segment 5"),
	VIVE_TOUCHPAD_S6("Touchpad Segment 6"),
	VIVE_TOUCHPAD_S7("Touchpad Segment 7"),
	VIVE_TOUCHPAD_S8("Touchpad Segment 8"),
	VIVE_TOUCHPAD_SWIPE_UP("Touchpad Swipe Up"),
	VIVE_TOUCHPAD_SWIPE_DOWN("Touchpad Swipe Down"),
	VIVE_TOUCHPAD_SWIPE_LEFT("Touchpad Swipe Left"),
	VIVE_TOUCHPAD_SWIPE_RIGHT("Touchpad Swipe Right"),

	OCULUS_INDEX_TRIGGER("Index Trigger"),
	OCULUS_HAND_TRIGGER("Hand Trigger"),
	OCULUS_AX("X", "A"),
	OCULUS_BY("Y", "B"),
	OCULUS_MENU("Menu"),
	OCULUS_STICK("Stick Press"),
	OCULUS_STICK_UP("Stick Up"),
	OCULUS_STICK_DOWN("Stick Down"),
	OCULUS_STICK_LEFT("Stick Left"),
	OCULUS_STICK_RIGHT("Stick Right"),
	
	WMR_STICK_UP("Stick Up"),
	WMR_STICK_DOWN("Stick Down"),
	WMR_STICK_LEFT("Stick Left"),
	WMR_STICK_RIGHT("Stick Right");

	public final String friendlyNameLeft;
	public final String friendlyNameRight;

	ButtonType(String friendlyNameLeft, String friendlyNameRight) {
		this.friendlyNameLeft = friendlyNameLeft;
		this.friendlyNameRight = friendlyNameRight;
	}

	ButtonType(String friendlyName) {
		this.friendlyNameLeft = friendlyName;
		this.friendlyNameRight = friendlyName;
	}
}
