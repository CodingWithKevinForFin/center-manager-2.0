package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.ColorGradient;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class ColorGradientToByteArrayConverter extends SimpleByteArrayConverter<ColorGradient> {

	public ColorGradientToByteArrayConverter() {
		super(ColorGradient.class, BasicTypes.COLOR_GRADIENT);
	}

	@Override
	protected ColorGradient read(FastDataInput stream) throws IOException {
		final int sc = stream.readInt();
		if (sc == -1)
			return null;

		ColorGradient cg = new ColorGradient();
		for (int i = 0; i < sc; i++) {
			double val = stream.readDouble();
			int oclor = stream.readInt();
			cg.addStop(val, oclor);
		}

		return cg;
	}

	@Override
	protected void write(ColorGradient o, FastDataOutput stream) throws IOException {
		if (o == null) {
			stream.writeInt(-1);
			return;
		}
		int sc = o.getStopsCount();
		stream.writeInt(sc);
		for (int i = 0; i < sc; i++) {
			stream.writeDouble(o.getStopValue(i));
			stream.writeInt(o.getStopColor(i));
		}
	}

}
