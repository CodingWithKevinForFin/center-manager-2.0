package com.f1.utils.bytecode;

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
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_LOOKUPSWITCH;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_LSTORE;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_MULTIANEWARRAY;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_NEW;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_NEWARRAY;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_PUTFIELD;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_PUTSTATIC;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_RET;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_SIPUSH;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_TABLESWITCH;
import static com.f1.utils.bytecode.ByteCodeConstants.BYTE_CODE_WIDE;

import java.io.DataInput;
import java.io.IOException;

import com.f1.utils.SH;
import com.f1.utils.ToDoException;

public class ByteCodeCode {

	final private byte[] code;
	final private ExceptionDef[] exceptions;
	final private int maxStack;
	final private int maxLocals;
	private ByteCodeClass clazz;

	public ByteCodeCode(DataInput dis, ByteCodeMethod method) throws IOException {
		this.maxStack = dis.readUnsignedShort();
		maxLocals = dis.readUnsignedShort();
		int codeLength = dis.readInt();
		this.code = new byte[codeLength];
		dis.readFully(code);
		final int exceptionsCount = dis.readUnsignedShort();
		ExceptionDef[] exceptions = new ExceptionDef[exceptionsCount];
		for (int i = 0; i < exceptionsCount; i++) {
			int startPc = dis.readUnsignedShort();
			int endPc = dis.readUnsignedShort();
			int handlerPc = dis.readUnsignedShort();
			int catchTypeIndex = dis.readUnsignedShort();
			ByteCodeConstClassRef catchType = catchTypeIndex == 0 ? null : method.getOwner().getClassRef(catchTypeIndex);
			exceptions[i] = new ExceptionDef(startPc, endPc, handlerPc, catchType);
		}
		this.exceptions = exceptions;
		this.clazz = method.getOwner();
	}

	public static class ExceptionDef {
		final private int startPc;
		final private int endPc;
		final private int handlerPc;
		final private ByteCodeConstClassRef catchType;

		public ExceptionDef(int startPc, int endPc, int handlerPc, ByteCodeConstClassRef catchType) {
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

		public ByteCodeConstClassRef getCatchType() {
			return catchType;
		}

	}

