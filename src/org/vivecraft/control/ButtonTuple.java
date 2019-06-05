package org.vivecraft.control;

import java.util.Collection;

public class ButtonTuple {
	public final ButtonType button;
	public final ControllerType controller;
	public final boolean isTouch;

	public ButtonTuple(ButtonType button, ControllerType controller, boolean isTouch) {
		this.button = button;
		this.controller = controller;
		this.isTouch = isTouch;
	}

	public ButtonTuple(ButtonType button, ControllerType controller) {
		this(button, controller, false);
	}
	
	public static ButtonTuple parse(String str) {
		boolean touch = false;
		if (str.endsWith("_TOUCH")) {
			touch = true;
			str = str.substring(0, str.lastIndexOf('_'));
		}
		ButtonType button = Enum.valueOf(ButtonType.class, str.substring(str.indexOf('_') + 1));
		ControllerType controller = Enum.valueOf(ControllerType.class, str.substring(0, str.indexOf('_')));
		return new ButtonTuple(button, controller, touch);
	}
	
	public boolean isInCollection(Collection<? extends ButtonTuple> collection, boolean matchButton, boolean matchController, boolean matchTouch) {
		for (ButtonTuple tuple : collection) {
			if ((!matchController || tuple.controller == this.controller) && (!matchButton || tuple.button == this.button) && (!matchTouch || tuple.isTouch == this.isTouch))
				return true;
		}
		return false;
	}

	// This is slightly dirty code for the sake of nicer button names in GUI
	public String toReadableString() {
		String prefix = controller == ControllerType.LEFT ? "Left " : "Right ";
		if (!button.friendlyNameLeft.equals(button.friendlyNameRight))
			prefix = "";
		return prefix + (controller == ControllerType.LEFT ? button.friendlyNameLeft : button.friendlyNameRight) + (isTouch ? " Touch" : "");
	}

	@Override
	public String toString() {
		return controller.name() + "_" + button.name() + (isTouch ? "_TOUCH" : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((button == null) ? 0 : button.hashCode());
		result = prime * result + ((controller == null) ? 0 : controller.hashCode());
		result = prime * result + (isTouch ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ButtonTuple other = (ButtonTuple) obj;
		if (button != other.button)
			return false;
		if (controller != other.controller)
			return false;
		if (isTouch != other.isTouch)
			return false;
		return true;
	}
}
