package com.f1.ami.web.style.impl;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.base.Caster;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.utils.CasterManager;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.ToDerivedString;

public class AmiWebStyleOption implements Comparable<AmiWebStyleOption>, Lockable, ToDerivedString {

	private String groupLabel;
	private String label;
	final private short key;
	final private String saveKey;
	final private byte type;
	private String namespace;
	private String variableName;
	private String description;
	private int fieldWidth = -1;
	private int fieldHeight = -1;
	private boolean locked;

	public AmiWebStyleOption(short key, String saveKey, String namespace, String groupLabel, String label, byte type) {
		this.key = key;
		this.saveKey = saveKey;
		this.namespace = namespace;
		this.groupLabel = groupLabel;
		this.label = label;
		OH.assertFalse(this.label.contains(":"));
		this.type = type;
		this.variableName = SH.replaceAll(namespace, ' ', "") + SH.uppercaseFirstChar(saveKey);
		this.description = namespace + "==>" + groupLabel + "==>" + label;
	}

	public String getVarname() {
		return this.variableName;
	}
	public String getNamespace() {
		return this.namespace;
	}
	public AmiWebStyleOption setNamespace(String ns) {
		LockedException.assertNotLocked(this);
		this.namespace = ns;
		this.variableName = SH.replaceAll(namespace, ' ', "") + SH.uppercaseFirstChar(saveKey);
		this.description = namespace + "==>" + groupLabel + "==>" + label;
		return this;
	}
	public AmiWebStyleOption setGroupLabel(String groupLabel) {
		LockedException.assertNotLocked(this);
		this.groupLabel = groupLabel;
		this.description = namespace + "==>" + groupLabel + "==>" + label;
		return this;
	}
	public AmiWebStyleOption setLabel(String label) {
		LockedException.assertNotLocked(this);
		this.label = label;
		this.description = namespace + "==>" + groupLabel + "==>" + label;
		return this;
	}

	public AmiWebStyleOption setFieldSize(int w, int h) {
		LockedException.assertNotLocked(this);
		this.fieldWidth = w;
		this.fieldHeight = h;
		return this;
	}

	public String getGroupLabel() {
		return groupLabel;
	}

	public String getLabel() {
		return label;
	}

	public short getKey() {
		return key;
	}

	public String getSaveKey() {
		return saveKey;
	}

	public byte getType() {
		return type;
	}

	public int getWidth() {
		return this.fieldWidth;
	}
	public int getHeight() {
		return this.fieldHeight;
	}

	@Override
	public int compareTo(AmiWebStyleOption o) {
		return OH.compare(variableName, o.variableName);
	}

	@Override
	public String toString() {
		return variableName;
	}

	@Override
	public int hashCode() {
		return OH.hashCode(this.variableName);
	}
	public String getDescription() {
		return this.description;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		return o.getClass() == getClass() && OH.eq(variableName, ((AmiWebStyleOption) o).variableName);
	}

	public Object toExportValue(AmiWebService service, Object value) {
		switch (type) {
			case AmiWebStyleConsts.TYPE_COLOR_GRADIENT:
				if (value instanceof ColorGradient)
					return value.toString();
			default:
				return value;
		}
	}
	public Object toAmiscriptValue(AmiWebService service, Object value) {
		return value;
	}

