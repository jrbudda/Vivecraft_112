package com.mtbs3d.minecrift.control;

import java.util.Collection;

public class ButtonTuple {
	public final ButtonType button;
	public final ControllerType controller;

	public ButtonTuple(ButtonType button, ControllerType controller) {
		this.button = button;
		this.controller = controller;
	}
	
	public static ButtonTuple parse(String str) {
		ButtonType button = Enum.valueOf(ButtonType.class, str.substring(str.indexOf('_') + 1));
		ControllerType controller = Enum.valueOf(ControllerType.class, str.substring(0, str.indexOf('_')));
		return new ButtonTuple(button, controller);
	}
	
	public boolean isInCollection(Collection<? extends ButtonTuple> collection, boolean button, boolean controller) {
		for (ButtonTuple tuple : collection) {
			if ((!controller || tuple.controller == this.controller) && (!button || tuple.button == this.button))
				return true;
		}
		return false;
	}

	// This is slightly dirty code for the sake of nicer button names in GUI
	public String toReadableString() {
		String buttonName = button.toString().substring(button.toString().indexOf('_') + 1);
		if (buttonName.equals("AX")) buttonName = controller == ControllerType.LEFT ? "X" : "A";
		if (buttonName.equals("BY")) buttonName = controller == ControllerType.LEFT ? "Y" : "B";
		return controller.toString() + "_" + buttonName;
	}

	@Override
	public String toString() {
		return controller.name() + "_" + button.name();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((button == null) ? 0 : button.hashCode());
		result = prime * result + ((controller == null) ? 0 : controller.hashCode());
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
		return true;
	}
}
