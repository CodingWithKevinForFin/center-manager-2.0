package app.fix42;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

import com.sjls.algos.eo.common.AmendOrderRequestMsg;
import com.sjls.algos.eo.common.OrderModifyException;

/**
 * A simple FIX Engine that uses Java classes to do stuff
 * 
 * @author Olu Emuleomo
 * @date 04/19/2010
 * 
 */
public class FIXEngine {
	public final static String CVS_ID = "$Id: FIXEngine.java,v 1.1.1.1 2011/12/06 23:26:23 olu Exp $";
	private static Logger m_logger = Logger.getLogger(FIXEngine.class.getName());

	private final IFIX42Client m_client;
	private final ScheduledThreadPoolExecutor m_executor = new ScheduledThreadPoolExecutor(2); // 2
																								// core
																								// threads
																								// should
																								// be
																								// enough

	private ConcurrentHashMap<String, OrderExecutor> m_ordersMap = new ConcurrentHashMap<String, OrderExecutor>();

	private final Random m_random = new Random();

	public FIXEngine(final IFIX42Client client) {
		m_client = client; // client to respond to!
	}

	public int getRandom1to10() {
		return 1 + m_random.nextInt(10);
	}

	public int getRandom1to100() {
		return 1 + m_random.nextInt(100);
	}

	public void onMsg(final NewOrderSingleMsg msg) {
		// wait for 1 sec
		//
		final OrderExecutor orderExectr = new OrderExecutor(m_client, m_executor, msg);
		m_ordersMap.put(makeKey(msg.getBlockID(), msg.getClOrdID()), orderExectr);

		// delay the pdgNew from 0 to 5secs
		final int pdgNewDelay = (int) (getRandom1to10() / 2.0);

		orderExectr.schedule(pdgNewDelay);
	}

	public synchronized void cancel(final String blockID, final String clOrdID) throws OrderModifyException {
		final OrderExecutor orderExectr = m_ordersMap.get(makeKey(blockID, clOrdID));
		if (orderExectr == null) {
			throw new OrderModifyException(String.format("BlockID=%s: Cant find child order [%s]", blockID, clOrdID));
		}
		orderExectr.cancel();
	}

	public synchronized void cxlRpl(final AmendOrderRequestMsg req, final String clOrdID) throws OrderModifyException {
		final OrderExecutor orderExectr = m_ordersMap.get(makeKey(req.getBlockID(), req.getOrigClOrdID()));
		if (orderExectr == null) {
			throw new OrderModifyException(String.format("BlockID=%s: Cant find child order [%s]", req.getBlockID(), req.getOrigClOrdID()));
		}
		orderExectr.cxlRpl(req, clOrdID, this);
	}

	public synchronized void replaceOrdID(final AmendOrderRequestMsg req, final String clOrdID) {
		final String oldKey = makeKey(req.getBlockID(), req.getOrigClOrdID());
		final String newKey = makeKey(req.getBlockID(), clOrdID);
		m_ordersMap.put(newKey, m_ordersMap.remove(oldKey));
		m_logger.info(String.format("BlockID=%s: Prev Order %s now known as %s", req.getBlockID(), req.getOrigClOrdID(), clOrdID));
	}

	public static String makeKey(final String blockID, final String clOrdID) {
		return blockID + "::" + clOrdID;
	}

}
