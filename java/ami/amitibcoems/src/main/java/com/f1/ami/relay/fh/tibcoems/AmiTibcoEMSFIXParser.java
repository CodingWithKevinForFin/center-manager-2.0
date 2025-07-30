package com.f1.ami.relay.fh.tibcoems;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Long;

public class AmiTibcoEMSFIXParser implements AmiTibcoEMSMessageParser {

	private static final Logger log = LH.get();
	private static final String PROP_COLUMN_DELIMITER = "fix.column.delimiter";
	private static final String PROP_KEYVAL_DELIMITER = "fix.keyval.delimiter";
	private static final String PROP_FIX_DICTIONARY = "fix.dictionary";
	private static final String PROP_FIX_FIELD_XPATH_EXP = "fix.dictionary.field.xpath.expression";
	private static final String PROP_FIX_FIELD_KEYVAL_MAPPING="fix.dictionary.field.keyval.mapping";
	private static final String PROP_FIX_FIELD_ATTRIBUTES="fix.dictionary.field.attributes";
	private static final String PROP_FIX_ENUM_XPATH_EXP = "fix.dictionary.enum.xpath.expression";
	private static final String PROP_FIX_ENUM_KEYVAL_MAPPING = "fix.dictionary.enum.keyval.mapping";
	private static final String PROP_FIX_ENUM_ATTRIBUTES="fix.dictionary.enum.attributes";
	
	private static final String UNKNOWN_TAG_PREFIX = "TAG";
	
	//TODO change to get optional to do default fix xml and default delimiters etc
	
	private String colDelimiter = null;
	private String keyValDelimiter = null;
	private Boolean useEnum = false;
	
	private Map<Integer, String> fieldsMap = null;
	private Map<Integer, Map<String, String>> fieldEnumsMap = null;

	AmiTibcoEMSFIXParser() {}	

	@Override
	public void init(PropertyController props) {
		colDelimiter = props.getOptional(PROP_COLUMN_DELIMITER, "\u0001");
		keyValDelimiter = props.getOptional(PROP_KEYVAL_DELIMITER, "=");
		
		fieldsMap = new HashMap<Integer, String>();
		String fieldsFile = props.getRequired(PROP_FIX_DICTIONARY);
		File xmlFile = new File(fieldsFile);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			Document xmlDocument;
			xmlDocument = builder.parse(xmlFile);
			XPath xPath = XPathFactory.newInstance().newXPath();
			String fieldsExpr = props.getOptional(PROP_FIX_FIELD_XPATH_EXP, "fix/fields/field");
			String fieldKeyValMappingStr = props.getOptional(PROP_FIX_FIELD_KEYVAL_MAPPING, "number=name");
			Boolean fieldsAttribute = Caster_Boolean.INSTANCE.cast(props.getOptional(PROP_FIX_FIELD_ATTRIBUTES, "true"));
			String[] fieldMapping = fieldKeyValMappingStr.split("=");
			if (fieldMapping.length != 2)
				throw new RuntimeException("Invalid field mapping("+fieldKeyValMappingStr+")! Valid syntax is key=val");
			
			// FIELDS
			NodeList fieldsList = null;
			fieldsList = (NodeList) xPath.compile(fieldsExpr).evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < fieldsList.getLength(); ++i) {
				Node node = fieldsList.item(i);
				
				if (fieldsAttribute) { // ATTRIBUTE
					if (!node.hasAttributes())
						continue;
					NamedNodeMap n = node.getAttributes();
					Integer fieldKey = Integer.parseInt(n.getNamedItem(fieldMapping[0]).getNodeValue().trim());
					String fieldVal = n.getNamedItem(fieldMapping[1]).getNodeValue().trim();
					fieldsMap.put(fieldKey, fieldVal);
				}
				else { // NON_ATTRIBUTE
					if (node instanceof Element) {
						Element ele = (Element)node;
						Integer fieldKey = Integer.parseInt(ele.getElementsByTagName(fieldMapping[0]).item(0).getTextContent().trim());
						String fieldVal = ele.getElementsByTagName(fieldMapping[1]).item(0).getTextContent().trim();
						fieldsMap.put(fieldKey, fieldVal);
					}
				}
			}
			
			//ENUMS			
			String enumExpr = props.getOptional(PROP_FIX_ENUM_XPATH_EXP, "fix/fields/field/value");
			
