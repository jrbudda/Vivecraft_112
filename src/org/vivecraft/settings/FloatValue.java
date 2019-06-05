/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package org.vivecraft.settings;

public class FloatValue extends OptionValue {

	private String formatString;
	float min, inc, max, defValue;
	public FloatValue( String name, String label, float defValue, String format,  float min, float max, float increment) {
		super(name,label);
		this.defValue = defValue;
		this.formatString = format;
		this.min = min;
		this.max = max;
		this.inc = increment;
		try {
			this.settingField = VRSettings.class.getDeclaredField(name);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	@Override
	public String getDisplayString() {
		return label+": "+String.format(formatString, new Object[]{getValue()});
	}

	@Override
	public String getSerializedValue() {
		return getValue().toString();
	}

	@Override
	public void setSerializeValue(String serString) {
        setValue(serString.equals("true") ? 1.0F : (serString.equals("false") ? 0.0F : Float.parseFloat(serString)));
	}

	Float getValue() {
		try {
			return this.settingField.getFloat(VRSettings.inst);
		} catch (Exception e) { e.printStackTrace(); return 0.0f; }
	}
	
	@Override
	public void setValue(float value){
		try {
			this.settingField.setFloat(VRSettings.inst, value);
		} catch (Exception e) { e.printStackTrace(); }
	};

	@Override
	public float getMin(){
		return min;
	}
	
	@Override
	public float getMax(){
		return max;
	}

	@Override
	public void setDefaultValue() {
		setValue( defValue );
	}
}
