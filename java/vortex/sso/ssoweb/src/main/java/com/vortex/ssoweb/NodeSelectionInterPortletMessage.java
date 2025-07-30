package com.vortex.ssoweb;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.f1.suite.web.portal.InterPortletMessage;

public class NodeSelectionInterPortletMessage implements InterPortletMessage {

	public static class Mask {
		public final String name;
		public final String type;
		public final String key;
		public final Object mask;
		public final int priority;
		public final Mask next;
		public final long groupId;

		public Mask(int priority, String name, String type, String key, Object mask, Mask next, long groupId) {
			this.priority = priority;
			this.name = name;
			this.type = type;
			this.key = key;
			this.mask = mask;
			this.next = next;
			this.groupId = groupId;
		}
		public Mask(String key, long value) {
			this(0, "", "", key, "^" + value + "$", null, 0);
		}
	}

	private List<Mask> masks;
	private Set<Long> selectedGroupIds = Collections.EMPTY_SET;
	private Set<Long> expectationIds = Collections.EMPTY_SET;

	public List<Mask> getMasks() {
		return masks;
	}

	public void setMasks(List<Mask> masks) {
		this.masks = masks;
	}

	public Set<Long> getSelectedGroupIds() {
		return selectedGroupIds;
	}
	public void setSelectedGroupIds(Set<Long> selectedGroupIds) {
		this.selectedGroupIds = selectedGroupIds;
	}

	public void setExpectationIds(Set<Long> expectationIds) {
		this.expectationIds = expectationIds;
	}

	public Set<Long> getExpectationIds() {
		return this.expectationIds;
	}
}
