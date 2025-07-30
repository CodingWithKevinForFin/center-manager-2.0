package com.f1.utils.bytecode;

import static com.f1.utils.bytecode.ByteCodeConstants.ATTR_CODE;
import static com.f1.utils.bytecode.ByteCodeConstants.ATTR_CONSTANT_VALUE;
import static com.f1.utils.bytecode.ByteCodeConstants.ATTR_RUNTIME_INVISIBLE_ANNOTATIONS;
import static com.f1.utils.bytecode.ByteCodeConstants.ATTR_RUNTIME_VISIBLE_ANNOTATIONS;
import static com.f1.utils.bytecode.ByteCodeConstants.ATTR_SIGNATURE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_ALOAD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_ANEWARRAY;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_ASTORE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_BIPUSH;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_CHECKCAST;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_DLOAD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_DSTORE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_FLOAD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_FSTORE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_GETFIELD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_GETSTATIC;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_GOTO;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_GOTO_W;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFEQ;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFGE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFGT;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFLE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFLT;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFNE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFNONNULL;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IFNULL;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ACMPEQ;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ACMPNE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ICMPEQ;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ICMPGE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ICMPGT;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ICMPLE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ICMPLT;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IF_ICMPNE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_IINC;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_ILOAD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_INSTANCEOF;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_INVOKEDYNAMIC;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_INVOKEINTERFACE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_INVOKESPECIAL;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_INVOKESTATIC;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_INVOKEVIRTUAL;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_ISTORE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_JSR;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_JSR_W;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_LDC;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_LDC2_W;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_LDC_W;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_LLOAD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_LSTORE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_MULTIANEWARRAY;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_NEW;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_NEWARRAY;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_PUTFIELD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_PUTSTATIC;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_RET;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_SIPUSH;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_WIDE;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_CLASS_REF;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_DOUBLE;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_FIELD_REF;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_FLOAT;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_INT;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_INTERFACE_METHOD_REF;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_INVOKE_DYNAMIC;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_LONG;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_METHOD_HANDLE;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_METHOD_REF;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_METHOD_TYPE;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_NAME_AND_TYPE;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_STRING;
import static com.f1.utils.bytecode.ByteCodeConstants.CONSTANT_UTF8;
import static com.f1.utils.bytecode.ByteCodeUtils.modifiersMaskToString;
import static com.f1.utils.bytecode.ByteCodeUtils.typeAsString;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.utils.AssertionException;
import com.f1.utils.CharReader;
import com.f1.utils.Duration;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.SearchPath;
import com.f1.utils.ToDoException;
import com.f1.utils.impl.StringCharReader;

//based on http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.2
public class ClassFileParser {

	public interface Bindable {

		void bind();

	}

	private static final Attribute[] EMPTY_ATTRIBUTES = new Attribute[0];

