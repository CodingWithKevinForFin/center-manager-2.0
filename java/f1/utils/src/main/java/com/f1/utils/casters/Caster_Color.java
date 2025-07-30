package com.f1.utils.casters;

import java.awt.Color;

import com.f1.utils.AbstractCaster;
import com.f1.utils.ColorHelper;

public class Caster_Color extends AbstractCaster<Color> {

	public static Caster_Color INSTANCE = new Caster_Color();

	public Caster_Color() {
		super(Color.class);
	}

	@Override
	protected Color castInner(Object o, boolean throwExceptionOnError) {
		if (o instanceof CharSequence) {
			if (throwExceptionOnError)
				return ColorHelper.parseColorNoThrow((CharSequence) o);
			else
				return ColorHelper.parseColor((CharSequence) o);
		} else if (o instanceof Number) {
			return ColorHelper.newColor(((Number) o).intValue());
		} else
			return null;
	}

}
