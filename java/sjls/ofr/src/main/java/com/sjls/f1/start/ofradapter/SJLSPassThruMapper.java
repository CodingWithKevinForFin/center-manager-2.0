package com.sjls.f1.start.ofradapter;

import com.f1.pofo.oms.Order;
import com.f1.utils.CH;
import com.sjls.algos.eo.common.AlgoParams;
import com.sjls.algos.eo.common.AlgoParamsIS;
import com.sjls.algos.eo.common.AlgoParamsTWAP;
import com.sjls.algos.eo.common.AlgoParamsVWAP;
import com.sjls.algos.eo.common.Amount;
import com.sjls.algos.eo.common.IAlgoParams;
import com.sjls.algos.eo.utils.DateUtils;
import com.sjls.start.common.SJLSCustomTags;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class SJLSPassThruMapper {
	protected final Logger log = Logger.getLogger(getClass().getName());
	/** ALGO MAPPER **/
	public static final String STRATEGY_VWAP = "SJLS_VWAP";
	public static final String STRATEGY_TWAP = "SJLS_TWAP";
	public static final String STRATEGY_IS = "SJLS_IS";
	public static final String STRATEGY_DEFAULT = STRATEGY_IS;

	public IAlgoParams getAlgoParams(final Order inner) {
		String strategy = CH.getOr(inner.getPassThruTags(), SJLSCustomTags.STRATEGY_TAG, STRATEGY_DEFAULT);

		AlgoParams params = null;
		if (STRATEGY_VWAP.equals(strategy)) {
			AlgoParamsVWAP vwap = new AlgoParamsVWAP();
			if (inner.getPassThruTags().containsKey(SJLSCustomTags.ENABLE_PROFILE_TILT_TAG)) {
				vwap.setProfileTiltEnabled(inner.getPassThruTags().get(SJLSCustomTags.ENABLE_PROFILE_TILT_TAG).equalsIgnoreCase("Y"));
			}
			params = vwap;
		} else if (STRATEGY_TWAP.equals(strategy)) {
			params = new AlgoParamsTWAP();
		} else if (STRATEGY_IS.equals(strategy)) {
			AlgoParamsIS is = new AlgoParamsIS();
			if (inner.getPassThruTags().containsKey(SJLSCustomTags.RISK_TOLERANCE_TAG)) {
				is.setRiskTolerance(Integer.parseInt(inner.getPassThruTags().get(SJLSCustomTags.RISK_TOLERANCE_TAG)));
			}
			params = is;
		} else
			throw new RuntimeException("unknown strategy: " + SJLSCustomTags.STRATEGY_TAG + "=" + strategy);

		if (inner.getPassThruTags().containsKey(SJLSCustomTags.PARTICIPATE_ON_CLOSE_TAG)) {
			params.setParticipateOnClose((inner.getPassThruTags().get(SJLSCustomTags.PARTICIPATE_ON_CLOSE_TAG)).equalsIgnoreCase("Y"));
		}
		if (inner.getPassThruTags().containsKey(SJLSCustomTags.PARTICIPATE_ON_OPEN_TAG)) {
			params.setParticipateOnOpen((inner.getPassThruTags().get(SJLSCustomTags.PARTICIPATE_ON_OPEN_TAG).equalsIgnoreCase("Y")));
		}
		if (inner.getPassThruTags().containsKey(SJLSCustomTags.END_TIME_TAG)) {
			try {
				params.setEndTime(DateUtils.parseTradingTime(inner.getPassThruTags().get(SJLSCustomTags.END_TIME_TAG)));
			} catch (ParseException e) {
				log.severe(e.toString());
				throw new RuntimeException(e);
			}
		}
		if (inner.getPassThruTags().containsKey(SJLSCustomTags.START_TIME_TAG)) {
			try {
				params.setStartTime(DateUtils.parseTradingTime(inner.getPassThruTags().get(SJLSCustomTags.START_TIME_TAG)));
			} catch (ParseException e) {
				log.severe(e.toString());
				throw new RuntimeException(e);
			}
		}
		if (inner.getPassThruTags().containsKey(SJLSCustomTags.OPEN_PCT_TAG)) {
			// params.setOpenPercent(Integer.parseInt(inner.getPassThruTags().get(SJLSCustomTags.OPEN_PCT_TAG)));
			params.setOnOpenAmt(new Amount().setPct(Integer.parseInt(inner.getPassThruTags().get(SJLSCustomTags.OPEN_PCT_TAG))));
		}

		if (inner.getPassThruTags().containsKey(SJLSCustomTags.LOWER_PCT_TAG)) {
			params.setLowerPct(Double.parseDouble(inner.getPassThruTags().get(SJLSCustomTags.LOWER_PCT_TAG)));
		}
		if (inner.getPassThruTags().containsKey(SJLSCustomTags.UPPER_PCT_TAG)) {
			params.setUpperPct(Double.parseDouble(inner.getPassThruTags().get(SJLSCustomTags.UPPER_PCT_TAG)));
		}
		return params;
	}

	public String getBorrowLocateString(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.BORROW_LOCATE_TAG);
	}

	public String getDeskID(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.DESKID_TAG);
	}

	public String getPM(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.PM_TAG);
	}

	public String getPMGroup(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.PM_GROUP_TAG);
	}

	public String getPMProduct(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.PM_PRODUCT_TAG);
	}

	public String getPMStrategyCode(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.PM_STRATEGY_CODE_TAG);
	}

	public String getPMSubProduct(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.PM_SUBPRODUCT_TAG);
	}

	public String getPairLinkID(Map<Integer, String> passThruTags) {
		return passThruTags.get(SJLSCustomTags.PAIR_LINK_TAG);
	}

    public String getRouteToBrokerID(Map<Integer, String> passThruTags){
        //final String routeToBrokerID = passThruTags.get(SJLSCustomTags.ROUTE_TO_BROKERID_TAG);
        final String routeToBrokerID = passThruTags.get(OfrAdapter.getRouteToBrokerTag());
        if(routeToBrokerID != null && routeToBrokerID.trim().length() > 0)
        {
            if(OfrAdapter.getRouteToBrokerIgnoreValue() != null &&
                    OfrAdapter.getRouteToBrokerIgnoreValue().trim().length() > 0 &&
                    routeToBrokerID.trim().equalsIgnoreCase(OfrAdapter.getRouteToBrokerIgnoreValue().trim()))
            {
                return null;
            }
            else
            {
                return routeToBrokerID.trim();
            }
        }
        else
        {
            return null;
        }

    }


	public List<String> getRestrictedBrokers(Map<Integer, String> passThruTags) {
		String csvList = passThruTags.get(SJLSCustomTags.RESTRICTED_BROKERS_TAG);
		if (csvList != null) {
			ArrayList<String> x = new ArrayList<String>(10);
			StringTokenizer tokenizer = new StringTokenizer(csvList, ",");
			while (tokenizer.hasMoreTokens()) {
				x.add(tokenizer.nextToken());
			}
			return x;
		}
		return Collections.emptyList();
	}

	public Date getShortSettleDate(Map<Integer, String> passThruTags) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd");
		try {
			String date = passThruTags.get(SJLSCustomTags.SHORT_SETTLE_DATE_TAG);
			if (date != null)
				return formatter.parse(date);
		} catch (ParseException e) {
			log.throwing(null, null, e);
		}
		return null;
	}
}
