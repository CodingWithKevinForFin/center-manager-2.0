package com.sjls.f1.start.ofr;

import com.sjls.algos.eo.common.PrimaryMarket;

public interface IMarketDataManager
{
    public static final String CVS_ID = "$Id: IMarketDataManager.java,v 1.2 2014/02/21 22:12:08 olu Exp $";

    public void subscribe(final String symbol, final String ric, final PrimaryMarket primaryMkt) throws Exception;
    public void addListener(IMarketDataListener listener);
}
