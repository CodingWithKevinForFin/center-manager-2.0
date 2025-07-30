package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.UploadQueryField;
import com.f1.base.Bytes;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField.FileData;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormUploadField<T> extends AmiWebScriptBaseMemberMethods<UploadQueryField> {

	private AmiWebScriptMemberMethods_FormUploadField() {
		super();

		addMethod(GET_FILE_CONTEXT_TYPE, "fileContent");
		addMethod(GET_FILE_BINARY_DATA, "fileBinaryData");
		addMethod(GET_FILE_TEXT_DATA, "fileTextData");
		addMethod(GET_FILE_NAME, "fileName");
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	public static final AmiAbstractMemberMethod<UploadQueryField> GET_FILE_CONTEXT_TYPE = new AmiAbstractMemberMethod<UploadQueryField>(UploadQueryField.class,
			"getFileContentType", String.class) {
		@Override
		public String invokeMethod2(CalcFrameStack sf, UploadQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FileData t = targetObject.getField().getValue();
			return t == null ? null : t.getContentType();
		}
		@Override
		protected String getHelp() {
			return "Returns the content type of the uploaded file, or null if no file is selected";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<UploadQueryField> GET_FILE_BINARY_DATA = new AmiAbstractMemberMethod<UploadQueryField>(UploadQueryField.class, "getFileBinaryData",
			Bytes.class) {
		@Override
		public Bytes invokeMethod2(CalcFrameStack sf, UploadQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FileData t = targetObject.getField().getValue();
			return t == null ? null : new Bytes(t.getData());
		}
		@Override
		protected String getHelp() {
			return "Returns the contents of the uploaded file as binary data, or null if no file is selected";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<UploadQueryField> GET_FILE_TEXT_DATA = new AmiAbstractMemberMethod<UploadQueryField>(UploadQueryField.class, "getFileTextData",
			String.class) {
		@Override
		public String invokeMethod2(CalcFrameStack sf, UploadQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FileData t = targetObject.getField().getValue();
			return t == null ? null : t.getDataText();
		}
		@Override
		protected String getHelp() {
			return "Returns the contents of the uploaded file as text, or null if no file is selected";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<UploadQueryField> GET_FILE_NAME = new AmiAbstractMemberMethod<UploadQueryField>(UploadQueryField.class, "getFileName",
			String.class) {
		@Override
		public String invokeMethod2(CalcFrameStack sf, UploadQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FileData t = targetObject.getField().getValue();
			return t == null ? null : t.getName();
		}
		@Override
		protected String getHelp() {
			return "Returns the name of the uploaded file, or null if no file is selected";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormUploadField";
	}

	@Override
	public String getVarTypeDescription() {
		return "A special type of field within a FormPanel that allows uploading a file";
	}

	@Override
	public Class<UploadQueryField> getVarType() {
		return UploadQueryField.class;
	}

	@Override
	public Class<UploadQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormUploadField INSTANCE = new AmiWebScriptMemberMethods_FormUploadField();

}
