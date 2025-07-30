package app.controlpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import com.sjls.algos.eo.common.IBinTradeData;

/**
 * OrderProgressChart generates Order trading trajectory Chart for a selected Order in control panel GUI
 * 
 * @author hsingh
 * 
 */
public class OrderProgressChart {
	private final TimeSeries liveTSData = new TimeSeries("Actual");
	private final TimeSeries projectedTSData = new TimeSeries("Projected");
	private static final String TITLE = "Order Progress";
	private static final String TITLE_XAXIS = "Time";
	private static final String TITLE_YAXIS = "Shares";

	private String m_parentOrderID = null;
	// private int m_cumlativeQty=0;
	private final JFreeChart chart;
	public final ChartPanel m_chartPanel;
	/**
	 * create Order Progress chart of a specified dimension
	 * 
	 * @param prefWidth
	 * @param prefHeight
	 */
	public OrderProgressChart(final int prefWidth, final int prefHeight) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(liveTSData);
		dataset.addSeries(projectedTSData);
		chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(prefWidth, prefHeight));
		chartPanel.setMouseZoomable(true, false);
		m_chartPanel = chartPanel;
	}

	public JPanel getPanel() {
		return m_chartPanel;
	}

	/**
	 * creates JFreeChart object for GUI. this chart is updated with data of selected order when user selects an OrderID from list
	 * 
	 * @param dataset
	 * @return
	 */
	private JFreeChart createChart(XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(TITLE, // title
				TITLE_XAXIS, // x-axis label
				TITLE_YAXIS, // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);
		chart.setAntiAlias(false);
		chart.setTitle(new TextTitle(TITLE, new Font("sansserif", Font.BOLD, 10)));
		chart.setBackgroundPaint(Color.white);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.GREEN);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setSeriesPaint(0, Color.GREEN);
			renderer.setSeriesPaint(1, Color.blue);
			// renderer.setBaseShapesFilled(true);

		}
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("hh:mm a"));
		return chart;
	}

	/**
	 * Sets Parent Order ID, initial trajectory and live trade data for this chart. This method is called by RulesEngine Order List selection listener in
	 * control panel GUI when user selects one of OrderID from list
	 * 
	 * @param orderID
	 * @param bindataList
	 * @param initdataList
	 */
	public void setInitialData(final String orderID, final List<IBinTradeData> bindataList, final List<IBinTradeData> initdataList) {
		m_parentOrderID = orderID;
		// chart.setTitle(TITLE+" for Block "+orderID);
		chart.setTitle(new TextTitle(TITLE + " for Block " + orderID, new Font("TimesNewRoman", Font.PLAIN, 20)));
		projectedTSData.clear();
		// projectedTSData = new TimeSeries("Projected Trajectory", Minute.class);
		int cumulativeShares = 0;
		for (IBinTradeData bindata : bindataList) {
			cumulativeShares += bindata.getShares();
			projectedTSData.addOrUpdate(new Minute(bindata.getStartTime()), cumulativeShares);
		}
		// liveTSData = new TimeSeries("Actual Trajectory", Minute.class);
		liveTSData.clear();
		// m_cumlativeQty=0;
		if (initdataList != null) {
			for (IBinTradeData bindata : initdataList) {
				// m_cumlativeQty+=bindata.getShares();
				liveTSData.addOrUpdate(new Minute(bindata.getStartTime()), bindata.getShares());
			}
		}
	}

	public void setInitialData(final String orderID, final String symbol, final String side, final int qty, final Double price,
			final List<IBinTradeData> bindataList, final List<IBinTradeData> initdataList) {
		m_parentOrderID = orderID;
		chart.setTitle(TITLE + " for Block " + orderID + " (" + side + " " + qty + " @ " + price + ")");

		projectedTSData.clear();
		int cumulativeShares = 0;
		for (IBinTradeData bindata : bindataList) {
			cumulativeShares += bindata.getShares();
			projectedTSData.addOrUpdate(new Minute(bindata.getStartTime()), cumulativeShares);
		}
		liveTSData.clear();
		if (initdataList != null) {
			for (IBinTradeData bindata : initdataList) {
				// m_cumlativeQty+=bindata.getShares();
				liveTSData.addOrUpdate(new Minute(bindata.getStartTime()), bindata.getShares());
			}
		}
	}

	/**
	 * updates Live trade data about a bin on chart if this live data update matches selected Parent OrderID/BlockId
	 * 
	 * @param orderID
	 * @param bindata
	 */
	public void setLiveData(final String orderID, final IBinTradeData bindata) {
		if (m_parentOrderID != null && m_parentOrderID.equals(orderID)) {
			liveTSData.addOrUpdate(new Minute(bindata.getStartTime()), bindata.getShares());
		}
	}
}
