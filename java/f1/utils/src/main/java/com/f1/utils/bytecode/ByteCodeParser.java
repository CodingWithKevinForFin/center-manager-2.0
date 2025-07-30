package com.f1.utils.bytecode;

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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.f1.utils.AssertionException;
import com.f1.utils.Duration;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SearchPath;

public class ByteCodeParser {

	public static void main(String a[]) throws AssertionException, FileNotFoundException, IOException {
		String f = "/home/rcooke/p4base/dev/java/f1/base/target/classes/com/f1/base/PID.class";
		ByteCodeParser cfp = new ByteCodeParser();
		ByteCodeClass bcc = cfp.parse(new DataInputStream(new BufferedInputStream(new FileInputStream(new File(f)))));
		StringBuilder sb = new StringBuilder();
		bcc.toJavaString("", sb);
		System.out.println(sb);

		SearchPath sp = new SearchPath("/home/rcooke/p4base");
		List<File> files = sp.search("*\\.class$", SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_RECURSE);
		int failCnt = 0;
		Duration d = new Duration();
		for (File file : files) {
			DataInputStream dis = null;
			try {
				ByteCodeParser cfp2 = new ByteCodeParser();
				cfp2.parse(dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
			} catch (Exception e) {
				System.out.println("!!! FAIL: " + file);
				e.printStackTrace(System.out);
				failCnt++;
			}
			IOH.close(dis);
		}
		System.out.println("Stats: " + d.stamp(files.size()));
		System.out.println("Stats: " + failCnt + " failed out of " + files.size());
	}

	public ByteCodeClass parse(DataInput dis) throws IOException {

		//header
		OH.assertEq(0xCA, dis.readUnsignedByte());
		OH.assertEq(0xFE, dis.readUnsignedByte());
		OH.assertEq(0xBA, dis.readUnsignedByte());
		OH.assertEq(0xBE, dis.readUnsignedByte());

		ByteCodeClass r = new ByteCodeClass();
		//version
		r.setMinorVersion(dis.readUnsignedShort());
		r.setMajorVersion(dis.readUnsignedShort());

		//consts
		int constsCount = dis.readUnsignedShort();
		ByteCodeConst[] consts = new ByteCodeConst[constsCount - 1];
		for (int i = 0; i < constsCount - 1; i++) {
			byte constType = dis.readByte();
			ByteCodeConst value;
			int index = i + 1;
			switch (constType) {
				case CONSTANT_UTF8:
					int len = dis.readUnsignedShort();
					value = new ByteCodeConstUtf(index, new String(IOH.readData(dis, len)));
					break;
				case CONSTANT_INT:
					value = new ByteCodeConstInt(index, dis.readInt());
					break;
				case CONSTANT_FLOAT:
					value = new ByteCodeConstFloat(index, dis.readFloat());
					break;
				case CONSTANT_STRING:
					value = new ByteCodeConstString(index, dis.readUnsignedShort());
					break;
				case CONSTANT_FIELD_REF:
					value = new ByteCodeConstFieldRef(index, dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				case CONSTANT_DOUBLE:
					value = new ByteCodeConstDouble(index, dis.readDouble());
					i++;
					break;
				case CONSTANT_LONG:
					value = new ByteCodeConstLong(index, dis.readLong());
					i++;
					break;
				case CONSTANT_METHOD_REF:
					value = new ByteCodeConstMethodRef(index, dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				case CONSTANT_NAME_AND_TYPE:
					value = new ByteCodeConstNameAndType(index, dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				case CONSTANT_CLASS_REF:
					value = new ByteCodeConstClassRef(index, dis.readUnsignedShort());
					break;
				case CONSTANT_METHOD_TYPE:
					value = new ByteCodeConstMethodType(index, dis.readUnsignedShort());
					break;
				case CONSTANT_INTERFACE_METHOD_REF:
					value = new ByteCodeConstMethodRef(index, dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				case CONSTANT_METHOD_HANDLE:
					value = new ByteCodeConstMethodHandle(index, dis.readUnsignedByte(), dis.readUnsignedShort());
					break;
				case CONSTANT_INVOKE_DYNAMIC:
					value = new ByteCodeConstInvokeDynamic(index, dis.readUnsignedShort(), dis.readUnsignedShort());
					break;
				default:
					throw new RuntimeException("unknown const type: " + constType + " at " + i);

			}
			consts[index - 1] = value;
		}
		r.setConsts(consts);
		for (ByteCodeConst o : consts)
			if (o != null)
				o.bind(r);

		//Class 
		r.setAccessFlags(dis.readUnsignedShort());
		r.setThisClass(r.getClassRef(dis.readUnsignedShort()));
		r.setSuperClass(r.getClassRef(dis.readUnsignedShort()));

		//Interfaces
		final int interfacesCount = dis.readUnsignedShort();
		ByteCodeConstClassRef[] interfaces = new ByteCodeConstClassRef[interfacesCount];
		for (int i = 0; i < interfacesCount; i++)
			interfaces[i] = r.getClassRef(dis.readUnsignedShort());
		r.setInterfaces(interfaces);

		//Fields
		final int fieldsCount = dis.readUnsignedShort();
		ByteCodeField[] fields = new ByteCodeField[fieldsCount];
		for (int i = 0; i < fieldsCount; i++) {
			ByteCodeField field = new ByteCodeField();
			field.setOwner(r);
			field.setAccessFlags(dis.readUnsignedShort());
			field.setName(r.getConstUtf(dis.readUnsignedShort()));
			field.setDescriptor(r.getConstUtf(dis.readUnsignedShort()));

			parseAttributes(dis, r, field);
			fields[i] = field;
		}
		r.setFields(fields);

		//Methods
		final int methodsCount = dis.readUnsignedShort();
		ByteCodeMethod[] methods = new ByteCodeMethod[methodsCount];
		for (int i = 0; i < methodsCount; i++) {
			ByteCodeMethod method = new ByteCodeMethod();
			method.setOwner(r);
			method.setAccessFlags(dis.readUnsignedShort());
			method.setName(r.getConstUtf(dis.readUnsignedShort()));
			method.setDescriptor(r.getConstUtf(dis.readUnsignedShort()));

			parseAttributes(dis, r, method);
			methods[i] = method;
		}
		r.setMethods(methods);

		parseAttributes(dis, r, r);
		return r;
	}

	private void parseAttributes(DataInput dis, ByteCodeClass pool, ByteCodeAttributable sink) throws IOException {
		final int attributesCount = dis.readUnsignedShort();
		for (int i = 0; i < attributesCount; i++) {
			final ByteCodeConstUtf key = pool.getConstUtf(dis.readUnsignedShort());
			final int length = dis.readInt();
			byte[] b = new byte[length];
			dis.readFully(b);
			sink.addAttribute(key, new DataInputStream(new ByteArrayInputStream(b)), length);
		}
	}
}
