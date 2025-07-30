package com.f1.ami.web.style;

public interface AmiWebStyleManagerListener {

	public void onStyleAdded(AmiWebStyle style);
	public void onStyleRemoved(AmiWebStyle style);
	public void onStyleLabelChanged(AmiWebStyle style, String old, String label);
	public void onStyleParentChanged(AmiWebStyleImpl target, String oldParentStyleId, String parentStyleId);
}
