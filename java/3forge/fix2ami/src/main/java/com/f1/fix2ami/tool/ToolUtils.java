package com.f1.fix2ami.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.utils.CH;
import com.f1.utils.LH;

import quickfix.BooleanField;
import quickfix.CharField;
import quickfix.DataDictionary;
import quickfix.DataDictionary.GroupInfo;
import quickfix.DoubleField;
import quickfix.FieldConvertError;
import quickfix.FieldType;
import quickfix.Group;
import quickfix.IntField;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.StringField;
import quickfix.UtcDateOnlyField;
import quickfix.UtcTimeOnlyField;
import quickfix.UtcTimeStampField;
import quickfix.field.Country;
import quickfix.field.Currency;
import quickfix.field.converter.UtcDateOnlyConverter;
import quickfix.field.converter.UtcTimeOnlyConverter;
import quickfix.field.converter.UtcTimestampConverter;
import quickfix.fix42.Allocation.NoAllocs;
import quickfix.fix42.Email.LinesOfText;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.ExecutionReport.NoContraBrokers;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelReject;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.OrderCancelReplaceRequest.NoTradingSessions;
import quickfix.fix42.OrderCancelRequest;

public class ToolUtils {
	private static final Logger log = LH.get();

	public static Message getAMsg(final DataDictionary dictionary, final BufferedReader reader, boolean updateTransTime) {
		String msgStr = null;
		Map<Integer, String> msgContent = new LinkedHashMap<>();

		try {
			while ((msgStr = reader.readLine()) != null) {
				boolean noTransTime = false;

				int msgBegin = msgStr.indexOf("8=FIX");
				if (-1 == msgBegin) {
					continue;
				}

				msgStr = msgStr.substring(msgBegin);
				String fields[] = msgStr.split("\u0001");
				String msgType = null;
				for (String field : fields) {
					if (null == field || field.length() <= 1) {
						continue;
					}
					String tagAndValue[] = field.split("=");
					switch (tagAndValue[0]) {
						// header
						case "34":
						case "8":
						case "9":
						case "49":
						case "56":
						case "115":
						case "128":
						case "90":
						case "91":
						case "50":
						case "142":
						case "57":
						case "143":
						case "116":
						case "144":
						case "129":
						case "145":
						case "43":
						case "97":
						case "52":
						case "122":
						case "212":
						case "213":
						case "347":
						case "369":
						case "370":
						case "10": // trailer 
							break;
						case "35":
							switch (tagAndValue[1]) {
								case "C": // for testing unsupport message handling.
									noTransTime = true;

								case "D":
								case "8":
								case "9":
								case "F":
								case "G":
								case "J": // for testing session reject message.
									msgType = tagAndValue[1];
									continue;
								default:
									continue;
							}
						default:
							msgContent.put(Integer.parseInt(tagAndValue[0]), tagAndValue[1]);
							break;
					}
				}

				if (null == msgType) {
					continue;
				}

				if (updateTransTime && !noTransTime) {
					msgContent.put(AbstractFix2AmiProcessor.TAG_TransactTime, UtcTimestampConverter.convert(new Date(), false));
				}
				return buildMessage(dictionary, (Map) CH.m(AbstractFix2AmiProcessor.TAG_MsgType, msgType), msgContent);
			}
		} catch (FileNotFoundException fnf) {
			LH.warning(log, "File Not Found Error: " + fnf);
			//			System.out.println(fnf);
		} catch (IOException io) {
			LH.warning(log, "IO Error: " + io);
			//			System.out.println(io);
		} catch (ParseException pe) {
			LH.warning(log, "Parse Exception Error: " + pe);
			//			System.out.println(pe);
		} catch (FieldConvertError fc) {
			LH.warning(log, "Field Convert Error: " + fc);
			//			System.out.println(fc);
		} catch (Exception e) {
			LH.warning(log, "Error: " + e);
			//			System.out.println(e);
		}
		return null;
	}

