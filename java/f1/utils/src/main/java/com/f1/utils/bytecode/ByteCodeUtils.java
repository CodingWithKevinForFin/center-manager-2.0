package com.f1.utils.bytecode;

import static com.f1.utils.bytecode.ByteCodeConstants.*;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.CharReader;
import com.f1.utils.MH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

//based on http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.2
public class ByteCodeUtils {

	private static final int[] ACC_MODIFIERS = new int[] { ACC_PUBLIC, ACC_FINAL, ACC_PROTECTED, ACC_STATIC, ACC_SUPER_OR_SYNCHRONIZED, ACC_BRIDGE, ACC_VARARGS, ACC_NATIVE,
			ACC_INTERFACE, ACC_ABSTRACT, ACC_STRICT, ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM };
	public static StringBuilder modifiersMaskToString(int mask, StringBuilder sb, boolean isClassInsteadOfMethod) {

		boolean first = true;
		for (int i : ACC_MODIFIERS) {
			if (MH.allBits(mask, i)) {
				if (first)
					first = false;
				else
					sb.append(' ');
				sb.append(modifierToString(i, isClassInsteadOfMethod));
			}
		}

		return sb;
	}

	private static String modifierToString(int mod, boolean isClassInsteadOfMethod) {
		switch (mod) {
			case ACC_ABSTRACT:
				return "abstract";
			case ACC_ANNOTATION:
				return "annotation";
			case ACC_ENUM:
				return "enum";
			case ACC_FINAL:
				return "final";
			case ACC_INTERFACE:
				return "interface";
			case ACC_PUBLIC:
				return "public";
			case ACC_SUPER_OR_SYNCHRONIZED:
				return isClassInsteadOfMethod ? "" : "synchronized";
			case ACC_SYNTHETIC:
				return "synthetic";
			case ACC_PRIVATE:
				return "private";
			case ACC_PROTECTED:
				return "protected";
			case ACC_STATIC:
				return "static";
			case ACC_BRIDGE:
				return "bridge";
			case ACC_VARARGS:
				return "varargs";
			case ACC_NATIVE:
				return "native";
			case ACC_STRICT:
				return "strict";
			default:
				return SH.toString(mod);
		}
	}