	public static void main(String a[]) throws AssertionException, FileNotFoundException, IOException {
		String f = "/home/rcooke/tmp/javaclass/Test.class";
		ClassFileParser cfp = new ClassFileParser(new DataInputStream(new BufferedInputStream(new FileInputStream(new File(f)))));
		System.out.println(cfp);
		System.exit(0);

		SearchPath sp = new SearchPath("/home/rcooke/p4base");
		List<File> files = sp.search("*\\.class$", SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_RECURSE);
		int failCnt = 0;
		Duration d = new Duration();
		for (File file : files) {
			DataInputStream dis = null;
			try {
				ClassFileParser cfp2 = new ClassFileParser(dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
			} catch (Exception e) {
				failCnt++;
			}
			IOH.close(dis);
		}
		System.out.println("Stats: " + d.stamp(files.size()));
		System.out.println("Stats: " + failCnt + " failed out of " + files.size());
	}

	final private int minorVersion;
	final private int majorVersion;
	final private Object[] consts;
	final private byte[] constTypes;
	final private int accessFlags;
	final private ClassRef thisClass;
	final private ClassRef superClass;
	final private ClassRef[] interfaces;
	final private Field[] fields;
	final private Method[] methods;
	private Attribute[] attibutes;

	public ClassFileParser(DataInput dis) throws AssertionException, IOException {

		//header
		OH.assertEq(0xCA, dis.readUnsignedByte());
		OH.assertEq(0xFE, dis.readUnsignedByte());
		OH.assertEq(0xBA, dis.readUnsignedByte());
		OH.assertEq(0xBE, dis.readUnsignedByte());

		//version
		this.minorVersion = dis.readUnsignedShort();
		this.majorVersion = dis.readUnsignedShort();

		//consts
		int constsCount = dis.readUnsignedShort();
		this.consts = new Object[constsCount - 1];
		this.constTypes = new byte[constsCount - 1];
		for (int i = 0; i < constsCount - 1; i++) {
			byte constType = dis.readByte();
			Object value;
			int pos = i;
			switch (constType) {
				case CONSTANT_UTF8: {
					int len = dis.readUnsignedShort();
					value = new String(IOH.readData(dis, len));
					break;
				}
				case CONSTANT_INT:
				case CONSTANT_FLOAT:
					value = dis.readInt();
					break;
				case CONSTANT_FIELD_REF:
					value = new FieldRef(dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				case CONSTANT_METHOD_REF:
					value = new MethodRef(dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				case CONSTANT_INTERFACE_METHOD_REF:
					value = dis.readInt();
					break;
				case CONSTANT_NAME_AND_TYPE:
					value = new NameAndType(dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				case CONSTANT_CLASS_REF:
					value = new ClassRef(dis.readUnsignedShort());
					break;
				case CONSTANT_STRING:
				case CONSTANT_METHOD_TYPE:
					value = dis.readUnsignedShort();
					break;
				case CONSTANT_DOUBLE:
				case CONSTANT_LONG:
					value = dis.readLong();
					i++;//wierd but true
					break;
				case CONSTANT_METHOD_HANDLE:
					throw new ToDoException();
				case CONSTANT_INVOKE_DYNAMIC:
					throw new ToDoException();
				default:
					throw new RuntimeException("unknown const type: " + constType + " at " + i);

			}
			consts[pos] = value;
			constTypes[pos] = constType;
		}
		for (Object o : consts) {
			if (o instanceof Bindable)
				((Bindable) o).bind();
		}

		//Class 
		accessFlags = dis.readUnsignedShort();
		thisClass = getClassRef(dis.readUnsignedShort());
		superClass = getClassRef(dis.readUnsignedShort());

		//Interfaces
		final int interfacesCount = dis.readUnsignedShort();
		this.interfaces = new ClassRef[interfacesCount];
		for (int i = 0; i < interfacesCount; i++)
			this.interfaces[i] = getClassRef(dis.readUnsignedShort());

		//Fields
		final int fieldsCount = dis.readUnsignedShort();
		this.fields = new Field[fieldsCount];
		for (int i = 0; i < fieldsCount; i++) {
			int fieldAccessFlags = dis.readUnsignedShort();
			String name = getConstUtf(dis.readUnsignedShort());
			String descriptor = getConstUtf(dis.readUnsignedShort());
			Attribute[] attributes = readAttributes(dis);
			fields[i] = new Field(fieldAccessFlags, name, descriptor, attributes);
		}

		//Methods
		final int methodsCount = dis.readUnsignedShort();
		this.methods = new Method[methodsCount];
		for (int i = 0; i < methodsCount; i++) {
			int fieldAccessFlags = dis.readUnsignedShort();
			Object name = getConstUtf(dis.readUnsignedShort());
			String descriptor = getConstUtf(dis.readUnsignedShort());
			Attribute[] attributes = readAttributes(dis);
			methods[i] = new Method(fieldAccessFlags, name, descriptor, attributes);
		}

		this.attibutes = readAttributes(dis);
	}

	private Attribute[] readAttributes(DataInput dis) throws IOException {
		int attributesCount = dis.readUnsignedShort();
		if (attributesCount == 0)
			return EMPTY_ATTRIBUTES;
		final Attribute[] r = new Attribute[attributesCount];
		for (int i = 0; i < attributesCount; i++) {
			final String key = getConstUtf(dis.readUnsignedShort());
			final int length = dis.readInt();
			final byte[] data = IOH.readData(dis, length);
			r[i] = new Attribute(key, data);
		}
		return r;
	}

	private String getConstUtf(int i) {
		return (String) getConst(i);
	}
	private ClassRef getClassRef(int i) {
		return (ClassRef) getConst(i);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(SH.NEWLINE);
		sb.append("Version").append(SH.NEWLINE);
		sb.append("  minor_version=").append(this.minorVersion).append(SH.NEWLINE);
		sb.append("  major_version=").append(this.majorVersion).append(SH.NEWLINE);
		sb.append("Const Pool").append(this.majorVersion).append(SH.NEWLINE);
		for (int i = 0; i < consts.length; i++)
			sb.append("  const #").append(i + 1).append(" ").append(typeAsString(constTypes[i])).append(" = ").append(consts[i]).append(SH.NEWLINE);
		sb.append(SH.NEWLINE);
		modifiersMaskToString(accessFlags, sb, true);
		sb.append(' ');
		parseClass(getThisClass().getName(), sb);
		sb.append(" extends ");
		parseClass(getSuperClass().getName(), sb);
		for (int i = 0; i < this.interfaces.length; i++) {
			if (i > 0)
				sb.append(',');
			else
				sb.append(" implements ");
			sb.append(this.interfaces[i]);
		}
		sb.append("{").append(SH.NEWLINE).append(SH.NEWLINE);
		sb.append("  // Field Declarations").append(SH.NEWLINE).append(SH.NEWLINE);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.toString("  ", sb);
			sb.append(SH.NEWLINE);
		}
		sb.append(SH.NEWLINE);
		sb.append("  // Methods").append(SH.NEWLINE).append(SH.NEWLINE);
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			method.toString("  ", sb);
			sb.append(SH.NEWLINE);
		}
		sb.append("}");
		sb.append(SH.NEWLINE);
		return sb.toString();
	}
	private ClassRef getSuperClass() {
		return superClass;
	}

	private ClassRef getThisClass() {
		return thisClass;
	}

	private Object getConst(int i) {
		return (Object) consts[i - 1];
	}

	public class Field extends Attributable {

		final private int accessFlags;
		final private String name;
		final private String descriptor;

		public Field(int accessFlags, String name, String descriptor, Attribute[] attributes) throws IOException {
			super(attributes);
			this.accessFlags = accessFlags;
			this.name = name;
			this.descriptor = descriptor;
		}
		public StringBuilder toString(String indent, StringBuilder sb) {
			super.toString(indent, sb);
			sb.append("  ");
			modifiersMaskToString(getAccessFlags(), sb, false).append(' ');
			parseDescriptor(descriptor, sb);
			sb.append(' ');
			sb.append(getName());
			if (getValue() != null)
				sb.append(" = ").append(getValue());
			sb.append(';');
			sb.append(SH.NEWLINE);
			return sb;
		}
		public int getAccessFlags() {
			return accessFlags;
		}

		public String getName() {
			return name;
		}

		public String getDescriptor() {
			return descriptor;
		}

	}

	private Annotation[] parseAnnotations(DataInput dis) throws IOException {

		int annotationsCount = dis.readUnsignedShort();
		Annotation[] r = new Annotation[annotationsCount];
		for (int i = 0; i < annotationsCount; i++) {
			String name = getConstUtf(dis.readUnsignedShort());
			int entriesCount = dis.readUnsignedShort();
			AnnotationEntry[] entries = new AnnotationEntry[entriesCount];
			for (int j = 0; j < entriesCount; j++) {
				String ename = getConstUtf(dis.readUnsignedShort());
				char etype = (char) dis.readUnsignedByte();
				Object eval = getConst(dis.readUnsignedShort());
				entries[j] = new AnnotationEntry(ename, etype, eval);
			}
			r[i] = new Annotation(name, entries);
		}
		return r;
	}

	public class Method extends Attributable {

		final private int accessFlags;
		final private Object name;
		final private String descriptor;
		private String returnType;
		private String[] arguments;

		public Method(int accessFlags, Object name, String descriptor, Attribute[] attributes) throws IOException {
			super(attributes);
			this.accessFlags = accessFlags;
			this.name = name;
			this.descriptor = descriptor;
			StringCharReader cr = new StringCharReader(descriptor);
			StringBuilder buf = new StringBuilder();
			List<String> argsAndRetType = parseArguments(cr, buf);
			this.arguments = new String[argsAndRetType.size() - 1];
			for (int i = 0; i < this.arguments.length; i++)
				this.arguments[i] = argsAndRetType.get(i);
			this.returnType = argsAndRetType.get(argsAndRetType.size() - 1);
		}

		public StringBuilder toString(String indent, StringBuilder sb) {
			super.toString(indent, sb);
			sb.append(indent);
			modifiersMaskToString(getAccessFlags(), sb, false).append(' ');
			sb.append(returnType);
			sb.append(' ');
			sb.append(getName());
			sb.append('(');
			SH.join(',', arguments, sb);
			sb.append(')');
			sb.append("{");
			sb.append(SH.NEWLINE);
			if (getCode() != null)
				getCode().toString(indent + "  ", sb);
			sb.append(indent);
			sb.append("}");
			sb.append(SH.NEWLINE);
			return sb;
		}

		public int getAccessFlags() {
			return accessFlags;
		}

		public Object getName() {
			return name;
		}

		public Object getDescriptor() {
			return descriptor;
		}

	}

	private CodeDef parseCode(DataInput dis) throws IOException {
		final int maxStack = dis.readUnsignedShort();
		final int maxLocals = dis.readUnsignedShort();
		final int codeLength = dis.readInt();
		final byte[] code = new byte[codeLength];
		dis.readFully(code);
		final int exceptionsCount = dis.readUnsignedShort();
		ExceptionDef[] exceptions = new ExceptionDef[exceptionsCount];
		for (int i = 0; i < exceptionsCount; i++) {
			int startPc = dis.readUnsignedShort();
			int endPc = dis.readUnsignedShort();
			int handlerPc = dis.readUnsignedShort();
			int catchTypeIndex = dis.readUnsignedShort();
			ClassRef catchType = catchTypeIndex == 0 ? null : getClassRef(catchTypeIndex);
			exceptions[i] = new ExceptionDef(startPc, endPc, handlerPc, catchType);
		}

		return new CodeDef(maxStack, maxLocals, code, exceptions);
	}

	private final FastByteArrayDataInputStream tempBuffer = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);

	public class Attribute {

		final private String key;
		final private byte[] data;

		public Attribute(String key, byte[] data) {
			this.key = key;
			this.data = data;
		}

		public DataInput getDataStream() {
			tempBuffer.reset(data);
			return tempBuffer;
		}

		public String getKey() {
			return key;
		}

		public byte[] getData() {
			return data;
		}
	}

	public class NameAndType implements Bindable {
		final private int nameIndex;
		final private int descriptorIndex;
		private String name;
		private String descriptor;

		public NameAndType(int nameIndex, int descriptorIndex) {
			this.nameIndex = nameIndex;
			this.descriptorIndex = descriptorIndex;
		}

		public int getNameIndex() {
			return nameIndex;
		}

		public int getDesciptorIndex() {
			return descriptorIndex;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + name + "," + descriptor;
		}

		@Override
		public void bind() {
			if (name == null) {
				name = getConstUtf(nameIndex);
				descriptor = getConstUtf(descriptorIndex);
			}
		}
	}

	public class MethodRef implements Bindable {
		final private int classIndex;
		final private int nameAndTypeIndex;
		private ClassRef clazz;
		private NameAndType nameAndType;

		public MethodRef(int classIndex, int nameAndTypeIndex) {
			this.classIndex = classIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}

		public int getClassIndex() {
			return classIndex;
		}
		public int getNameAndTypeIndex() {
			return nameAndTypeIndex;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + clazz + "," + nameAndType;
		}

		@Override
		public void bind() {
			if (clazz == null) {
				clazz = (ClassRef) getConst(classIndex);
				clazz.bind();
				nameAndType = (NameAndType) getConst(nameAndTypeIndex);
				nameAndType.bind();
			}
		}

	}

	public class FieldRef implements Bindable {
		final private int classIndex;
		final private int nameAndTypeIndex;
		private ClassRef clazz;
		private NameAndType nameAndType;

		public FieldRef(int classIndex, int nameAndTypeIndex) {
			this.classIndex = classIndex;
			this.nameAndTypeIndex = nameAndTypeIndex;
		}

		public int getClassIndex() {
			return classIndex;
		}
		public int getNameAndTypeIndex() {
			return nameAndTypeIndex;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + clazz + "," + nameAndType;
		}

		@Override
		public void bind() {
			if (clazz == null) {
				clazz = getClassRef(classIndex);
				clazz.bind();
				nameAndType = (NameAndType) getConst(nameAndTypeIndex);
				nameAndType.bind();
			}
		}

	}

	public class ClassRef implements Bindable {
		final private int nameIndex;
		private String name;

		public ClassRef(int nameIndex) {
			this.nameIndex = nameIndex;
		}

		public int getNameIndex() {
			return nameIndex;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + name;
		}

		@Override
		public void bind() {
			if (name == null) {
				name = getConstUtf(nameIndex);
			}
		}
		public String getName() {
			return name;
		}

	}

	public static class Annotation {
		final private String type;
		final private AnnotationEntry[] entries;

		public Annotation(String type, AnnotationEntry[] entries) {
			this.type = type;
			this.entries = entries;
		}

		public void toString(StringBuilder sb) {
			sb.append('@');
			parseDescriptor(type, sb);
			sb.append('(');
			boolean first = true;
			for (AnnotationEntry entry : entries) {
				if (first)
					first = false;
				else
					sb.append(',');
				entry.toString(sb);
			}
			sb.append(')').append(SH.NEWLINE);
		}

		public String getType() {
			return type;
		}

		public AnnotationEntry[] getEntries() {
			return entries;
		}
	}

	public static class AnnotationEntry {

		final private String name;
		final private Object value;
		final private char type;

		public AnnotationEntry(String name, char etype, Object eval) {
			this.name = name;
			this.type = etype;
			this.value = eval;
		}

		public void toString(StringBuilder sb) {
			sb.append(name);
			sb.append('=');
			if (type == 's')
				sb.append('"').append(value).append('"');
			else
				sb.append(value);
		}

		public String getName() {
			return name;
		}
		public char getType() {
			return type;
		}
		public Object getValue() {
			return value;
		}

	}

	public static class CodeDef {
		final private int maxStack;
		final private int maxLocals;
		final private ExceptionDef[] exceptions;
		final private byte[] code;

		public CodeDef(int maxStack, int maxLocals, byte[] code, ExceptionDef[] exceptions) {
			this.maxStack = maxStack;
			this.maxLocals = maxLocals;
			this.code = code;
			this.exceptions = exceptions;
		}

		public void toString(String indent, StringBuilder sb) {
			for (int i = 0; i < code.length; i++) {
				byte b = code[i];
				sb.append(indent).append(ByteCodeUtils.byteCodeToString(b)).append(SH.NEWLINE);
				int skip = 0;
				switch (b) {
					case BYTE_CODE_ALOAD:
						skip = 1;
						break;
					case BYTE_CODE_ANEWARRAY:
						skip = 2;
						break;
					case BYTE_CODE_ASTORE:
						skip = 1;
						break;
					case BYTE_CODE_BIPUSH:
						skip = 1;
						break;
					case BYTE_CODE_CHECKCAST:
						skip = 2;
						break;
					case BYTE_CODE_DLOAD:
						skip = 1;
						break;
					case BYTE_CODE_DSTORE:
						skip = 1;
						break;
					case BYTE_CODE_FLOAD:
						skip = 1;
						break;
					case BYTE_CODE_FSTORE:
						skip = 1;
						break;
					case BYTE_CODE_GETFIELD:
						skip = 2;
						break;
					case BYTE_CODE_GETSTATIC:
						skip = 2;
						break;
					case BYTE_CODE_GOTO:
						skip = 2;
						break;
					case BYTE_CODE_GOTO_W:
						skip = 4;
						break;
					case BYTE_CODE_IF_ACMPEQ:
						skip = 2;
						break;
					case BYTE_CODE_IF_ACMPNE:
						skip = 2;
						break;
					case BYTE_CODE_IF_ICMPEQ:
						skip = 2;
						break;
					case BYTE_CODE_IF_ICMPNE:
						skip = 2;
						break;
					case BYTE_CODE_IF_ICMPLT:
						skip = 2;
						break;
					case BYTE_CODE_IF_ICMPGE:
						skip = 2;
						break;
					case BYTE_CODE_IF_ICMPGT:
						skip = 2;
						break;
					case BYTE_CODE_IF_ICMPLE:
						skip = 2;
						break;
					case BYTE_CODE_IFEQ:
						skip = 2;
						break;
					case BYTE_CODE_IFNE:
						skip = 2;
						break;
					case BYTE_CODE_IFLT:
						skip = 2;
						break;
					case BYTE_CODE_IFGE:
						skip = 2;
						break;
					case BYTE_CODE_IFGT:
						skip = 2;
						break;
					case BYTE_CODE_IFLE:
						skip = 2;
						break;
					case BYTE_CODE_IFNONNULL:
						skip = 2;
						break;
					case BYTE_CODE_IFNULL:
						skip = 2;
						break;
					case BYTE_CODE_IINC:
						skip = 2;
						break;
					case BYTE_CODE_ILOAD:
						skip = 1;
						break;
					case BYTE_CODE_INSTANCEOF:
						skip = 2;
						break;
					case BYTE_CODE_INVOKEDYNAMIC:
						skip = 4;
						break;
					case BYTE_CODE_INVOKEINTERFACE:
						skip = 4;
						break;
					case BYTE_CODE_INVOKESPECIAL:
						skip = 2;
						break;
					case BYTE_CODE_INVOKESTATIC:
						skip = 2;
						break;
					case BYTE_CODE_INVOKEVIRTUAL:
						skip = 2;
						break;
					case BYTE_CODE_ISTORE:
						skip = 1;
						break;
					case BYTE_CODE_JSR:
						skip = 2;
						break;
					case BYTE_CODE_JSR_W:
						skip = 4;
						break;
					case BYTE_CODE_LDC:
						skip = 1;
						break;
					case BYTE_CODE_LDC_W:
						skip = 2;
						break;
					case BYTE_CODE_LDC2_W:
						skip = 2;
						break;
					case BYTE_CODE_LLOAD:
						skip = 1;
						break;
					case BYTE_CODE_LSTORE:
						skip = 1;
						break;
					case BYTE_CODE_MULTIANEWARRAY:
						skip = 3;
						break;
					case BYTE_CODE_NEW:
						skip = 2;
						break;
					case BYTE_CODE_NEWARRAY:
						skip = 1;
						break;
					case BYTE_CODE_PUTFIELD:
						skip = 2;
						break;
					case BYTE_CODE_PUTSTATIC:
						skip = 2;
						break;
					case BYTE_CODE_RET:
						skip = 1;
						break;
					case BYTE_CODE_SIPUSH:
						skip = 2;
						break;
					//TODO: case BYTE_CODE_LOOKUPSWITCH: skip = 4+;break;
					//TODO: case BYTE_CODE_TABLESWITCH: skip = 4+;break;
					case BYTE_CODE_WIDE:
						skip = 40973;
						break;
				}
				i += skip;
			}
		}
		public int getMaxStack() {
			return maxStack;
		}

		public int getMaxLocals() {
			return maxLocals;
		}

		public byte[] getCode() {
			return code;
		}

		public ExceptionDef[] getExceptions() {
			return exceptions;
		}

	}

	public static class ExceptionDef {
		final private int startPc;
		final private int endPc;
		final private int handlerPc;
		final private ClassRef catchType;

		public ExceptionDef(int startPc, int endPc, int handlerPc, ClassRef catchType) {
			this.startPc = startPc;
			this.endPc = endPc;
			this.handlerPc = handlerPc;
			this.catchType = catchType;
		}
		public int getStartPc() {
			return startPc;
		}

		public int getEndPc() {
			return endPc;
		}

		public int getHandlerPc() {
			return handlerPc;
		}

		public ClassRef getCatchType() {
			return catchType;
		}

	}

	private static StringBuilder parseDescriptor(String name, StringBuilder sb) {
		return parseDescriptor(0, name, sb);
	}
	private static StringBuilder parseDescriptor(int offset, String name, StringBuilder sb) {
		if (name.length() == 0)
			return sb;
		char c = name.charAt(offset);
		if (c == 'L') {
			parseClass(name.substring(offset + 1, name.length() - 1), sb);
		} else if (c == '[') {
			parseDescriptor(offset + 1, name, sb);
			sb.append("[]");
		} else {
			Class<?> clazz = OH.getTypeForClassCode(c);
			if (clazz != null) {
				sb.append(clazz.getSimpleName());
			} else
				sb.append(name);//shouldn't get here
		}
		return sb;
	}

	private static void parseClass(String name, StringBuilder sb) {
		sb.append(SH.replaceAll(name, '/', '.'));
	}

	public class Attributable {

		final private Attribute[] attributes;
		private CodeDef code;
		private Object constValue;
		private Object signature;
		private Annotation[] visibleAnnotations;
		private Annotation[] invisibleAnnotations;

		public Attributable(Attribute attributes[]) throws IOException {
			this.attributes = attributes;
			for (Attribute att : attributes) {
				DataInput dis = att.getDataStream();
				if (ATTR_CODE.equals(att.getKey())) {
					this.code = parseCode(dis);
				} else if (ATTR_CONSTANT_VALUE.equals(att.getKey())) {
					constValue = getConst(dis.readUnsignedShort());
				} else if (ATTR_SIGNATURE.equals(att.getKey())) {
					signature = getConst(dis.readUnsignedShort());
				} else if (ATTR_RUNTIME_VISIBLE_ANNOTATIONS.equals(att.getKey())) {
					this.visibleAnnotations = parseAnnotations(dis);
				} else if (ATTR_RUNTIME_INVISIBLE_ANNOTATIONS.equals(att.getKey())) {
					this.invisibleAnnotations = parseAnnotations(dis);
				}
			}
		}

		public StringBuilder toString(String indent, StringBuilder sb) {
			if (getVisibleAnnotations() != null)
				for (Annotation a : getVisibleAnnotations())
					a.toString(sb.append(indent));
			if (getInvisibleAnnotations() != null)
				for (Annotation a : getInvisibleAnnotations())
					a.toString(sb.append(indent));
			return sb;
		}

		public CodeDef getCode() {
			return code;
		}
		public Attribute[] getAttributes() {
			return attributes;
		}
		public Annotation[] getVisibleAnnotations() {
			return visibleAnnotations;
		}
		public Annotation[] getInvisibleAnnotations() {
			return invisibleAnnotations;
		}
		public Object getValue() {
			return constValue;
		}
		public Object getSignature() {
			return signature;
		}
	}

	private static final int[] SLASH_OR_SEMI = new int[] { '/', ';' };

	static private List<String> parseArguments(CharReader cr, StringBuilder buf) {
		List<String> arguments = new ArrayList<String>();
		int dimensions = 0;
		cr.expect('(');
		outer: for (;;) {
			int c = cr.readCharOrEof();
			String arg;
			switch (c) {
				case CharReader.EOF:
					break outer;
				case ')':
					continue;//this is the end of the arguments, next is the return type
				case RH.JAVA_SIGNATURE_ARRAY:
					dimensions++;
					continue;
				case RH.JAVA_SIGNATURE_BOOLEAN:
					arg = "boolean";
					break;
				case RH.JAVA_SIGNATURE_BYTE:
					arg = "byte";
					break;
				case RH.JAVA_SIGNATURE_CHAR:
					arg = "char";
					break;
				case RH.JAVA_SIGNATURE_CLASS:
					while (cr.readUntilAny(SLASH_OR_SEMI, buf) == '/') {
						buf.append('.');
						cr.expect('/');
					}
					cr.expect(';');
					arg = buf.toString();
					break;
				case RH.JAVA_SIGNATURE_DOUBLE:
					arg = "double";
					break;
				case RH.JAVA_SIGNATURE_FLOAT:
					arg = "float";
					break;
				case RH.JAVA_SIGNATURE_INT:
					arg = "int";
					break;
				case RH.JAVA_SIGNATURE_LONG:
					arg = "long";
					break;
				case RH.JAVA_SIGNATURE_SHORT:
					arg = "short";
					break;
				case RH.JAVA_SIGNATURE_VOID:
					arg = "void";
					break;
				default:
					throw new RuntimeException("invalid description: " + cr);
			}
			if (dimensions > 0) {
				buf.append(arg);
				SH.repeat("[]", dimensions, buf);
				arguments.add(SH.toStringAndClear(buf));
				dimensions = 0;
			} else {
				arguments.add(arg);
			}
		}
		return arguments;
	}

}
