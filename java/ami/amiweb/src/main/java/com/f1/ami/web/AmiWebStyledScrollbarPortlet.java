package com.f1.ami.web;

public interface AmiWebStyledScrollbarPortlet {

	public String getScrollGripColor();
	public void setScrollGripColor(String scrollGripColor);

	public String getScrollTrackColor();
	public void setScrollTrackColor(String scrollTrackColor);

	public String getScrollButtonColor();
	public void setScrollButtonColor(String scrollButtonColor);

	public String getScrollIconsColor();
	public void setScrollIconsColor(String scrollIconsColor);

	public Integer getScrollBarWidth();
	public void setScrollBarWidth(Integer scrollBarWidth);

	public String getScrollBorderColor();
	public void setScrollBorderColor(String color);

	public Integer getScrollBarRadius();
	public void setScrollBarRadius(Integer borderRadius);
	public void setScrollBarHideArrows(Boolean hide);
	public void setScrollBarCornerColor(String color);
	public Boolean getScrollBarHideArrows();
	public String getScrollBarCornerColor();

}
