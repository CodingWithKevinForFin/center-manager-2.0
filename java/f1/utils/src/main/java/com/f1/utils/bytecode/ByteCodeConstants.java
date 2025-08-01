package com.f1.utils.bytecode;


//based on http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.2
public class ByteCodeConstants {

	final public static String ATTR_CODE = "Code";
	final public static String ATTR_CONSTANT_VALUE = "ConstantValue";
	final public static String ATTR_SIGNATURE = "Signature";
	final public static String ATTR_RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
	final public static String ATTR_RUNTIME_INVISIBLE_ANNOTATIONS = "RuntimeInvisibleAnnotations";

	final public static int CONSTANT_UTF8 = 1;
	final public static int CONSTANT_INT = 3;
	final public static int CONSTANT_FLOAT = 4;
	final public static int CONSTANT_LONG = 5;
	final public static int CONSTANT_DOUBLE = 6;
	final public static int CONSTANT_CLASS_REF = 7;
	final public static int CONSTANT_STRING = 8;
	final public static int CONSTANT_FIELD_REF = 9;
	final public static int CONSTANT_METHOD_REF = 10;
	final public static int CONSTANT_INTERFACE_METHOD_REF = 11;
	final public static int CONSTANT_NAME_AND_TYPE = 12;
	final public static int CONSTANT_METHOD_HANDLE = 15;
	final public static int CONSTANT_METHOD_TYPE = 16;
	final public static int CONSTANT_INVOKE_DYNAMIC = 18;

	final public static int ACC_PUBLIC = 0x0001;
	final public static int ACC_PRIVATE = 0x0002;
	final public static int ACC_PROTECTED = 0x0004;
	final public static int ACC_STATIC = 0x0008;
	final public static int ACC_FINAL = 0x0010;
	final public static int ACC_SUPER_OR_SYNCHRONIZED = 0x0020;//are they this desperate to conserve bits?
	final public static int ACC_BRIDGE = 0x0040;
	final public static int ACC_VARARGS = 0x0080;
	final public static int ACC_NATIVE = 0x0100;
	final public static int ACC_INTERFACE = 0x0200;
	final public static int ACC_ABSTRACT = 0x0400;
	final public static int ACC_STRICT = 0x0800;
	final public static int ACC_SYNTHETIC = 0x1000;
	final public static int ACC_ANNOTATION = 0x2000;
	final public static int ACC_ENUM = 0x4000;

