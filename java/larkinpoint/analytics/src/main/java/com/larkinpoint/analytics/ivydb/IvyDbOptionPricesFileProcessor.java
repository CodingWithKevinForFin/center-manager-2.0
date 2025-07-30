package com.larkinpoint.analytics.ivydb;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.db.DbService;
import com.larkinpoint.analytics.LarkinPointState;
import com.larkinpoint.analytics.state.OptionDataRoot.SecurityInfo;
import com.larkinpoint.messages.LoadFileMessage;

public class IvyDbOptionPricesFileProcessor extends BasicProcessor<LoadFileMessage, LarkinPointState> {

	private File basePath;
	private boolean skipFirstRow;
	private Set<Date> validFridayExpirys;
	private Set<Date> validSaturdayExpirys;
	private Set<Date> validThursday2ndWeekExpirys;
	private Set<Date> validThursday3rdWeekExpirys;
	private Set<Date> validWednesdayExpirys;
	private final String insertString ="insert_ivy_daily_option_price";
	private final String deleteString ="delete_ivy_daily_option_price";
	private final String updateString ="update_ivy_daily_option_price";

	public IvyDbOptionPricesFileProcessor() {
		super(LoadFileMessage.class, LarkinPointState.class);
		validFridayExpirys = new HashSet<Date>();
		validSaturdayExpirys = new HashSet<Date>();
		validThursday2ndWeekExpirys = new HashSet<Date>();
		validThursday3rdWeekExpirys = new HashSet<Date>();
		validWednesdayExpirys = new HashSet<Date>();
		Calendar cal = Calendar.getInstance();
		for (int year = 1990; year < 2030; year++) {
			for (int month = 0; month < 12; month++) {
				cal.clear();
				cal.set(year, month, 0, 0, 0);

				cal.setMinimalDaysInFirstWeek(2);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				cal.set(Calendar.WEEK_OF_MONTH, 3);

				validFridayExpirys.add(cal.getTime());

				cal.add(Calendar.DAY_OF_MONTH, 1);
				validSaturdayExpirys.add(cal.getTime());

				cal.clear();
				cal.set(year, month, 0, 0, 0);
				cal.setMinimalDaysInFirstWeek(3);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

				cal.set(Calendar.WEEK_OF_MONTH, 2);
				validThursday2ndWeekExpirys.add(cal.getTime());

				cal.set(Calendar.WEEK_OF_MONTH, 3);
				validThursday3rdWeekExpirys.add(cal.getTime());

				cal.clear();
				cal.set(year, month, 0, 0, 0);
				cal.setMinimalDaysInFirstWeek(4);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
				cal.set(Calendar.WEEK_OF_MONTH, 3);
				validWednesdayExpirys.add(cal.getTime());

			}
		}

	}
	@Override
	public void processAction(LoadFileMessage action, LarkinPointState state, ThreadScope threadScope) throws Exception {
		final String fileName = action.getLoadFilename();
		final DbService dbservice = (DbService) getServices().getService("OPTIONSDB");
		System.out.println("Processing file:" + fileName);
		File file = new File(basePath, fileName);
		File[] files = IOH.listFiles(file);
		System.out.println("Starting processing " + files.length + " files. ");
		Set<Long> options = new HashSet<Long>();
		int k = 1;
		for (File f : files) {
			System.out.println("Starting processing file: " + f);
			String[] lines = SH.splitLines(IOH.readText(f));
			Date date, date1;
			final Connection connection = dbservice.getConnection();
			try {
				final Map<Object, Object> params = new HashMap<Object, Object>();
				SimpleDateFormat sdfSource = new SimpleDateFormat("yyyyMMdd");
				//insert file name
				long fileId = getServices().getUidGenerator().createNextId("IDS");
				params.put("id", fileId);
				params.put("filename", f.getName());
				params.put("upload_time", getTools().getNow());
				dbservice.execute("insert_file_input_data", params, connection);
				Set<Date> expiry;
				for (int i = 0; i < lines.length; i++) {
					boolean delete = false;
					boolean insert = true;
					params.clear();
					if (i == 0 && skipFirstRow)
						continue;
					final String line = lines[i];
					String dbCommand= insertString;
					int offset = 0;

					try {
						final String[] cells = SH.split('\t', line);
						if ( cells[0].equals("I")){
							delete = true;
							offset++;
						}else if ( cells[0].equals("D")){
							delete = true;
							insert = false;
							offset++;
						}
						
						date = sdfSource.parse(cells[offset+5]);
						SecurityInfo sec = state.getOptionDataRoot().getSecurityInfo(SH.parseLong(cells[offset]));

						if (sec == null) {
							//LH.warning(log, "Unknown sec for " + cells[0]);
							continue;
						}
						if (sec.getExpiry_dayofweek() == 5 && sec.getExpiry_weekofmonth() == 2) {
							expiry = validThursday2ndWeekExpirys;
						} else if (sec.getExpiry_dayofweek() == 5 && sec.getExpiry_weekofmonth() == 2) {
							expiry = validThursday3rdWeekExpirys;
						} else if (sec.getExpiry_dayofweek() == 7) {
							expiry = validSaturdayExpirys;

						} else if (sec.getExpiry_dayofweek() == 6) {
							expiry = validFridayExpirys;
						} else if (sec.getExpiry_dayofweek() == 4) {
							expiry = validWednesdayExpirys;
						} else
						
							expiry = null;

						if (expiry != null && expiry.contains(date)) {
							//System.out.println("date= " + date);
							//	LH.warning(log, "Good expiry for " + cells[0] + " on " + date);

						} else if (expiry != null) {
							//	System.out.println("Skipping record for " + date);
							//	LH.warning(log, "Bad expiry for " + cells[0] + " on " + date);
							continue;
						}
					
						params.put("security_id", cells[offset]);
						params.put("quote_date", cells[offset+1]);
						params.put("option_id", cells[offset+2]);
						params.put("exchange", cells[offset+3]);
						
						if( delete ==true){
							dbCommand = deleteString;
							dbservice.execute(dbCommand, params, connection);
						}
						if( insert == true){
							dbCommand= insertString;
							long id = getServices().getUidGenerator().createNextId("IDS");
							params.put("id", id);
							params.put("file_id", fileId);
							params.put("currency", cells[offset+4]);
							params.put("expiry_date", cells[offset+5]);
							params.put("strike_price", (SH.parseDouble(cells[offset+6])) / 1000);
							params.put("callput", ((cells[offset+7].equals("C")) ? 0 : 1));
						
							params.put("symbol", cells[offset+8]);
							params.put("bid", cells[offset+9]);
							params.put("ask", cells[offset+10]);
							params.put("last", cells[offset+11]);
							params.put("volume", cells[offset+12]);
							params.put("open_interest", cells[offset+13]);
							params.put("special_settlement", cells[offset+14]);
	
							params.put("implied_vol", (cells[offset+15].isEmpty() ? "0.0" : cells[offset+15]));
							params.put("delta", cells[offset+16]);
							params.put("gamma", cells[offset+17]);
							params.put("vega", cells[offset+18]);
							params.put("theta", cells[offset+19]);
							params.put("adj_factor", cells[offset+20]);
							params.put("exercise_style", cells[offset+21]);
							params.put("symbol_flag", cells[offset+22]);
	
							dbservice.execute(dbCommand, params, connection);
						}
					} catch (Exception e) {
						LH.warning(log, "at line " + i + ": " + line, e);
						
					}
				}
			} finally {
	
				IOH.close(connection);
				
			}

			// TODO Auto-generated method stub
			System.out.println(k++ + " Finished processing file : " + f);
		}
		System.out.println("Finished processing all Files");
	}
	@Override
	public void init() {
		super.init();
		basePath = getServices().getPropertyController().getRequired("options.directory", File.class);
		skipFirstRow = getServices().getPropertyController().getOptional("options.skipfirstrow", false);

	}

}
