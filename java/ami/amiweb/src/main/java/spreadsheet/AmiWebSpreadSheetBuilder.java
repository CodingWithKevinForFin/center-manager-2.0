package spreadsheet;

import java.util.List;
import java.util.Set;

import com.f1.base.Bytes;
import com.f1.office.spreadsheet.SpreadSheetWorksheetBase;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.impl.SpreadSheetBuilder;

public class AmiWebSpreadSheetBuilder {
	private SpreadSheetBuilder sb;

	public AmiWebSpreadSheetBuilder() {
		reset();
	}
	public void reset() {
		this.sb = new SpreadSheetBuilder();
		/* to be used later
		try {
			byte[] logo = IOH.readDataFromResource("amiweb/3forge_new.png");
			if (logo == null || logo.length != 40910 || Cksum.cksum(logo) != 4183921028L)
				throw new RuntimeException("resource error");
			System.out.println(logo.length);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
		*/
	}
	public void setTitle(String title) {
		this.sb.setTitle(title);
	}
	public String getTitle() {
		return this.sb.getTitle();
	}
	public byte[] build() {
		byte[] b = this.sb.build();
		reset();
		return b;
	}
	public void addSheet(FastTablePortlet t, String sheetName, boolean onlySelectedRows, boolean shouldFormat) {
		this.sb.addSheet(t, sheetName, onlySelectedRows, shouldFormat);
	}
	public void addSheet(String sheetName, byte[] imageData) {
		this.sb.addSheet(sheetName, imageData);
	}
	public boolean copySheet(String targetSheet, String newSheet) {
		if (!this.sb.getSheetNames().contains(targetSheet) || this.sb.getSheetNames().contains(newSheet)) {
			return false;
		}
		this.sb.copySheet(targetSheet, newSheet);
		return true;
	}
	public boolean setTimezoneOffset(String timezone) {
		return this.sb.setTimezoneOffset(timezone);
	}
	public boolean setTimezoneOffset(Long offset) {
		return this.sb.setTimezoneOffset(offset);
	}
	public Set<String> getSheetNames() {
		return this.sb.getSheetNames();
	}
	public int getSheetsCount() {
		return this.sb.getSheetsCount();
	}
	
	public SpreadSheetWorksheetBase getWorksheet(String name) {
		return this.sb.getWorksheet(name);
	}
	
	public void loadExistingSheets(Bytes data) {
		this.sb.loadExistingSheets(data);
	}
	
	public void loadExistingSheets(Bytes data, final List<String> sheetNames) {
		this.sb.loadExistingSheets(data, sheetNames);
	}
	
	public void addFlexSheet(String sheetName) {
		this.sb.addFlexSheet(sheetName);
	}
	
	public void hideSpreadSheet(String sheetName) {
		this.sb.hideSpreadSheet(sheetName);
	}
	public void showSpreadSheet(String sheetName) {
		this.sb.showSpreadSheet(sheetName);
	}
	public void deleteSpreadSheet(String sheetName) {
		this.sb.deleteSpreadSheet(sheetName);
	}
	public void renameSpreadSheet(String sheetName, String newSheetName) {
		this.sb.renameSpreadSheet(sheetName, newSheetName);
	}
}
