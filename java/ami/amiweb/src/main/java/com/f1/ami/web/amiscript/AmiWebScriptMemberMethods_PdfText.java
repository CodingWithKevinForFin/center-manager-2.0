package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.pdf.PdfText;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_PdfText extends AmiWebScriptBaseMemberMethods<PdfText> {

	private AmiWebScriptMemberMethods_PdfText() {
		super();
		addMethod(SET_HORIZONTAL_ALIGNMENT);
		addMethod(GET_HORIZONTAL_ALIGNMENT, "horizontalAlignment");
		addMethod(GET_SPACING_BEFORE, "spacingBefore");
		addMethod(GET_SPACING_AFTER, "spacingAfter");
		addMethod(SET_LINE_SPACING);
		addMethod(GET_LINE_SPACING, "lineSpacing");
		addMethod(SET_FONT);
		addMethod(SET_SUBSCRIPT);
		addMethod(SET_SUPERSCRIPT);
		addMethod(GET_SUBSCRIPT);
		addMethod(GET_SUPERSCRIPT);
		addMethod(GET_FONT, "font");
		addMethod(APPEND_TEXT);
	}

	private static final AmiAbstractMemberMethod<PdfText> APPEND_TEXT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "appendText", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.appendText((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "text" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "text" };
		}
		@Override
		protected String getHelp() {
			return "append text to the pdf";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> SET_FONT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "setFont", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setFont((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "font" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "any combination of size (in inches),family,color,style, ex: 15 Helvetica bold intalic #FF0000" };
		}
		@Override
		protected String getHelp() {
			return "set current font for use with text. Families include: Courier, Helvetica, Times-Roman,  Symbol, ZapfDingbats. Styles include: Bold, Italic, Underline, Line-through  ";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> SET_HORIZONTAL_ALIGNMENT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "setHorizontalAlignment", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setHorizontalAlignment((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "alignment" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "either: LEFT, CENTER, RIGHT" };
		}
		@Override
		protected String getHelp() {
			return "set current alignment, note this will create a line break if a paragraph is in progress";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> GET_HORIZONTAL_ALIGNMENT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "getHorizontalAlignment", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHorizontalAlignment();
		}

		@Override
		protected String getHelp() {
			return "get current alignment (either: LEFT, CENTER, RIGHT)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<PdfText> GET_FONT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "getFont", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFont();
		}
		@Override
		protected String getHelp() {
			return "get current font for use with text";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> GET_SPACING_BEFORE = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "getSpacingBefore", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSpacingBefore();
		}

		@Override
		protected String getHelp() {
			return "get spacing before paragraphs, tables, images in inches";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<PdfText> GET_SPACING_AFTER = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "getSpacingAfter", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSpacingAfter();
		}

		@Override
		protected String getHelp() {
			return "get spacing after paragraphs, tables, images in inches";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<PdfText> SET_LINE_SPACING = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "setLineSpacing", Object.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			Number number = (Number) params[0];
			if (number != null)
				targetObject.setLineSpacing(number.floatValue());
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "lineSpacingInches" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Spacing in inches between lines (plus height of font)" };
		}
		@Override
		protected String getHelp() {
			return "Set the spacing between each line of text within a paragraph";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> GET_LINE_SPACING = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "getLineSpacing", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getLineSpacing();
		}

		@Override
		protected String getHelp() {
			return "get line spacing in inches";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<PdfText> GET_SUPERSCRIPT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "getSuperscript", Float.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTextRise();
		}
		@Override
		protected String getHelp() {
			return "get the text rise";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> SET_SUPERSCRIPT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "setSuperscript", Object.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			Number col = (Number) params[0];
			if (col == null)
				col = 0f;
			targetObject.setTextRise(col.floatValue());
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "textRisePercent" };
		}
		protected String[] buildParamDescriptions() {
			return new String[] { "The percent to raise (valid range is 0.0 - 1.0), .3 is a good suggestion" };
		}
		@Override
		protected String getHelp() {
			return "set the percent rise of the text, zero to set to no rise";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> GET_SUBSCRIPT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "getSubscript", Float.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			return -targetObject.getTextRise();
		}
		@Override
		protected String getHelp() {
			return "get the text lower amount";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<PdfText> SET_SUBSCRIPT = new AmiAbstractMemberMethod<PdfText>(PdfText.class, "setSubscript", Object.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PdfText targetObject, Object[] params, DerivedCellCalculator caller) {
			Number col = (Number) params[0];
			if (col == null)
				col = 0f;
			targetObject.setTextRise(-col.floatValue());
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "textLowerPercent" };
		}
		protected String[] buildParamDescriptions() {
			return new String[] { "The percent to lower (valid range is 0.0 - 1.0), .3 is a good suggestion" };
		}
		@Override
		protected String getHelp() {
			return "set the percent lower of the text, zero to set to no rise";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "PdfText";
	}
	@Override
	public String getVarTypeDescription() {
		return "Text for display in PdfBuilder";
	}
	@Override
	public Class<PdfText> getVarType() {
		return PdfText.class;
	}
	@Override
	public Class<PdfText> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_PdfText INSTANCE = new AmiWebScriptMemberMethods_PdfText();
}
