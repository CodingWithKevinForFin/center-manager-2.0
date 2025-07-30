package com.larkinpoint.analytics.ivydb;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.db.DbService;
import com.larkinpoint.analytics.LarkinPointState;
import com.larkinpoint.messages.LoadFileMessage;

public class IvyDbZeroCurveFileProcessor extends BasicProcessor<LoadFileMessage, LarkinPointState> {

	private File basePath;
	private boolean skipFirstRow;

	public IvyDbZeroCurveFileProcessor() {
		super(LoadFileMessage.class, LarkinPointState.class);
	}

	@Override
	public void processAction(LoadFileMessage action, LarkinPointState state, ThreadScope threadScope) throws Exception {
		final String fileName = action.getLoadFilename();
		final DbService dbservice = (DbService) getServices().getService("OPTIONSDB");
		System.out.println("Processing file:" + fileName);
		File file = new File(basePath, fileName);
		File[] files = IOH.listFiles(file);

		for (File f : files) {
			String[] lines = SH.splitLines(IOH.readText(f));
			Date date, date1;
			final Connection connection = dbservice.getConnection();
			try {
				final Map<Object, Object> params = new HashMap<Object, Object>();
				SimpleDateFormat sdfSource = new SimpleDateFormat("MM/dd/yyyy");
				//insert file name
				long fileId = getServices().getUidGenerator().createNextId("IDS");
				params.put("id", fileId);
				params.put("filename", f.getName());
				params.put("upload_time", getTools().getNow());
				dbservice.execute("insert_file_input_data", params, connection);

				for (int i = 0; i < lines.length - 1; i++) {
					params.clear();
					if (i == 0 && skipFirstRow)
						continue;
					final String line = lines[i];
					try {
						final String[] cells = SH.split(',', line);
						if (!"RUT".equals(cells[0]) || !SH.startsWith(cells[3], "RUT", 0) || !Character.isDigit(cells[3].charAt(3)))
							continue;

						long rowId = getServices().getUidGenerator().createNextId("IDS");
						params.put("id", rowId);
						params.put("file_id", fileId);
						params.put("symbol", cells[0]);
						params.put("u_close", cells[1]);
						params.put("exchange_code", cells[2]);
						params.put("option_symbol", cells[3]);

						params.put("cp", ((cells[5].equals("call")) ? 0 : 1));

						//parse the string into Date object
						date = sdfSource.parse(cells[6]);
						date1 = sdfSource.parse(cells[7]);
						params.put("expiry_date", date);
						params.put("quote_date", date1);

						params.put("strike_price", MH.noNan(SH.parseDouble(cells[8]), 0.0));
						params.put("last", MH.noNan(SH.parseDouble(cells[9]), 0.0));
						params.put("bid", MH.noNan(SH.parseDouble(cells[10]), 0.0));
						params.put("ask", MH.noNan(SH.parseDouble(cells[11]), 0.0));
						params.put("volume", SH.parseLong(cells[12]));
						params.put("open_interest", SH.parseLong(cells[13]));
						params.put("implied_vol", MH.noNan(SH.parseDouble(cells[14]), 0.0));
						params.put("delta", MH.noNan(SH.parseDouble(cells[15]), 0.0));
						params.put("gamma", MH.noNan(SH.parseDouble(cells[16]), 0.0));
						params.put("theta", MH.noNan(SH.parseDouble(cells[17]), 0.0));
						params.put("vega", MH.noNan(SH.parseDouble(cells[18]), 0.0));

						dbservice.execute("insert_ivy_zero_curves_data", params, connection);
					} catch (Exception e) {
						throw new RuntimeException("at line " + i + ": " + line, e);
					}
				}
			} finally {
				IOH.close(connection);
			}

			//		state.getUnderlyings().add

			//	OptionTradeDates tradeDates = state.getUnderlyings().getTradeDates("MSFT");
			//OptionExpiries expiry = tradeDates.getExpiry(20120410);
			//	OptionStrikes strike = expiry.getStrike(20130405);
			//CallsAndPuts callAndPuts = strike.getStrike(50.05);
			//OptionValues call = callAndPuts.getCalls();
			//	call.getAsk();

			// TODO Auto-generated method stub
			System.out.println("Finished processing file: " + f);
		}
	}
	@Override
	public void init() {
		super.init();
		basePath = getServices().getPropertyController().getRequired("options.directory", File.class);
		skipFirstRow = getServices().getPropertyController().getOptional("options.skipfirstrow", false);

	}

}
