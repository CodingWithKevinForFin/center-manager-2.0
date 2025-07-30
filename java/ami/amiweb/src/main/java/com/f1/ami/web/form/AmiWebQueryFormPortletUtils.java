package com.f1.ami.web.form;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.web.form.AmiWebEditableFormPortletManager.Guide;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.http.HttpUtils;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.ComparableComparator;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.IntKeyMap;

public class AmiWebQueryFormPortletUtils {

	public static Guide getClickedGuide(AmiWebEditableFormPortlet queryForm, int formX, int formY) {
		AmiWebEditableFormPortletManager editableManager = queryForm.getEditableManager();
		IntKeyMap<Guide> guidesIntKeyMap = editableManager.getGuides();
		int[] guidesKeys = guidesIntKeyMap.getKeys();
		Guide g;
		boolean isVertical;
		final int tolerance = 7; // So that the user doesn't have to click precisely on the guide
		for (int i = 0; i < editableManager.getGuidesCount(); i++) {
			g = guidesIntKeyMap.get(guidesKeys[i]);
			isVertical = g.isVertical();
			if ((isVertical && formX >= g.getRealizedOffsetPx() - tolerance && formX <= g.getRealizedOffsetPx() + tolerance)
					|| (!isVertical && formY >= g.getRealizedOffsetPx() - tolerance && formY <= g.getRealizedOffsetPx() + tolerance)) {
				return g;
			}
		}
		return null;
	}

	public static Guide getClickedGuide(AmiWebEditableFormPortlet queryForm, MouseEvent mouseEvent) {
		int formX = mouseEvent.getMouseX() + queryForm.getClipLeft() - PortletHelper.getAbsoluteLeft(queryForm);
		int formY = mouseEvent.getMouseY() + queryForm.getClipTop() - PortletHelper.getAbsoluteTop(queryForm);
		return AmiWebQueryFormPortletUtils.getClickedGuide(queryForm, formX, formY);
	}

	public static QueryField<?> getClickedField(AmiWebEditableFormPortlet queryForm, int formX, int formY) {
		QueryField<?> q;
		for (int i = 0; i < queryForm.getFieldsCount(); i++) {
			q = AmiWebQueryFormPortlet.getQueryField(queryForm.getFieldAt(i));
			if (q.isInsideFieldPx(formX, formY))
				return q;
		}
		return null;
	}
	public static QueryField<?> getClickedField(AmiWebEditableFormPortlet queryForm, MouseEvent mouseEvent) {
		return getClickedField(queryForm, mouseEvent.getMouseX() + queryForm.getClipLeft() - PortletHelper.getAbsoluteLeft(queryForm),
				mouseEvent.getMouseY() + queryForm.getClipTop() - PortletHelper.getAbsoluteTop(queryForm));
	}

	public static FormPortletField<?> getClickedFormField(FormPortlet form, int formX, int formY) {
		FormPortletField<?> f;
		for (int i = 0; i < form.getFieldsCount(); i++) {
			f = form.getFieldAt(i);
			if (f.isInsideFieldPx(formX, formY))
				return f;
		}
		return null;
	}
	public static FormPortletField<?> getClickedFormField(FormPortlet form, MouseEvent mouseEvent) {
		return getClickedFormField(form, mouseEvent.getMouseX() + form.getClipLeft() - PortletHelper.getAbsoluteLeft(form),
				mouseEvent.getMouseY() + form.getClipTop() - PortletHelper.getAbsoluteTop(form));
	}

