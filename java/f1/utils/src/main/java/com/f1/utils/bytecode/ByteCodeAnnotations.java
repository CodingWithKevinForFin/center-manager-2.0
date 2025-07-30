package com.f1.utils.bytecode;

import java.io.DataInput;
import java.io.IOException;

import com.f1.base.Caster;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Simple;

public class ByteCodeAnnotations {

	private ByteCodeAnnotation[] annotations;

	public ByteCodeAnnotations(DataInput dis, ByteCodeClass owner) throws IOException {
		annotations = parseAnnotations(dis, owner);
	}

	public void toJavaString(String indent, StringBuilder sb) {
		for (ByteCodeAnnotation ann : annotations)
			ann.toJavaString(indent, sb);
	}

	private ByteCodeAnnotation[] parseAnnotations(DataInput dis, ByteCodeClass owner) throws IOException {

		int annotationsCount = dis.readUnsignedShort();
		ByteCodeAnnotation[] r = new ByteCodeAnnotation[annotationsCount];
		for (int i = 0; i < annotationsCount; i++) {
			ByteCodeConstUtf name = owner.getConstUtf(dis.readUnsignedShort());
			int entriesCount = dis.readUnsignedShort();
			ByteCodeAnnotationEntry[] entries = new ByteCodeAnnotationEntry[entriesCount];
			for (int j = 0; j < entriesCount; j++) {
				Object ename = owner.getConstValued(dis.readUnsignedShort()).getValue();
				char etype = (char) dis.readUnsignedByte();
				Object value = parseAnnotationValue(etype, dis, owner);
				entries[j] = new ByteCodeAnnotationEntry(ename, etype, value);
			}
			r[i] = new ByteCodeAnnotation(name, entries);
		}
		return r;
	}

	private Object parseAnnotationValue(char etype, DataInput dis, ByteCodeClass owner) throws IOException {
		switch (etype) {
			case '[': {
				int arraySize = dis.readUnsignedShort();
				Object[] r = new Object[arraySize];
				for (int i = 0; i < arraySize; i++) {
					char etype2 = (char) dis.readUnsignedByte();
					r[i] = parseAnnotationValue(etype2, dis, owner);
				}
				return r;
			}
			case 'e': {
				Object type = ByteCodeUtils.parseClassDescriptor(owner.getConstUtf(dis.readUnsignedShort()).getUtf());
				Object value = owner.getConstValued(dis.readUnsignedShort()).getValue();
				return type + "." + value;//TODO: is this the best way to handle enums??
			}
			case 's': {
				Object eval = owner.getConstValued(dis.readUnsignedShort()).getValue();
				return eval.toString();
			}
			default: {
				Object eval = owner.getConstValued(dis.readUnsignedShort()).getValue();
				Caster<?> caster = OH.getCasterForClassCode(etype);
				if (caster != Caster_Simple.OBJECT)
					return caster.cast(eval);
				else
					return eval;
			}
		}
	}
}