			if (SH.is(enumExpr)) {
				// to check if enum is child of field node
				int levelDiff = countLevels(enumExpr, fieldsExpr, '/');
				if (levelDiff == -1)
					throw new RuntimeException("Enum expression \"" + enumExpr + 
							"\" needs to be a child level of field \"" + fieldsExpr + "\"");
				
				useEnum = true;
				String enumMappingStr = props.getOptional(PROP_FIX_ENUM_KEYVAL_MAPPING, "enum=description");
				Boolean enumAttribute = Caster_Boolean.INSTANCE.cast(props.getOptional(PROP_FIX_ENUM_ATTRIBUTES, "true"));
				fieldEnumsMap = new HashMap<Integer, Map<String, String>>();
				
				String[] enumMapping = enumMappingStr.split("=");
				if (enumAttribute && enumMapping.length != 2)
					throw new RuntimeException("Invalid field mapping("+enumMappingStr+")! Valid syntax is key=val");
				NodeList enumList;
				enumList = (NodeList) xPath.compile(enumExpr).evaluate(xmlDocument, XPathConstants.NODESET);
				Node firstNode = getFieldNode(enumList.item(0), levelDiff);
				Integer fieldKey = fieldsAttribute ? Integer.parseInt(firstNode.getAttributes().
						getNamedItem(fieldMapping[0]).getNodeValue().trim()) : Integer.parseInt(((Element)firstNode)
								.getElementsByTagName(fieldMapping[0]).item(0).getTextContent().trim());
				
				Map<String, String> enumMap = new HashMap<String, String>();
				for (int i = 0; i < enumList.getLength(); ++i) {
					Node node = enumList.item(i);
					Node parentNode = getFieldNode(enumList.item(i), levelDiff);
					Integer currFieldKey = fieldsAttribute ? Integer.parseInt(parentNode.getAttributes().
							getNamedItem(fieldMapping[0]).getNodeValue().trim()) : Integer.parseInt(((Element)parentNode)
									.getElementsByTagName(fieldMapping[0]).item(0).getTextContent().trim());
					if (!fieldKey.equals(currFieldKey)) {
						// End of current field - to insert into map
						fieldEnumsMap.put(fieldKey, enumMap);
						fieldKey = currFieldKey;
						enumMap = new HashMap<String, String>();
					}
					
					if (enumAttribute) { // ATTRIBUTE
						if (!node.hasAttributes())
							continue;
						NamedNodeMap n = node.getAttributes();
						String enumKey = n.getNamedItem(enumMapping[0]).getNodeValue().trim();
						String enumValue = n.getNamedItem(enumMapping[1]).getNodeValue().trim();
						enumMap.put(enumKey, enumValue);
					}
					else { // NON-ATTRIBUTE
						NodeList enums = node.getChildNodes();
						for (int j = 0; j < enums.getLength(); ++j) {
							Node _enum = enums.item(j);
							if (_enum instanceof Element) {
								String enumKey = _enum.getNodeName().trim();
								String enumValue = _enum.getTextContent().trim();
								enumMap.put(enumKey, enumValue);
							}
						}
					}
				}
				fieldEnumsMap.put(fieldKey, enumMap);
			}
			
			for (Entry<Integer, String> entry : fieldsMap.entrySet()) {
				LH.info(log, "!!!For " + entry.getKey() + " : " + entry.getValue());
				Map<String, String> enumMap = fieldEnumsMap.get(entry.getKey());
				if (enumMap != null) {
					for (Entry<String, String> e : enumMap.entrySet()) {
						LH.info(log, e.getKey() + " : " + e.getValue());
					}
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Error with xml parser, check your xpath expression(s): " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error with xml parser, check your xpath expression(s): " + e.getMessage());
		}
	}
	
	@Override
	public void parseMessage(AmiRelayMapToBytesConverter converter, Message msg) {
		TextMessage textMsg = (TextMessage)msg;
		String message;
		try {
			message = textMsg.getText();		
			String[] cols = SH.split(this.colDelimiter, message);
			for (String col : cols) {
				String[] keyVal = SH.split(this.keyValDelimiter, col);
				if (keyVal.length != 2) {
					LH.warning(log, "Invalid col input at: " + col);
					continue;
				}
				String key = fieldsMap.get(Integer.parseInt(keyVal[0].trim()));
				if (key == null)
					key = UNKNOWN_TAG_PREFIX + keyVal[0].trim();
				
				String val = keyVal[1].trim();
				if (useEnum) {
					Map<String, String> fieldEnums = fieldEnumsMap.get(Integer.parseInt(keyVal[0].trim()));
					if (fieldEnums != null)
						val = fieldEnums.get(val);
				}
				
				if (SH.isDouble(val)) { // instance of Number
					if (SH.isWholeNumber(val))
						converter.appendLong(keyVal[0].trim(), Caster_Long.INSTANCE.cast(val));
					else
						converter.appendDouble(keyVal[0].trim(), Caster_Double.INSTANCE.cast(val));
				}
				else
					converter.appendString(key, val);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Tibco EMS: error with parsing plain text message: " + e.getMessage());
		}	
	}
	
	private int countLevels(String child, String parent, char delimiter) {
		if (child.startsWith(parent)) {
			child = SH.stripSuffix(child, String.valueOf(delimiter), false);
			parent = SH.stripSuffix(parent, String.valueOf(delimiter), false);
			String levelsStr = SH.afterFirst(child, parent);
			return SH.count(delimiter, levelsStr);
		}
		return -1;
	}
	
	private Node getFieldNode (Node childNode, int levelDiff) {
		Node parentNode = childNode;
		for (int level = 0; level < levelDiff; ++level)
			parentNode = parentNode.getParentNode();
		return parentNode;
	}
}