package com.f1.ami.relay.fh.hazelcast.portable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;

public class HazelcastProducer {
	//This is a test function for putting custom objects into a Hazelcast map
	public static void main(String[] args) {
		
		try {
			RefPricePortable RefPriceIDSetter = new RefPricePortable();
			FXRatePortable FXRateIDSetter = new FXRatePortable();
			FXForwardPortable FXForwardIDSetter = new FXForwardPortable();
			RefPriceIDSetter.setPortableFactoryID(1002);
			FXRateIDSetter.setPortableFactoryID(1002);
			FXForwardIDSetter.setPortableFactoryID(1002);
			
			RefPriceIDSetter.setPortableClassID(22);
			FXRateIDSetter.setPortableClassID(24);
			FXForwardIDSetter.setPortableClassID(26);
			
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getSerializationConfig().addPortableFactory(1002, new VeritionPortableFactory());			
			HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig);
			
			IMap<Integer, RefPricePortable> refPriceMap = hz.getMap("refPriceMap");
			IMap<Integer, FXRatePortable> fxRateMap = hz.getMap("fxRateMap");
			IMap<Integer, FXForwardPortable> fxForwardMap = hz.getMap("fxForwardMap");
			
			//RefPricePortable test
			refPriceMap.put(1, new RefPricePortable("vert_investment_code1", "pricing_source1", "symbol1", 1L, 1L, 1.1, 1.1, 1.1, 1.1, "ccy1", "eid1", 1));
			refPriceMap.put(2, new RefPricePortable("vert_investment_code2", "pricing_source2", "symbol2", 2L, 2L, 2.2, 2.2, 2.2, 2.2, "ccy2", "eid2", 2));
			refPriceMap.put(3, new RefPricePortable("vert_investment_code3", "pricing_source3", "symbol3", 3L, 3L, 3.3, 3.3, 3.3, 3.3, "ccy3", "eid3", 3));
			refPriceMap.put(4, new RefPricePortable("vert_investment_code4", "pricing_source4", "symbol4", 4L, 4L, 4.4, 4.4, 4.4, 4.4, "ccy4", "eid4", 4));
			
			//FXRatePortable test
			fxRateMap.put(1, new FXRatePortable("base1", "term1", 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1L, 1));
			fxRateMap.put(2, new FXRatePortable("base2", "term2", 2.2, 2.2, 2.2, 2.2, 2.2, 2.2, 2L, 2));
			fxRateMap.put(3, new FXRatePortable("base3", "term3", 3.3, 3.3, 3.3, 3.3, 3.3, 3.3, 3L, 3));
			fxRateMap.put(4, new FXRatePortable("base4", "term4", 4.4, 4.4, 4.4, 4.4, 4.4, 4.4, 4L, 4));
			
			//FXForwardPortable test
			fxForwardMap.put(1, new FXForwardPortable("symbol1", "source1", 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1L, 1));
			fxForwardMap.put(2, new FXForwardPortable("symbol2", "source2", 2.2, 2.2, 2.2, 2.2, 2.2, 2.2, 2L, 2));
			fxForwardMap.put(3, new FXForwardPortable("symbol3", "source3", 3.3, 3.3, 3.3, 3.3, 3.3, 3.3, 3L, 3));
			fxForwardMap.put(4, new FXForwardPortable("symbol4", "source4", 4.4, 4.4, 4.4, 4.4, 4.4, 4.4, 4L, 4));
			
			hz.shutdown();
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
    }
}