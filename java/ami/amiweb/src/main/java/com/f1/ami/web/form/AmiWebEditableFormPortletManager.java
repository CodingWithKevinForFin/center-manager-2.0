package com.f1.ami.web.form;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocationListener;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntSet;

public class AmiWebEditableFormPortletManager {

	public static class Guide implements WebAbsoluteLocationListener {
		private int id;
		private boolean isVertical;
		final private WebAbsoluteLocation location;
		private FormPortlet form;

		public Guide(int id, boolean isVertical) {
			this.id = id;
			this.isVertical = isVertical;
			this.location = new WebAbsoluteLocation(this);
		}

		public void setForm(FormPortlet form) {
			if (this.form != null && form != null) {
				throw new IllegalStateException("already member of a form");
			}
			this.form = form;
		}

		public int getGuideId() {
			return id;
		}

		public boolean isVertical() {
			return isVertical;
		}

		public int getRealizedOffsetPx() {
			return this.location.getStartPxFromAlignment();
		}

		@Override
		public void onLocationChanged(WebAbsoluteLocation webAbsoluteLoc) {
			fireLayoutChanged();
		}

		private void fireLayoutChanged() {
			if (this.form != null) {
				this.form.flagLayoutChanged();
			}
		}

		public boolean setLocation(int pos) {
			byte alignment = this.location.getAlignment();
			return setLocation(pos, alignment);
		}

		/**
		 * Set position of guide from start
		 * 
		 * @param pos
		 *            - The distance in pixels from the left or top edge of a form for a vertical or horizontal guide, respectively.
		 * @return True if it is necessary to update the position of the guide on the front end.
		 */
		public boolean setLocation(int pos, byte alignment) {
			switch (alignment) {
				case WebAbsoluteLocation.ALIGN_START_LOCK:
					int startPx = this.location.getStartPx();
					if (WebAbsoluteLocation.is(startPx) && startPx == pos) {
						return false;
					}
					this.location.setStartPx(pos);
					this.location.setSizePx(0);
					return true;
				case WebAbsoluteLocation.ALIGN_END_LOCK:
					int endPx = this.location.getEndPx();
					if (WebAbsoluteLocation.is(endPx) && endPx == (this.location.getOuterSize() - pos)) {
						return false;
					}
					this.location.setEndPx(this.location.getOuterSize() - pos);
					this.location.setSizePx(0);
					return true;
				case WebAbsoluteLocation.ALIGN_CENTER_STRETCH:
					double posPct = ((double) pos) / this.location.getOuterSize();
					double startPct = this.location.getStartPct();
					if (WebAbsoluteLocation.is(startPct) && startPct == posPct) {
						return false;
					}
					this.location.setStartPct(posPct);
					this.location.setEndPct(1.0 - posPct);
					return true;
				default: // Case for new guides
					this.location.setStartPx(pos);
					this.location.setSizePx(0);
					return true;
			}
		}
		public void switchToLockStartAlign() {
			byte alignment = this.location.getAlignment();
			if (alignment == WebAbsoluteLocation.ALIGN_START_LOCK) { // Start lock
				return;
			} else if (alignment == WebAbsoluteLocation.ALIGN_END_LOCK) { // End lock
				this.location.setStartPx(this.location.getOuterSize() - this.location.getEndPx());
				this.location.clearEndPx();
			} else { // Stretch
				this.location.setStartPx(WebAbsoluteLocation.convertPctToPx(this.location.getStartPct(), this.location.getOuterSize()));
				this.location.setSizePx(0);
				this.location.clearStartPct();
				this.location.clearEndPct();
			}
		}
		public void switchToLockEndAlign() {
			byte alignment = this.location.getAlignment();
			if (alignment == WebAbsoluteLocation.ALIGN_END_LOCK) { // End lock
				return;
			} else if (alignment == WebAbsoluteLocation.ALIGN_START_LOCK) { // Start lock
				this.location.setEndPx(this.location.getOuterSize() - this.location.getStartPx());
				this.location.clearStartPx();
			} else { // Stretch
				this.location.setEndPx(WebAbsoluteLocation.convertPctToPx(this.location.getEndPct(), this.location.getOuterSize()));
				this.location.setSizePx(0);
				this.location.clearStartPct();
				this.location.clearEndPct();
			}
		}
		public void switchToRatioAlign() {
			byte alignment = this.location.getAlignment();
			if (alignment == WebAbsoluteLocation.ALIGN_CENTER_STRETCH) {
				return;
			} else if (alignment == WebAbsoluteLocation.ALIGN_START_LOCK) { // Start lock
				this.location.setStartPct(((double) this.location.getStartPx()) / this.location.getOuterSize());
				this.location.setEndPct(1.0 - this.location.getStartPct());
				this.location.clearStartPx();
				this.location.clearSizePx();
			} else if (alignment == WebAbsoluteLocation.ALIGN_END_LOCK) { // End lock
				this.location.setEndPct(((double) this.location.getEndPx()) / this.location.getOuterSize());
				this.location.setStartPct(1.0 - this.location.getEndPct());
				this.location.clearEndPx();
				this.location.clearSizePx();
			}
		}
		final public void updateFormSize() {
			this.location.setOuterSize(this.isVertical ? this.form.getWidth() : this.form.getHeight());
		}
		public byte getAlignment() {
			return this.location.getAlignment();
		}
	}

