/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.settings;

import java.lang.reflect.Field;

public abstract class OptionValue {
	public OptionValue(String name, String label) {
		this.label = label;
		this.fieldName = name;
	}
	protected String label; //For use in display string
	public String fieldName;
	protected Field settingField;
	
	public abstract String getDisplayString(); //For showing in GUI: "Label: Value"
	public abstract String getSerializedValue(); //For serialization
	public abstract void setSerializeValue(String serString);
	public abstract void setDefaultValue();

	public void interact(){}; //For boolean/toggles
	public void setValue(float value){}; //For sliders 
	public float getMin(){return 0;}
	public float getMax(){return 100;}
}
