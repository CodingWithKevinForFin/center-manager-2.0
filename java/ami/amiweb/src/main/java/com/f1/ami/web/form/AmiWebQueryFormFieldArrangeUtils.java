package com.f1.ami.web.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebQueryFormFieldArrangeUtils {
	public static Comparator<QueryField<?>> compareLeft = new Comparator<QueryField<?>>() {
		public int compare(QueryField<?> o1, QueryField<?> o2) {
			return OH.compare(o1.getRealizedLeftPosPx(), o2.getRealizedLeftPosPx());
		}
	};
	public static Comparator<QueryField<?>> compareTop = new Comparator<QueryField<?>>() {
		public int compare(QueryField<?> o1, QueryField<?> o2) {
			return OH.compare(o1.getRealizedTopPosPx(), o2.getRealizedTopPosPx());
		}
	};

	public static void createArrangeFieldsMenu(BasicWebMenu sink, boolean enabled) {
		BasicWebMenu r = new BasicWebMenu("Arrange Fields", enabled);
		sink.add(r);
		if (!enabled)
			return;
		r.add(new BasicWebMenuLink("Stack Horizontally", true, "arrange_sx_"));
		r.add(new BasicWebMenuLink("Stack Vertically", true, "arrange_sy_"));
		r.add(new BasicWebMenuLink("Distribute Horizontally", true, "arrange_dx_"));
		r.add(new BasicWebMenuLink("Distribute Vertically", true, "arrange_dy_"));
		r.add(new BasicWebMenuDivider());
		r.add(new BasicWebMenuLink("Align Left", true, "arrange_al_"));
		r.add(new BasicWebMenuLink("Align Center", true, "arrange_ac_"));
		r.add(new BasicWebMenuLink("Align Right", true, "arrange_ar_"));
		r.add(new BasicWebMenuDivider());
		r.add(new BasicWebMenuLink("Align Top", true, "arrange_at_"));
		r.add(new BasicWebMenuLink("Align Middle", true, "arrange_am_"));
		r.add(new BasicWebMenuLink("Align Bottom", true, "arrange_ab_"));
	}

	public static void onArrangeMenuItem(String id, QueryField<?> active, Set<QueryField<?>> selected) {
		String action = SH.stripPrefix(id, "arrange_", true);
		if (action.startsWith("al_")) {
			int left = active.getRealizedLeftPosPx();
			AmiWebQueryFormFieldArrangeUtils.alignLeft(selected, left);
		} else if (action.startsWith("ac_")) {
			int center = active.getRealizedLeftPosPx() + (active.getRealizedWidthPx() / 2);
			AmiWebQueryFormFieldArrangeUtils.alignCenter(selected, center);
		} else if (action.startsWith("ar_")) {
			int right = active.getRealizedLeftPosPx() + active.getRealizedWidthPx();
			AmiWebQueryFormFieldArrangeUtils.alignRight(selected, right);
		} else if (action.startsWith("at_")) {
			int top = active.getRealizedTopPosPx();
			AmiWebQueryFormFieldArrangeUtils.alignTop(selected, top);
		} else if (action.startsWith("am_")) {
			int middle = active.getRealizedTopPosPx() + (active.getRealizedHeightPx() / 2);
			AmiWebQueryFormFieldArrangeUtils.alignMiddle(selected, middle);
		} else if (action.startsWith("ab_")) {
			int bottom = active.getRealizedTopPosPx() + active.getRealizedHeightPx();
			AmiWebQueryFormFieldArrangeUtils.alignBottom(selected, bottom);
		} else if (action.startsWith("dx_")) {
			AmiWebQueryFormFieldArrangeUtils.distributeX(selected);
		} else if (action.startsWith("dy_")) {
			AmiWebQueryFormFieldArrangeUtils.distributeY(selected);
		} else if (action.startsWith("sx_")) {
			AmiWebQueryFormFieldArrangeUtils.stackX(selected);
		} else if (action.startsWith("sy_")) {
			AmiWebQueryFormFieldArrangeUtils.stackY(selected);
		}
	}

	public static void alignLeft(Collection<QueryField<?>> fields, int left) {
		for (QueryField<?> field : fields) {
			field.setRealizedLeftPosPx(left);
		}
	}
	public static void alignRight(Collection<QueryField<?>> fields, int right) {
		for (QueryField<?> field : fields) {
			int left = right - field.getRealizedWidthPx();
			field.setRealizedLeftPosPx(left);
		}
	}
	public static void alignCenter(Collection<QueryField<?>> fields, int center) {
		for (QueryField<?> field : fields) {
			int left = center - (field.getRealizedWidthPx() / 2);
			field.setRealizedLeftPosPx(left);
		}
	}
	public static void alignTop(Collection<QueryField<?>> fields, int top) {
		for (QueryField<?> field : fields) {
			field.setRealizedTopPosPx(top);
		}
	}
	public static void alignBottom(Collection<QueryField<?>> fields, int bottom) {
		for (QueryField<?> field : fields) {
			int top = bottom - field.getRealizedHeightPx();
			field.setRealizedTopPosPx(top);
		}
	}
	public static void alignMiddle(Collection<QueryField<?>> fields, int middle) {
		for (QueryField<?> field : fields) {
			int top = middle - (field.getRealizedHeightPx() / 2);
			field.setRealizedTopPosPx(top);
		}
	}
	public static void stackX(Collection<QueryField<?>> fields) {
		int size = fields.size();
		if (size > 1) {
			List<QueryField<?>> sorted = new ArrayList<QueryField<?>>(size);
			sorted.addAll(fields);
			Collections.sort(sorted, compareLeft);
			boolean even = size % 2 == 0;

			int centerField = even ? (size / 2) - 1 : (size / 2);
			int rightOfCenter = sorted.get(centerField).getRealizedLeftPosPx() + sorted.get(centerField).getRealizedWidthPx();
			int start = even ? rightOfCenter : ((rightOfCenter + sorted.get(centerField + 1).getRealizedLeftPosPx())) / 2;

			//Place fields to the left of the start position
			int pos = start;
			for (int i = centerField; i >= 0; i--) {
				QueryField<?> field = sorted.get(i);
				pos -= field.getRealizedWidthPx();
				field.setRealizedLeftPosPx(pos);
			}

			pos = start;
			for (int i = centerField + 1; i < size; i++) {
				QueryField<?> field = sorted.get(i);
				field.setRealizedLeftPosPx(pos);
				pos += field.getRealizedWidthPx();
			}
		}
	}
	public static void stackY(Collection<QueryField<?>> fields) {
		int size = fields.size();
		if (size > 1) {
			List<QueryField<?>> sorted = new ArrayList<QueryField<?>>(size);
			sorted.addAll(fields);
			Collections.sort(sorted, compareTop);
			boolean even = size % 2 == 0;

			int centerField = even ? (size / 2) - 1 : (size / 2);
			int bottomOfMiddle = sorted.get(centerField).getRealizedTopPosPx() + sorted.get(centerField).getRealizedHeightPx();
			int start = even ? bottomOfMiddle : ((bottomOfMiddle + sorted.get(centerField + 1).getRealizedTopPosPx())) / 2;

			//Place fields to the left of the start position
			int pos = start;
			for (int i = centerField; i >= 0; i--) {
				QueryField<?> field = sorted.get(i);
				pos -= field.getRealizedHeightPx();
				field.setRealizedTopPosPx(pos);
			}

			pos = start;
			for (int i = centerField + 1; i < size; i++) {
				QueryField<?> field = sorted.get(i);
				field.setRealizedTopPosPx(pos);
				pos += field.getRealizedHeightPx();
			}
		}
	}
	public static void distributeX(Collection<QueryField<?>> fields) {
		int size = fields.size();
		if (size > 1) {
			List<QueryField<?>> sorted = new ArrayList<QueryField<?>>(size);
			sorted.addAll(fields);
			Collections.sort(sorted, compareLeft);
			int totalWidth = 0;
			for (int i = 0; i < size; i++) {
				totalWidth += sorted.get(i).getRealizedWidthPx();
			}
			int left = sorted.get(0).getRealizedLeftPosPx();
			int right = sorted.get(size - 1).getRealizedLeftPosPx() + sorted.get(size - 1).getRealizedWidthPx();

			double spacing = (double) ((right - left) - totalWidth) / (size - 1);

			//Place fields
			double pos = left;
			for (int i = 0; i < size; i++) {
				QueryField<?> field = sorted.get(i);
				field.setRealizedLeftPosPx((int) Math.round(pos));
				pos += (double) field.getRealizedWidthPx() + spacing;
			}
		}
	}
	public static void distributeY(Collection<QueryField<?>> fields) {
		int size = fields.size();
		if (size > 1) {
			List<QueryField<?>> sorted = new ArrayList<QueryField<?>>(size);
			sorted.addAll(fields);
			Collections.sort(sorted, compareTop);
			int totalHeight = 0;
			for (int i = 0; i < size; i++) {
				totalHeight += sorted.get(i).getRealizedHeightPx();
			}
			int top = sorted.get(0).getRealizedTopPosPx();
			int bottom = sorted.get(size - 1).getRealizedTopPosPx() + sorted.get(size - 1).getRealizedHeightPx();

			double spacing = (double) ((bottom - top) - totalHeight) / (size - 1);

			//Place fields
			double pos = top;
			for (int i = 0; i < size; i++) {
				QueryField<?> field = sorted.get(i);
				field.setRealizedTopPosPx((int) Math.round(pos));
				pos += (double) field.getRealizedHeightPx() + spacing;
			}
		}
	}

}
