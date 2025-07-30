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

public class IvyDbExchangesFileProcessor extends BasicProcessor<LoadFileMessage, LarkinPointState> {

	private File basePath;
	private boolean skipFirstRow;

	public IvyDbExchangesFileProcessor() {
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

						//long rowId = getServices().getUidGenerator().createNextId("IDS");

						params.put("file_id", fileId);
						params.put("exchange_code", cells[0]);
						params.put("symbol", cells[1]);
						params.put("country", cells[2]);
						params.put("name", cells[3]);

						dbservice.execute("insert_ivy_exchange_data", params, connection);
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
