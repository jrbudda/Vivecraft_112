/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.settings;

public abstract class IntValue extends OptionValue {
	
	int defValue;
	public IntValue(String name, String label, int defValue) {
		super(name,label);
		this.defValue = defValue;
	}
	void setValue( int value ) {
		try {
			this.settingField.setInt(VRSettings.inst, value);
		} catch (Exception e) { e.printStackTrace(); }
		
	}
	Integer getValue() {
		try {
			return this.settingField.getInt(VRSettings.inst);
		} catch (Exception e) { e.printStackTrace(); return 0; }
		
	}
	@Override
	public String getSerializedValue() {
		return label+": "+getValue().toString();
	}

	@Override
	public void setSerializeValue(String serString) {
		setValue( Integer.parseInt(serString));
	}

	@Override
	public void setDefaultValue() {
		setValue(defValue);
	}

}
