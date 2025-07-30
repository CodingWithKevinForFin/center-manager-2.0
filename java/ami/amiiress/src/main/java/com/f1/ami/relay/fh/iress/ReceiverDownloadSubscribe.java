package com.f1.ami.relay.fh.iress;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import com.f1.ami.relay.fh.iress.AmiIressFH.Handler;
import com.f1.utils.LH;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.EnumTypeDeclaration;
import com.feedos.api.requests.InstrumentCharacteristics;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.ListOfTagValue;
import com.feedos.api.requests.MarketBranchId;
import com.feedos.api.requests.QuotationContentMask;
import com.feedos.api.requests.Receiver_Referential_DownloadAndSubscribe;
import com.feedos.api.requests.RequestSender;
import com.feedos.api.requests.TagDeclaration;
import com.feedos.api.requests.TradeConditionsDictionaryEntry;
import com.feedos.api.requests.VariableIncrementPriceBandTable;

public class ReceiverDownloadSubscribe implements Receiver_Referential_DownloadAndSubscribe {
	PolymorphicInstrumentCode[] instrumentsLevel1;
	PolymorphicInstrumentCode[] instrumentsMBL;
	int MBLDepth;

	private static final Logger Log = LH.get();
	private Handler messagehandler;
	private RequestSender async_requester;
	private boolean enableLevel1;
	private boolean enableMBL;

	HashSet<Integer> trackedTags = new HashSet<Integer>();
	HashMap<Integer, InstrumentQuotationData> trackedData = new HashMap<>();
	HashMap<Integer, ListOfTagValue> referentialData = new HashMap<>();

	public ReceiverDownloadSubscribe(PolymorphicInstrumentCode[] instrumentsLevel1, PolymorphicInstrumentCode[] instrumentsMBL, int MBLDepth, Handler messageHandler,
			HashSet<Integer> trackedTags, RequestSender async_requester, boolean enableLevel1, boolean enableMBL) {
		this.instrumentsLevel1 = instrumentsLevel1;
		this.instrumentsMBL = instrumentsMBL;
		this.messagehandler = messageHandler;
		this.trackedTags = trackedTags;
		this.async_requester = async_requester;
		this.MBLDepth = MBLDepth;
		this.enableLevel1 = enableLevel1;
		this.enableMBL = enableMBL;
	}

	@Override
	public void refDownloadBranchBegin(MarketBranchId arg0, int arg1, int arg2, int arg3, int arg4) {
		LH.info(Log, "Starting to downloaded ref data.");
	}

	@Override
	public void refDownloadInstrumentsCreated(InstrumentCharacteristics[] arg0) {
		for (InstrumentCharacteristics c : arg0) {
			int code = c.getInternal_instrument_code();
			referentialData.put(code, c.getRef_values());
		}
	}

	@Override
	public void refDownloadInstrumentsModified(InstrumentCharacteristics[] arg0) {
		for (InstrumentCharacteristics c : arg0) {
			int code = c.getInternal_instrument_code();
			referentialData.put(code, c.getRef_values());
		}
	}

	@Override
	public void refDownloadInstrumentsDeleted(PolymorphicInstrumentCode[] arg0) {
		LH.info(Log, "Ref data instruments deleted");
	}

	@Override
	public void refDownloadAndSubscribeFailed(int arg0, Object arg1, int arg2) {
		LH.info(Log, "Failed to download ref data");
	}

	@Override
	public void refDownloadAndSubscribeMarkerTimestamp(long arg0) {

	}

	@Override
	public void refDownloadAndSubscribeMetaData(TagDeclaration[] arg0, EnumTypeDeclaration[] arg1, String[] arg2) {

	}

	@Override
	public void refDownloadAndSubscribeRealtimeBegin() {
		LH.info(Log, "Downloaded ref data. Starting to subscribe");
		if (enableLevel1) {
			ReceiverL1 receiverLevel1 = new ReceiverL1(instrumentsLevel1, messagehandler, trackedTags, referentialData);
			async_requester.asyncQuotSubscribeInstrumentsL1_start(receiverLevel1, "L1 Subscription", instrumentsLevel1, null, new QuotationContentMask(true));
		}

		if (enableMBL) {
			ReceiverMBL receiverMBL = new ReceiverMBL(messagehandler, referentialData, MBLDepth);
			async_requester.asyncQuotSubscribeInstrumentsMBL_start(receiverMBL, "MBL Subacription", instrumentsMBL, new int[MBLDepth]);
		}
	}

	@Override
	public void refDownloadAndSubscribeResponse(int arg0, Object arg1, int arg2) {

	}

	@Override
	public void refDownloadAndSubscribeTradeConditionDictionary(TradeConditionsDictionaryEntry[] arg0) {

	}

	@Override
	public void refDownloadAndSubscribeUnsubNotif(int arg0, Object arg1, int arg2) {

	}

	@Override
	public void refDownloadAndSubscribeVariableIncrementPriceBandTable(VariableIncrementPriceBandTable[] arg0) {

	}

}