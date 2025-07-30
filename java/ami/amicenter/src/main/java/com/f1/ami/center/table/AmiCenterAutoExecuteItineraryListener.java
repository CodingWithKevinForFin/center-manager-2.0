package com.f1.ami.center.table;

public interface AmiCenterAutoExecuteItineraryListener {

	void onAutoExecuteError(AmiCenterAutoExecuteItinerary amiCenterAutoExecuteItinerary, String message, Exception exception);
	void onAutoExecuteComplete(AmiCenterAutoExecuteItinerary amiCenterAutoExecuteItinerary);

}
