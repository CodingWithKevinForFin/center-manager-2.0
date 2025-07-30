package com.sjls.f1.taploader;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.client.AmiClient;
import com.f1.base.ObjectGenerator;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.utils.CH;
import com.f1.utils.DBH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.db.DbService;
import com.f1.utils.db.DriverToDataSource;

public class TapLoaderMain {
	public static final Logger log = LH.get(TapLoaderMain.class);

	public static void main(String a[]) throws Exception {
		ContainerBootstrap bs = new ContainerBootstrap(TapLoaderMain.class, a);
		bs.setConfigDirProperty("./src/main/config");
		bs.setTerminateFileProperty("${f1.conf.dir}/../.taploader.prc");
		bs.setLoggingOverrideProperty("info");
		bs.startup();
		init(bs.getProperties(), bs.getCodeGenerator());
	}
	//	@Override
	//	public void init(ContainerTools tools, PropertyController props) {
	//		init(tools, tools.getServices().getGenerator());
	//
	//	}
	static public void init(PropertyController properties, ObjectGenerator generator) {
		try {

			AmiClient ac = new AmiClient();
			String groupBys = properties.getRequired("taploader.groupby");
			ac.start(properties.getOptional("taploader.ami.host", "localhost"), properties.getOptional("taploader.ami.port", 3289), "taploader", 0);
			DriverToDataSource datasource = DBH.createDataSource(properties.getRequired("taploader.db"), properties.getRequired("taploader.db.password"));
			DbService ds = new DbService(datasource, generator);
			if (properties.getOptional("sql.dir") != null)
				ds.add(properties.getRequired("sql.dir", File.class), ".sql");

			Connection conn = ds.getConnection();
			List<Date> dates = DBH.toList(ds.executeSqlQuery(GET_DATES, CH.m(), conn), java.sql.Date.class);
			if (dates.size() > 0) {
				Collections.sort(dates);
				LH.info(log, "Found: " + dates.size() + " date(s) for processing: " + CH.first(dates) + " - " + CH.last(dates));
				for (Date date : dates) {
					LH.info(log, "working on date: " + date);
					Table table = DBH.toTable(ds.executeSqlQuery(POPULATE_TAP_SUMMARY, CH.m("date", date), conn));
					Map<String, Integer> counts = new HashMap<String, Integer>();
					counts.put("tapsummary", table.getSize());
					for (Row r : table.getRows()) {
						ac.startObjectMessage("tapsummary", null, 0);
						ac.addMessageParams((Map) r);
						ac.sendMessage();
					}
					for (String s : SH.split(",", groupBys)) {
						table = DBH.toTable(ds.executeSqlQuery(POPULATE_TAP_GRAPHS, CH.m("date", date, "groupby", s), conn));
						counts.put(s, table.getSize());
						for (Row r : table.getRows()) {
							ac.startObjectMessage("tapgraph_" + s, null, 0);
							ac.addMessageParams((Map) r);
							ac.sendMessage();
						}
						//ds.execute("populate_tapgraph", CH.m("date", date), conn);
						ac.flush();
					}
					LH.info(log, "Counts: " + counts);
				}
			} else
				LH.info(log, "No dates found for processing");
		} catch (Exception e) {
			LH.severe(log, "Tap Loader Failed: ", e);
		}
	}

