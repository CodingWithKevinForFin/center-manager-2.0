package com.f1.ami.amiscript;

import com.f1.base.Bytes;
import com.f1.base.DateMillis;
import com.f1.base.CalcFrame;
import com.f1.utils.EH;
import com.f1.utils.SH;
import com.f1.utils.ZipBuilder;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_ZipBuilder extends AmiScriptBaseMemberMethods<ZipBuilder> {

	private AmiScriptMemberMethods_ZipBuilder() {
		super();

		addMethod(INIT);
		addMethod(ADD_FILE);
		addMethod(ADD_FILE2);
		addMethod(BUILD);
	}

	private static final AmiAbstractMemberMethod<ZipBuilder> INIT = new AmiAbstractMemberMethod<ZipBuilder>(ZipBuilder.class, null, ZipBuilder.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ZipBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			ZipBuilder r = new ZipBuilder();
			return r;
		}
		@Override
		protected String getHelp() {
			return "Constructs a new empty ZipBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<ZipBuilder> ADD_FILE = new AmiAbstractMemberMethod<ZipBuilder>(ZipBuilder.class, "addFile", Boolean.class, String.class,
			Bytes.class) {

		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, ZipBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.isBuilt())
				return false;
			String fileName = (String) params[0];
			Bytes data = (Bytes) params[1];
			if (SH.isnt(fileName) || data == null)
				return false;
			targetObject.append(fileName, data.getBytes(), EH.currentTimeMillis());
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "fileName", "data" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "fileName", "data" };
		}
		@Override
		protected String getHelp() {
			return "Adds the file as an entry to the zip file given the filename and data. Returns false if filename is not valid, data is null or the build() has already been called, which indicates the file was not added. Otherwise returns true.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<ZipBuilder> ADD_FILE2 = new AmiAbstractMemberMethod<ZipBuilder>(ZipBuilder.class, "addFile", Boolean.class, String.class,
			Bytes.class, DateMillis.class) {

		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, ZipBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.isBuilt())
				return false;
			String fileName = (String) params[0];
			Bytes data = (Bytes) params[1];
			DateMillis modtime = (DateMillis) params[2];
			if (SH.isnt(fileName) || data == null)
				return false;
			targetObject.append(fileName, data.getBytes(), modtime == null ? EH.currentTimeMillis() : modtime.getDate());
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "fileName", "data", "modifiedTimestamp" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "fileName of the entry to add", "data of the entry to add", "modified timestamp of the entry to add" };
		}
		@Override
		protected String getHelp() {
			return "Adds the file as an entry to the zip file given the filename, data, and the modified timestamp. Returns false if filename is not valid, data is null or the build() has already been called, which indicates the file was not added. Otherwise returns true.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<ZipBuilder> BUILD = new AmiAbstractMemberMethod<ZipBuilder>(ZipBuilder.class, "build", Bytes.class) {

		@Override
		public Bytes invokeMethod2(CalcFrameStack sf, ZipBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new Bytes(targetObject.build());
		}
		@Override
		protected String getHelp() {
			return "Returns a Byte representing a zip file.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "ZipBuilder";
	}
	@Override
	public String getVarTypeDescription() {
		return "Used for Building a zip file by adding individual files";
	}
	@Override
	public Class<ZipBuilder> getVarType() {
		return ZipBuilder.class;
	}
	@Override
	public Class<? extends ZipBuilder> getVarDefaultImpl() {
		return ZipBuilder.class;
	}

	public static AmiScriptMemberMethods_ZipBuilder INSTANCE = new AmiScriptMemberMethods_ZipBuilder();
}
