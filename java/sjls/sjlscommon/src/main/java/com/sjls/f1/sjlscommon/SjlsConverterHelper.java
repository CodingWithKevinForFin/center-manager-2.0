package com.sjls.f1.sjlscommon;

import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class SjlsConverterHelper {

	public static void registerConverters(ObjectToByteArrayConverter converter) {
		converter.registerCustomConverter(new AlgoParamsConverter());
		converter.registerCustomConverter(new BinDataConverter());
		converter.registerCustomConverter(new DateTimeConverter());
		converter.registerCustomConverter(new ParentOrderStatusUpdateMsgConverter());
		converter.registerCustomConverter(new TCMEstimateConverter());
		converter.registerCustomConverter(new IStrategyUpdateMsgConverter());
        converter.registerCustomConverter(new AlgoParamsUpdateMsgConverter());
        converter.registerCustomConverter(new AlertMsgConverter());
	}
}
