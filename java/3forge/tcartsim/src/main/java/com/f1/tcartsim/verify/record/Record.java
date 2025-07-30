package com.f1.tcartsim.verify.record;

import com.f1.tcartsim.verify.format.RecordFormat;
import com.f1.utils.SH;

public abstract class Record {
	private long time;
	private char format;
	private String variants;

	public Record(String[] data) {
		this.setTime(getTime(data));
		this.setFormat(getFormat(data));
	}

	protected static long getTime(String[] data) {
		return SH.parseLong(data[RecordFormat.POSITION_TIME]);
	}

	public static char getFormat(String[] data) {
		return SH.parseChar(data[RecordFormat.POSITION_FORMAT]);
	}

	public static boolean isWithinTimeFrame(String[] data, long start, long end) {
		long time = getTime(data);
		return time < start ? false : time < end ? true : false;
	}
	public long getTime() {
		return this.time;
	}

	public char getFormat() {
		return this.format;
	}

	public String getVariants() {
		return this.variants;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setFormat(char format) {
		this.format = format;
	}

	public void setVariants(String variants) {
		this.variants = variants;
	}

	//	
	//	public boolean passFilter(String key, String filter){
	//		return SH.equals(filter, attributes.get(key));
	//	}
	//	
	//	public boolean passFilterNot(String key, String filter){
	//		return !passFilter(key, filter);
	//	}
	//	
	//	public boolean passFilterEq(String key, String filter, char type){
	//		return VerifyMath.eq(filter, attributes.get(key), type);
	//	}
	//	
	//	public boolean passFilterNeq(String key, String filter, char type){
	//		return !passFilterEq(key, filter, type);
	//	}
	//	
	//	public boolean passFilterGt(String key, String filter, char type){
	//		return VerifyMath.gt(attributes.get(key), filter, type);
	//	}
	//	
	//	public boolean passFilterGte(String key, String filter, char type){
	//		return VerifyMath.gte(attributes.get(key), filter, type);
	//	}
	//	
	//	public boolean passFilterLt(String key, String filter, char type){
	//		return !passFilterGte(key, filter, type);
	//	}
	//	
	//	public boolean passFilterLte(String key, String filter, char type){
	//		return !passFilterGt(key, filter, type);
	//	}
	//	
	//	public boolean passFilterBetween(String key, String filterL, String filterR, char type){
	//		return VerifyMath.between(attributes.get(key), filterL, filterR, type);
	//	}
	//	
	//	public boolean passFilterNBetween(String key, String filterL, String filterR, char type){
	//		return !passFilterBetween(attributes.get(key), filterL, filterR, type);
	//	}
	//	
	//	
	//	public boolean passFilter(HashMap<String, String> filter){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key),"")){
	//				continue;
	//			}
	//			else if(!passFilter(key, filter.get(key))){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterNot(HashMap<String, String> filter){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key),"")){
	//				continue;
	//			}
	//			else if(passFilter(key, filter.get(key))){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterEq(HashMap<String, String> filter, char type){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key),"")){
	//				continue;
	//			}
	//			else if(!passFilterEq(key, filter.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterNeq(HashMap<String, String> filter, char type){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key),"")){
	//				continue;
	//			}
	//			else if(passFilterEq(key, filter.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterGt(HashMap<String, String> filter, char type){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key),"")){
	//				continue;
	//			}
	//			else if(!passFilterGt(key, filter.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterGte(HashMap<String, String> filter, char type){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key),"")){
	//				continue;
	//			}
	//			else if(!passFilterGte(key, filter.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterLt(HashMap<String, String> filter, char type){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key), "")){
	//				continue;
	//			}
	//			else if(passFilterGte(key, filter.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterLte(HashMap<String, String> filter, char type){
	//		boolean pass = true;
	//		for(String key: filter.keySet()){
	//			if(SH.equals(filter.get(key), "")){
	//				continue;
	//			}
	//			else if(passFilterGt(key, filter.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterBetween(HashMap<String, String> filterL, HashMap<String, String> filterR, char type){
	//		assert(filterL.keySet().size() == filterR.keySet().size());
	//		boolean pass = true;
	//		for(String key: filterL.keySet()){
	//			assert(filterR.containsKey(key));
	//			if(SH.equals(filterL.get(key),"") || SH.equals(filterR.get(key), "")){
	//				continue;
	//			}
	//			else if(!passFilterBetween(key, filterL.get(key), filterR.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}
	//	
	//	public boolean passFilterNBetween(HashMap<String, String> filterL, HashMap<String, String> filterR, char type){
	//		assert(filterL.keySet().size() == filterR.keySet().size());
	//		boolean pass = true;
	//		for(String key: filterL.keySet()){
	//			assert(filterR.containsKey(key));
	//			if(SH.equals(filterL.get(key),"") || SH.equals(filterR.get(key), "")){
	//				continue;
	//			}
	//			else if(passFilterBetween(key, filterL.get(key), filterR.get(key), type)){
	//				pass = false;
	//				break;
	//			}
	//		}
	//		return pass;
	//	}

}