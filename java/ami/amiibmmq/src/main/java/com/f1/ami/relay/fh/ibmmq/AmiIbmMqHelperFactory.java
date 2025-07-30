package com.f1.ami.relay.fh.ibmmq;

public class AmiIbmMqHelperFactory {

	//mapping helper classes to deserializer classes
	public AmiIbmMqHelper getIBMHelper(String format) {
		switch (format) {
			case "JSON":
				return new AmiIbmMqHelperJson();
			case "XML":
				return new AmiIbmMqHelperXml();
			case "FIX":
				return new AmiIbmMqHelperFix();
			default:
				return null;
		}
	}
}
