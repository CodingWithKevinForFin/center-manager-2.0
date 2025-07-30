package com.f1.utils.converter.json2;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class JsonTests {

	@Test
	public void test1() {
		ObjectToJsonConverter c = new ObjectToJsonConverter();
		StringBuilder stream = new StringBuilder();
		ToJsonConverterSession out = new BasicToJsonConverterSession(c, stream);
		Map m = new HashMap<String, Object>();
		m.put("id", 13);
		m.put("width", 250);
		m.put("height", 110);
		m.put("data", SH.repeat('a', 5));
		m.put("test", new Tuple2("this", 123));

		String text = c.objectToString(m);
		Object obj = c.stringToObject(text);
		String text2 = c.objectToString(obj);
		System.out.println(text);
		System.out.println(obj);
		System.out.println(text2);
		assertEquals(text, text2);
	}

	@Test
	public void test2() {
		ObjectToJsonConverter c = new ObjectToJsonConverter();
		String key = "\n" + "{\n" + "   \"results\" : [\n" + "      {\n" + "         \"address_components\" : [\n" + "            {\n" + "               \"long_name\" : \"50\",\n"
				+ "               \"short_name\" : \"50\",\n" + "               \"types\" : [ \"street_number\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"85th Street Transverse\",\n" + "               \"short_name\" : \"85th Street Transverse\",\n"
				+ "               \"types\" : [ \"route\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"Central Park\",\n"
				+ "               \"short_name\" : \"Central Park\",\n" + "               \"types\" : [ \"neighborhood\", \"political\" ]\n" + "            },\n"
				+ "            {\n" + "               \"long_name\" : \"Manhattan\",\n" + "               \"short_name\" : \"Manhattan\",\n"
				+ "               \"types\" : [ \"sublocality_level_1\", \"sublocality\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"New York\",\n"
				+ "               \"types\" : [ \"locality\", \"political\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"New York County\",\n"
				+ "               \"short_name\" : \"New York County\",\n" + "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" + "            },\n"
				+ "            {\n" + "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"NY\",\n"
				+ "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"10128\",\n"
				+ "               \"short_name\" : \"10128\",\n" + "               \"types\" : [ \"postal_code\" ]\n" + "            }\n" + "         ],\n"
				+ "         \"formatted_address\" : \"50 85th Street Transverse, New York, NY 10128, USA\",\n" + "         \"geometry\" : {\n" + "            \"location\" : {\n"
				+ "               \"lat\" : 40.7837665,\n" + "               \"lng\" : -73.96511750000001\n" + "            },\n" + "            \"location_type\" : \"ROOFTOP\",\n"
				+ "            \"viewport\" : {\n" + "               \"northeast\" : {\n" + "                  \"lat\" : 40.78511548029149,\n"
				+ "                  \"lng\" : -73.96376851970851\n" + "               },\n" + "               \"southwest\" : {\n"
				+ "                  \"lat\" : 40.7824175197085,\n" + "                  \"lng\" : -73.96646648029152\n" + "               }\n" + "            }\n"
				+ "         },\n" + "         \"types\" : [ \"street_address\" ]\n" + "      },\n" + "      {\n" + "         \"address_components\" : [\n" + "            {\n"
				+ "               \"long_name\" : \"10024\",\n" + "               \"short_name\" : \"10024\",\n" + "               \"types\" : [ \"postal_code\" ]\n"
				+ "            },\n" + "            {\n" + "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"New York\",\n"
				+ "               \"types\" : [ \"locality\", \"political\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"New York\",\n"
				+ "               \"short_name\" : \"NY\",\n" + "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "            },\n"
				+ "            {\n" + "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            }\n" + "         ],\n"
				+ "         \"formatted_address\" : \"New York, NY 10024, USA\",\n" + "         \"geometry\" : {\n" + "            \"bounds\" : {\n"
				+ "               \"northeast\" : {\n" + "                  \"lat\" : 40.798198,\n" + "                  \"lng\" : -73.962856\n" + "               },\n"
				+ "               \"southwest\" : {\n" + "                  \"lat\" : 40.773376,\n" + "                  \"lng\" : -73.99570489999999\n" + "               }\n"
				+ "            },\n" + "            \"location\" : {\n" + "               \"lat\" : 40.7859464,\n" + "               \"lng\" : -73.97418739999999\n"
				+ "            },\n" + "            \"location_type\" : \"APPROXIMATE\",\n" + "            \"viewport\" : {\n" + "               \"northeast\" : {\n"
				+ "                  \"lat\" : 40.798198,\n" + "                  \"lng\" : -73.962856\n" + "               },\n" + "               \"southwest\" : {\n"
				+ "                  \"lat\" : 40.773376,\n" + "                  \"lng\" : -73.99570489999999\n" + "               }\n" + "            }\n" + "         },\n"
				+ "         \"types\" : [ \"postal_code\" ]\n" + "      },\n" + "      {\n" + "         \"address_components\" : [\n" + "            {\n"
				+ "               \"long_name\" : \"Central Park\",\n" + "               \"short_name\" : \"Central Park\",\n"
				+ "               \"types\" : [ \"neighborhood\", \"political\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"Manhattan\",\n"
				+ "               \"short_name\" : \"Manhattan\",\n" + "               \"types\" : [ \"sublocality_level_1\", \"sublocality\", \"political\" ]\n"
				+ "            },\n" + "            {\n" + "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"New York\",\n"
				+ "               \"types\" : [ \"locality\", \"political\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"New York County\",\n"
				+ "               \"short_name\" : \"New York County\",\n" + "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" + "            },\n"
				+ "            {\n" + "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"NY\",\n"
				+ "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            }\n" + "         ],\n"
				+ "         \"formatted_address\" : \"Central Park, New York, NY, USA\",\n" + "         \"geometry\" : {\n" + "            \"bounds\" : {\n"
				+ "               \"northeast\" : {\n" + "                  \"lat\" : 40.800704,\n" + "                  \"lng\" : -73.94928080000001\n" + "               },\n"
				+ "               \"southwest\" : {\n" + "                  \"lat\" : 40.764389,\n" + "                  \"lng\" : -73.98163409999999\n" + "               }\n"
				+ "            },\n" + "            \"location\" : {\n" + "               \"lat\" : 40.7711329,\n" + "               \"lng\" : -73.97418739999999\n"
				+ "            },\n" + "            \"location_type\" : \"APPROXIMATE\",\n" + "            \"viewport\" : {\n" + "               \"northeast\" : {\n"
				+ "                  \"lat\" : 40.800704,\n" + "                  \"lng\" : -73.94928080000001\n" + "               },\n" + "               \"southwest\" : {\n"
				+ "                  \"lat\" : 40.764389,\n" + "                  \"lng\" : -73.98163409999999\n" + "               }\n" + "            }\n" + "         },\n"
				+ "         \"types\" : [ \"neighborhood\", \"political\" ]\n" + "      },\n" + "      {\n" + "         \"address_components\" : [\n" + "            {\n"
				+ "               \"long_name\" : \"Manhattan\",\n" + "               \"short_name\" : \"Manhattan\",\n"
				+ "               \"types\" : [ \"sublocality_level_1\", \"sublocality\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"New York\",\n"
				+ "               \"types\" : [ \"locality\", \"political\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"New York County\",\n"
				+ "               \"short_name\" : \"New York County\",\n" + "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" + "            },\n"
				+ "            {\n" + "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"NY\",\n"
				+ "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            }\n" + "         ],\n"
				+ "         \"formatted_address\" : \"Manhattan, New York, NY, USA\",\n" + "         \"geometry\" : {\n" + "            \"bounds\" : {\n"
				+ "               \"northeast\" : {\n" + "                  \"lat\" : 40.882214,\n" + "                  \"lng\" : -73.907\n" + "               },\n"
				+ "               \"southwest\" : {\n" + "                  \"lat\" : 40.6795479,\n" + "                  \"lng\" : -74.047285\n" + "               }\n"
				+ "            },\n" + "            \"location\" : {\n" + "               \"lat\" : 40.790278,\n" + "               \"lng\" : -73.959722\n" + "            },\n"
				+ "            \"location_type\" : \"APPROXIMATE\",\n" + "            \"viewport\" : {\n" + "               \"northeast\" : {\n"
				+ "                  \"lat\" : 40.820045,\n" + "                  \"lng\" : -73.90331300000001\n" + "               },\n" + "               \"southwest\" : {\n"
				+ "                  \"lat\" : 40.698078,\n" + "                  \"lng\" : -74.03514899999999\n" + "               }\n" + "            }\n" + "         },\n"
				+ "         \"types\" : [ \"sublocality_level_1\", \"sublocality\", \"political\" ]\n" + "      },\n" + "      {\n" + "         \"address_components\" : [\n"
				+ "            {\n" + "               \"long_name\" : \"New York County\",\n" + "               \"short_name\" : \"New York County\",\n"
				+ "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"NY\",\n"
				+ "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            }\n" + "         ],\n"
				+ "         \"formatted_address\" : \"New York County, NY, USA\",\n" + "         \"geometry\" : {\n" + "            \"bounds\" : {\n"
				+ "               \"northeast\" : {\n" + "                  \"lat\" : 40.8792779,\n" + "                  \"lng\" : -73.907\n" + "               },\n"
				+ "               \"southwest\" : {\n" + "                  \"lat\" : 40.6795929,\n" + "                  \"lng\" : -74.04726289999999\n" + "               }\n"
				+ "            },\n" + "            \"location\" : {\n" + "               \"lat\" : 40.7830603,\n" + "               \"lng\" : -73.9712488\n" + "            },\n"
				+ "            \"location_type\" : \"APPROXIMATE\",\n" + "            \"viewport\" : {\n" + "               \"northeast\" : {\n"
				+ "                  \"lat\" : 40.8792779,\n" + "                  \"lng\" : -73.907\n" + "               },\n" + "               \"southwest\" : {\n"
				+ "                  \"lat\" : 40.6795929,\n" + "                  \"lng\" : -74.04726289999999\n" + "               }\n" + "            }\n" + "         },\n"
				+ "         \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" + "      },\n" + "      {\n" + "         \"address_components\" : [\n"
				+ "            {\n" + "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"New York\",\n"
				+ "               \"types\" : [ \"locality\", \"political\" ]\n" + "            },\n" + "            {\n" + "               \"long_name\" : \"New York County\",\n"
				+ "               \"short_name\" : \"New York County\",\n" + "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" + "            },\n"
				+ "            {\n" + "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"NY\",\n"
				+ "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            }\n" + "         ],\n" + "         \"formatted_address\" : \"New York, NY, USA\",\n"
				+ "         \"geometry\" : {\n" + "            \"bounds\" : {\n" + "               \"northeast\" : {\n" + "                  \"lat\" : 40.91525559999999,\n"
				+ "                  \"lng\" : -73.70027209999999\n" + "               },\n" + "               \"southwest\" : {\n" + "                  \"lat\" : 40.4959961,\n"
				+ "                  \"lng\" : -74.2590879\n" + "               }\n" + "            },\n" + "            \"location\" : {\n"
				+ "               \"lat\" : 40.7127837,\n" + "               \"lng\" : -74.0059413\n" + "            },\n" + "            \"location_type\" : \"APPROXIMATE\",\n"
				+ "            \"viewport\" : {\n" + "               \"northeast\" : {\n" + "                  \"lat\" : 40.91525559999999,\n"
				+ "                  \"lng\" : -73.70027209999999\n" + "               },\n" + "               \"southwest\" : {\n" + "                  \"lat\" : 40.496006,\n"
				+ "                  \"lng\" : -74.25573489999999\n" + "               }\n" + "            }\n" + "         },\n"
				+ "         \"types\" : [ \"locality\", \"political\" ]\n" + "      },\n" + "      {\n" + "         \"address_components\" : [\n" + "            {\n"
				+ "               \"long_name\" : \"New York\",\n" + "               \"short_name\" : \"NY\",\n"
				+ "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "            },\n" + "            {\n"
				+ "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            }\n" + "         ],\n" + "         \"formatted_address\" : \"New York, USA\",\n"
				+ "         \"geometry\" : {\n" + "            \"bounds\" : {\n" + "               \"northeast\" : {\n" + "                  \"lat\" : 45.015865,\n"
				+ "                  \"lng\" : -71.85626429999999\n" + "               },\n" + "               \"southwest\" : {\n" + "                  \"lat\" : 40.4959961,\n"
				+ "                  \"lng\" : -79.76214379999999\n" + "               }\n" + "            },\n" + "            \"location\" : {\n"
				+ "               \"lat\" : 43.2994285,\n" + "               \"lng\" : -74.21793260000001\n" + "            },\n"
				+ "            \"location_type\" : \"APPROXIMATE\",\n" + "            \"viewport\" : {\n" + "               \"northeast\" : {\n"
				+ "                  \"lat\" : 45.015865,\n" + "                  \"lng\" : -71.85626429999999\n" + "               },\n" + "               \"southwest\" : {\n"
				+ "                  \"lat\" : 40.4960191,\n" + "                  \"lng\" : -79.76214379999999\n" + "               }\n" + "            }\n" + "         },\n"
				+ "         \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" + "      },\n" + "      {\n" + "         \"address_components\" : [\n"
				+ "            {\n" + "               \"long_name\" : \"United States\",\n" + "               \"short_name\" : \"US\",\n"
				+ "               \"types\" : [ \"country\", \"political\" ]\n" + "            }\n" + "         ],\n" + "         \"formatted_address\" : \"United States\",\n"
				+ "         \"geometry\" : {\n" + "            \"bounds\" : {\n" + "               \"northeast\" : {\n" + "                  \"lat\" : 71.389888,\n"
				+ "                  \"lng\" : -66.94976079999999\n" + "               },\n" + "               \"southwest\" : {\n" + "                  \"lat\" : 18.9110642,\n"
				+ "                  \"lng\" : 172.4458955\n" + "               }\n" + "            },\n" + "            \"location\" : {\n"
				+ "               \"lat\" : 37.09024,\n" + "               \"lng\" : -95.712891\n" + "            },\n" + "            \"location_type\" : \"APPROXIMATE\",\n"
				+ "            \"viewport\" : {\n" + "               \"northeast\" : {\n" + "                  \"lat\" : 49.38,\n" + "                  \"lng\" : -66.94\n"
				+ "               },\n" + "               \"southwest\" : {\n" + "                  \"lat\" : 25.82,\n" + "                  \"lng\" : -124.39\n"
				+ "               }\n" + "            }\n" + "         },\n" + "         \"types\" : [ \"country\", \"political\" ]\n" + "      }\n" + "   ],\n"
				+ "   \"status\" : \"OK\"\n" + "}\n" + "";
		System.out.println(c.stringToObject(key));

	}

	@Test
	public void test3() {
		assertSame("test\u00a0this");
		assertSame("test\uFFffthis");
		assertSame("test\u0000this");
		assertSame("test\nthis");
		assertSame("test\\nthis");
	}

	private void assertSame(String s1) {
		ObjectToJsonConverter c = new ObjectToJsonConverter();
		String s2 = c.objectToString(s1);
		String s3 = (String) c.stringToObject(s2);
		System.out.println(s1 + " ==> " + s2 + " ==> " + s3);
		assertEquals(s1, s3);
	}
}