	public static class Rect {
		final private int id;
		private int x, y, w, h;
		final private String fieldId;

		public Rect(int id, String fieldId) {
			this.id = id;
			this.x = this.y = this.w = this.h = Integer.MIN_VALUE;
			this.fieldId = fieldId;
		}

		public boolean setLocation(int x, int y, int w, int h) {
			if (this.x == x && this.y == y && this.w == w && this.h == h)
				return false;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			return true;
		}
	}

	private IntKeyMap<Guide> guides = new IntKeyMap<Guide>();
	private IntKeyMap<Rect> rects = new IntKeyMap<Rect>();
	private Map<String, Rect> rectsByFieldId = new HashMap<String, AmiWebEditableFormPortletManager.Rect>();
	public static final String CALLBACK_EDITMODEMENU = "editModeMenu";
	private AmiWebEditableFormPortlet form;
	private int nextGuideId = 1;
	private JsFunction jsFunction;
	private boolean isVisible;
	private IntSet selectedRectIds = new IntSet();
	private int activeRectId = -1;
	private boolean layoutChanged = false;
	private int snapSize = -1;

	public AmiWebEditableFormPortletManager(AmiWebEditableFormPortlet form) {
		this.form = form;
		this.jsFunction = new JsFunction(form.getJsObjectName() + ".getForm().editor");
	}