	public static String byteCodeToString(byte code) {
		switch (code) {
			case BYTE_CODE_AALOAD:
				return "aload";
			case BYTE_CODE_AASTORE:
				return "aastore";
			case BYTE_CODE_ACONST_NULL:
				return "aconst_null";
			case BYTE_CODE_ALOAD:
				return "aload";
			case BYTE_CODE_ALOAD_0:
				return "aload_0";
			case BYTE_CODE_ALOAD_1:
				return "aload_1";
			case BYTE_CODE_ALOAD_2:
				return "aload_2";
			case BYTE_CODE_ALOAD_3:
				return "aload_3";
			case BYTE_CODE_ANEWARRAY:
				return "anewarray";
			case BYTE_CODE_ARETURN:
				return "areturn";
			case BYTE_CODE_ARRAYLENGTH:
				return "arraylength";
			case BYTE_CODE_ASTORE:
				return "astore";
			case BYTE_CODE_ASTORE_0:
				return "astore_0";
			case BYTE_CODE_ASTORE_1:
				return "astore_1";
			case BYTE_CODE_ASTORE_2:
				return "astore_2";
			case BYTE_CODE_ASTORE_3:
				return "astore_3";
			case BYTE_CODE_ATHROW:
				return "athrow";
			case BYTE_CODE_BALOAD:
				return "baload";
			case BYTE_CODE_BASTORE:
				return "bastore";
			case BYTE_CODE_BIPUSH:
				return "bipush";
			case BYTE_CODE_CALOAD:
				return "caload";
			case BYTE_CODE_CASTORE:
				return "castore";
			case BYTE_CODE_CHECKCAST:
				return "checkcast";
			case BYTE_CODE_D2F:
				return "d2f";
			case BYTE_CODE_D2I:
				return "d2i";
			case BYTE_CODE_D2L:
				return "d2l";
			case BYTE_CODE_DADD:
				return "dadd";
			case BYTE_CODE_DALOAD:
				return "daload";
			case BYTE_CODE_DASTORE:
				return "dastore";
			case BYTE_CODE_DCMPG:
				return "dcmpg";
			case BYTE_CODE_DCMPL:
				return "dcmpl";
			case BYTE_CODE_DCONST_0:
				return "dconst_0";
			case BYTE_CODE_DCONST_1:
				return "dconst_1";
			case BYTE_CODE_DDIV:
				return "ddiv";
			case BYTE_CODE_DLOAD:
				return "dload";
			case BYTE_CODE_DLOAD_0:
				return "dload_0";
			case BYTE_CODE_DLOAD_1:
				return "dload_1";
			case BYTE_CODE_DLOAD_2:
				return "dload_2";
			case BYTE_CODE_DLOAD_3:
				return "dload_3";
			case BYTE_CODE_DMUL:
				return "dmul";
			case BYTE_CODE_DNEG:
				return "dneg";
			case BYTE_CODE_DREM:
				return "drem";
			case BYTE_CODE_DRETURN:
				return "dreturn";
			case BYTE_CODE_DSTORE:
				return "dstore";
			case BYTE_CODE_DSTORE_0:
				return "dstore_0";
			case BYTE_CODE_DSTORE_1:
				return "dstore_1";
			case BYTE_CODE_DSTORE_2:
				return "dstore_2";
			case BYTE_CODE_DSTORE_3:
				return "dstore_3";
			case BYTE_CODE_DSUB:
				return "dsub";
			case BYTE_CODE_DUP:
				return "dup";
			case BYTE_CODE_DUP_X1:
				return "dup_x1";
			case BYTE_CODE_DUP_X2:
				return "dup_x2";
			case BYTE_CODE_DUP2:
				return "dup2";
			case BYTE_CODE_DUP2_X1:
				return "dup2_x1";
			case BYTE_CODE_DUP2_X2:
				return "dup2_x2";
			case BYTE_CODE_F2D:
				return "f2d";
			case BYTE_CODE_F2I:
				return "f2i";
			case BYTE_CODE_F2L:
				return "f2l";
			case BYTE_CODE_FADD:
				return "fadd";
			case BYTE_CODE_FALOAD:
				return "faload";
			case BYTE_CODE_FASTORE:
				return "fastore";
			case BYTE_CODE_FCMPG:
				return "fcmpg";
			case BYTE_CODE_FCMPL:
				return "fcmpl";
			case BYTE_CODE_FCONST_0:
				return "fconst_0";
			case BYTE_CODE_FCONST_1:
				return "fconst_1";
			case BYTE_CODE_FCONST_2:
				return "fconst_2";
			case BYTE_CODE_FDIV:
				return "fdiv";
			case BYTE_CODE_FLOAD:
				return "fload";
			case BYTE_CODE_FLOAD_0:
				return "fload_0";
			case BYTE_CODE_FLOAD_1:
				return "fload_1";
			case BYTE_CODE_FLOAD_2:
				return "fload_2";
			case BYTE_CODE_FLOAD_3:
				return "fload_3";
			case BYTE_CODE_FMUL:
				return "fmul";
			case BYTE_CODE_FNEG:
				return "fneg";
			case BYTE_CODE_FREM:
				return "frem";
			case BYTE_CODE_FRETURN:
				return "freturn";
			case BYTE_CODE_FSTORE:
				return "fstore";
			case BYTE_CODE_FSTORE_0:
				return "fstore_0";
			case BYTE_CODE_FSTORE_1:
				return "fstore_1";
			case BYTE_CODE_FSTORE_2:
				return "fstore_2";
			case BYTE_CODE_FSTORE_3:
				return "fstore_3";
			case BYTE_CODE_FSUB:
				return "fsub";
			case BYTE_CODE_GETFIELD:
				return "getfield";
			case BYTE_CODE_GETSTATIC:
				return "getstatic";
			case BYTE_CODE_GOTO:
				return "goto";
			case BYTE_CODE_GOTO_W:
				return "goto_w";
			case BYTE_CODE_I2B:
				return "i2b";
			case BYTE_CODE_I2C:
				return "i2c";
			case BYTE_CODE_I2D:
				return "i2d";
			case BYTE_CODE_I2F:
				return "i2f";
			case BYTE_CODE_I2L:
				return "i2l";
			case BYTE_CODE_I2S:
				return "i2s";
			case BYTE_CODE_IADD:
				return "iadd";
			case BYTE_CODE_IALOAD:
				return "iaload";
			case BYTE_CODE_IAND:
				return "iand";
			case BYTE_CODE_IASTORE:
				return "iastore";
			case BYTE_CODE_ICONST_M1:
				return "iconst_m1";
			case BYTE_CODE_ICONST_0:
				return "iconst_0";
			case BYTE_CODE_ICONST_1:
				return "iconst_1";
			case BYTE_CODE_ICONST_2:
				return "iconst_2";
			case BYTE_CODE_ICONST_3:
				return "iconst_3";
			case BYTE_CODE_ICONST_4:
				return "iconst_4";
			case BYTE_CODE_ICONST_5:
				return "iconst_5";
			case BYTE_CODE_IDIV:
				return "idiv";
			case BYTE_CODE_IF_ACMPEQ:
				return "if_acmpeq";
			case BYTE_CODE_IF_ACMPNE:
				return "if_acmpne";
			case BYTE_CODE_IF_ICMPEQ:
				return "if_icmpeq";
			case BYTE_CODE_IF_ICMPNE:
				return "if_icmpne";
			case BYTE_CODE_IF_ICMPLT:
				return "if_icmplt";
			case BYTE_CODE_IF_ICMPGE:
				return "if_icmpge";
			case BYTE_CODE_IF_ICMPGT:
				return "if_icmpgt";
			case BYTE_CODE_IF_ICMPLE:
				return "if_icmple";
			case BYTE_CODE_IFEQ:
				return "ifeq";
			case BYTE_CODE_IFNE:
				return "ifne";
			case BYTE_CODE_IFLT:
				return "iflt";
			case BYTE_CODE_IFGE:
				return "ifge";
			case BYTE_CODE_IFGT:
				return "ifgt";
			case BYTE_CODE_IFLE:
				return "ifle";
			case BYTE_CODE_IFNONNULL:
				return "ifnonnull";
			case BYTE_CODE_IFNULL:
				return "ifnull";
			case BYTE_CODE_IINC:
				return "iinc";
			case BYTE_CODE_ILOAD:
				return "iload";
			case BYTE_CODE_ILOAD_0:
				return "iload_0";
			case BYTE_CODE_ILOAD_1:
				return "iload_1";
			case BYTE_CODE_ILOAD_2:
				return "iload_2";
			case BYTE_CODE_ILOAD_3:
				return "iload_3";
			case BYTE_CODE_IMUL:
				return "imul";
			case BYTE_CODE_INEG:
				return "ineg";
			case BYTE_CODE_INSTANCEOF:
				return "instanceof";
			case BYTE_CODE_INVOKEDYNAMIC:
				return "invokedynamic";
			case BYTE_CODE_INVOKEINTERFACE:
				return "invokeinterface";
			case BYTE_CODE_INVOKESPECIAL:
				return "invokespecial";
			case BYTE_CODE_INVOKESTATIC:
				return "invokestatic";
			case BYTE_CODE_INVOKEVIRTUAL:
				return "invokevirtual";
			case BYTE_CODE_IOR:
				return "ior";
			case BYTE_CODE_IREM:
				return "irem";
			case BYTE_CODE_IRETURN:
				return "ireturn";
			case BYTE_CODE_ISHL:
				return "ishl";
			case BYTE_CODE_ISHR:
				return "ishr";
			case BYTE_CODE_ISTORE:
				return "istore";
			case BYTE_CODE_ISTORE_0:
				return "istore_0";
			case BYTE_CODE_ISTORE_1:
				return "istore_1";
			case BYTE_CODE_ISTORE_2:
				return "istore_2";
			case BYTE_CODE_ISTORE_3:
				return "istore_3";
			case BYTE_CODE_ISUB:
				return "isub";
			case BYTE_CODE_IUSHR:
				return "iushr";
			case BYTE_CODE_IXOR:
				return "ixor";
			case BYTE_CODE_JSR:
				return "jsr";
			case BYTE_CODE_JSR_W:
				return "jsr_w";
			case BYTE_CODE_L2D:
				return "l2d";
			case BYTE_CODE_L2F:
				return "l2f";
			case BYTE_CODE_L2I:
				return "l2i";
			case BYTE_CODE_LADD:
				return "ladd";
			case BYTE_CODE_LALOAD:
				return "laload";
			case BYTE_CODE_LAND:
				return "land";
			case BYTE_CODE_LASTORE:
				return "lastore";
			case BYTE_CODE_LCMP:
				return "lcmp";
			case BYTE_CODE_LCONST_0:
				return "lconst_0";
			case BYTE_CODE_LCONST_1:
				return "lconst_1";
			case BYTE_CODE_LDC:
				return "ldc";
			case BYTE_CODE_LDC_W:
				return "ldc_w";
			case BYTE_CODE_LDC2_W:
				return "ldc2_w";
			case BYTE_CODE_LDIV:
				return "ldiv";
			case BYTE_CODE_LLOAD:
				return "lload";
			case BYTE_CODE_LLOAD_0:
				return "lload_0";
			case BYTE_CODE_LLOAD_1:
				return "lload_1";
			case BYTE_CODE_LLOAD_2:
				return "lload_2";
			case BYTE_CODE_LLOAD_3:
				return "lload_3";
			case BYTE_CODE_LMUL:
				return "lmul";
			case BYTE_CODE_LNEG:
				return "lneg";
			case BYTE_CODE_LOOKUPSWITCH:
				return "lookupswitch";
			case BYTE_CODE_LOR:
				return "lor";
			case BYTE_CODE_LREM:
				return "lrem";
			case BYTE_CODE_LRETURN:
				return "lreturn";
			case BYTE_CODE_LSHL:
				return "lshl";
			case BYTE_CODE_LSHR:
				return "lshr";
			case BYTE_CODE_LSTORE:
				return "lstore";
			case BYTE_CODE_LSTORE_0:
				return "lstore_0";
			case BYTE_CODE_LSTORE_1:
				return "lstore_1";
			case BYTE_CODE_LSTORE_2:
				return "lstore_2";
			case BYTE_CODE_LSTORE_3:
				return "lstore_3";
			case BYTE_CODE_LSUB:
				return "lsub";
			case BYTE_CODE_LUSHR:
				return "lushr";
			case BYTE_CODE_LXOR:
				return "lxor";
			case BYTE_CODE_MONITORENTER:
				return "monitorenter";
			case BYTE_CODE_MONITOREXIT:
				return "monitorexit";
			case BYTE_CODE_MULTIANEWARRAY:
				return "multianewarray";
			case BYTE_CODE_NEW:
				return "new";
			case BYTE_CODE_NEWARRAY:
				return "newarray";
			case BYTE_CODE_NOP:
				return "nop";
			case BYTE_CODE_POP:
				return "pop";
			case BYTE_CODE_POP2:
				return "pop2";
			case BYTE_CODE_PUTFIELD:
				return "putfield";
			case BYTE_CODE_PUTSTATIC:
				return "putstatic";
			case BYTE_CODE_RET:
				return "ret";
			case BYTE_CODE_RETURN:
				return "return";
			case BYTE_CODE_SALOAD:
				return "saload";
			case BYTE_CODE_SASTORE:
				return "sastore";
			case BYTE_CODE_SIPUSH:
				return "sipush";
			case BYTE_CODE_SWAP:
				return "swap";
			case BYTE_CODE_TABLESWITCH:
				return "tableswitch";
			case BYTE_CODE_WIDE:
				return "wide";
			case BYTE_CODE_BREAKPOINT:
				return "breakpoint";
			case BYTE_CODE_IMPDEP1:
				return "impdep1";
			case BYTE_CODE_IMPDEP2:
				return "impdep2";
			default:
				return SH.toString(code);
		}
	}
	public static String typeAsString(byte constType) {
		switch (constType) {
			case CONSTANT_UTF8:
				return "ascii";
			case CONSTANT_INT:
				return "int";
			case CONSTANT_FLOAT:
				return "float";
			case CONSTANT_FIELD_REF:
				return "field";
			case CONSTANT_METHOD_REF:
				return "method";
			case CONSTANT_INTERFACE_METHOD_REF:
				return "interfaceMethod";
			case CONSTANT_NAME_AND_TYPE:
				return "nameAndType";
			case CONSTANT_CLASS_REF:
				return "class";
			case CONSTANT_STRING:
				return "string";
			case CONSTANT_DOUBLE:
				return "double";
			case CONSTANT_LONG:
				return "long";
			default:
				return SH.toString(constType);
		}
	}
	private static final int[] SLASH_OR_SEMI = new int[] { '/', ';' };

