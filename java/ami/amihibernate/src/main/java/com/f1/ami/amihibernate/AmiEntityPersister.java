package com.f1.ami.amihibernate;

import java.util.function.Consumer;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metamodel.spi.RuntimeModelCreationContext;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
import org.hibernate.sql.ast.spi.SqlAstCreationState;
import org.hibernate.sql.ast.tree.from.TableGroup;
import org.hibernate.sql.ast.tree.predicate.Predicate;

public class AmiEntityPersister extends SingleTableEntityPersister {

	public AmiEntityPersister(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy, NaturalIdDataAccess naturalIdRegionAccessStrategy,
			RuntimeModelCreationContext creationContext) throws HibernateException {
		super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
	}
	@Override
	public String generateDeleteString(int j) {
		// TODO Auto-generated method stub
		return super.generateDeleteString(j);
	}
	@Override
	public void delete(Object id, Object version, int j, Object object, String sql, SharedSessionContractImplementor session, Object[] loadedState) throws HibernateException {
		// TODO Auto-generated method stub
		super.delete(id, version, j, object, sql, session, loadedState);
	}
	public void delete(Object id, Object version, Object object, SharedSessionContractImplementor session) throws HibernateException {
		// TODO Auto-generated method stub
		super.delete(id, version, object, session);
	};
	@Override
	protected Update createUpdate() {
		return new AmiUpdate(getFactory().getJdbcServices().getDialect());
	}
	@Override
	protected Delete createDelete() {
		return new AmiDelete();
	}
	@Override
	protected String[] generateSQLDeleteStrings(Object[] loadedState) {
		// TODO Auto-generated method stub
		return super.generateSQLDeleteStrings(loadedState);
	}
	@Override
	public String[] getSQLDeleteStrings() {
		// TODO Auto-generated method stub
		return super.getSQLDeleteStrings();
	}
	@Override
	public void applyWhereRestrictions(Consumer<Predicate> predicateConsumer, TableGroup tableGroup, boolean useQualifier, SqlAstCreationState creationState) {
		// TODO Auto-generated method stub
		super.applyWhereRestrictions(predicateConsumer, tableGroup, useQualifier, creationState);
	}

}