	//	static Message buildMessage(Map<Integer, String> header, Map<Integer, String> body) throws ParseException, FieldConvertError {
	//		return buildMessage(DICTIONARY, header, body);
	//	}

	public static Message buildMessage(final DataDictionary dictionary, Map<Integer, String> header, Map<Integer, String> body) throws ParseException, FieldConvertError {
		// TODO need a better way to create a concrete Message type.
		final String msgType = header.get(AbstractFix2AmiProcessor.TAG_MsgType);
		Message message = null;
		switch (msgType) {
			case quickfix.field.MsgType.EXECUTION_REPORT:
				message = new ExecutionReport();
				break;
			case quickfix.field.MsgType.ORDER_SINGLE:
				message = new NewOrderSingle();
				break;
			case quickfix.field.MsgType.ORDER_CANCEL_REJECT:
				message = new OrderCancelReject();
				break;
			case quickfix.field.MsgType.ORDER_CANCEL_REQUEST:
				message = new OrderCancelRequest();
				break;
			case quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST:
				message = new OrderCancelReplaceRequest();
				break;
			default:
				message = new Message();
				break;
		}

		final Header headerMessage = message.getHeader();

		Iterator<Map.Entry<Integer, String>> itr = body.entrySet().iterator();
		Map.Entry<Integer, String> entry = null;
		while (itr.hasNext() || null != entry) {
			if (null == entry) {
				entry = itr.next();
			}

			if (dictionary.isGroup(msgType, entry.getKey())) {
				int groupCountTag = entry.getKey();
				//				int groupCount = Integer.parseInt(entry.getValue());
				//				Group g = new Group(groupCountTag, 1);
				Group g = createGroup(groupCountTag);

				GroupInfo gInfo = dictionary.getGroup(msgType, groupCountTag);

				List<Integer> members = new ArrayList<>();
				for (int i : gInfo.getDataDictionary().getOrderedFields()) {
					members.add(i);
				}

				int previousPosition = -1;
				while (itr.hasNext()) {
					entry = itr.next();
					int currentPosition = members.indexOf(entry.getKey());
					if (-1 == currentPosition) {
						// end of current repeating group
						break;
					}
					if (previousPosition >= currentPosition) {
						// beginning of next occurrence of the same repeating group.
						message.addGroup(g);
						g = new Group(groupCountTag, 1);
					}

					previousPosition = currentPosition;
					setGroupOrMsgField(dictionary, g, null, entry.getKey(), entry.getValue());
					entry = null;
				}
				message.addGroup(g);
			} else {
				setGroupOrMsgField(dictionary, null, message, entry.getKey(), entry.getValue());
				entry = null;
			}
		}

		for (Map.Entry<Integer, String> e : header.entrySet())
			headerMessage.setString(e.getKey(), e.getValue());
		return message;
	}

	// TODO need a better way to create a concrete group.
	private static Group createGroup(int groupCountTag) {
		// NoAllocs 78, NoTradingSessions 386, NoContraBrokers 382
		// LinesOfText 33 (for testing).
		switch (groupCountTag) {
			case 78:
				return new NoAllocs();
			case 386:
				return new NoTradingSessions();
			case 382:
				return new NoContraBrokers();
			case 33:
				return new LinesOfText();
		}
		throw new IllegalArgumentException("Unsuppport group type for tag " + groupCountTag);
	}