	public StringBuilder toJavaString(String indent, StringBuilder sb) {
		for (int i = 0; i < code.length; i++) {
			byte b = code[i];
			sb.append(indent);
			SH.rightAlign(' ', SH.toString(i), 4, false, sb);
			sb.append(": ").append(ByteCodeUtils.byteCodeToString(b)).append(' ');
			int skip = 0;
			switch (b) {
				case BYTE_CODE_ALOAD: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_ANEWARRAY: {
					ByteCodeConstClassRef m = clazz.getClassRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_ASTORE: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_BIPUSH: {
					sb.append(toSignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_CHECKCAST: {
					ByteCodeConstClassRef m = clazz.getClassRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_DLOAD: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_DSTORE: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_FLOAD: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_FSTORE: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_GETFIELD: {
					ByteCodeConstFieldRef m = clazz.getFieldRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_GETSTATIC: {
					ByteCodeConstFieldRef m = clazz.getFieldRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_GOTO: {
					sb.append((i + toSignedShort(code[i + 1], code[i + 2])));
					skip = 2;
					break;
				}
				case BYTE_CODE_GOTO_W: {
					sb.append((i + toSignedInt(code[i + 1], code[i + 2], code[i + 3], code[i + 4])));
					skip = 4;
					break;
				}
				case BYTE_CODE_IF_ACMPEQ: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IF_ACMPNE: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IF_ICMPEQ: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IF_ICMPNE: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IF_ICMPLT: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IF_ICMPGE: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IF_ICMPGT: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IF_ICMPLE: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFEQ: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFNE: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFLT: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFGE: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFGT: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFLE: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFNONNULL: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IFNULL: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_IINC: {
					skip = 2;
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					break;
				}
				case BYTE_CODE_ILOAD: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_INSTANCEOF: {
					ByteCodeConstClassRef m = clazz.getClassRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_INVOKEDYNAMIC: {
					ByteCodeConstMethodRef m = clazz.getMethodRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 4;
					break;
				}
				case BYTE_CODE_INVOKEINTERFACE: {
					ByteCodeConstMethodRef m = clazz.getMethodRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 4;
					break;
				}
				case BYTE_CODE_INVOKESPECIAL: {
					ByteCodeConstMethodRef m = clazz.getMethodRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_INVOKESTATIC: {
					ByteCodeConstMethodRef m = clazz.getMethodRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_INVOKEVIRTUAL: {
					ByteCodeConstMethodRef m = clazz.getMethodRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_ISTORE: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_JSR: {
					sb.append(toUnsignedByte(code[i + 1])).append(' ').append(toSignedByte(code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_JSR_W: {
					sb.append((i + toSignedInt(code[i + 1], code[i + 2], code[i + 3], code[i + 4])));
					skip = 4;
					break;
				}
				case BYTE_CODE_LDC: {
					sb.append(clazz.getConstValued(toUnsignedByte(code[i + 1])).getValue());
					skip = 1;
					break;
				}
				case BYTE_CODE_LDC_W: {
					sb.append(clazz.getConstValued(toUnsignedInt(code[i + 1], code[i + 2])).getValue());
					skip = 2;
					break;
				}
				case BYTE_CODE_LDC2_W: {
					sb.append(clazz.getConstValued(toUnsignedInt(code[i + 1], code[i + 2])).getValue());
					skip = 2;
					break;
				}
				case BYTE_CODE_LLOAD: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_LSTORE: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_MULTIANEWARRAY: {
					sb.append(clazz.getClassRef(toUnsignedInt(code[i + 1], code[i + 2])).getClassNameText());
					int dimensions = toUnsignedByte(code[i + 3]);
					SH.repeat("[]", dimensions, sb);
					skip = 3;
					break;
				}
				case BYTE_CODE_NEW: {
					sb.append(clazz.getClassRef(toUnsignedInt(code[i + 1], code[i + 2])).getClassNameText());
					skip = 2;
					break;
				}
				case BYTE_CODE_NEWARRAY: {
					sb.append(ByteCodeUtils.newarrayCodeToString(toUnsignedByte(code[i + 1])));
					skip = 1;
					break;
				}
				case BYTE_CODE_PUTFIELD: {
					ByteCodeConstFieldRef m = clazz.getFieldRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_PUTSTATIC: {
					ByteCodeConstFieldRef m = clazz.getFieldRef(toUnsignedInt(code[i + 1], code[i + 2]));
					m.toJavaString(sb);
					skip = 2;
					break;
				}
				case BYTE_CODE_RET: {
					sb.append("lcv").append(toUnsignedByte(code[i + 1]));
					skip = 1;
					break;
				}
				case BYTE_CODE_SIPUSH: {
					sb.append(toSignedShort(code[i + 1], code[i + 2]));
					skip = 2;
					break;
				}
				case BYTE_CODE_LOOKUPSWITCH: {
					int padding = 3 - (i % 4);
					int j = 1 + i + padding;
					int dfltAddr = i + (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
					j += 4;
					int n = (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
					skip = padding + 8 + n * 8;//each n represents a pair of 32-bit ints which is 8 bytes
					for (int k = 0; k < n; k++) {
						j += 4;
						int key = (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
						j += 4;
						int addr = i + (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
						sb.append(' ').append(key).append(":").append(addr);
					}
					sb.append(" dflt: ").append(dfltAddr);

					break;
				}
				case BYTE_CODE_TABLESWITCH: {
					int padding = 3 - (i % 4);
					int j = 1 + i + padding;
					int dfltAddr = i + (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
					j += 4;
					int lowVal = (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
					j += 4;
					int highVal = (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
					j += 4;
					int n = highVal - lowVal + 1;
					skip = padding + 12 + n * 4;//each n represents a pair of 32-bit ints which is 8 bytes
					for (int k = 0; k < n; k++) {
						int key = lowVal + k;
						int addr = i + (int) toUnsignedInt(code[j], code[j + 1], code[j + 2], code[j + 3]);
						j += 4;
						sb.append(' ').append(key).append(":").append(addr);
					}
					sb.append(" dflt: ").append(dfltAddr);

					break;
				}
				case BYTE_CODE_WIDE:
					throw new ToDoException();
				//TODO: case BYTE_CODE_LOOKUPSWITCH: skip = 4+;break;
				//TODO: case BYTE_CODE_TABLESWITCH: skip = 4+;break;
				//TODO: case BYTE_CODE_WIDE: skip = 3 or 5;
			}
			i += skip;
			sb.append(SH.NEWLINE);
		}
		return sb;
	}
	private int toSignedShort(byte b1, byte b2) {
		return (b1 << 8) + (b2 & 0xff);
	}
	private int toSignedInt(byte b1, byte b2, byte b3, byte b4) {
		return (b1 << 24) + ((b2 & 0xff) << 16) + ((b3 & 0xff) << 8) + (b4 & 0xff);
	}
	private long toUnsignedInt(byte b1, byte b2, byte b3, byte b4) {
		return ((0xffL & b1) << 24) + ((b2 & 0xffL) << 16) + ((b3 & 0xffL) << 8) + (b4 & 0xffL);
	}

	private int toUnsignedByte(byte b) {
		return 0xff & b;
	}

	private byte toSignedByte(byte b) {
		return b;
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

	static public int toUnsignedInt(byte b1, byte b2) {
		return ((0xff & b1) << 8) + (0xff & b2);
	}
}
