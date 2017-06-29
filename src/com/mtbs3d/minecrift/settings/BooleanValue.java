/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.settings;

public class BooleanValue extends OptionValue {

	private String truePrint;
	private String falsePrint;
	
	boolean defValue;
	
	public BooleanValue( String name, String label, boolean defValue) {
		this( name , label, defValue, "ON","OFF");
	}

	public BooleanValue( String name, String label, boolean defValue, String truePrint, String falsePrint ) {
		super(name,label);
		this.defValue = defValue;
		this.truePrint = truePrint;
		this.falsePrint = falsePrint;
		try {
			this.settingField = VRSettings.class.getDeclaredField(name);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public Boolean getValue() {
		try {
			return this.settingField.getBoolean(VRSettings.inst);
		} catch (Exception e) { e.printStackTrace(); return false;}
	}
	
	public void setValue( boolean value ) {
		try {
			this.settingField.setBoolean(VRSettings.inst, value );
		} catch (Exception e) { e.printStackTrace(); }
	}

	@Override
	public String getDisplayString() {
		return label + ": "+ (getValue() ? truePrint : falsePrint ); 
	}

	@Override
	public String getSerializedValue() {
		return getValue().toString();
	}

	@Override
	public void setSerializeValue(String serString) {
		setValue(Boolean.parseBoolean(serString));
	}
	
	@Override
	public void interact() {
		setValue(!getValue());
	}

	@Override
	public void setDefaultValue() {
		setValue(defValue);
	}

}
