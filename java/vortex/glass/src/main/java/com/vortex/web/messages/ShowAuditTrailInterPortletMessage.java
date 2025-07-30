package com.vortex.web.messages;

import java.util.List;

import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.Tuple2;

public class ShowAuditTrailInterPortletMessage implements InterPortletMessage {

	final private List<Tuple2<F1AppAuditTrailEvent, Object>> removed;
	final private List<Tuple2<F1AppAuditTrailEvent, Object>> added;

	public ShowAuditTrailInterPortletMessage(List<Tuple2<F1AppAuditTrailEvent, Object>> removed, List<Tuple2<F1AppAuditTrailEvent, Object>> added) {
		this.removed = removed;
		this.added = added;
	}

	public List<Tuple2<F1AppAuditTrailEvent, Object>> getRemoved() {
		return removed;
	}

	public List<Tuple2<F1AppAuditTrailEvent, Object>> getAdded() {
		return added;
	}

}
