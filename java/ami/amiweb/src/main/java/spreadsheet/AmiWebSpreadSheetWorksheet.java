package spreadsheet;

import com.f1.base.Table;
import com.f1.office.spreadsheet.SpreadSheetWorksheet;

public class AmiWebSpreadSheetWorksheet extends AmiWebWorksheet {
	
	private final SpreadSheetWorksheet worksheet;
	private final AmiWebSpreadSheetBuilder builder;

	public AmiWebSpreadSheetWorksheet() {
		super();
		this.worksheet = null;
		this.builder = null;
	}
	
	public AmiWebSpreadSheetWorksheet(final AmiWebSpreadSheetBuilder builder, String name) {
		super(builder, name);
		this.worksheet = (SpreadSheetWorksheet)this.getWorksheet();
		this.builder = builder;
	}
	
	public Table getTable() {
		return this.worksheet.getTable();
	}
	
	public boolean createColWithFormula(int pos, String colName, String formula) {
		this.worksheet.addColumn(pos, colName, formula);
		return true;
	}
}
