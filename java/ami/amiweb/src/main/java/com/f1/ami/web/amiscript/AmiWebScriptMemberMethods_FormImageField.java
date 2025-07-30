package com.f1.ami.web.amiscript;

import java.awt.image.BufferedImage;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.ImageQueryField;
import com.f1.base.Bytes;
import com.f1.utils.ImageHelper;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormImageField extends AmiWebScriptBaseMemberMethods<ImageQueryField> {

	private AmiWebScriptMemberMethods_FormImageField() {
		super();

		addMethod(SET_VALUE);
		addMethod(GET_VALUE);
	}

	private static final AmiAbstractMemberMethod<ImageQueryField> SET_VALUE = new AmiAbstractMemberMethod<ImageQueryField>(ImageQueryField.class, "setValue", Boolean.class,
			BufferedImage.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ImageQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				BufferedImage img = (BufferedImage) params[0];
				targetObject.updateHtml(new Bytes(ImageHelper.fromImage(img, "png"))); // TODO: remove hardcoded format
				return true;
			} catch (Exception e) {
				LH.warning(log, "Exception setting value in FormImageField ", e);
			}
			return false;
		}

		@Override
		protected String getHelp() {
			return "Sets the image field with data contained by the Image object. Returns true if operation is successful, false otherwise.";
		}

		protected String[] buildParamNames() {
			return new String[] { "img" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Image object. Example: imageField.setValue(img)" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<ImageQueryField> GET_VALUE = new AmiAbstractMemberMethod<ImageQueryField>(ImageQueryField.class, "getValue", BufferedImage.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ImageQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns an Image object. Null if operation is not successful.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormImageField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Object for image fields";
	}

	@Override
	public Class<ImageQueryField> getVarType() {
		return ImageQueryField.class;
	}

	@Override
	public Class<ImageQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormImageField INSTANCE = new AmiWebScriptMemberMethods_FormImageField();
}