	public static void moveFieldZIndex(AmiWebQueryFormPortlet form, Set<QueryField<?>> selected, String action) {
		int selectedSize = selected.size();
		if (selectedSize == 0)
			return;
		IndexedList<String, Integer> fieldsZIndexes = form.getFieldZIndexes();
		int size = fieldsZIndexes.getSize();
		if (selectedSize == size)
			return;

		Map<String, QueryField<?>> fieldsById = form.getFieldsById();
		if ("front".equals(action)) {
			BasicIndexedList<String, Integer> selectedFields = new BasicIndexedList<String, Integer>();
			BasicIndexedList<String, Integer> sortedFields = new BasicIndexedList<String, Integer>();
			for (QueryField<?> field : selected) {
				String fldId = field.getId();
				selectedFields.add(fldId, field.getField().getZIndex());
			}
			selectedFields.sortByValues(ComparableComparator.instance(Integer.class));
			int sortedSize = 0;
			for (int i = 0; i < fieldsZIndexes.getSize(); i++) {
				String key = fieldsZIndexes.getKeyAt(i);
				if (selectedFields.containsKey(key))
					continue;
				sortedSize++;
				sortedFields.add(key, sortedSize);
				fieldsById.get(key).getField().setZIndex(sortedSize);
			}
			for (int i = 0; i < selectedFields.getSize(); i++) {
				String key = selectedFields.getKeyAt(i);
				sortedSize++;
				sortedFields.add(key, sortedSize);
				fieldsById.get(key).getField().setZIndex(sortedSize);
			}
			form.setFieldZIndexes(sortedFields);
			selectedFields.clear();
		} else if ("back".equals(action)) {
			BasicIndexedList<String, Integer> selectedFields = new BasicIndexedList<String, Integer>();
			BasicIndexedList<String, Integer> sortedFields = new BasicIndexedList<String, Integer>();
			for (QueryField<?> field : selected) {
				String fldId = field.getId();
				selectedFields.add(fldId, field.getField().getZIndex());
			}
			selectedFields.sortByValues(ComparableComparator.instance(Integer.class));
			int sortedSize = 0;
			for (int i = 0; i < selectedFields.getSize(); i++) {
				String key = selectedFields.getKeyAt(i);
				sortedSize++;
				sortedFields.add(key, sortedSize);
				fieldsById.get(key).getField().setZIndex(sortedSize);
			}
			for (int i = 0; i < fieldsZIndexes.getSize(); i++) {
				String key = fieldsZIndexes.getKeyAt(i);
				if (selectedFields.containsKey(key))
					continue;
				sortedSize++;
				sortedFields.add(key, sortedSize);
				fieldsById.get(key).getField().setZIndex(sortedSize);
			}
			form.setFieldZIndexes(sortedFields);
			selectedFields.clear();
		} else if ("forward".equals(action)) {
			Map<Integer, String> selectedFields = new HashMap<Integer, String>();
			for (QueryField<?> field : selected) {
				String fldName = field.getName();
				selectedFields.put(field.getField().getZIndex(), fldName);
			}

			BasicIndexedList<String, Integer> sortedFields = new BasicIndexedList<String, Integer>();
			String a = null;
			String b = null;
			boolean orderChanged = false;
			for (int i = size - 1; i > 0; i--) {
				if (!orderChanged)
					a = fieldsZIndexes.getKeyAt(i);
				b = fieldsZIndexes.getKeyAt(i - 1);

				if (selectedFields.containsValue(a)) {
					sortedFields.add(a, i + 1);
					orderChanged = false;
				} else if (selectedFields.containsValue(b)) {
					sortedFields.add(b, i + 1);
					orderChanged = true;
				} else {
					sortedFields.add(a, i + 1);
					orderChanged = false;
				}
				if (i - 1 == 0)
					if (orderChanged)
						sortedFields.add(a, i);
					else
						sortedFields.add(b, i);
			}
			sortedFields.sortByValues(ComparableComparator.instance(Integer.class));
			form.setFieldZIndexes(sortedFields);
			for (int i = 0; i < size; i++) {
				fieldsById.get(fieldsZIndexes.getKeyAt(i)).getField().setZIndex(fieldsZIndexes.getAt(i));
			}
			selectedFields.clear();
		} else if ("backward".equals(action)) {
			Map<Integer, String> selectedFields = new HashMap<Integer, String>();
			for (QueryField<?> field : selected) {
				String fldName = field.getName();
				selectedFields.put(field.getField().getZIndex(), fldName);
			}

			BasicIndexedList<String, Integer> sortedFields = new BasicIndexedList<String, Integer>();
			String a = null;
			String b = null;
			boolean orderChanged = false;
			for (int i = 0; i < size - 1; i++) {
				if (!orderChanged)
					a = fieldsZIndexes.getKeyAt(i);
				b = fieldsZIndexes.getKeyAt(i + 1);

				if (selectedFields.containsValue(a)) {
					sortedFields.add(a, i + 1);
					orderChanged = false;
				} else if (selectedFields.containsValue(b)) {
					sortedFields.add(b, i + 1);
					orderChanged = true;
				} else {
					sortedFields.add(a, i + 1);
					orderChanged = false;
				}
				if (i + 1 == size - 1)
					if (orderChanged)
						sortedFields.add(a, i + 2);
					else
						sortedFields.add(b, i + 2);
			}
			form.setFieldZIndexes(sortedFields);
			for (int i = 0; i < size; i++) {
				fieldsById.get(fieldsZIndexes.getKeyAt(i)).getField().setZIndex(fieldsZIndexes.getAt(i));
			}
			selectedFields.clear();
		}
	}
	public static void pasteFields(AmiWebQueryFormPortlet form, int formX, int formY) {
		formX = form.getEditableForm().getEditableManager().snapX(formX);
		formY = form.getEditableForm().getEditableManager().snapX(formY);
		QueryField<?> clickedField = form.getClipboardClickedField();
		int clickedFieldX = clickedField.getRealizedLeftPosPx();
		int clickedFieldY = clickedField.getRealizedTopPosPx();
		for (QueryField<?> existingQueryField : form.getClipboardFields()) {
			FormPortletField<?> f = existingQueryField.getField();
			String newLabel = SH.getNextId(f.getTitle(), form.getQueryFieldLabels());
			String newVarName = SH.getNextId(existingQueryField.getName(), form.getQueryFieldNames());
			AmiWebFormFieldFactory<?> factory = form.getService().getFormFieldFactory(existingQueryField.getFactory().getEditorTypeId());
			HashMap<String, Object> json = new HashMap<String, Object>();
			existingQueryField.getJson(json);
			QueryField<?> newQueryField = factory.createQueryField(form);
			newQueryField.init(json);
			newQueryField.setVarName(newVarName);
			newQueryField.updateAri();
			newQueryField.addToDomManager();
			newQueryField.getField().setTitle(newLabel);

			newQueryField = form.addQueryField(newQueryField, true);

			newQueryField.updateFormSize();
			int newFieldRealizedWidthPx = existingQueryField.getRealizedWidthPx();
			int newFieldRealizedHeightPx = existingQueryField.getRealizedHeightPx();
			byte existingHorizAlign = existingQueryField.getHorizontalPosAlignment();
			byte existingVertAlign = existingQueryField.getVerticalPosAlignment();
			if (existingQueryField == clickedField) {
				newQueryField.setRealizedHorizontalPosPx(formX, newFieldRealizedWidthPx, existingHorizAlign);
				newQueryField.setRealizedVerticalPosPx(formY, newFieldRealizedHeightPx, existingVertAlign);
			} else {
				int deltaX = existingQueryField.getRealizedLeftPosPx() - clickedFieldX;
				int deltaY = existingQueryField.getRealizedTopPosPx() - clickedFieldY;
				newQueryField.setRealizedHorizontalPosPx(formX + deltaX, newFieldRealizedWidthPx, existingHorizAlign);
				newQueryField.setRealizedVerticalPosPx(formY + deltaY, newFieldRealizedHeightPx, existingVertAlign);
			}
			newQueryField.onInitDone();
		}
	}
	public static void copyPositioningToQueryField(FormPortletField<?> source, QueryField<?> target) {
		target.setWidthPx(source.getWidthPx());
		target.setLeftPosPx(source.getLeftPosPx());
		target.setRightPosPx(source.getRightPosPx());
		target.setWidthPct(source.getWidthPct());
		target.setLeftPosPct(source.getLeftPosPct());
		target.setRightPosPct(source.getRightPosPct());
		target.setHorizontalOffsetFromCenterPct(source.getHorizontalOffsetFromCenterPct());

		target.setHeightPx(source.getHeightPx());
		target.setTopPosPx(source.getTopPosPx());
		target.setBottomPosPx(source.getBottomPosPx());
		target.setHeightPct(source.getHeightPct());
		target.setTopPosPct(source.getTopPosPct());
		target.setBottomPosPct(source.getBottomPosPct());
		target.setVerticalOffsetFromCenterPct(source.getVerticalOffsetFromCenterPct());
	}

