package com.f1.ami.amihibernate;

import org.hibernate.dialect.identity.MySQLIdentityColumnSupport;

public class AmiIdentityColumnSupport extends MySQLIdentityColumnSupport {

	@Override
	public String getIdentitySelectString(String table, String column, int type) {
		return "select last(id) from " + table;
	}

	@Override
	public String getIdentityColumnString(int type) {
		//starts with 0, implicitly
		return "NONULL";
	}

	//	@Override
	//	public boolean supportsIdentityColumns() {
	//		// TODO Auto-generated method stub
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean supportsInsertSelectIdentity() {
	//		// TODO Auto-generated method stub
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean hasDataTypeInIdentityColumn() {
	//		// TODO Auto-generated method stub
	//		return false;
	//	}
	//
	//	@Override
	//	public String appendIdentitySelectToInsert(String insertString) {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//
	//	@Override
	//	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//
	//	@Override
	//	public String getIdentityColumnString(int type) throws MappingException {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//
	//	@Override
	//	public String getIdentityInsertString() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//
	//	@Override
	//	public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}

}