	public static String parseClassName(String text) {
		return SH.replaceAll(text, '/', '.');
	}
	public static String parseClassDescriptor(String text) {
		CharReader cr = new StringCharReader(text);
		StringBuilder buf = new StringBuilder();
		int dimensions = 0;
		String arg;
		outer: for (;;) {
			int c = cr.readCharOrEof();
			switch (c) {
				case RH.JAVA_SIGNATURE_CLASS:
					while (cr.readUntilAny(SLASH_OR_SEMI, buf) == '/') {
						buf.append('.');
						cr.expect('/');
					}
					cr.expect(';');
					arg = buf.toString();
					break;
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
			break;
		}
		if (dimensions > 0) {
			buf.append(arg);
			SH.repeat("[]", dimensions, buf);
			return buf.toString();
		} else {
			return arg;
		}
	}
	public static List<String> parseArguments(String text) {
		CharReader cr = new StringCharReader(text);
		StringBuilder buf = new StringBuilder();
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
				case RH.JAVA_SIGNATURE_CLASS:
					while (cr.readUntilAny(SLASH_OR_SEMI, buf) == '/') {
						buf.append('.');
						cr.expect('/');
					}
					cr.expect(';');
					arg = SH.toStringAndClear(buf);
					break;
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

	public static String newarrayCodeToString(int code) {
		switch (code) {
			case 4:
				return "boolean";
			case 5:
				return "char";
			case 6:
				return "float";
			case 7:
				return "double";
			case 8:
				return "byte";
			case 9:
				return "short";
			case 10:
				return "int";
			case 11:
				return "long";
		}
		return SH.toString(code);
	}

}
