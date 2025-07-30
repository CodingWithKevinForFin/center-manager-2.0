package com.f1.fix2ami.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import com.f1.ami.client.AmiClient;

import quickfix.BooleanField;
import quickfix.CharField;
import quickfix.DoubleField;
import quickfix.Field;
import quickfix.IntField;
import quickfix.StringField;
import quickfix.UtcDateOnlyField;
import quickfix.UtcTimeOnlyField;
import quickfix.UtcTimeStampField;
import quickfix.UtcTimestampPrecision;
import quickfix.field.converter.UtcDateOnlyConverter;
import quickfix.field.converter.UtcTimeOnlyConverter;

public abstract class AbstractAmiPublishField {
	final String columnName;
	final Field<?> field;

	public AbstractAmiPublishField(final String columnName, final Field<?> field) {
		this.columnName = columnName;
		this.field = field;
	}

	public String getColumnName() {
		return columnName;
	}

	public abstract void publish(final AmiClient amiClient);

	private static void publishStringField(final AmiClient amiClient, final String columnName, final String string) {
		if (null != string) {
			amiClient.addMessageParamString(columnName, string);
		}
	}
	private static void publishLongField(final AmiClient amiClient, final String columnName, final Long value) {
		if (null != value) {
			amiClient.addMessageParamLong(columnName, value);
		}
	}

	public static class IntField2Ami extends AbstractAmiPublishField {
		public IntField2Ami(final String columnName, final IntField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			amiClient.addMessageParamInt(columnName, ((IntField) field).getValue());
		}
	}

	public static class StringField2Ami extends AbstractAmiPublishField {
		public StringField2Ami(final String columnName, final StringField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			publishStringField(amiClient, columnName, ((StringField) field).getValue());
		}
	}

	public static class DoubleField2Ami extends AbstractAmiPublishField {
		public DoubleField2Ami(final String columnName, final DoubleField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			amiClient.addMessageParamDouble(columnName, ((DoubleField) field).getValue());
		}
	}

	public static class CharField2Ami extends AbstractAmiPublishField {
		public CharField2Ami(final String columnName, final CharField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			publishStringField(amiClient, columnName, String.valueOf(((CharField) field).getValue()));
		}
	}

	public static class BooleanField2Ami extends AbstractAmiPublishField {
		public BooleanField2Ami(final String columnName, final BooleanField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			amiClient.addMessageParamBoolean(columnName, ((BooleanField) field).getValue());
		}
	}

	public static class UtcDateOnlyField2Ami extends AbstractAmiPublishField {
		public UtcDateOnlyField2Ami(final String columnName, final UtcDateOnlyField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			publishStringField(amiClient, columnName, UtcDateOnlyConverter.convert(((UtcDateOnlyField) field).getValue()));
			LocalDate value = ((UtcDateOnlyField) field).getValue();
			publishLongField(amiClient, columnName, value.toEpochDay() * 86400000L);
		}
	}

	public static class UtcTimeOnlyField2Ami extends AbstractAmiPublishField {
		public UtcTimeOnlyField2Ami(final String columnName, final UtcTimeOnlyField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			publishStringField(amiClient, columnName, UtcTimeOnlyConverter.convert(((UtcTimeOnlyField) field).getValue(), UtcTimestampPrecision.MILLIS));
			LocalTime value = ((UtcTimeOnlyField) field).getValue();
			publishLongField(amiClient, columnName, value.toNanoOfDay() / 1000000L);
		}
	}

	private static final ZoneId UTC = ZoneId.of("UTC");

	public static class UtcTimeStampField2Ami extends AbstractAmiPublishField {

		public UtcTimeStampField2Ami(final String columnName, final UtcTimeStampField field) {
			super(columnName, field);
		}

		public void publish(final AmiClient amiClient) {
			LocalDateTime value = ((UtcTimeStampField) field).getValue();
			publishLongField(amiClient, columnName, value.atZone(UTC).toInstant().toEpochMilli());
		}
	}
}
