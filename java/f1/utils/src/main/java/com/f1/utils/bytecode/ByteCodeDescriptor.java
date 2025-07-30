package com.f1.utils.bytecode;

public class ByteCodeDescriptor {

	private int dimensions;
	private String type = null;
	private boolean isPrimitive;

	public String getType() {
		return type;
	}

	public boolean getIsPimitive() {
		return isPrimitive;
	}

	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

}
