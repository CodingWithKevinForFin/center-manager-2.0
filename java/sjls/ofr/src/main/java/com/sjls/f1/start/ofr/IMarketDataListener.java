package com.sjls.f1.start.ofr;

import com.sjls.algos.eo.common.ITickData;


public interface IMarketDataListener {
    public static String CVS_ID = "$Id: IMarketDataListener.java,v 1.2 2014/02/24 19:54:24 olu Exp $";
    public void onDisconnect(String reason, boolean propagateToOfr);
    public void onMarketData(ITickData[] pair);
    public void onLogin();
    public void onLoginFailure();
    public void onSubscriptionFailure();
}
