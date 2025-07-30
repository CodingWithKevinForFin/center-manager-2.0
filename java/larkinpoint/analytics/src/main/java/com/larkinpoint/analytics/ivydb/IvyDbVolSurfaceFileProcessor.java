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

public class IvyDbVolSurfaceFileProcessor extends BasicProcessor<LoadFileMessage, LarkinPointState> {

	private File basePath;
	private boolean skipFirstRow;

	public IvyDbVolSurfaceFileProcessor() {
		super(LoadFileMessage.class, LarkinPointState.class);
	}

	@Override
	public void processAction(LoadFileMessage action, LarkinPointState state, ThreadScope threadScope) throws Exception {

		final String fileName = action.getLoadFilename();
		final DbService dbservice = (DbService) getServices().getService("OPTIONSDB");
		System.out.println("Processing file:" + fileName);
		File file = new File(basePath, fileName);
		File[] files = IOH.listFiles(file);
		System.out.println("Starting processing " + files.length + " files. ");

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

				for (int i = 0; i < lines.length - 1; i++) {
					params.clear();
					if (i == 0 && skipFirstRow)
						continue;
					final String line = lines[i];
					try {
						final String[] cells = SH.split('\t', line);

						long rowId = getServices().getUidGenerator().createNextId("IDS");
						params.put("id", rowId);
						params.put("file_id", fileId);
						params.put("security_id", cells[0]);
						//parse the string into Date object
						//	date = sdfSource.parse(cells[1]);
						params.put("quote_date", cells[1]);
						params.put("days", SH.parseInt(cells[2]));
						params.put("delta", SH.parseInt(cells[3]));
						params.put("cp", ((cells[4].equals("C")) ? 0 : 1));

						params.put("implied_vol", MH.noNan(SH.parseDouble(cells[5]), 0.0));
						params.put("implied_strike", MH.noNan(SH.parseDouble(cells[6]), 0.0));
						params.put("implied_premium", MH.noNan(SH.parseDouble(cells[7]), 0.0));
						params.put("dispersion", MH.noNan(SH.parseDouble(cells[8]), 0.0));
						params.put("currency_id", SH.parseLong(cells[9]));

						dbservice.execute("insert_ivy_vol_surface_data", params, connection);
					} catch (Exception e) {
						//throw new RuntimeException("at line " + i + ": " + line, e);
					}
				}
			} finally {
				IOH.close(connection);
			}

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