	public boolean handleCallback(String callback, Map<String, String> attributes) {
		if (callback.equals("editModeSelection") || CALLBACK_EDITMODEMENU.equals(callback)) {
			String values = attributes.get("values");
			if (values != null) {
				List<String> stringToObject = (List<String>) ObjectToJsonConverter.INSTANCE_COMPACT.stringToObject(values);
				this.selectedRectIds.clear();
				for (String s : stringToObject) {
					int i = SH.parseInt(s);
					if (this.rects.containsKey(i))
						this.selectedRectIds.add(i);
				}
				this.activeRectId = CH.getOrNoThrow(Caster_Integer.INSTANCE, attributes, "active", -1);
			} else {
				clearSelectedFieldIds();
			}
		} else if (callback.equals(CALLBACK_EDITMODEMENU)) {
		} else if (callback.equals("editItem")) {
		} else if (callback.equals("setGuidePos")) {
			final int id = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "id");
			final int pos = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "pos");
			Guide g = this.guides.get(id);
			if (g.setLocation(pos)) {
				g.setLocation(pos);
			}
		} else if (callback.equals("setRectPos")) {
			final int id = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "id");
			final int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			final int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			final int w = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "w");
			final int h = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "h");
			Rect rect = this.rects.get(id);
			if (rect.setLocation(x, y, w, h)) {
				QueryField<?> queryField = AmiWebQueryFormPortlet.getQueryField(this.form.getField(rect.fieldId));
				queryField.setRealizedLeftPosPx(x);
				queryField.setRealizedWidthPx(w);
				queryField.setRealizedTopPosPx(y);
				queryField.setRealizedHeightPx(h);
				onLayoutChanged();
			}
		} else
			return false;
		return true;
	}

	public void removeGuide(int gid) {
		this.guides.remove(gid);
		onLayoutChanged();
	}

	public IntKeyMap<Guide> getGuides() {
		return guides;
	}
	public int getGuidesCount() {
		return guides.size();
	}
	public int getNextGuideId() {
		return this.nextGuideId++;
	}

	private void callJsFunction_Reset() {
		callJsFunction("reset").end();
	}
	private void callJsFunction_Repaint() {
		callJsFunction("repaint").end();
	}
	private void callJsFunction_AddGuide(Guide guide) {
		callJsFunction("addGuide").addParam(guide.id).addParam(guide.getRealizedOffsetPx()).addParam(guide.isVertical).end();
	}
	private void callJsFunction_AddRect(Rect rect) {
		boolean isSelected = this.selectedRectIds.contains(rect.fieldId);
		callJsFunction("addRect").addParam(rect.id).addParam(rect.x).addParam(rect.y).addParam(rect.w).addParam(rect.h).addParam(isSelected).end();
	}
	private void callJsFunction_removeRect(Rect rect) {
		callJsFunction("removeRect").addParam(rect.id).end();
	}
	private void callJsFunction_setSize() {
		callJsFunction("setSize").addParam(form.getWidth()).addParam(form.getHeight()).end();
	}
	private void callJsFunction_setSnap(int snap) {
		callJsFunction("setSnap").addParam(snap).addParam(form.getHeight()).end();
	}
	private void callJsFunction_setVisible(boolean v) {
		callJsFunction("setVisible").addParam(v).end();
	}

	private JsFunction callJsFunction(String functionName) {
		return this.jsFunction.reset(form.getManager().getPendingJs(), functionName);
	}

	public void initJs() {
		callJsFunction_Reset();
		for (Guide i : this.guides.values())
			callJsFunction_AddGuide(i);
		for (Rect i : this.rects.values())
			callJsFunction_AddRect(i);
		if (this.isVisible)
			callJsFunction("setVisible").addParam(true).end();
		for (int i = 0; i < getGuidesCount(); i++) {
			int gid = getGuides().getKeys()[i];
			Guide guide = getGuides().get(gid);
			callJsFunction_AddGuide(guide);
		}
		callJsFunction_Repaint();
		this.layoutChanged = false;
	}

	public void onLayoutChanged() {
		this.form.flagPendingAjax();
		layoutChanged = true;
	}
	public boolean getIsVisible() {
		return this.isVisible;
	}

	public void setIsVisible(boolean b) {
		if (this.isVisible == b)
			return;
		this.isVisible = b;
		onLayoutChanged();
	}

	public Set<String> getSelectedFieldIds() {
		Set<String> selectedFields = new HashSet<String>();
		for (int fid : this.selectedRectIds) {
			Rect rect = getRectById(fid);
			selectedFields.add(rect.fieldId);
		}
		return selectedFields;
	}

	private Rect getRectById(int fid) {
		try {
			return this.rects.getOrThrow(fid);
		} catch (Exception e) {
			throw new RuntimeException("Selected ids: " + this.selectedRectIds, e);
		}
	}

	public void clearSelectedFieldIds() {
		this.selectedRectIds.clear();
		this.activeRectId = -1;
	}
	public boolean hasSelectedFields() {
		return getSelectedFieldIds().size() > 0;
	}

	public FormPortletField<?> getActiveField() {
		if (activeRectId == -1)
			return null;
		return this.form.getField(getRectById(activeRectId).fieldId);
	}

	public Set<QueryField<?>> getSelectedFields() {
		Set<QueryField<?>> selectedFields = new HashSet<QueryField<?>>();
		for (int fid : this.selectedRectIds) {
			Rect rect = getRectById(fid);
			selectedFields.add(AmiWebQueryFormPortlet.getQueryField(this.form.getField(rect.fieldId)));
		}
		return selectedFields;
	}

	public Rect getOrCreateRect(FormPortletField<?> field) {
		Rect rect = this.rectsByFieldId.get(field.getId());
		if (rect == null) {
			rect = new Rect(nextGuideId++, field.getId());
			this.rects.put(rect.id, rect);
			this.rectsByFieldId.put(rect.fieldId, rect);
		}
		return rect;
	}

	public Rect getRect(FormPortletField<?> field) {
		return this.rectsByFieldId.get(field.getId());

	}

	public void drainJavascript() {
		if (!layoutChanged)
			return;
		callJsFunction_Reset();
		callJsFunction_setVisible(isVisible);
		if (isVisible) {
			this.callJsFunction_setSize();
			for (Guide guide : this.guides.values())
				guide.updateFormSize();
			for (int i = 0, l = this.form.getFieldsCount(); i < l; i++) {
				FormPortletField<?> field = this.form.getFieldAt(i);
				if (!field.isVisible())
					continue;
				Rect rect = getOrCreateRect(field);
				QueryField<?> queryField = AmiWebQueryFormPortlet.getQueryField(field);
				rect.setLocation(queryField.getRealizedLeftPosPx(), queryField.getRealizedTopPosPx(), queryField.getRealizedWidthPx(), queryField.getRealizedHeightPx());
				callJsFunction_AddRect(rect);
			}
			for (Guide guide : guides.values())
				callJsFunction_AddGuide(guide);
			callJsFunction_setSnap(this.snapSize);
			callJsFunction_Repaint();
		}
		layoutChanged = false;
	}

	public void updateRectangle(QueryField<?> queryField) {
		if (!queryField.getField().isVisible())
			return;
		Rect rect = this.rectsByFieldId.get(queryField.getId());
		if (rect.setLocation(queryField.getRealizedLeftPosPx(), queryField.getRealizedTopPosPx(), queryField.getRealizedWidthPx(), queryField.getRealizedHeightPx())) {
			onLayoutChanged();
		}
	}

	public int getSnapsize() {
		return this.snapSize;
	}

	public void setSnapsize(int snapSize) {
		if (this.snapSize == snapSize)
			return;
		this.snapSize = snapSize;
		onLayoutChanged();
	}

	public void addGuide(Guide guide) {
		this.guides.put(guide.id, guide);
		onLayoutChanged();
	}

	public int snapX(int x) {
		return snap(x, true);
	}
	public int snapY(int y) {
		return snap(y, false);
	}

	private int snap(int n, boolean isX) {
		if (snapSize == -1) {
			int r = n;
			int best = 20;
			for (Guide i : this.guides.values()) {
				if (i.isVertical != isX)
					continue;
				int diff = MH.diff(i.getRealizedOffsetPx(), n);
				if (diff < best) {
					r = i.getRealizedOffsetPx();
					best = diff;
				}
			}
			return r;
		} else {
			int r = MH.roundBy(n, this.snapSize, MH.ROUND_HALF_EVEN);
			int best = MH.diff(n, r);
			for (Guide i : this.guides.values()) {
				if (i.isVertical != isX)
					continue;
				int diff = MH.diff(i.getRealizedOffsetPx(), n);
				if (diff < best) {
					r = i.getRealizedOffsetPx();
					best = diff;
				}
			}
			return r;
		}
	}

	public void removeField(FormPortletField<?> field) {
		Rect rect = this.rectsByFieldId.remove(field.getId());
		if (rect != null) {
			this.rects.remove(rect.id);
			this.selectedRectIds.remove(rect.id);
			if (this.activeRectId == rect.id)
				this.activeRectId = -1;
			onLayoutChanged();
		}
	}

}
