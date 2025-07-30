package com.f1.ami.web.diff;

import java.util.List;
import java.util.Map;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.utils.SH;

public class AmiWebJsonDiffTreeFormatters {

	public static final String COLOR_BASE = "#666600";
	public static final String COLOR_THIERS = "#880088";
	public static final String COLOR_YOURS = "#008800";
	public static final String COLOR_RESULT = "#0000AA";
	public static final String COLOR_MANUAL = "#0000AA";
	public static final String COLOR_DISABLED = "#AAAAAA";

	public static class ChoiceFormatter implements WebTreeNodeFormatter {

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			AmiWebJsonDiffNode<?> dn = (AmiWebJsonDiffNode<?>) node.getData();
			if (dn == null)
				return;
			if (dn.getSameness() == AmiWebJsonDiffNode.SAME_AL && dn.getUserChoice() != AmiWebJsonDiffNode.STATE_MANUAL && dn.getUserChoice() != AmiWebJsonDiffNode.STATE_DISABLED
					&& dn.getUserChoice() != AmiWebJsonDiffNode.STATE_MERGE) {
				sink.append("no change");
				style.append("_fg=" + COLOR_DISABLED);
			} else {
				switch (dn.getUserChoice()) {
					case AmiWebJsonDiffNode.STATE_BASE:
						sink.append("base");
						style.append("_fg=" + COLOR_BASE);
						break;
					case AmiWebJsonDiffNode.STATE_LEFT:
						sink.append("theirs");
						style.append("_fg=" + COLOR_THIERS);
						break;
					case AmiWebJsonDiffNode.STATE_RIGHT:
						sink.append("yours");
						style.append("_fg=" + COLOR_YOURS);
						break;
					case AmiWebJsonDiffNode.STATE_MANUAL:
						sink.append("edit");
						style.append("_fg=" + COLOR_MANUAL);
						break;
					case AmiWebJsonDiffNode.STATE_DISABLED:
						sink.append("disabled");
						style.append("_fg=" + COLOR_DISABLED);
						break;
					case AmiWebJsonDiffNode.STATE_MERGE:
						sink.append("merge");
						style.append("_fg=" + COLOR_MANUAL);
						break;
				}
			}
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiWebJsonDiffNode<?> dn = (AmiWebJsonDiffNode<?>) node.getData();
			switch (dn.getUserChoice()) {
				case AmiWebJsonDiffNode.STATE_BASE:
					sink.append("base");
					break;
				case AmiWebJsonDiffNode.STATE_LEFT:
					sink.append("theirs");
					break;
				case AmiWebJsonDiffNode.STATE_RIGHT:
					sink.append("yours");
					break;
				case AmiWebJsonDiffNode.STATE_MANUAL:
					sink.append("edit");
					break;
				case AmiWebJsonDiffNode.STATE_DISABLED:
					sink.append("disabled");
					break;
				case AmiWebJsonDiffNode.STATE_MERGE:
					sink.append("merge");
					break;
			}
		}

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return 0;
		}

		@Override
		public Object getValue(WebTreeNode node) {
			AmiWebJsonDiffNode<?> dn = (AmiWebJsonDiffNode<?>) node.getData();
			return dn.getUserChoice();
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return formatToText(getValue(node));
		}

		@Override
		public String formatToText(Object data) {
			return null;
		}
	}

	public static class SideFormatter implements WebTreeNodeFormatter {

		private int pos;

		public SideFormatter(int i) {
			this.pos = i;
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			AmiWebJsonDiffNode<?> dn = (AmiWebJsonDiffNode<?>) node.getData();
			if (dn == null)
				return;
			Object value;
			byte pc = dn.getParent() == null ? AmiWebJsonDiffNode.STATE_MERGE : dn.getParent().getUserChoiceMaterialized();
			switch (pos) {
				case 0:
					value = dn.getOrig();
					break;
				case 1:
					value = dn.getLeft();
					break;
				case 2:
					value = dn.getRight();
					break;
				case 3:
					value = dn.getOut();
					break;
				default:
					throw new IllegalStateException();

			}
			if (value instanceof Map) {
				return;
				//				Map<String, Object> m = (Map<String, Object>) value;
				//				sink.append("{...}");
			} else if (value instanceof List) {
				return;
				//				List<Object> l = (List<Object>) value;
				//				sink.append("[...]");
			} else if (value instanceof String)
				SH.doubleQuote((String) value, sink);
			else if (value != null)
				sink.append(value);
			final boolean isDiff;
			switch (pos) {
				case 0:
					isDiff = dn.getSameness() == AmiWebJsonDiffNode.SAME_LR || dn.getSameness() == 0;
					break;
				case 1:
					isDiff = dn.getSameness() == AmiWebJsonDiffNode.SAME_BR || dn.getSameness() == 0;
					break;
				case 2:
					isDiff = dn.getSameness() == AmiWebJsonDiffNode.SAME_BL || dn.getSameness() == 0;
					break;
				case 3:
					isDiff = false;//dn.isSame() != AmiWebJsonDiffNode.SAME_AL;
					break;
				default:
					throw new IllegalStateException();
			}
			if (dn.getSameness() == AmiWebJsonDiffNode.SAME_AL && dn.getUserChoice() != AmiWebJsonDiffNode.STATE_MANUAL && dn.getUserChoice() != AmiWebJsonDiffNode.STATE_DISABLED
					&& dn.getUserChoice() != AmiWebJsonDiffNode.STATE_MERGE) {
				style.append("_fg=" + COLOR_DISABLED);
			} else
				switch (pos) {
					case 0:
						style.append("_fg=" + COLOR_BASE);
						break;
					case 1:
						style.append("_fg=" + COLOR_THIERS);
						break;
					case 2:
						style.append("_fg=" + COLOR_YOURS);
						break;
					case 3:
						switch (dn.getUserChoiceMaterialized()) {
							case AmiWebJsonDiffNode.STATE_LEFT:
								style.append("_fg=" + COLOR_THIERS);
								break;
							case AmiWebJsonDiffNode.STATE_RIGHT:
								style.append("_fg=" + COLOR_YOURS);
								break;
							case AmiWebJsonDiffNode.STATE_BASE:
								style.append("_fg=" + COLOR_BASE);
								break;
							default:
								style.append("_fg=" + COLOR_RESULT);
								break;
						}
						break;
				}
			if (isDiff)
				style.append("|_bg=#ffffAA88");
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			sink.append(getValue(node));
		}

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return 0;
		}

		@Override
		public Object getValue(WebTreeNode node) {
			AmiWebJsonDiffNode<?> dn = (AmiWebJsonDiffNode<?>) node.getData();
			if (dn == null)
				return null;
			Object value = pos == 0 ? dn.getOrig() : pos == 1 ? dn.getLeft() : dn.getRight();
			if (value instanceof Map) {
				Map<String, Object> m = (Map<String, Object>) value;
				return "{...}";
			}
			if (value instanceof List) {
				List<Object> l = (List<Object>) value;
				return "[...]";
			}
			return value;
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return data.toString();
		}

	}

}
