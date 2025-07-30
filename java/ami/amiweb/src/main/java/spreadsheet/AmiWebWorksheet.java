package spreadsheet;

import com.f1.office.spreadsheet.SpreadSheetWorksheetBase;

public class AmiWebWorksheet {

	private final SpreadSheetWorksheetBase worksheet;

	public AmiWebWorksheet() {
		worksheet = null;
	}
	
	public AmiWebWorksheet(final AmiWebSpreadSheetBuilder builder, String name) {
		this.worksheet = builder.getWorksheet(name);
	}

	public void setTitle(String title) {
		this.worksheet.setTitle(title);
	}
	
	public String getTitle() {
		return this.worksheet.getTitle();
	}
	
	public SpreadSheetWorksheetBase getWorksheet() {
		return this.worksheet;
	}
}