	final public static byte BYTE_CODE_AALOAD = (byte) 0x32;
	final public static byte BYTE_CODE_AASTORE = (byte) 0x53;
	final public static byte BYTE_CODE_ACONST_NULL = (byte) 0x1;
	final public static byte BYTE_CODE_ALOAD = (byte) 0x19;
	final public static byte BYTE_CODE_ALOAD_0 = (byte) 0x2a;
	final public static byte BYTE_CODE_ALOAD_1 = (byte) 0x2b;
	final public static byte BYTE_CODE_ALOAD_2 = (byte) 0x2c;
	final public static byte BYTE_CODE_ALOAD_3 = (byte) 0x2d;
	final public static byte BYTE_CODE_ANEWARRAY = (byte) 0xbd;
	final public static byte BYTE_CODE_ARETURN = (byte) 0xb0;
	final public static byte BYTE_CODE_ARRAYLENGTH = (byte) 0xbe;
	final public static byte BYTE_CODE_ASTORE = (byte) 0x3a;
	final public static byte BYTE_CODE_ASTORE_0 = (byte) 0x4b;
	final public static byte BYTE_CODE_ASTORE_1 = (byte) 0x4c;
	final public static byte BYTE_CODE_ASTORE_2 = (byte) 0x4d;
	final public static byte BYTE_CODE_ASTORE_3 = (byte) 0x4e;
	final public static byte BYTE_CODE_ATHROW = (byte) 0xbf;
	final public static byte BYTE_CODE_BALOAD = (byte) 0x33;
	final public static byte BYTE_CODE_BASTORE = (byte) 0x54;
	final public static byte BYTE_CODE_BIPUSH = (byte) 0x10;
	final public static byte BYTE_CODE_CALOAD = (byte) 0x34;
	final public static byte BYTE_CODE_CASTORE = (byte) 0x55;
	final public static byte BYTE_CODE_CHECKCAST = (byte) 0xc0;
	final public static byte BYTE_CODE_D2F = (byte) 0x90;
	final public static byte BYTE_CODE_D2I = (byte) 0x8e;
	final public static byte BYTE_CODE_D2L = (byte) 0x8f;
	final public static byte BYTE_CODE_DADD = (byte) 0x63;
	final public static byte BYTE_CODE_DALOAD = (byte) 0x31;
	final public static byte BYTE_CODE_DASTORE = (byte) 0x52;
	final public static byte BYTE_CODE_DCMPG = (byte) 0x98;
	final public static byte BYTE_CODE_DCMPL = (byte) 0x97;
	final public static byte BYTE_CODE_DCONST_0 = (byte) 0x0e;
	final public static byte BYTE_CODE_DCONST_1 = (byte) 0x0f;
	final public static byte BYTE_CODE_DDIV = (byte) 0x6f;
	final public static byte BYTE_CODE_DLOAD = (byte) 0x18;
	final public static byte BYTE_CODE_DLOAD_0 = (byte) 0x26;
	final public static byte BYTE_CODE_DLOAD_1 = (byte) 0x27;
	final public static byte BYTE_CODE_DLOAD_2 = (byte) 0x28;
	final public static byte BYTE_CODE_DLOAD_3 = (byte) 0x29;
	final public static byte BYTE_CODE_DMUL = (byte) 0x6b;
	final public static byte BYTE_CODE_DNEG = (byte) 0x77;
	final public static byte BYTE_CODE_DREM = (byte) 0x73;
	final public static byte BYTE_CODE_DRETURN = (byte) 0xaf;
	final public static byte BYTE_CODE_DSTORE = (byte) 0x39;
	final public static byte BYTE_CODE_DSTORE_0 = (byte) 0x47;
	final public static byte BYTE_CODE_DSTORE_1 = (byte) 0x48;
	final public static byte BYTE_CODE_DSTORE_2 = (byte) 0x49;
	final public static byte BYTE_CODE_DSTORE_3 = (byte) 0x4a;
	final public static byte BYTE_CODE_DSUB = (byte) 0x67;
	final public static byte BYTE_CODE_DUP = (byte) 0x59;
	final public static byte BYTE_CODE_DUP_X1 = (byte) 0x5a;
	final public static byte BYTE_CODE_DUP_X2 = (byte) 0x5b;
	final public static byte BYTE_CODE_DUP2 = (byte) 0x5c;
	final public static byte BYTE_CODE_DUP2_X1 = (byte) 0x5d;
	final public static byte BYTE_CODE_DUP2_X2 = (byte) 0x5e;
	final public static byte BYTE_CODE_F2D = (byte) 0x8d;
	final public static byte BYTE_CODE_F2I = (byte) 0x8b;
	final public static byte BYTE_CODE_F2L = (byte) 0x8c;
	final public static byte BYTE_CODE_FADD = (byte) 0x62;
	final public static byte BYTE_CODE_FALOAD = (byte) 0x30;
	final public static byte BYTE_CODE_FASTORE = (byte) 0x51;
	final public static byte BYTE_CODE_FCMPG = (byte) 0x96;
	final public static byte BYTE_CODE_FCMPL = (byte) 0x95;
	final public static byte BYTE_CODE_FCONST_0 = (byte) 0x0b;
	final public static byte BYTE_CODE_FCONST_1 = (byte) 0x0c;
	final public static byte BYTE_CODE_FCONST_2 = (byte) 0x0d;
	final public static byte BYTE_CODE_FDIV = (byte) 0x6e;
	final public static byte BYTE_CODE_FLOAD = (byte) 0x17;
	final public static byte BYTE_CODE_FLOAD_0 = (byte) 0x22;
	final public static byte BYTE_CODE_FLOAD_1 = (byte) 0x23;
	final public static byte BYTE_CODE_FLOAD_2 = (byte) 0x24;
	final public static byte BYTE_CODE_FLOAD_3 = (byte) 0x25;
	final public static byte BYTE_CODE_FMUL = (byte) 0x6a;
	final public static byte BYTE_CODE_FNEG = (byte) 0x76;
	final public static byte BYTE_CODE_FREM = (byte) 0x72;
	final public static byte BYTE_CODE_FRETURN = (byte) 0xae;
	final public static byte BYTE_CODE_FSTORE = (byte) 0x38;
	final public static byte BYTE_CODE_FSTORE_0 = (byte) 0x43;
	final public static byte BYTE_CODE_FSTORE_1 = (byte) 0x44;
	final public static byte BYTE_CODE_FSTORE_2 = (byte) 0x45;
	final public static byte BYTE_CODE_FSTORE_3 = (byte) 0x46;
	final public static byte BYTE_CODE_FSUB = (byte) 0x66;
	final public static byte BYTE_CODE_GETFIELD = (byte) 0xb4;
	final public static byte BYTE_CODE_GETSTATIC = (byte) 0xb2;
	final public static byte BYTE_CODE_GOTO = (byte) 0xa7;
	final public static byte BYTE_CODE_GOTO_W = (byte) 0xc8;
	final public static byte BYTE_CODE_I2B = (byte) 0x91;
	final public static byte BYTE_CODE_I2C = (byte) 0x92;
	final public static byte BYTE_CODE_I2D = (byte) 0x87;
	final public static byte BYTE_CODE_I2F = (byte) 0x86;
	final public static byte BYTE_CODE_I2L = (byte) 0x85;
	final public static byte BYTE_CODE_I2S = (byte) 0x93;
	final public static byte BYTE_CODE_IADD = (byte) 0x60;
	final public static byte BYTE_CODE_IALOAD = (byte) 0x2e;
	final public static byte BYTE_CODE_IAND = (byte) 0x7e;
	final public static byte BYTE_CODE_IASTORE = (byte) 0x4f;
	final public static byte BYTE_CODE_ICONST_M1 = (byte) 0x2;
	final public static byte BYTE_CODE_ICONST_0 = (byte) 0x3;
	final public static byte BYTE_CODE_ICONST_1 = (byte) 0x4;
	final public static byte BYTE_CODE_ICONST_2 = (byte) 0x5;
	final public static byte BYTE_CODE_ICONST_3 = (byte) 0x6;
	final public static byte BYTE_CODE_ICONST_4 = (byte) 0x7;
	final public static byte BYTE_CODE_ICONST_5 = (byte) 0x8;
	final public static byte BYTE_CODE_IDIV = (byte) 0x6c;
	final public static byte BYTE_CODE_IF_ACMPEQ = (byte) 0xa5;
	final public static byte BYTE_CODE_IF_ACMPNE = (byte) 0xa6;
	final public static byte BYTE_CODE_IF_ICMPEQ = (byte) 0x9f;
	final public static byte BYTE_CODE_IF_ICMPNE = (byte) 0xa0;
	final public static byte BYTE_CODE_IF_ICMPLT = (byte) 0xa1;
	final public static byte BYTE_CODE_IF_ICMPGE = (byte) 0xa2;
	final public static byte BYTE_CODE_IF_ICMPGT = (byte) 0xa3;
	final public static byte BYTE_CODE_IF_ICMPLE = (byte) 0xa4;
	final public static byte BYTE_CODE_IFEQ = (byte) 0x99;
	final public static byte BYTE_CODE_IFNE = (byte) 0x9a;
	final public static byte BYTE_CODE_IFLT = (byte) 0x9b;
	final public static byte BYTE_CODE_IFGE = (byte) 0x9c;
	final public static byte BYTE_CODE_IFGT = (byte) 0x9d;
	final public static byte BYTE_CODE_IFLE = (byte) 0x9e;
	final public static byte BYTE_CODE_IFNONNULL = (byte) 0xc7;
	final public static byte BYTE_CODE_IFNULL = (byte) 0xc6;
	final public static byte BYTE_CODE_IINC = (byte) 0x84;
	final public static byte BYTE_CODE_ILOAD = (byte) 0x15;
	final public static byte BYTE_CODE_ILOAD_0 = (byte) 0x1a;
	final public static byte BYTE_CODE_ILOAD_1 = (byte) 0x1b;
	final public static byte BYTE_CODE_ILOAD_2 = (byte) 0x1c;
	final public static byte BYTE_CODE_ILOAD_3 = (byte) 0x1d;
	final public static byte BYTE_CODE_IMUL = (byte) 0x68;
	final public static byte BYTE_CODE_INEG = (byte) 0x74;
	final public static byte BYTE_CODE_INSTANCEOF = (byte) 0xc1;
	final public static byte BYTE_CODE_INVOKEDYNAMIC = (byte) 0xba;
	final public static byte BYTE_CODE_INVOKEINTERFACE = (byte) 0xb9;
	final public static byte BYTE_CODE_INVOKESPECIAL = (byte) 0xb7;
	final public static byte BYTE_CODE_INVOKESTATIC = (byte) 0xb8;
	final public static byte BYTE_CODE_INVOKEVIRTUAL = (byte) 0xb6;
	final public static byte BYTE_CODE_IOR = (byte) 0x80;
	final public static byte BYTE_CODE_IREM = (byte) 0x70;
	final public static byte BYTE_CODE_IRETURN = (byte) 0xac;
	final public static byte BYTE_CODE_ISHL = (byte) 0x78;
	final public static byte BYTE_CODE_ISHR = (byte) 0x7a;
	final public static byte BYTE_CODE_ISTORE = (byte) 0x36;
	final public static byte BYTE_CODE_ISTORE_0 = (byte) 0x3b;
	final public static byte BYTE_CODE_ISTORE_1 = (byte) 0x3c;
	final public static byte BYTE_CODE_ISTORE_2 = (byte) 0x3d;
	final public static byte BYTE_CODE_ISTORE_3 = (byte) 0x3e;
	final public static byte BYTE_CODE_ISUB = (byte) 0x64;
	final public static byte BYTE_CODE_IUSHR = (byte) 0x7c;
	final public static byte BYTE_CODE_IXOR = (byte) 0x82;
	final public static byte BYTE_CODE_JSR = (byte) 0xa8;
	final public static byte BYTE_CODE_JSR_W = (byte) 0xc9;
	final public static byte BYTE_CODE_L2D = (byte) 0x8a;
	final public static byte BYTE_CODE_L2F = (byte) 0x89;
	final public static byte BYTE_CODE_L2I = (byte) 0x88;
	final public static byte BYTE_CODE_LADD = (byte) 0x61;
	final public static byte BYTE_CODE_LALOAD = (byte) 0x2f;
	final public static byte BYTE_CODE_LAND = (byte) 0x7f;
	final public static byte BYTE_CODE_LASTORE = (byte) 0x50;
	final public static byte BYTE_CODE_LCMP = (byte) 0x94;
	final public static byte BYTE_CODE_LCONST_0 = (byte) 0x9;
	final public static byte BYTE_CODE_LCONST_1 = (byte) 0x0a;
	final public static byte BYTE_CODE_LDC = (byte) 0x12;
	final public static byte BYTE_CODE_LDC_W = (byte) 0x13;
	final public static byte BYTE_CODE_LDC2_W = (byte) 0x14;
	final public static byte BYTE_CODE_LDIV = (byte) 0x6d;
	final public static byte BYTE_CODE_LLOAD = (byte) 0x16;
	final public static byte BYTE_CODE_LLOAD_0 = (byte) 0x1e;
	final public static byte BYTE_CODE_LLOAD_1 = (byte) 0x1f;
	final public static byte BYTE_CODE_LLOAD_2 = (byte) 0x20;
	final public static byte BYTE_CODE_LLOAD_3 = (byte) 0x21;
	final public static byte BYTE_CODE_LMUL = (byte) 0x69;
	final public static byte BYTE_CODE_LNEG = (byte) 0x75;
	final public static byte BYTE_CODE_LOOKUPSWITCH = (byte) 0xab;
	final public static byte BYTE_CODE_LOR = (byte) 0x81;
	final public static byte BYTE_CODE_LREM = (byte) 0x71;
	final public static byte BYTE_CODE_LRETURN = (byte) 0xad;
	final public static byte BYTE_CODE_LSHL = (byte) 0x79;
	final public static byte BYTE_CODE_LSHR = (byte) 0x7b;
	final public static byte BYTE_CODE_LSTORE = (byte) 0x37;
	final public static byte BYTE_CODE_LSTORE_0 = (byte) 0x3f;
	final public static byte BYTE_CODE_LSTORE_1 = (byte) 0x40;
	final public static byte BYTE_CODE_LSTORE_2 = (byte) 0x41;
	final public static byte BYTE_CODE_LSTORE_3 = (byte) 0x42;
	final public static byte BYTE_CODE_LSUB = (byte) 0x65;
	final public static byte BYTE_CODE_LUSHR = (byte) 0x7d;
	final public static byte BYTE_CODE_LXOR = (byte) 0x83;
	final public static byte BYTE_CODE_MONITORENTER = (byte) 0xc2;
	final public static byte BYTE_CODE_MONITOREXIT = (byte) 0xc3;
	final public static byte BYTE_CODE_MULTIANEWARRAY = (byte) 0xc5;
	final public static byte BYTE_CODE_NEW = (byte) 0xbb;
	final public static byte BYTE_CODE_NEWARRAY = (byte) 0xbc;
	final public static byte BYTE_CODE_NOP = (byte) 0x0;
	final public static byte BYTE_CODE_POP = (byte) 0x57;
	final public static byte BYTE_CODE_POP2 = (byte) 0x58;
	final public static byte BYTE_CODE_PUTFIELD = (byte) 0xb5;
	final public static byte BYTE_CODE_PUTSTATIC = (byte) 0xb3;
	final public static byte BYTE_CODE_RET = (byte) 0xa9;
	final public static byte BYTE_CODE_RETURN = (byte) 0xb1;
	final public static byte BYTE_CODE_SALOAD = (byte) 0x35;
	final public static byte BYTE_CODE_SASTORE = (byte) 0x56;
	final public static byte BYTE_CODE_SIPUSH = (byte) 0x11;
	final public static byte BYTE_CODE_SWAP = (byte) 0x5f;
	final public static byte BYTE_CODE_TABLESWITCH = (byte) 0xaa;
	final public static byte BYTE_CODE_WIDE = (byte) 0xc4;
	final public static byte BYTE_CODE_BREAKPOINT = (byte) 0xca;
	final public static byte BYTE_CODE_IMPDEP1 = (byte) 0xfe;
	final public static byte BYTE_CODE_IMPDEP2 = (byte) 0xff;

}
