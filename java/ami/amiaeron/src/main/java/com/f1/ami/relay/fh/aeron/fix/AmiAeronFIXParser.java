package com.f1.ami.relay.fh.aeron.fix;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.agrona.DirectBuffer;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.ami.relay.fh.aeron.AmiAeronMessageParser;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.aeron.logbuffer.Header;

public class AmiAeronFIXParser implements AmiAeronMessageParser {
	
	private static final Logger log = LH.get();	
	private static final String PROP_FIX_FIELDS_FILE = "fix.parser.fields.file";
	private static final String PROP_DELIMITER = "fix.parser.delimiter";
	private static final String UNKNOWN_TAG_PREFIX = "TAG";
	
	private Map<Integer, String> fieldsMap;
	private Map<Integer, Map<String, String>> fieldValuesMap;
	private String delimiter;

	public AmiAeronFIXParser() {}
	
	@Override
	public void init(PropertyController props) {
		try {
			this.delimiter = props.getOptional(PROP_DELIMITER, "\u0001");
			
			XmlMapper xmlMapper = new XmlMapper();
			String fieldsFile = props.getRequired(PROP_FIX_FIELDS_FILE);
	    	File xmlFile = new File(fieldsFile);
	    	fields fieldsList = xmlMapper.readValue(xmlFile, fields.class);
	    	this.fieldsMap = new HashMap<Integer, String>();
	    	this.fieldValuesMap = new HashMap<Integer, Map<String, String>>();
	    	for (field _field : fieldsList.field) {
	    		fieldsMap.put(Integer.parseInt(_field.number), _field.name);
	    		if (_field.value.size() > 0) {
	    			Map<String, String> m = new HashMap<String, String>();
	    			for (value _value : _field.value)
	    				m.put(_value._enum, _value.description);
	    			fieldValuesMap.put(Integer.parseInt(_field.number), m);
	    		}
	    	}
		} catch (Exception e) {
			LH.warning(log, "Init exception! " + e.getMessage());
		}
	}
	
	@Override
	public AmiRelayMapToBytesConverter parseMessage(DirectBuffer buffer, int offset, int length, Header header) {
		final String msg = buffer.getStringWithoutLengthAscii(offset, length);
		AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
		//TODO: parse message
		String[] fields = msg.split(this.delimiter);
    	for (String field : fields) {
    		if (SH.isnt(field)) 
    			continue;
    		String[] tagValuePair = field.split("=");
    		String columnName = null;
    		try {
    			columnName = fieldsMap.get(Integer.parseInt(tagValuePair[0]));
    		} catch (Exception e) {
    			LH.warning(log, "Problem with data line: " + msg);
    			LH.warning(log, e);
    		}
    		if (columnName == null)
    			columnName = UNKNOWN_TAG_PREFIX + tagValuePair[0];
    		String value = null;
    		Map<String, String> fieldEnums = fieldValuesMap.get(Integer.parseInt(tagValuePair[0]));
    		try {
    			if (fieldEnums != null)
    				value = fieldEnums.get(tagValuePair[1]);
    			value = value == null ? tagValuePair[1] : value;
    		} catch (Exception e) {
    			//may have empty/null pair values
    		}
    		
    		converter.append(columnName, value);
    	}
    	return converter;
	}
}