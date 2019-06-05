package org.vivecraft.control;

public class AxisInfo {
	private final AxisType axis;
	private final boolean isY;
	private final boolean negative;
	
	public AxisInfo(AxisType axis, boolean isY, boolean negative) {
		this.axis = axis;
		this.isY = isY;
		this.negative = negative;
	}

	public AxisType getAxis() {
		return axis;
	}

	public boolean isX() {
		return !isY;
	}

	public boolean isY() {
		return isY;
	}

	public boolean isNegative() {
		return negative;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((axis == null) ? 0 : axis.hashCode());
		result = prime * result + (isY ? 1231 : 1237);
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
		AxisInfo other = (AxisInfo) obj;
		if (axis != other.axis)
			return false;
		if (isY != other.isY)
			return false;
		return true;
	}
}