	private static final String GET_DATES = "select distinct date(date_gen) from trades";
	private static final String POPULATE_TAP_SUMMARY = "SELECT "
			+ "date_gen, "
			+ "client_code,"
			+ "view, "
			+ "sum(if (side=\"B\",1,0)) AS buy_c, "
			+ "sum(if (side=\"B\",shares_ordered,0)) AS buy_os,  "
			+ "sum(if (side=\"B\",shares_ordered * avg_exec_price * exchange_rate,0)) AS buy_oa, "
			+ "sum(if (side=\"B\",shares_executed,0)) AS buy_es,  "
			+ "sum(if (side=\"B\",shares_executed * avg_exec_price * exchange_rate,0)) AS buy_ea, "
			+ "sum(if (side=\"S\",1,0)) AS sell_c, "
			+ "sum(if (side=\"S\",shares_ordered,0)) AS sell_os,  "
			+ "sum(if (side=\"S\",shares_ordered * avg_exec_price * exchange_rate,0)) AS sell_oa, "
			+ "sum(if (side=\"S\",shares_executed,0)) AS sell_es,  "
			+ "sum(if (side=\"S\",shares_executed * avg_exec_price * exchange_rate,0)) AS sell_ea, "
			+ "sum(horizon_length) AS ola, "
			+ "sum(horizon_length * shares_executed) AS olsw, "
			+ "sum(horizon_length * shares_executed * avg_exec_price * exchange_rate) AS oldw, "
			+ "sum(shares_executed / interval_volume) AS pow, "
			+ "sum((avg_exec_price - arrival_price) * if(side=\"S\",1,-1)) AS eq_is_num, sum((avg_exec_price - arrival_price) * if(side=\"S\",1,-1)/arrival_price) AS eq_is_tot, sum(arrival_price) AS eq_is_den, "
			+ "sum((avg_exec_price - po           ) * if(side=\"S\",1,-1)) AS pf_op_num, sum((avg_exec_price - po           ) * if(side=\"S\",1,-1)/po           ) AS pf_op_tot, sum(po           ) AS pf_op_den, "
			+ "sum((avg_exec_price - Minus3M      ) * if(side=\"S\",1,-1)) AS bf_3m_num, sum((avg_exec_price - Minus3M      ) * if(side=\"S\",1,-1)/Minus3M      ) AS bf_3m_tot, sum(Minus3M      ) AS bf_3m_den "
			+ "FROM trades t  " + "JOIN exchange_rates e  ON t.date_gen=e.trade_date and t.currency=e.currency  "
			+ "JOIN sec_daily_stats s ON t.date_gen=s.trade_date and t.sjls_secid=s.sjls_secid  "
			+ "JOIN adjusted_prices a ON a.price_adjustment_date = t.date_gen AND (t.date_gen = a.trade_date OR t.date_closed = a.trade_date) and t.sjls_secid=a.sjls_secid  "
			+ "where date_gen=?{date} " + "GROUP BY date_gen, client_code,view";

	private static final String POPULATE_TAP_GRAPHS = "SELECT "
			+ "date_gen,view,??{groupby} as groupby,"
			+ "client_code,"
			+ "count(*) AS tot_c,"
			+ "sum((avg_exec_price - arrival_price) * if(side=\"S\",1,-1)) AS eq_is_num, sum((avg_exec_price - arrival_price) * if(side=\"S\",1,-1)/arrival_price) AS eq_is_tot, sum(arrival_price) AS eq_is_den,"
			+ "sum((avg_exec_price - po           ) * if(side=\"S\",1,-1)) AS pf_op_num, sum((avg_exec_price - po           ) * if(side=\"S\",1,-1)/po           ) AS pf_op_tot, sum(po           ) AS pf_op_den,"
			+ "sum((avg_exec_price - Minus3M      ) * if(side=\"S\",1,-1)) AS bf_3m_num, sum((avg_exec_price - Minus3M      ) * if(side=\"S\",1,-1)/Minus3M      ) AS bf_3m_tot, sum(Minus3M      ) AS bf_3m_den "
			+ "FROM trades t " + "JOIN exchange_rates e  ON t.date_gen=e.trade_date and t.currency=e.currency "
			+ "JOIN sec_daily_stats s ON t.date_gen=s.trade_date and t.sjls_secid=s.sjls_secid "
			+ "JOIN adjusted_prices a ON  a.price_adjustment_date = t.date_gen AND (t.date_gen = a.trade_date OR t.date_closed = a.trade_date) and t.sjls_secid=a.sjls_secid "
			+ "where date_gen=?{date}" + "GROUP BY date_gen,view,client_code, ??{groupby}";

}
