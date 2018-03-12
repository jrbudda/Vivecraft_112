package com.mtbs3d.minecrift.settings;

public class StringValue extends OptionValue {
	
	String defValue;
	public StringValue(String name, String label, String defValue) {
		super(name, label);
	}

	protected String getValue() {
		try {
			return (String)settingField.get(VRSettings.inst);
		} catch (Exception e) { e.printStackTrace(); return ""; }
	}

	public void setValue(String value) {
		try {
			settingField.set(VRSettings.inst,value);
		} catch (Exception e) { e.printStackTrace(); }
	}

	@Override
	public String getDisplayString() {
		return getValue();
	}

	@Override
	public String getSerializedValue() {
		return getValue();
	}

	@Override
	public void setSerializeValue(String serString) {
		setValue(serString);

	}

	@Override
	public void setDefaultValue() {
		setValue(defValue);
	}

}