	// TODO need a better way to populate Group or Message with proper Field type.
	private static void setGroupOrMsgField(final DataDictionary dictionary, Group g, Message msg, int tag, String value) throws ParseException, FieldConvertError {
		FieldType fieldType = dictionary.getFieldType(tag);
		String fieldTypeName = "STRING";
		if (null != fieldType) {
			fieldTypeName = fieldType.name();
		}

		try {
			switch (fieldTypeName) {
				case "AMT":
				case "PRICE":
				case "QTY":
				case "FLOAT":
				case "PRICEOFFSET":
				case "PERCENTAGE":
					if (null != g) {
						g.setField(new DoubleField(tag, Double.parseDouble(value)));
					}
					if (null != msg) {
						msg.setField(new DoubleField(tag, Double.parseDouble(value)));
					}
					break;
				case "INT":
				case "DAYOFMONTH":
				case "NUMINGROUP":
				case "SEQNUM":
				case "LENGTH":
					if (null != g) {
						g.setField(new IntField(tag, Integer.parseInt(value)));
					}
					if (null != msg) {
						msg.setField(new IntField(tag, Integer.parseInt(value)));
					}
					break;
				case "UNKNOWN":
				case "STRING":
				case "EXCHANGE":
				case "MONTHYEAR": // YYYYMM
				case "MULTIPLEVALUESTRING": // space delimited multiple strings
				case "TIME":
					if (null != g) {
						g.setField(new StringField(tag, value));
					}
					if (null != msg) {
						msg.setField(new StringField(tag, value));
					}
					break;
				case "CHAR":
					if (null != g) {
						g.setField(new CharField(tag, value.charAt(0)));
					}
					if (null != msg) {
						msg.setField(new CharField(tag, value.charAt(0)));
					}
					break;
				case "CURRENCY":
					Currency currency = new Currency();
					currency.setTag(tag);
					currency.setValue(value);
					if (null != g) {
						g.setField(currency);
					}
					if (null != msg) {
						msg.setField(currency);
					}
					break;
				case "BOOLEAN":
					if (null != g) {
						g.setField(new BooleanField(tag, Boolean.parseBoolean(value)));
					}
					if (null != msg) {
						msg.setField(new BooleanField(tag, Boolean.parseBoolean(value)));
					}
					break;
				case "UTCDATEONLY":
				case "UTCDATE":
					UtcDateOnlyField utcDateOnlyField = new UtcDateOnlyField(tag);
					utcDateOnlyField.setValue(UtcDateOnlyConverter.convertToLocalDate(value));
					if (null != g) {
						g.setField(utcDateOnlyField);
					}
					if (null != msg) {
						msg.setField(utcDateOnlyField);
					}
					break;
				case "UTCTIMEONLY":
					UtcTimeOnlyField utcTimeOnlyField = new UtcTimeOnlyField(tag);
					utcTimeOnlyField.setValue(UtcTimeOnlyConverter.convertToLocalTime(value));

					if (null != g) {
						g.setField(utcTimeOnlyField);
					}
					if (null != msg) {
						msg.setField(utcTimeOnlyField);
					}
					break;
				case "LOCALMKTDATE":
				case "UTCTIMESTAMP":
					UtcTimeStampField utcTimeStampField = new UtcTimeStampField(tag);
					utcTimeStampField.setValue(UtcTimestampConverter.convertToLocalDateTime(value));

					if (null != g) {
						g.setField(utcTimeStampField);
					}
					if (null != msg) {
						msg.setField(utcTimeStampField);
					}
					break;
				case "COUNTRY":
					if (null != g) {
						g.setField(new Country(value));
					}
					if (null != msg) {
						msg.setField(new Country(value));
					}
					break;
				case "DATA":
				default:
					throw new IllegalArgumentException("Unsuppport field type for tag " + tag);
			}
		} catch (FieldConvertError fc) {
			LH.warning(log, "Field Convert Error: " + fc);
			//			System.out.println(fc);
			if (null != g) {
				g.setField(new StringField(tag, value));
			}
			if (null != msg) {
				msg.setField(new StringField(tag, value));
			}
		}
	}

	public static void pause(int intervalInMillis) {
		try {
			Thread.sleep(intervalInMillis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<Integer, String> createMap(Object... items) {
		Map<Integer, String> result = new LinkedHashMap<>();
		for (int i = 0; i < items.length; i = i + 2) {
			result.put((Integer) items[i], (String) items[i + 1]);
		}
		return result;
	}

}
