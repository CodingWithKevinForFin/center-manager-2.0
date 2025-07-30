package com.f1.ami.web.amiscript;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebAbstractTablePortlet;
import com.f1.ami.web.AmiWebObjectTablePortlet;
import com.f1.ami.web.AmiWebTabEntry;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.Bytes;
import com.f1.office.spreadsheet.SpreadSheetFlexsheet;
import com.f1.office.spreadsheet.SpreadSheetWorksheet;
import com.f1.office.spreadsheet.SpreadSheetWorksheetBase;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.XlsxHelper;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

import spreadsheet.AmiWebSpreadSheetBuilder;
import spreadsheet.AmiWebSpreadSheetFlexsheet;
import spreadsheet.AmiWebSpreadSheetWorksheet;
import spreadsheet.AmiWebWorksheet;

public class AmiWebScriptMemberMethods_SpreadSheetBuilder extends AmiWebScriptBaseMemberMethods<AmiWebSpreadSheetBuilder> {
	private AmiWebScriptMemberMethods_SpreadSheetBuilder() {
		super();
		addMethod(INIT);
		addMethod(BUILD);
		addMethod(ADD_SHEET);
		addMethod(ADD_SHEET2);
		addMethod(GET_SHEET_NAMES, "sheetNames");
		addMethod(GET_SHEETS_COUNT, "sheetsCount");
		addMethod(GET_WORK_SHEET);
		addMethod(LOAD_EXISTING_SHEETS);
		addMethod(LOAD_EXISTING_SHEETS2);
		addMethod(ADD_FLEX_SHEET);
		addMethod(HIDE_SHEET);
		addMethod(SHOW_SHEET);
		addMethod(COPY_SHEET);
		addMethod(DELETE_SHEET);
		addMethod(RENAME_SHEET);
		addMethod(GET_EXCEL_POSITION);
		addMethod(SET_TIMEZONE_OFFSET);
		addMethod(SET_TIMEZONE_OFFSET2);
	}

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> INIT = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class, null,
			AmiWebSpreadSheetBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new AmiWebSpreadSheetBuilder();
		}

		@Override
		protected String getHelp() {
			return "Creates a builder for generating a spreadsheet";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("SpreadSheetBuilder builder = new SpreadSheetBuilder();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "builder" }, "This creates a spreadsheet builder that can then be used to perform other functions."
					+ " Call the builder on .xlsx files to import spreadsheets into AMI."));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> BUILD = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class, "build",
			Bytes.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new Bytes(targetObject.build());
		}
		@Override
		protected String getHelp() {
			return "Builds and returns a binary containing the spreadsheet. The spreadsheet builder will be reset after this is called.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> ADD_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"addSheet", Boolean.class, AmiWebAbstractTablePortlet.class, String.class, Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				AmiWebAbstractTablePortlet tp = (AmiWebAbstractTablePortlet) params[0];
				FastTablePortlet ftp = tp.getTablePortlet();
				String sheetName = (String) params[1];
				boolean onlySelectedRows = Caster_Boolean.PRIMITIVE.cast(params[2]);
				boolean shouldFormat = Caster_Boolean.PRIMITIVE.cast(params[3]);
				targetObject.addSheet(ftp, sheetName, onlySelectedRows, shouldFormat);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Exception building spreadsheet off of Table: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "tablePanel", "sheetName", "onlySelectedRows", "shouldFormat" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The table to build the spreadsheet off of", "name of the sheet", "true to only include selectedRows, false to build the entire table",
					"true to include table format (bg color, greybar color etc.), false to use default formatting" };
		}
		@Override
		protected String getHelp() {
			return "Adds a sheet into the spreadsheet workbook. Returns true if operation is sucessful; false in any other case including passing null as parameter(s).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> ADD_SHEET2 = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"addSheet", Boolean.class, AmiWebTabEntry.class, Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				AmiWebTabEntry tabEntry = (AmiWebTabEntry) params[0];
				boolean onlySelectedRows = Caster_Boolean.PRIMITIVE.cast(params[1]);
				boolean shouldFormat = Caster_Boolean.PRIMITIVE.cast(params[2]);

				Collection<AmiWebAbstractTablePortlet> tables = AmiWebUtils.findPortletsByTypeFollowUndocked(tabEntry.getTab().getPortlet(), AmiWebAbstractTablePortlet.class);
				for (AmiWebAbstractTablePortlet i : tables) {
					if (i.getTable().getRows().size() > SpreadSheetWorksheet.MAX_ROW_COUNT) {
						warning(sf, "Row limit exceeded for " + i.getTable().getTable().getTitle(), CH.m("Row count: ", i.getTable().getRows().size()), null);
						LH.warning(log, "Aborting excel build from Tab: " + tabEntry.getTab().getTitle() + ". Row limit exceeded for table:  " + i.getTable().getTable().getTitle()
								+ ", row count: " + i.getTable().getRows().size(), ", Maximum size:  " + SpreadSheetWorksheet.MAX_ROW_COUNT);
						return false;
					}
				}

				for (AmiWebAbstractTablePortlet i : tables) {
					if (i instanceof AmiWebObjectTablePortlet) {
						AmiWebObjectTablePortlet rtTable = (AmiWebObjectTablePortlet) i;
						if (rtTable.isHalted()) {
							rtTable.setVisible(true);
							targetObject.addSheet(i.getTablePortlet(), i.getTablePortlet().getTable().getTable().getTitle(), onlySelectedRows, shouldFormat);
							rtTable.stopProcessingAMiData(true);
							rtTable.setVisible(false);
							continue;
						}
					}
					targetObject.addSheet(i.getTablePortlet(), i.getTablePortlet().getTable().getTable().getTitle(), onlySelectedRows, shouldFormat);

				}
				return true;
			} catch (Exception e) {
				LH.warning(log, "Exception building spread sheet off of Tab: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "tab", "onlySelectedRows", "shouldFormat" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "tab containing tables", "true to only include selectedRows, false to build entire tables contained in the tab",
					"true to include table format (bg color, greybar color etc.), false to use default formatting" };
		}
		@Override
		protected String getHelp() {
			return "Finds all the tables contained by the tab and builds sheets off of them. Returns true if operation is successful; false in any other case including passing null as parameter(s).";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> GET_SHEET_NAMES = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"getSheetNames", Set.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSheetNames();
		}

		@Override
		protected String getHelp() {
			return "Returns a set containing the names of the sheets added by the builder.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> GET_SHEETS_COUNT = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"getSheetsCount", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSheetsCount();
		}
		@Override
		protected String getHelp() {
			return "Returns the number of sheets built by the builder.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> GET_WORK_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"getWorksheet", AmiWebWorksheet.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			SpreadSheetWorksheetBase ws = targetObject.getWorksheet(name);
			if (ws instanceof SpreadSheetWorksheet)
				return new AmiWebSpreadSheetWorksheet(targetObject, name);
			else if (ws instanceof SpreadSheetFlexsheet)
				return new AmiWebSpreadSheetFlexsheet(targetObject, name);
			else if (ws == null)
				throw new RuntimeException("Specified spreadsheet does not exist");
			else
				throw new UnsupportedOperationException("Unknown spread sheet type detected");
		}
		protected String[] buildParamNames() {
			return new String[] { "worksheetName" };
		}

		@Override
		protected String getHelp() {
			return "Returns an existing worksheet with the input name. (Returned type is a base class of either SpreadSheetWorkSheet or SpreadSheetFlexSheet";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> LOAD_EXISTING_SHEETS = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(
			AmiWebSpreadSheetBuilder.class, "loadExistingSheets", Boolean.class, Bytes.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes data = (Bytes) params[0];
			targetObject.loadExistingSheets(data);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "data" };
		}

		@Override
		protected String getHelp() {
			return "Loads an existing xlsx file into individual flex sheets";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> LOAD_EXISTING_SHEETS2 = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(
			AmiWebSpreadSheetBuilder.class, "loadExistingSheets", Boolean.class, Bytes.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes data = (Bytes) params[0];
			@SuppressWarnings("unchecked")
			List<String> sheetNames = (List<String>) params[1];
			targetObject.loadExistingSheets(data, sheetNames);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "data", "sheetNames" };
		}

		@Override
		protected String getHelp() {
			return "Loads specified sheets from an existing xlsx file into individual flex sheets";
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Byte data of the spreadsheet file", "List of names of sheets to be loaded" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> COPY_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"copySheet", Boolean.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			String targetSheet = (String) params[0];
			String newSheet = (String) params[1];
			return targetObject.copySheet(targetSheet, newSheet);
		}

		protected String[] buildParamNames() {
			return new String[] { "targetSheetName", "newSheetName" };
		}

		@Override
		protected String getHelp() {
			return "Copies an existing sheet into a new sheet with the given name. Returns true on success";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> ADD_FLEX_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"addFlexSheet", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String name = (String) params[0];
				targetObject.addFlexSheet(name);
				return true;
			} catch (Exception e) {
				log.warning(e.toString());
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "name" };
		}

		@Override
		protected String getHelp() {
			return "Creates a new empty flex sheet with the given name. Note that if used alongside the loadExisting functionality, this should be added after otherwise loading behavior is undefined";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> HIDE_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"hideSheet", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String sheetName = SH.toString(params[0]);
				targetObject.hideSpreadSheet(sheetName);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Exception hiding spread sheet: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "sheetName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name of sheet to be hidden" };
		}
		@Override
		protected String getHelp() {
			return "Hides the specified spreadsheet";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> SHOW_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"showSheet", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String sheetName = SH.toString(params[0]);
				targetObject.showSpreadSheet(sheetName);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Exception showing spread sheet: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "sheetName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name of sheet to be unhidden" };
		}
		@Override
		protected String getHelp() {
			return "Shows the specified hidden spreadsheet";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> DELETE_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"deleteSheet", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String sheetName = SH.toString(params[0]);
				targetObject.deleteSpreadSheet(sheetName);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Exception deleting spread sheet: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "sheetName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name of sheet to be deleted" };
		}
		@Override
		protected String getHelp() {
			return "Deletes the specified spreadsheet";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> RENAME_SHEET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(AmiWebSpreadSheetBuilder.class,
			"renameSheet", Boolean.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String sheetName = SH.toString(params[0]);
				String newSheetName = SH.toString(params[1]);
				targetObject.renameSpreadSheet(sheetName, newSheetName);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Exception renaming spread sheet: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "sheetName", "newSheetName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name of sheet to be renamed", "New name to use" };
		}
		@Override
		protected String getHelp() {
			return "Renames the specified spreadsheet";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> GET_EXCEL_POSITION = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(
			AmiWebSpreadSheetBuilder.class, "getExcelPosition", String.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			final int position = (int) params[0];
			return XlsxHelper.getExcelPosition(position);
		}

		protected String[] buildParamNames() {
			return new String[] { "position" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Position to be converted to excel based position, starts from 0=A,1=B,26=AA,..." };
		}

		@Override
		protected String getHelp() {
			return "Returns the alphabetical representation of the zero-based position (based upon excel's format)";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> SET_TIMEZONE_OFFSET = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(
			AmiWebSpreadSheetBuilder.class, "setTimezoneOffset", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String timezone = SH.toString(params[0]);
				return targetObject.setTimezoneOffset(timezone);
			} catch (Exception e) {
				LH.warning(log, "Exception setting spreadsheet's timezone: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "timezone" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Timezone to be used for date parsing" };
		}
		@Override
		protected String getHelp() {
			return "Sets the spreadsheet's timezone offset value based on a given timezone. Offsets are used for computing datetime values and are offsetted from GMT+0 and uses the current date to determine the offset value (affected by daylight savings). For more precision, pass in the expected offset with a long value";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder> SET_TIMEZONE_OFFSET2 = new AmiAbstractMemberMethod<AmiWebSpreadSheetBuilder>(
			AmiWebSpreadSheetBuilder.class, "setTimezoneOffset", Boolean.class, Long.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Long offset = (Long) params[0];
				return targetObject.setTimezoneOffset(offset);
			} catch (Exception e) {
				LH.warning(log, "Exception setting spreadsheet's timezone offset: ", e);
				return false;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "timezone" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Timezone offset to be used for date parsing" };
		}
		@Override
		protected String getHelp() {
			return "Sets the spreadsheet's timezone offset value based on a given offset.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "SpreadSheetBuilder";
	}
	@Override
	public String getVarTypeDescription() {
		return "Builder for generating a Spreadsheet";
	}
	@Override
	public Class<AmiWebSpreadSheetBuilder> getVarType() {
		return AmiWebSpreadSheetBuilder.class;
	}
	@Override
	public Class<AmiWebSpreadSheetBuilder> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_SpreadSheetBuilder INSTANCE = new AmiWebScriptMemberMethods_SpreadSheetBuilder();
}
