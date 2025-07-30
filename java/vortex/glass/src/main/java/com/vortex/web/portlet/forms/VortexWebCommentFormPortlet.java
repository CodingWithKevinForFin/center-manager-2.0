package com.vortex.web.portlet.forms;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.web.VortexWebEyeService;

public class VortexWebCommentFormPortlet extends GridPortlet implements FormPortletListener {

	private static final String ICON_ADD = "add.gif";
	private static final String ICON_COPY = "copy.gif";
	private static final String ICON_EDIT = "edit.gif";
	private static final String ICON_DELETE = "remove.gif";
	private static final String ICON_NOT_ENTITLED = "not_entitled.gif";
	private String parentPortletId;
	private VortexWebEyeService service;
	protected FormPortletButton submitButton;
	private FormPortletTextAreaField comment;
	private List<? extends VortexEyeRequest> reqs;
	private HtmlPortlet imagePortlet;
	protected FormPortlet formPortlet;
	protected FormPortlet commentFormPortlet;
	private String imageUrl;
	private String iconUrl;
	private FormPortletButton closeButton;
	private ChangeListener[] changeListerList = new ChangeListener[0];

	protected FormPortletTextAreaField getCommentField() {
		return comment;
	}

	public VortexWebCommentFormPortlet(PortletConfig config, String parentPortletId, VortexEyeRequest req, String buttonName, String imageUrl) {
		this(config, parentPortletId, req == null ? Collections.EMPTY_LIST : CH.l(req), buttonName, imageUrl);
	}
	public VortexWebCommentFormPortlet(PortletConfig config, String parentPortletId, List<? extends VortexEyeRequest> req, String buttonName, String imageUrl) {
		super(config);
		this.imagePortlet = addChild(new HtmlPortlet(generateConfig(), "", "comment_header"), 0, 0);
		this.imageUrl = imageUrl;
		this.formPortlet = addChild(new FormPortlet(generateConfig()), 0, 1);
		HtmlPortlet labelPortlet = addChild(new HtmlPortlet(generateConfig(), "<BR>&nbsp;&nbsp;Enter a comment (optional):"), 0, 2);
		this.commentFormPortlet = addChild(new FormPortlet(generateConfig()), 0, 3);
		this.commentFormPortlet.setLabelsWidth(0);
		this.setRowSize(0, 125);
		this.setRowSize(2, 35);
		this.setRowSize(3, 125);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.parentPortletId = parentPortletId;
		this.reqs = req;

		if (buttonName != null) {
			submitButton = commentFormPortlet.addButton(new FormPortletButton(req.size() == 1 ? buttonName : (buttonName + " on "
					+ (req.size() > 0 ? Integer.toString(req.size()) + " " : "") + "selected items")));
		} else
			submitButton = null;
		comment = commentFormPortlet.addField(new FormPortletTextAreaField(""));
		commentFormPortlet.addFormPortletListener(this);
		formPortlet.addFormPortletListener(this);
		updateHeader();
	}
	private void updateHeader() {
		if (SH.is(imageUrl))
			imagePortlet.setHtml("<div style=\"width:100%;height:100%;background-image:url('rsc/headers/" + imageUrl
					+ "');background-repeat:no-repeat;background-position:center;text-align:center;padding:5px 5px\">"
					+ (SH.is(iconUrl) ? ("<img src='rsc/headers/" + iconUrl + "'>") : "") + "</div>");

	}
	protected String getUserComment() {
		return comment.getValue();
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		this.onUserPressedButton(button);
	}

	public FormPortletField<?> removeFieldNoThrow(FormPortletField<?> field) {
		return this.formPortlet.removeFieldNoThrow(field);
	}
	public FormPortletField<?> removeField(FormPortletField<?> field) {
		return this.formPortlet.removeField(field);
	}

	public <T extends FormPortletField<?>> boolean addFieldNoThrow(T field) {
		return this.formPortlet.addFieldNoThrow(field);
	}
	public <T extends FormPortletField<?>> T addField(T field) {
		return this.formPortlet.addField(field);
	}
	public <T extends FormPortletField<?>> T addField(T field, int pos) {
		return this.formPortlet.addField(field, pos);
	}
	public FormPortletButton removeButton(FormPortletButton button) {
		return this.commentFormPortlet.removeButton(button);
	}
	public <T extends FormPortletButton> T addButton(T button) {
		return this.commentFormPortlet.addButton(button);
	}

	public boolean hasField(FormPortletField<?> field) {
		return this.formPortlet.hasField(field);
	}

	public void clearButtons() {
		this.commentFormPortlet.clearButtons();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		onUserChangedValue(field, attributes);
		fireChanged();
	}
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	final public int getSuggestedHeight(PortletMetrics pm) {
		return 220 + formPortlet.getSuggestedHeight(pm) + commentFormPortlet.getSuggestedHeight(pm);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 400;
	}

	//public static Portlet wrapInHeader(Portlet p, String imageUrl) {
	//return wrapInHeader(p, imageUrl, null);
	//}
	//public static Portlet wrapInHeader(Portlet p, String imageUrl, String ico) {
	//GridPortlet gp = new GridPortlet(p.getManager().generateConfig());
	//gp.addChild(p, 0, 1);
	//HtmlPortlet img = gp.addChild(new HtmlPortlet(p.getManager().generateConfig(), "", "comment_header"), 0, 0);
	//img.setHtml("<div style=\"width:100%;height:100%;background-image:url('rsc/headers/" + imageUrl
	//+ "');background-repeat:no-repeat;background-position:center right;text-align:right;padding:5px 5px\">"
	//+ (SH.is(ico) ? ("<img src='rsc/headers/" + ico + "'>") : "") + "</div>");
	//gp.setRowSize(0, 125);
	//gp.setSuggestedSize(Math.max(400, p.getSuggestedWidth(p.getManager().getPortletMetrics())), p.getSuggestedHeight(p.getManager().getPortletMetrics()) + 130);
	//return gp;
	//}

	protected void onUserPressedButton(FormPortletButton button) {
		if (button != null) {
			if (button == closeButton) {
				close();
			} else if (button == submitButton) {
				for (VortexEyeRequest req : reqs) {
					req.setComment(comment.getValue());
					service.sendRequestToBackend(parentPortletId, req);
				}
				close();
			}
		}
	}
	public VortexWebCommentFormPortlet setIconToEdit() {
		setIcon(ICON_EDIT);
		return this;
	}
	public VortexWebCommentFormPortlet setIconToCopy() {
		setIcon(ICON_COPY);
		return this;
	}
	public VortexWebCommentFormPortlet setIconToAdd() {
		setIcon(ICON_ADD);
		return this;
	}
	public VortexWebCommentFormPortlet setIconToDelete() {
		setIcon(ICON_DELETE);
		return this;
	}
	public void setIcon(String iconUrl) {
		if (ICON_NOT_ENTITLED.equals(this.iconUrl))//this is a final state
			return;
		this.iconUrl = iconUrl;
		updateHeader();
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	public void addFormPortletListener(FormPortletListener listener) {
		this.formPortlet.addFormPortletListener(listener);
	}

	public void addChangeListener(ChangeListener l) {
		this.changeListerList = AH.append(this.changeListerList, l);
	}

	private final ChangeEvent CE = new ChangeEvent(this);

	protected void fireChanged() {
		for (ChangeListener l : this.changeListerList)
			l.stateChanged(CE);
	}
}
