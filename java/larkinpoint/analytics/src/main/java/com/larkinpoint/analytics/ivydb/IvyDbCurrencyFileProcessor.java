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

public class IvyDbCurrencyFileProcessor extends BasicProcessor<LoadFileMessage, LarkinPointState> {

	private File basePath;
	private boolean skipFirstRow;

	public IvyDbCurrencyFileProcessor() {
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
					final String line = lines[i];
					try {
						final String[] cells = SH.split('\t', line);
						//	if (!"RUT".equals(cells[0]) || !SH.startsWith(cells[3], "RUT", 0) || !Character.isDigit(cells[3].charAt(3)))
						//		continue;

						long rowId = getServices().getUidGenerator().createNextId("IDS");
						params.put("currency_id", cells[0]);
						params.put("file_id", fileId);
						params.put("symbol", cells[1]);
						params.put("name", cells[2]);

						dbservice.execute("insert_ivy_currency_data", params, connection);
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
