package com.f1.ami.amihibernate;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class AmiStringType extends AbstractSingleColumnStandardBasicType<String> implements DiscriminatorType<String> {
	public static final AmiStringType INSTANCE = new AmiStringType();

	public AmiStringType() {
		super(VarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
	}

	@Override
	public String objectToSQLString(String value, Dialect dialect) throws Exception {
		// TODO Auto-generated method stub
		return '"' + value + '"';
	}

	public String getName() {
		return "string";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

	public String stringToObject(String xml) throws Exception {
		return xml;
	}

	public String toString(String value) {
		return value;
	}

}
