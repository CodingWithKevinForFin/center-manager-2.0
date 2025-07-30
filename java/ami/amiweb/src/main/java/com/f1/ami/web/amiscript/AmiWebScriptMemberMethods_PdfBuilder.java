package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.pdf.AmiWebPdfBuilder;
import com.f1.base.Bytes;
import com.f1.base.Table;
import com.f1.utils.WebRectangle;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_PdfBuilder extends AmiWebScriptBaseMemberMethods<AmiWebPdfBuilder> {

	private AmiWebScriptMemberMethods_PdfBuilder() {
		super();
		addMethod(INIT);
		//builder
		addMethod(RESET);
		addMethod(BUILD);

		//Page
		addMethod(SET_PAGE_SIZD);
		addMethod(GET_PAGE_HEIGHT, "pageHeight");
		addMethod(GET_PAGE_WIDTH, "pageWidth");
		addMethod(SET_PAGE_MARGIN);
		addMethod(GET_PAGE_MARGIN_TOP, "pageMarginTop");
		addMethod(GET_PAGE_MARGIN_RIGHT, "pageMarginRight");
		addMethod(GET_PAGE_MARGIN_BOTTOM, "pageMarginBottom");
		addMethod(GET_PAGE_MARGIN_LEFT, "pageMarginLeft");
		addMethod(GET_PAGE_MARGIN_BELOW_HEADER, "pageMarginBelowHeader");
		addMethod(GET_PAGE_MARGIN_ABOVE_FOOTER, "pageMarginAboveFooter");
		addMethod(ADD_HEADER);
		addMethod(ADD_FOOTER);
		addMethod(ADD_HEADER_IMAGE);
		addMethod(ADD_FOOTER_IMAGE);
		addMethod(ADD_HEADER_IMAGE2);
		addMethod(ADD_FOOTER_IMAGE2);
		addMethod(SET_PAGE_BACKGROUND);
		addMethod(GET_PAGE_BACKGROUND, "pageBackground");
		addMethod(SET_INDENT);
		addMethod(MOVE_3FORGE_LOGO);

		//Generate formatting
		addMethod(SET_HORIZONTAL_ALIGNMENT);
		addMethod(GET_HORIZONTAL_ALIGNMENT, "horizontalAlignment");
		addMethod(SET_SPACING);
		addMethod(GET_SPACING_BEFORE, "spacingBefore");
		addMethod(GET_SPACING_AFTER, "spacingAfter");

		//append
		addMethod(APPEND_TABLE);
		addMethod(APPEND_PAGE_BREAK);
		addMethod(APPEND_LINE_BREAK);
		addMethod(APPEND_TEXT);
		addMethod(APPEND_IMAGE);

		//Text
		addMethod(SET_LINE_SPACING);
		addMethod(GET_LINE_SPACING, "lineSpacing");
		addMethod(SET_FONT);
		addMethod(GET_FONT, "font");
		addMethod(ADD_CUSTOM_FONT);

		//Table 
		addMethod(SET_TABLE_HEADER_FONT);
		addMethod(GET_TABLE_HEADER_FONT, "tableHeaderFont");
		addMethod(SET_TABLE_HEADER_BACKGROUND);
		addMethod(GET_TABLE_HEADER_BACKGROUND, "tableHeaderBackground");
		addMethod(SET_TABLE_BACKGROUND);
		addMethod(GET_TABLE_BACKGROUND, "tableBackground");
		addMethod(GET_TABLE_ALTERNATIVE_ROW_BACKGROUND, "tableAlternativeRowBackground");
		addMethod(SET_TABLE_CELL_HORIZONTAL_ALIGNMENT);
		addMethod(GET_TABLE_CELL_HORIZONTAL_ALIGNMENT, "tableCellHorizontalAlignment");
		addMethod(SET_TABLE_CELL_PADDING);
		addMethod(GET_TABLE_CELL_PADDING_TOP, "tableCellPaddingTop");
		addMethod(GET_TABLE_CELL_PADDING_RIGHT, "tableCellPaddingRight");
		addMethod(GET_TABLE_CELL_PADDING_BOTTOM, "tableCellPaddingBottom");
		addMethod(GET_TABLE_CELL_PADDING_LEFT, "tableCellPaddingLeft");
		addMethod(SET_TABLE_CELL_FONT);
		addMethod(GET_TABLE_CELL_FONT, "tableCellFont");
		addMethod(SET_TABLE_WIDTH);
		addMethod(GET_TABLE_WIDTH, "tableWidth");
		addMethod(SET_TABLE_BORDER);
		addMethod(GET_TABLE_BORDER_COLOR, "tableBorderColor");
		addMethod(GET_TABLE_BORDER_WIDTH, "tableBorderWidth");
		addMethod(SET_TABLE_COLUMN_BORDER);
		addMethod(GET_TABLE_COLUMN_BORDER_COLOR, "tableColumnBorderColor");
		addMethod(GET_TABLE_COLUMN_BORDER_WIDTH, "tableColumnBorderWidth");
		addMethod(SET_TABLE_ROW_BORDER);
		addMethod(GET_TABLE_ROW_BORDER_COLOR, "tableRowBorderColor");
		addMethod(GET_TABLE_ROW_BORDER_WIDTH, "tableRowBorderWidth");
		addMethod(ADD_CELL_STYLE);
		addMethod(CLEAR_CELL_STYLE);

		//Image 
		addMethod(SET_IMAGE_BORDER);
		addMethod(SET_IMAGE_BACKGROUND);
		addMethod(SET_COLUMN_WEIGHT);
		addMethod(GET_COLUMN_WEIGHT);
		addMethod(SET_SUPERSCRIPT);
		addMethod(GET_SUPERSCRIPT);
		addMethod(SET_SUBSCRIPT);
		addMethod(GET_SUBSCRIPT);

		//Span
		addMethod(SPAN_IMAGE);

		//Columns
		addMethod(CREATE_COLUMN);
		addMethod(ADD_COLUMN);
		addMethod(END_COLUMN);

	}

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> INIT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, null, AmiWebPdfBuilder.class,
			false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return new AmiWebPdfBuilder();
			} catch (Throwable t) {
				throw new FlowControlThrow("Could not initialize pdf engine, most likely pdf jar files are not in classpath");
			}
		}

		@Override
		protected String getHelp() {
			return "Create a builder for generating a PDF.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> RESET = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "reset", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.reset();
			return null;
		}
		@Override
		protected String getHelp() {
			return "reset the pdf builder";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> BUILD = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "build", Bytes.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.build();
		}
		@Override
		protected String getHelp() {
			return "Builds and returns a binary containing the pdf. The pdf builder will be reset after this is called";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_PAGE_SIZD = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setPageSize",
			Object.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Number w = (Number) params[0];
			Number h = (Number) params[1];
			targetObject.setPageSize(w, h);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "widthInches", "heightInches" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "width or null to keep existing width", "height or null to keep existing width" };
		}
		@Override
		protected String getHelp() {
			return "Sets the size of each page with the width and height.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_HEIGHT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getPageHeight",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageHeight();
		}
		@Override
		protected String getHelp() {
			return "Returns page height in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_WIDTH = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getPageWidth",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageWidth();
		}
		@Override
		protected String getHelp() {
			return "Returns page width in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_PAGE_MARGIN = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setPageMargin",
			Object.class, Number.class, Number.class, Number.class, Number.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Number t = (Number) params[0];
			Number r = (Number) params[1];
			Number b = (Number) params[2];
			Number l = (Number) params[3];
			Number bh = (Number) params[4];
			Number af = (Number) params[5];
			targetObject.setPageMargin(t, r, b, l, bh, af);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "topInches", "rightInches", "bottomInches", "leftInches", "belowHeaderInches", "aboveFooterInches" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "top or null to keep existing", "right or null to keep existing", " bottom or null to keep existing", " left or null to keep existing",
					"distance between bottom of header and top of body", "distance between top of footer and bottom of body" };
		}
		@Override
		protected String getHelp() {
			return "Sets the margins for each page.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_MARGIN_TOP = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getPageMarginTop",
			Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageMarginTop();
		}

		@Override
		protected String getHelp() {
			return "Returns the page's top margin in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_MARGIN_RIGHT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getPageMarginRight", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageMarginRight();
		}

		@Override
		protected String getHelp() {
			return "Returns the page's right margin in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_MARGIN_BOTTOM = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getPageMarginBottom", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageMarginBottom();
		}

		@Override
		protected String getHelp() {
			return "Returns the page's bottom margin in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_MARGIN_LEFT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getPageMarginLeft",
			Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageMarginLeft();
		}

		@Override
		protected String getHelp() {
			return "Returns the page's left margin in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_MARGIN_BELOW_HEADER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getPageMarginBelowHeader", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageMarginBelowHeader();
		}

		@Override
		protected String getHelp() {
			return "get the page margin below header in inches";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_MARGIN_ABOVE_FOOTER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getPageMarginAboveFooter", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageMarginAboveFooter();
		}

		@Override
		protected String getHelp() {
			return "Returns the page's margin above the footer in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> APPEND_TABLE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "appendTable", Object.class,
			Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Table t = (Table) params[0];
			if (t != null)
				targetObject.appendTable(t);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "table" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "table" };
		}
		@Override
		protected String getHelp() {
			return "Appends a table to the pdf.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> APPEND_PAGE_BREAK = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "appendPageBreak",
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.appendPageBreak();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Starts a new page in the pdf.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> APPEND_LINE_BREAK = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "appendLineBreak",
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.appendLineBreak();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Starts a new paragraph in the pdf.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> APPEND_TEXT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "appendText", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Appends the given text to the pdf.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_FONT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setFont", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Sets the current font for use with text. The available fonts are Courier, Helvetica, Times-Roman, Symbol, ZapfDingbats. Styles include: Bold, Italic, Underline, Line-through.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_HORIZONTAL_ALIGNMENT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setHorizontalAlignment", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Sets the current alignment. This will create a line break if a paragraph is in progress.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_HORIZONTAL_ALIGNMENT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getHorizontalAlignment", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHorizontalAlignment();
		}

		@Override
		protected String getHelp() {
			return "Returns the current alignment. The return will be either LEFT, CENTER, or RIGHT.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_HEADER_FONT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setTableHeaderFont", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setTableHeaderFont((String) params[0]);
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
			return "Sets the current font for use with table header. See setFont() for options.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_HEADER_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setTableHeaderBackground", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setTableHeaderBackground((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "color" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "ex: #FF0000" };
		}
		@Override
		protected String getHelp() {
			return "Sets the current background color with the given color hexcode for table header.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_HEADER_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableHeaderBackground", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableHeaderBackground();
		}

		@Override
		protected String getHelp() {
			return "Returns the current background color applied to table headers as a hexcode.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setTableBackground", Object.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setTableBackground((String) params[0]);
			targetObject.setTableAltRowBackground((String) params[1]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "color", "altColor" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "color for even numbered rows", "color for odd numbered rows" };
		}
		@Override
		protected String getHelp() {
			return "Sets the background color for table rows given the color hexcode.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableBackground", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableBackground();
		}

		@Override
		protected String getHelp() {
			return "Returns the current background color of the tables as a hexcode.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_ALTERNATIVE_ROW_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableAlternativeRowBackground", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableAltRowBackground();
		}

		@Override
		protected String getHelp() {
			return "Returns the current background color of the alternative rows of tables as a hexcode.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_CELL_HORIZONTAL_ALIGNMENT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setTableCellHorizontalAlignment", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setTableCellHorizontalAlignment((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "alignment" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Either: LEFT, CENTER, RIGHT" };
		}
		@Override
		protected String getHelp() {
			return "Sets the alignment of text within table cells.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_CELL_HORIZONTAL_ALIGNMENT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableCellHorizontalAlignment", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableCellHorizontalAlignment();
		}

		@Override
		protected String getHelp() {
			return "Returns the horizontal alignment for table cells (either LEFT, CENTER, or RIGHT).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_CELL_PADDING = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setTableCellPadding", Object.class, Number.class, Number.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Number t = (Number) params[0];
			Number r = (Number) params[1];
			Number b = (Number) params[2];
			Number l = (Number) params[3];
			targetObject.setTableCellPadding(t, r, b, l);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "topInches", "rightInches", "bottomInches", "leftInches" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "top or null to keep existing", "right or null to keep existing", " bottom or null to keep existing", " left or null to keep existing" };
		}
		@Override
		protected String getHelp() {
			return "Sets the padding between text and the border for each cell within a table.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_CELL_PADDING_TOP = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableCellPaddingTop", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableCellPaddingTop();
		}
		protected String getHelp() {
			return "Returns table cell padding top in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_CELL_PADDING_RIGHT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableCellPaddingRight", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableCellPaddingRight();
		}
		protected String getHelp() {
			return "Returns the table cell padding right in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_CELL_PADDING_BOTTOM = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableCellPaddingBottom", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableCellPaddingBottom();
		}
		protected String getHelp() {
			return "Returns the table cell padding bottom in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_CELL_PADDING_LEFT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableCellPaddingLeft", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableCellPaddingLeft();
		}
		protected String getHelp() {
			return "Returns the table cell padding left in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_WIDTH = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setTableWidth",
			Object.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Number t = (Number) params[0];
			targetObject.setTableWidth(t);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "widthInches" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "width of table in inches or null to auto-size" };
		}
		@Override
		protected String getHelp() {
			return "Sets the table width given the width.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_WIDTH = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getTableWidth",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableWidth();
		}
		protected String getHelp() {
			return "Returns the table width in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> APPEND_IMAGE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "appendImage", Object.class,
			Bytes.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes t = (Bytes) params[0];
			Number w = (Number) params[1];
			if (t != null)
				targetObject.appendImage(t.getBytes(), w);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "image", "widthInches" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the image to add", "width in inches, or null to use default image width" };
		}
		@Override
		protected String getHelp() {
			return "Appends an image to the pdf. If the image width is greater than page width it is automatically scaled to fit.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_IMAGE_BORDER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setImageBorder",
			Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String t = (String) params[0];
			targetObject.setImageBorder(t);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "sizeAndOrColor" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "any combination of size(in inches) and color, ex: .0005 #FF0000" };
		}
		@Override
		protected String getHelp() {
			return "Sets the border to draw around images.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_BORDER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setTableBorder",
			Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String t = (String) params[0];
			targetObject.setTableBorder(t);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "sizeAndOrColor" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "any combination of size(in inches) and color, ex: .0005 #FF0000" };
		}
		@Override
		protected String getHelp() {
			return "Sets the border to draw around tables.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_BORDER_COLOR = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableBorderColor", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableBorderColor();
		}

		@Override
		protected String getHelp() {
			return "Returns the current border color of the tables.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_BORDER_WIDTH = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableBorderWidth", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableBorderWidth();
		}

		@Override
		protected String getHelp() {
			return "Returns the current border width (in inches) set for tables.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_COLUMN_BORDER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setTableColumnBorder", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String t = (String) params[0];
			targetObject.setCellBorderV(t);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "sizeAndOrColor" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "any combination of size(in inches) and color, ex: .0005 #FF0000" };
		}
		@Override
		protected String getHelp() {
			return "Sets the border to draw between columns.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_COLUMN_BORDER_COLOR = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableColumnBorderColor", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableColumnBorderColor();
		}

		@Override
		protected String getHelp() {
			return "Returns the current border color of table columns.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_COLUMN_BORDER_WIDTH = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableColumnBorderWidth", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableColumnBorderWidth();
		}

		@Override
		protected String getHelp() {
			return "Returns the current border width (in inches) set for table columns.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_ROW_BORDER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setTableRowBorder",
			Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String t = (String) params[0];
			targetObject.setCellBorderH(t);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "sizeAndOrColor" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "any combination of size(in inches) and color, ex: .0005 #FF0000" };
		}
		@Override
		protected String getHelp() {
			return "Sets the border to draw between rows.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_ROW_BORDER_COLOR = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableRowBorderColor", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableRowBorderColor();
		}

		@Override
		protected String getHelp() {
			return "Returns the current border color of tables rows as a hexcode.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_ROW_BORDER_WIDTH = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableRowBorderWidth", Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableRowBorderWidth();
		}

		@Override
		protected String getHelp() {
			return "Returns the current border width (in inches) set for table rows.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_IMAGE_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"setImageBackground", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setImageBackground((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "color" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "color or null for transparent" };
		}
		@Override
		protected String getHelp() {
			return "Sets the background color for images.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_HEADER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addHeader", Object.class,
			String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.addHeader(null, (String) params[0], (String) params[1], (String) params[2], null, null);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "text", "alignment", "font" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "text or null to remove, ${page} will be substituted for current page number", "either CENTER or RIGHT", "see setFont for examples" };
		}
		@Override
		protected String getHelp() {
			return "Adds a header (or remove if text is null). Note, there can be up to two headers at once (center, right).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_FOOTER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addFooter", Object.class,
			String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String text = (String) params[0];
			String alignment = (String) params[1];
			String font = (String) params[2];
			targetObject.addFooter(null, text, alignment, font, null, null);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "text", "alignment", "font" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "text or null to remove, ${page} will be substituted for current page number", "either LEFT, CENTER or RIGHT", "see setFont for examples" };
		}
		@Override
		protected String getHelp() {
			return "Adds a footer (or remove if text is null). Note, there can be up to three footers at once (left, center, right).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_HEADER_IMAGE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addHeaderImage",
			Object.class, Bytes.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes image = (Bytes) params[0];
			String alignment = (String) params[1];
			String url = (String) params[2];
			targetObject.addHeader(image == null ? null : image.getBytes(), null, alignment, null, url, null);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "image", "alignment", "url" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "image or null to remove, ${page} will be substituted for current page number", "LEFT, CENTER or RIGHT", "url to open when clicked" };
		}
		@Override
		protected String getHelp() {
			return "Adds a header (or remove if text is null). Note, there can be up to two headers at once (left, center, right).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_FOOTER_IMAGE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addFooterImage",
			Object.class, Bytes.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes image = (Bytes) params[0];
			String alignment = (String) params[1];
			String url = (String) params[2];
			targetObject.addFooter(image == null ? null : image.getBytes(), null, alignment, null, url, null);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "image", "alignment", "url" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "image or null to remove, ${page} will be substituted for current page number", "either LEFT, CENTER or RIGHT", "url to open when clicked" };
		}
		@Override
		protected String getHelp() {
			return "Adds a footer (or remove if text is null). Note, there can be up to three footers at once (left, center, right).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_HEADER_IMAGE2 = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addHeaderImage",
			Object.class, Bytes.class, String.class, String.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes image = (Bytes) params[0];
			String alignment = (String) params[1];
			String url = (String) params[2];
			Number height = (Number) params[3];
			targetObject.addHeader(image == null ? null : image.getBytes(), null, alignment, null, url, height);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "image", "alignment", "url", "height" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "image or null to remove, ${page} will be substituted for current page number", "LEFT, CENTER or RIGHT", "url to open when clicked",
					"Height in inches, default is .25" };
		}
		@Override
		protected String getHelp() {
			return "Adds a header (or remove if text is null). Note, there can be up to two headers at once (left,center,right).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_FOOTER_IMAGE2 = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addFooterImage",
			Object.class, Bytes.class, String.class, String.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes image = (Bytes) params[0];
			String alignment = (String) params[1];
			String url = (String) params[2];
			Number height = (Number) params[3];
			targetObject.addFooter(image == null ? null : image.getBytes(), null, alignment, null, url, height);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "image", "alignment", "url", "height" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "image or null to remove, ${page} will be substituted for current page number", "either LEFT, CENTER or RIGHT", "url to open when clicked",
					"Height in inches, default is .25" };
		}
		@Override
		protected String getHelp() {
			return "Adds a footer (or remove if text is null). Note, there can be up to three footers at once (left, center, right).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_LINE_SPACING = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setLineSpacing",
			Object.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setSpacing(null, null, (Number) params[0]);
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
			return "Sets the spacing between each line of text within a paragraph.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_LINE_SPACING = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getLineSpacing",
			Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_SPACING = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setSpacing", Object.class,
			Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setSpacing((Number) params[0], (Number) params[1], null);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "beforeParagraphInches", "afterParagraphInches" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Spacing, in inches, , images and tables", "Spacing, in inches, after paragraphs, images and tables" };
		}
		@Override
		protected String getHelp() {
			return "Adds spacing after text, images, or tables. This is a way to vertically align on the pdf.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_SPACING_BEFORE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getSpacingBefore",
			Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSpacingBefore();
		}

		@Override
		protected String getHelp() {
			return "Returns the spacing before paragraphs, tables, images in inches.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_SPACING_AFTER = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getSpacingAfter",
			Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSpacingAfter();
		}

		@Override
		protected String getHelp() {
			return "Returns the spacing after paragraphs, tables, images in inches.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_TABLE_CELL_FONT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setTableCellFont",
			Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setTableCellFont((String) params[0]);
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
			return "Sets the current font for use with table cells. See setFont() for options.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_FONT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getFont", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_CELL_FONT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getTableCellFont",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableCellFont();
		}
		@Override
		protected String getHelp() {
			return "Returns the current font for use with table cells.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_TABLE_HEADER_FONT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class,
			"getTableHeaderFont", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableHeaderFont();
		}
		@Override
		protected String getHelp() {
			return "Returns the current font for use with table headers.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_CELL_STYLE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addCellStyle",
			Object.class, WebRectangle.class, String.class, String.class, String.class, String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			WebRectangle pos = (WebRectangle) params[0];
			if (pos == null)
				return null;
			String tb = (String) params[1];
			String rb = (String) params[2];
			String bb = (String) params[3];
			String lb = (String) params[4];
			String ft = (String) params[5];
			String bg = (String) params[6];
			targetObject.addCellsStyle(pos, tb, rb, bb, lb, ft, bg);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "area", "topBorder", "rightBorder", "bottomBorder", "leftBorder", "font", "backgroundColor" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "location of cells to apply formatting to", "Optional, combination of size(in inches) and color, ex: .0005 #FF0000",
					"Optional, combination of size(in inches) and color, ex: .0005 #FF0000", "Optional, combination of size(in inches) and color, ex: .0005 #FF0000",
					"Optional, combination of size(in inches) and color, ex: .0005 #FF0000",
					"Optional, any combination of size (in inches),family,color,style, alignment (LEFT,CENTER,RIGHT, TOP, MIDDLE, BOTTOM) , ex: 15 Helvetica bold intalic #FF0000 left",
					"Optional, color" };
		}
		@Override
		protected String getHelp() {
			return "Cells inside the area will use the supplied formats. Area is required but the style arguments may be null.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> CLEAR_CELL_STYLE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "clearCellStyles",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.clearCellStyles();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Clears the cell styles and resets column widths to a weighting of 1.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_COLUMN_WEIGHT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setColumnWeight",
			Object.class, Integer.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer col = (Integer) params[0];
			if (col == null)
				return null;
			Number n = (Number) params[1];
			if (n == null)
				n = 1d;
			targetObject.setColumnWidthWeight(col.intValue(), n.doubleValue());
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "columnPosition", "weighting" };
		}
		protected String[] buildParamDescriptions() {
			return new String[] { "0 is the left most column, 1 is the next column, etc",
					"1 is the default weighting for all columns, 2 would be twice the width, .5 would be 1/2 the width. Must be a positive number" };
		}
		@Override
		protected String getHelp() {
			return "Sets the column width (as a weighting) for tables, see appendTable(...).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_COLUMN_WEIGHT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getColumnhWeight",
			Object.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer col = (Integer) params[0];
			if (col == null || col.intValue() < 0)
				return null;
			return targetObject.getColumnWidthWeight(col.intValue());
		}
		protected String[] buildParamNames() {
			return new String[] { "columnPosition" };
		}
		protected String[] buildParamDescriptions() {
			return new String[] { "0 is the left most column, 1 is the next column, etc" };
		}
		@Override
		protected String getHelp() {
			return "Returns the column width (as a weighting).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_SUPERSCRIPT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getSuperscript",
			Float.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTextRise();
		}
		@Override
		protected String getHelp() {
			return "Returns the text rise as a Float.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_SUPERSCRIPT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setSuperscript",
			Object.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Sets the percent rise of the text. Setting it as 0.0 is normal baseline.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_SUBSCRIPT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getSubscript",
			Float.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return -targetObject.getTextRise();
		}
		@Override
		protected String getHelp() {
			return "Returns the text lower as a Float.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_SUBSCRIPT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setSubscript",
			Object.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Sets the percent lower of the text. Setting it as 0.0 is normal baseline.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_PAGE_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setPageBackground",
			Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setPageBackground((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "color" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "color for page" };
		}
		@Override
		protected String getHelp() {
			return "Sets the background color for the page with the given the color hexcode.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> GET_PAGE_BACKGROUND = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "getPageBackground",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPageBackground();

		}
		@Override
		protected String getHelp() {
			return "Returns page background color.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SPAN_IMAGE = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "spanImage", Object.class,
			Bytes.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes t = (Bytes) params[0];
			Number w = (Number) params[1];
			Number offset = (Number) params[2];

			if (t != null)
				targetObject.spanImage(t.getBytes(), w, offset);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "image", "widthInches", "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the image to add", "width in inches, or null to use default image width", "the image offset in the x direction" };
		}
		@Override
		protected String getHelp() {
			return "Spans an image to the pdf. The image offset determines the spacing between images that are spanned";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> CREATE_COLUMN = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "createColumn",
			Object.class, String.class, Number.class, Number.class, Number.class, Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String content = (String) params[0];
			Number lowerLeftX = (Number) params[1];
			Number lowerLeftY = (Number) params[2];
			Number upperRightX = (Number) params[3];
			Number upperRightY = (Number) params[4];
			String style = (String) params[5];

			if (content != null && lowerLeftX != null && lowerLeftY != null && upperRightX != null && upperRightY != null) {
				targetObject.createColumn(content, lowerLeftX, lowerLeftY, upperRightX, upperRightY, style);
			}
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "Text content", "Lower Left X Position", "Lower Left Y position", "Upper Right X Position", "Upper Right Y Position", "Text style" };
		}
		protected String[] buildParamDescriptions() {
			return new String[] { "The text content you want to display in the column",
					"The x coordinate of the lower left position of the rectangle. Two points (lower left, upper right) of rectangle",
					"The y coordinate of the lower left position of the rectangle", "The upper right x position of the rectangle", "The upper right y position of the rectangle",
					"The style of the text ex) (\"0.12 normal #BBF000\")" };
		}
		@Override
		protected String getHelp() {
			return "Creates a column within the pdf so that you can style your pdfs into 2,3, or more columns. Can also style the text within the columns.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_CUSTOM_FONT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addCustomFont",
			Object.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String fontPath = (String) params[0];
			String fontName = (String) params[1];
			if (fontPath != null && fontName != null) {
				targetObject.addFont(fontPath, fontName);
			}
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "fontPath", "fontName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "path of ttf file of font to add", "font name" };
		}
		@Override
		protected String getHelp() {
			return "Adds a new font that can be used within the pdf that is not in the default fonts";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> ADD_COLUMN = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "addColumn", Object.class,
			Integer.class, Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			int numColumns = (int) params[0];
			Double gutter = ((Double) params[1]) / 100;

			if (numColumns != 0 && gutter != null) {
				targetObject.addColumn(numColumns, gutter.floatValue());
			}
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "Number of columns", "Gutter Spacing" };
		}
		protected String[] buildParamDescriptions() {
			return new String[] { "Number of columns you want to split the page", "Width of gutter spacing between columns as percentage of page width" };
		}
		@Override
		protected String getHelp() {
			return "Creates a specified number of columns within a page. If gutter spacing is 5, it will be 5% of the page width. Spans columns automatically across the pdf, creating new pages if needed.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> END_COLUMN = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "endColumn", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.endColumn();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Ends the started column page which displays all the column text on the document";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> SET_INDENT = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "setIndent", Object.class,
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.endColumn();
			Number indent = ((Number) params[0]);
			if (indent != null) {
				targetObject.setIndent(indent);
			}
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "Indent" };
		}
		protected String[] buildParamDescriptions() {
			return new String[] { "Indent in inches" };
		}
		@Override
		protected String getHelp() {
			return "Sets indent for text";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPdfBuilder> MOVE_3FORGE_LOGO = new AmiAbstractMemberMethod<AmiWebPdfBuilder>(AmiWebPdfBuilder.class, "move3forgeLogo",
			Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPdfBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String alignment = (String) params[0];
			if (alignment != null)
				targetObject.move3forgeLogo(alignment);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "alignment" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "EITHER UPPER LEFT, UPPER RIGHT, UPPER CENTER, LOWER LEFT, LOWER RIGHT, LOWER CENTER" };
		}
		@Override
		protected String getHelp() {
			return "Moves the 3forge logo to one of the corners. The default is upper left. The 3forge logo can only be moved not removed.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "PdfBuilder";
	}
	@Override
	public String getVarTypeDescription() {
		return "Builder for generating a Pdf";
	}
	@Override
	public Class<AmiWebPdfBuilder> getVarType() {
		return AmiWebPdfBuilder.class;
	}
	@Override
	public Class<AmiWebPdfBuilder> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_PdfBuilder INSTANCE = new AmiWebScriptMemberMethods_PdfBuilder();
}
