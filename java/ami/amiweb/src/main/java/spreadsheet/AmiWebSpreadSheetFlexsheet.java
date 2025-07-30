package spreadsheet;

import java.util.logging.Logger;

import com.f1.base.Table;
import com.f1.office.spreadsheet.SpreadSheetFlexsheet;
import com.f1.office.spreadsheet.SpreadSheetUtils;
import com.f1.utils.LH;

public class AmiWebSpreadSheetFlexsheet extends AmiWebWorksheet {

	SpreadSheetFlexsheet fs;

	private static Logger log = LH.get();

	public AmiWebSpreadSheetFlexsheet() {
		super();
		fs = (SpreadSheetFlexsheet) this.getWorksheet();
	}

	public AmiWebSpreadSheetFlexsheet(final AmiWebSpreadSheetBuilder builder, String name) {
		super(builder, name);
		fs = (SpreadSheetFlexsheet) this.getWorksheet();
	}

	public boolean setValue(String dimension, Object value) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimension) && !SpreadSheetUtils.isValidExcelDimensionRange(dimension)) {
			log.warning("Invalid dimension: " + dimension);
			return false;
		}

		this.fs.setValue(dimension, value);
		return true;
	}

	public boolean setValue(final String dimension, final Table value, final boolean useHeader) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimension)) {
			log.warning("Invalid dimension: " + dimension);
			return false;
		}

		this.fs.setValue(dimension, value, useHeader);
		return true;
	}

	public Object getValue(final String dimension, final boolean getRaw) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimension)) {
			log.warning("Invalid dimension: " + dimension);
			return null;
		}
		return this.fs.getValue(dimension, getRaw);
	}

	public boolean setStyle(final String dimension, final Integer id) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimension) && !SpreadSheetUtils.isValidExcelDimensionRange(dimension)) {
			log.warning("Invalid dimension: " + dimension);
			return false;
		}

		this.fs.setStyle(dimension, id);
		return true;
	}

	public Integer getStyle(final String dimension) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimension)) {
			log.warning("Invalid dimension: " + dimension);
			return null;
		}

		return this.fs.getStyle(dimension);
	}

	public Boolean setValueNamedRange(final String namedRange, final Object value) {
		try {
			this.fs.setValueNamedRange(namedRange, value);
			return true;
		} catch (Exception e) {
			log.warning(e.toString());
			return false;
		}
	}

	public Table getValues(final String dimension, final Boolean hasHeader, final Class<?>[] types, boolean getRaw) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimension) && !SpreadSheetUtils.isValidExcelDimensionRange(dimension)) {
			throw new RuntimeException("Invalid dimension: " + dimension);
		}

		return this.fs.getValues(dimension, hasHeader, types, getRaw);
	}

	public Object getValueNamedRange(final String namedRange, boolean getRaw) {
		return this.fs.getValueNamedRange(namedRange, getRaw);
	}

	public Table getValuesNamedRange(final String namedRange, final Boolean hasHeader, final Class<?>[] types, boolean getRaw) {
		return this.fs.getValuesNamedRange(namedRange, hasHeader, types, getRaw);
	}

	public Boolean clearCell(final String dimension) {
		try {
			if (!SpreadSheetUtils.isValidExcelDimension(dimension) && !SpreadSheetUtils.isValidExcelDimensionRange(dimension)) {
				log.warning("Invalid dimension: " + dimension);
				return false;
			}

			this.fs.clearCell(dimension);
			return true;
		} catch (Exception e) {
			log.warning(e.toString());
			return false;
		}
	}

	public Boolean clearCellNamedRange(final String namedRange) {
		try {
			this.fs.clearCellNamedRange(namedRange);
			return true;
		} catch (Exception e) {
			log.warning(e.toString());
			return false;
		}
	}
	public Boolean setFormula(final String dimensions, final String value) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimensions) && !SpreadSheetUtils.isValidExcelDimensionRange(dimensions)) {
			log.warning("Invalid dimension: " + dimensions);
			return false;
		}

		this.fs.setFormula(dimensions, value);
		return true;
	}

	public String getFormula(final String dimension) {
		if (!SpreadSheetUtils.isValidExcelDimension(dimension)) {
			log.warning("Invalid dimension: " + dimension);
			return null;
		}

		return this.fs.getFormula(dimension);
	}
}