	public static boolean isMouseEventInsideForm(FormPortlet form, MouseEvent mouseEvent) {
		int formAbsLeft = PortletHelper.getAbsoluteLeft(form);
		int formAbsRight = form.getWidth() + formAbsLeft;
		int formAbsTop = PortletHelper.getAbsoluteTop(form);
		int formAbsBottom = form.getHeight() + formAbsTop;
		int mouseX = mouseEvent.getMouseX();
		int mouseY = mouseEvent.getMouseY();
		return mouseX > formAbsLeft && mouseX < formAbsRight && mouseY > formAbsTop && mouseY < formAbsBottom;
	}

	public static boolean getMapFromValuesField(LinkedHashMap<String, String> sink, FormPortletTextAreaField valuesField) {
		BasicCharMatcher EQ_COMMA = new BasicCharMatcher("=,\n\r", true);
		BasicCharMatcher COMMA = new BasicCharMatcher(",\n\r", true);
		String value = valuesField.getValue();
		if (value == null || value.length() == 0)
			return true;
		//		value = WebHelper.escapeHtml(value);
		StringCharReader scr = new StringCharReader(value);
		scr.setStrictEscape(false);
		StringBuilder sb = new StringBuilder();
		String key = null;
		for (;;) {
			scr.readUntilAnySkipEscaped(key == null ? EQ_COMMA : COMMA, '\\', sb);
			int c = scr.readCharOrEof();
			switch (c) {
				case '\n':
					continue;
				case '\r':
					continue;
				case '=':
					key = SH.toStringAndClear(sb);
					break;
				case ',':
				case CharReader.EOF:
					String val = SH.toStringAndClear(sb);
					sink.put(key == null ? val : key, val);
					key = null;
					if (c == CharReader.EOF)
						return true;
			}

		}
	}
	public static void showCustomCssDialog(String text, PortletManager portletManager) {
		portletManager.showAlert("<div style='font-family:monospace;font-size:12px;text-align:left;width:100%;height:100%;display:flex;justify-content:center;'><div>"
				+ HttpUtils.escapeHtml(text, 0, text.length(), true, "<BR>", new StringBuilder()).toString() + "</div></div>");
	}

	public static String getValuesFieldFromMap(Map<String, String> options) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Entry<String, String> i : options.entrySet()) {
			if (first)
				first = false;
			else
				sb.append(",");
			String k = SH.toStringEncode(i.getKey(), (char) 0);
			k = SH.replaceAll(k, "=", "\\=");
			k = SH.replaceAll(k, ",", "\\,");
			String v = SH.toStringEncode(i.getValue(), (char) 0);
			v = SH.replaceAll(v, "=", "\\=");
			v = SH.replaceAll(v, ",", "\\,");
			sb.append(k);
			if (OH.ne(k, v))
				sb.append('=').append(v);
		}
		return sb.toString();
	}
}
