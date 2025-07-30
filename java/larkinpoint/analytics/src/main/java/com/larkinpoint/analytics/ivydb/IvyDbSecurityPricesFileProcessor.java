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
import com.f1.utils.SH;
import com.f1.utils.db.DbService;
import com.larkinpoint.analytics.LarkinPointState;
import com.larkinpoint.messages.LoadFileMessage;

public class IvyDbSecurityPricesFileProcessor extends BasicProcessor<LoadFileMessage, LarkinPointState> {

	private File basePath;
	private boolean skipFirstRow;

	public IvyDbSecurityPricesFileProcessor() {
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

				for (int i = 0; i < lines.length; i++) {
					params.clear();
					if (i == 0 && skipFirstRow)
						continue;
					long id = getServices().getUidGenerator().createNextId("IDS");
					final String line = lines[i];
					String sql;
					try {
						final String[] cells = SH.split('\t', line);
						int colOffset = 0;
						if ("I".equals(cells[0])) {
							colOffset = 1;
							sql = "update_ivy_security_price_data";

						} else if ("D".equals(cells[0])) {
							sql = "delete_ivy_security_price_data";
							colOffset = 1;
						} else {
							sql = "insert_ivy_security_price_data";
							sql = "update_ivy_security_price_data";
						}
						params.put("id", id);
						params.put("file_id", fileId);
						params.put("security_id", cells[colOffset++]);
						params.put("quote_date", cells[colOffset++]);
						params.put("exchange", SH.parseInt(cells[colOffset++]));
						params.put("currency", cells[colOffset++]);

						params.put("bid", cells[colOffset++]);
						params.put("ask", cells[colOffset++]);
						params.put("open", cells[colOffset++]);
						params.put("close", cells[colOffset++]);
						params.put("volume", SH.parseLong(cells[colOffset++]));
						params.put("total_return", cells[colOffset++]);
						params.put("adj_factor", cells[colOffset++]);
						params.put("cum_total_return", cells[colOffset]);

						dbservice.execute(sql, params, connection);

					} catch (Exception e) {
						//throw new RuntimeException("at line " + i + ": " + line, e);
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
