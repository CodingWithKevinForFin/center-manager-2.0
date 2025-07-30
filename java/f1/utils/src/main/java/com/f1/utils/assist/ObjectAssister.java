/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.IdentityHashMap;

import com.f1.base.Legible;
import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;

public class ObjectAssister implements Assister<Object> {

	//TODO: check for fields, methods before calling.
	@Override
	public Object getNestedValue(Object o, String value, boolean throwOnError) {
		try {
			return RH.getField(o, value);
		} catch (Exception e) {
			String method = "get" + SH.uppercaseFirstChar(value);
			try {
				return RH.invokeMethod(o, method, OH.EMPTY_OBJECT_ARRAY);
			} catch (Exception e2) {
			}
			if (throwOnError)
				throw new RuntimeException("can not get '" + value + "' from " + o.getClass().getName(), e);
			return null;
		}
	}

	@Override
	public void toString(Object o, StringBuilder sb, IdentityHashMap<Object, Object> visisted) {
		if (o instanceof String)
			SH.quote('"', (String) o, sb);
		else
			sb.append(o);
	}

	@Override
	public Object clone(Object o, IdentityHashMap<Object, Object> visisted) {
		return o;// TODO:SHOULD BE OPTION
	}

	@Override
	public void toLegibleString(Object o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visisted, int maxlength) {
		if (sb.length() >= maxlength)
			return;
		String text;
		if (o instanceof Legible)
			text = ((Legible) o).toLegibleString();
		else
			text = o.toString();
		if (text.length() + sb.length() > maxlength) {
			text = text.substring(0, maxlength - sb.length());
		}
		boolean first = true;
		for (String s : SH.split(SH.NEWLINE, text)) {
			if (first)
				first = false;
			else
				sb.appendNewLine();
			sb.append(s);
		}
	}

	@Override
	public void toJson(Object o, StringBuilder sb) {
		SH.quote(o.toString(), sb);
	}

	@Override
	public Object toMapList(Object o, boolean storeNulls, String keyForClassNameOrNull) {
		return o;
	}

}