	//returns null if it can't be safely converted
	public Object toInternalStorageValue(AmiWebService service, Object value) {
		if (value instanceof String && SH.startsWith((String) value, '$'))
			return value;
		switch (type) {
			case AmiWebStyleConsts.TYPE_COLOR_GRADIENT: {
				if (value instanceof ColorGradient)
					return new ColorGradient(0, 01, (ColorGradient) value);
				if (value instanceof String) {
					String s = (String) value;
					if (ColorHelper.isColor(s))
						return new ColorGradient(0, 1, "").addStop(.5, ColorHelper.parseRgb(s));
					else
						return new ColorGradient(0, 1, (String) value);
				}
			}
			case AmiWebStyleConsts.TYPE_COLOR: {
				long c = ColorHelper.parseRgbNoThrow(OH.toString(value));
				return c == ColorHelper.NO_COLOR ? null : ColorHelper.toString((int) c);
			}
			case AmiWebStyleConsts.TYPE_COLOR_ARRAY: {
				if (value instanceof List) {
					List<Object> vlist = (List<Object>) value;
					List<String> parts = new ArrayList<String>(vlist.size());
					for (int i = 0; i < vlist.size(); i++) {
						long c = ColorHelper.parseRgbNoThrow(AmiUtils.s(vlist.get(i)));
						if (c == ColorHelper.NO_COLOR)
							return null;
						parts.add(ColorHelper.toString((int) c));
					}
					return parts;
				} else {
					String s = AmiUtils.s(value);
					List<String> parts = SH.splitToList(",", s);
					for (int i = 0; i < parts.size(); i++) {
						long c = ColorHelper.parseRgbNoThrow(parts.get(i));
						if (c == ColorHelper.NO_COLOR)
							return null;
						parts.set(i, ColorHelper.toString((int) c));
					}
					return parts;
				}
			}

			case AmiWebStyleConsts.TYPE_FONT:
				return service.getFontsManager().findFont(AmiUtils.s(value));

			case AmiWebStyleConsts.TYPE_CSS_CLASS:
				if ("".equals(value))
					return "";
				return value;
			//				return service.getCustomCssManager().getClassNames().contains(value) ? value : null;

			case AmiWebStyleConsts.TYPE_ENUM:
			case AmiWebStyleConsts.TYPE_NUMBER:
			case AmiWebStyleConsts.TYPE_BOOLEAN:
			default:
				throw new IllegalStateException("Unsupported type: " + type);
		}
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return this.locked;
	}

	@Override
	public String toDerivedString() {
		return toDerivedString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append("STYLE_OPTION[").append(getFormattedType()).append(' ').append(this.variableName).append("]");
	}

	public String getFormattedType() {
		switch (getType()) {
			case AmiWebStyleConsts.TYPE_COLOR:
				return "COLOR";
			case AmiWebStyleConsts.TYPE_BOOLEAN:
				return "BOOLEAN";
			case AmiWebStyleConsts.TYPE_COLOR_ARRAY:
				return "COLOR";
			case AmiWebStyleConsts.TYPE_COLOR_GRADIENT:
				return "COLOR_GRADIENT";
			case AmiWebStyleConsts.TYPE_CSS_CLASS:
				return "CSS_CLASS";
			case AmiWebStyleConsts.TYPE_ENUM:
				return "ENUM";
			case AmiWebStyleConsts.TYPE_FONT:
				return "FONT";
			case AmiWebStyleConsts.TYPE_NUMBER:
				return "INTEGER";
			default:
				return "UNKNOWN_" + SH.toString(getType());
		}
	}

	public AmiWebStyleOption copy() {
		AmiWebStyleOption r = new AmiWebStyleOption(getKey(), getSaveKey(), getNamespace(), getGroupLabel(), getLabel(), getType());
		copyFields(r);
		return r;
	}

	protected void copyFields(AmiWebStyleOption target) {
		target.setFieldSize(this.getWidth(), this.getHeight());
	}

	public Caster<?> getCaster() {
		switch (getType()) {
			case AmiWebStyleConsts.TYPE_COLOR_ARRAY:
				return CasterManager.getCaster(List.class);
			case AmiWebStyleConsts.TYPE_COLOR:
			case AmiWebStyleConsts.TYPE_COLOR_GRADIENT:
			case AmiWebStyleConsts.TYPE_CSS_CLASS:
			case AmiWebStyleConsts.TYPE_FONT:
				return Caster_String.INSTANCE;
			default:
				throw new RuntimeException("Bad type: " + getType());
		}
	}

}
