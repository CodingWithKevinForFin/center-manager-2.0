/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.awt.Rectangle;

public class WebRectangle {
	private int top, left, width, height;

	public WebRectangle(int left, int top, int width, int height) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
	}

	public WebRectangle(WebRectangle webRectangle) {
		this.top = webRectangle.top;
		this.left = webRectangle.left;
		this.width = webRectangle.width;
		this.height = webRectangle.height;
	}

	public WebRectangle(Rectangle clip) {
		this.top = clip.y;
		this.left = clip.x;
		this.width = clip.width;
		this.height = clip.height;
	}

	public int getTop() {
		return top;
	}

	public WebRectangle setTop(int top) {
		this.top = top;
		return this;
	}

	public int getLeft() {
		return left;
	}

	public WebRectangle setLeft(int left) {
		this.left = left;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public WebRectangle setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public WebRectangle setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	public String toString() {
		return "WebRectangle [top=" + top + ", left=" + left + ", width=" + width + ", height=" + height + "]";
	}

	@Override
	public int hashCode() {
		return OH.hashCode(height, left, top, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || obj.getClass() != getClass())
			return false;
		WebRectangle other = (WebRectangle) obj;
		return (height == other.height) && (left == other.left) && (top == other.top) && (width == other.width);
	}

	public WebRectangle clone() {
		return new WebRectangle(this);
	}

	public WebRectangle grow(int i) {
		this.left -= i;
		this.top -= i;
		this.width += i + i;
		this.height += i + i;
		return this;
	}
	public WebRectangle grow(int x, int y) {
		this.left -= x;
		this.top -= y;
		this.width += x + x;
		this.height += y + y;
		return this;
	}
	public WebRectangle grow(int left, int top, int right, int bottom) {
		this.left -= left;
		this.top -= top;
		this.width += left + right;
		this.height += top + bottom;
		return this;
	}

	public boolean containsPoint(int x, int y) {
		return x >= left && x < left + width && y >= top && y < top + height;
	}

	public int getRight() {
		return left + width;
	}
	public int getBottom() {
		return top + height;
	}

}
