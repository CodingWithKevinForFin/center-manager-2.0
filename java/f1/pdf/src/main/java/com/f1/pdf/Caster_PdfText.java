package com.f1.pdf;

import com.f1.utils.AbstractCaster;
import com.f1.utils.casters.Caster_String;

public class Caster_PdfText extends AbstractCaster<PdfText> {

	public static final Caster_PdfText INSTANCE = new Caster_PdfText();

	public Caster_PdfText() {
		super(PdfText.class);
	}

	@Override
	protected PdfText castInner(Object o, boolean throwExceptionOnError) {
		return new PdfText(Caster_String.INSTANCE.cast(o, throwExceptionOnError));
	}

}
