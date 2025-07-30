package com.f1.generator;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix2ami.Fix2AmiMain;
import com.f1.generator.handler.AbstractHandler;
import com.f1.generator.handler.CancelRejectHandler;
import com.f1.generator.handler.CancelRequestGenerator;
import com.f1.generator.handler.CancelRequestHandler;
import com.f1.generator.handler.ClientEventGenerator;
import com.f1.generator.handler.ClientPublishHandler;
import com.f1.generator.handler.ExchangeEventGenerator;
import com.f1.generator.handler.ExchangePublishHandler;
import com.f1.generator.handler.ExecutionReportHandler;
import com.f1.generator.handler.FIXMsgDispatcher;
import com.f1.generator.handler.FillGenerator;
import com.f1.generator.handler.NewOrderHandler;
import com.f1.generator.handler.NewRequestGenerator;
import com.f1.generator.handler.ReplaceRequestGenerator;
import com.f1.generator.handler.ReplaceRequestHandler;
import com.f1.transportManagement.SessionManager;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

import quickfix.ConfigError;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class OrderGeneratorMain {
	private static final Logger log = Logger.getLogger(OrderGeneratorMain.class.getName());

	static BasicPartitionResolver<OrderEvent> pr = new BasicPartitionResolver<OrderEvent>(OrderEvent.class, null) {
		@Override
		public Object getPartitionId(OrderEvent event) {
			return event.getPartitionId();
		}
	};

	public static FIXMsgDispatcher setup(final PropertyController props, final ClientEventGenerator clientEventGenerator, final ExchangeEventGenerator exchangeEventGenerator)
			throws ConfigError {
		Suite rs = CONTAINER.getSuiteController().getRootSuite();

		FIXMsgDispatcher fixMsgDispatcher = new FIXMsgDispatcher();

		AbstractHandler newOrderHandler = new NewOrderHandler(props);
		ExecutionReportHandler executionReportHandler = new ExecutionReportHandler(props);
		BasicProcessor<OrderEvent, OrderCache> canelRejectHandler = new CancelRejectHandler(props);
		AbstractHandler cancelRequestHandler = new CancelRequestHandler(props);
		AbstractHandler replaceRequestHandler = new ReplaceRequestHandler(props);

		BasicProcessor<OrderEvent, OrderCache> exchangePublishHandler = new ExchangePublishHandler(props);
		BasicProcessor<OrderEvent, OrderCache> clientPublishHandler = new ClientPublishHandler(props);

		fixMsgDispatcher.setPartitionResolver(pr);
		newOrderHandler.setPartitionResolver(pr);
		executionReportHandler.setPartitionResolver(pr);
		canelRejectHandler.setPartitionResolver(pr);
		cancelRequestHandler.setPartitionResolver(pr);
		replaceRequestHandler.setPartitionResolver(pr);
		exchangePublishHandler.bindToPartition("GLOBAL_STATE1");
		clientPublishHandler.bindToPartition("GLOBAL_STATE2");

		// generator side handling.

		AbstractHandler newRequestGenerator = new NewRequestGenerator(props);
		AbstractHandler cancelRequestGenerator = new CancelRequestGenerator(props);
		AbstractHandler replaceRequestGenerator = new ReplaceRequestGenerator(props);

		AbstractHandler fillGenerator = new FillGenerator(props);

		newRequestGenerator.setPartitionResolver(pr);
		cancelRequestGenerator.setPartitionResolver(pr);
		replaceRequestGenerator.setPartitionResolver(pr);
		fillGenerator.setPartitionResolver(pr);

		rs.addChildren(fixMsgDispatcher, newOrderHandler, executionReportHandler, canelRejectHandler, cancelRequestHandler, replaceRequestHandler, exchangePublishHandler,
				clientPublishHandler, clientEventGenerator, newRequestGenerator, cancelRequestGenerator, replaceRequestGenerator, exchangeEventGenerator, fillGenerator);
		rs.wire(fixMsgDispatcher.newOrderPort, newOrderHandler, true);
		rs.wire(fixMsgDispatcher.executionReportPort, executionReportHandler, true);
		rs.wire(fixMsgDispatcher.cancelRejectPort, canelRejectHandler, true);
		rs.wire(fixMsgDispatcher.cancelRequestPort, cancelRequestHandler, true);
		rs.wire(fixMsgDispatcher.replaceRequestPort, replaceRequestHandler, true);

		rs.wire(newOrderHandler.fixMsgPort, exchangePublishHandler, true);
		rs.wire(cancelRequestHandler.fixMsgPort, exchangePublishHandler, true);
		rs.wire(replaceRequestHandler.fixMsgPort, exchangePublishHandler, true);

		// generator side wiring.
		rs.wire(clientEventGenerator.newOrderPort, newRequestGenerator, true);
		rs.wire(clientEventGenerator.cancelRequestPort, cancelRequestGenerator, true);
		rs.wire(clientEventGenerator.replaceRequestPort, replaceRequestGenerator, true);
		rs.wire(newRequestGenerator.fixMsgPort, clientPublishHandler, true);
		rs.wire(cancelRequestGenerator.fixMsgPort, clientPublishHandler, true);
		rs.wire(replaceRequestGenerator.fixMsgPort, clientPublishHandler, true);

		rs.wire(exchangeEventGenerator.fillPort, fillGenerator, true); // partial fill, fill, trade correction, trade bust.
		rs.wire(fillGenerator.fixMsgPort, exchangePublishHandler, true);

		return fixMsgDispatcher;
	}

	private final static Container CONTAINER = new BasicContainer();

	public static Container getContainer() {
		return CONTAINER;
	}

	public static void main(String[] args) throws Exception {
		final ContainerBootstrap cam = new ContainerBootstrap(Fix2AmiMain.class, args);
		cam.setConfigDirProperty("./src/main/config/ordergenerator");
		cam.setTerminateFileProperty("${f1.conf.dir}/../." + cam.getMainClass().getSimpleName().toLowerCase() + ".prc");
		final PropertyController props = cam.getProperties();
		cam.prepareContainer(CONTAINER);

		pullMarketInfo(props);

		ClientEventGenerator clientEventGenerator = new ClientEventGenerator(props);
		ExchangeEventGenerator exchangeEventGenerator = new ExchangeEventGenerator(props);

		FIXMsgDispatcher handlerDispatcher = setup(props, clientEventGenerator, exchangeEventGenerator);

		cam.startupContainer(CONTAINER);
		cam.keepAlive();

		SessionManager sessionManager = new SessionManager(CONTAINER, handlerDispatcher, props);
		ClientPublishHandler.setSessionManager(sessionManager);
		ExchangePublishHandler.setSessionManager(sessionManager);

		sessionManager.start();
		clientEventGenerator.startScheduleEvent();
		exchangeEventGenerator.startScheduleEvent();
	}

	public static void pullMarketInfo(final PropertyController props) {
		String[] tmp = SH.split(',', props.getOptional(AbstractHandler.ATTR_GENERATOR_SYMBOLS, AbstractHandler.DEFAULT_SYMBOL_LIST));
		String[] symbols = new String[tmp.length];

		for (int i = 0; i < tmp.length; i++) {
			symbols[i] = tmp[i].toUpperCase().trim();
		}

		Stock stock = null;

		int priceSwingPercentage = props.getOptional(AbstractHandler.ATTR_GENERATOR_PRICE_SWING + ".All", AbstractHandler.DEFAULT_MAX_PRICE_SWING);
		int maxQty = props.getOptional(AbstractHandler.ATTR_GENERATOR_MAX_QTY + ".All", AbstractHandler.DEFAULT_MAX_QTY);
		try {
			Map<String, Stock> stocks = YahooFinance.get(symbols);

			for (String symbol : symbols) {
				stock = stocks.get(symbol);
				if (null == stock) {
					LH.info(log, "failed to get stock info on ", symbol);
					continue;
				}

				int priceSwingPercentageBySymbol = props.getOptional(AbstractHandler.ATTR_GENERATOR_PRICE_SWING + '.' + symbol, priceSwingPercentage);
				int maxQtyBySymbol = props.getOptional(AbstractHandler.ATTR_GENERATOR_MAX_QTY + '.' + symbol, maxQty);
				StockInfo.getStockInfo().put(symbol,
						new StockInfo(symbol, stock.getQuote().getPreviousClose().doubleValue(), stock.getQuote().getPrice().doubleValue(),
								stock.getQuote().getDayHigh().doubleValue(), stock.getQuote().getDayLow().doubleValue(), stock.getQuote().getAsk().doubleValue(),
								stock.getQuote().getBid().doubleValue(), stock.getQuote().getAskSize(), stock.getQuote().getBidSize(), priceSwingPercentageBySymbol,
								stock.getQuote().getAvgVolume(), stock.getStockExchange(), maxQtyBySymbol));
				LH.info(log, "successfully loaded ", symbol);
			}

		} catch (IOException ioe) {
			LH.info(log, "failed to load market info");
		}

	}

}
