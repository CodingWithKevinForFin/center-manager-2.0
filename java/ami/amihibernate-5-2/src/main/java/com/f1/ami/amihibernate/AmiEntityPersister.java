package com.f1.ami.amihibernate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.tuple.entity.EntityMetamodel;

public class AmiEntityPersister extends SingleTableEntityPersister implements Loadable {

	public AmiEntityPersister(PersistentClass persistentClass, EntityRegionAccessStrategy cacheAccessStrategy, NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
			PersisterCreationContext creationContext) throws HibernateException {
		super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
	}

	@Override
	protected void createLoaders() {
		// TODO Auto-generated method stub
		//TODO:??? necessary?
		//		if (true)
		//			super.createLoaders();
		final Map loaders = getLoaders();
		loaders.put(LockMode.NONE, createEntityLoader(LockMode.NONE));

		UniqueEntityLoader readLoader = createEntityLoader(LockMode.READ);
		loaders.put(LockMode.READ, readLoader);

		//TODO: inexact, what we really need to know is: are any outer joins used?
		boolean disableForUpdate = getSubclassTableSpan() > 1 && hasSubclasses() && !getFactory().getDialect().supportsOuterJoinForUpdate();

		loaders.put(LockMode.UPGRADE, disableForUpdate ? readLoader : createEntityLoader(LockMode.UPGRADE));
		loaders.put(LockMode.UPGRADE_NOWAIT, disableForUpdate ? readLoader : createEntityLoader(LockMode.UPGRADE_NOWAIT));
		loaders.put(LockMode.UPGRADE_SKIPLOCKED, disableForUpdate ? readLoader : createEntityLoader(LockMode.UPGRADE_SKIPLOCKED));
		loaders.put(LockMode.FORCE, disableForUpdate ? readLoader : createEntityLoader(LockMode.FORCE));
		loaders.put(LockMode.PESSIMISTIC_READ, disableForUpdate ? readLoader : createEntityLoader(LockMode.PESSIMISTIC_READ));
		loaders.put(LockMode.PESSIMISTIC_WRITE, disableForUpdate ? readLoader : createEntityLoader(LockMode.PESSIMISTIC_WRITE));
		loaders.put(LockMode.PESSIMISTIC_FORCE_INCREMENT, disableForUpdate ? readLoader : createEntityLoader(LockMode.PESSIMISTIC_FORCE_INCREMENT));
		loaders.put(LockMode.OPTIMISTIC, createEntityLoader(LockMode.OPTIMISTIC));
		loaders.put(LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT));

		loaders.put("merge", new AmiCascadeEntityLoader(this, CascadingActions.MERGE, getFactory()));
		loaders.put("refresh", new AmiCascadeEntityLoader(this, CascadingActions.REFRESH, getFactory()));
	}
	private String getRootAlias() {
		return StringHelper.generateAlias(getEntityName());
	}

	@Override
	protected String generateSnapshotSelectString() {
		// TODO Auto-generated method stub
		//		return super.generateSnapshotSelectString();
		//TODO: should we use SELECT .. FOR UPDATE?

		AmiSelect select = new AmiSelect(getFactory().getDialect());

		if (getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
			select.setComment("get current state " + getEntityName());
		}

		String[] aliasedIdColumns = StringHelper.qualify(getRootAlias(), getIdentifierColumnNames());
		String selectClause = StringHelper.join(", ", aliasedIdColumns) + concretePropertySelectFragment(getRootAlias(), getPropertyUpdateability());

		String fromClause = fromTableFragment(getRootAlias()) + fromJoinFragment(getRootAlias(), true, false);

		String whereClause = new StringBuilder().append(StringHelper.join("=? and ", aliasedIdColumns)).append("=?").append(whereJoinFragment(getRootAlias(), true, false))
				.toString();

		/*if ( isVersioned() ) {
			where.append(" and ")
				.append( getVersionColumnName() )
				.append("=?");
		}*/

		return select.setSelectClause(selectClause).setFromClause(fromClause).setOuterJoins("", "").setWhereClause(whereClause).toStatementString();
	}
	@Override
	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		// NOTE : Not calling createJoin here is just a performance optimization
		Set<String> empty = Collections.emptySet();
		return getSubclassTableSpan() == 1 ? "" : createJoin(alias, innerJoin, includeSubclasses, empty).toFromFragmentString();
	}
	@Override
	public String fromTableFragment(String name) {
		//		System.out.println(name);
		return getTableName() + " as " + name;
	}

	@Override
	public String generateDeleteString(int j) {
		if (true)
			return super.generateDeleteString(j);
		//TODO:
		final AmiDelete delete = new AmiDelete();
		delete.setTableName(getTableName(j)).addPrimaryKeyColumns(getKeyColumns(j));
		if (j == 0) {
			delete.setVersionColumnName(getVersionColumnName());
		}
		if (getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
			delete.setComment("delete " + getEntityName());
		}
		return delete.toStatementString();
	}

	/**
	 * Generate the SQL that updates a row by id (and version)
	 */
	protected String generateUpdateString(final boolean[] includeProperty, final int j, final Object[] oldFields, final boolean useRowId) {
		if (true)
			return super.generateUpdateString(includeProperty, j, oldFields, useRowId);

		//TODO:
		AmiUpdate update = new AmiUpdate(getFactory().getDialect());
		update.setTableName(getTableName(j));

		// select the correct row by either pk or rowid
		if (useRowId) {
			update.addPrimaryKeyColumns(new String[] { rowIdName }); //TODO: eventually, rowIdName[j]
		} else {
			update.addPrimaryKeyColumns(getKeyColumns(j));
		}

		EntityMetamodel entityMetamodel = this.getEntityMetamodel();
		boolean[][] propertyColumnUpdateable = this.getPropertyColumnUpdateable();
		boolean hasColumns = false;
		for (int i = 0; i < entityMetamodel.getPropertySpan(); i++) {
			if (includeProperty[i] && isPropertyOfTable(i, j)) {
				//			if (includeProperty[i] && isPropertyOfTable(i, j) && !lobProperties.contains(i)) {
				// this is a property of the table, which we are updating
				update.addColumns(getPropertyColumnNames(i), propertyColumnUpdateable[i], getPropertyColumnWriters(i));
				hasColumns = hasColumns || getPropertyColumnSpan(i) > 0;
			}
		}

		// HHH-4635
		// Oracle expects all Lob properties to be last in inserts
		// and updates.  Insert them at the end.
		//		for (int i : lobProperties) {
		//			if (includeProperty[i] && isPropertyOfTable(i, j)) {
		//				// this property belongs on the table and is to be inserted
		//				update.addColumns(getPropertyColumnNames(i), propertyColumnUpdateable[i], getPropertyColumnWriters(i));
		//				hasColumns = true;
		//			}
		//		}

		if (j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION) {
			// this is the root (versioned) table, and we are using version-based
			// optimistic locking;  if we are not updating the version, also don't
			// check it (unless this is a "generated" version column)!
			if (checkVersion(includeProperty)) {
				update.setVersionColumnName(getVersionColumnName());
				hasColumns = true;
			}
		} else if (isAllOrDirtyOptLocking() && oldFields != null) {
			// we are using "all" or "dirty" property-based optimistic locking

			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL ? getPropertyUpdateability()
					//optimistic-lock="all", include all updatable properties
					: includeProperty; //optimistic-lock="dirty", include all properties we are updating this time

			boolean[] versionability = getPropertyVersionability();
			org.hibernate.type.Type[] types = getPropertyTypes();
			for (int i = 0; i < entityMetamodel.getPropertySpan(); i++) {
				boolean include = includeInWhere[i] && isPropertyOfTable(i, j) && versionability[i];
				if (include) {
					// this property belongs to the table, and it is not specifically
					// excluded from optimistic locking by optimistic-lock="false"
					String[] propertyColumnNames = getPropertyColumnNames(i);
					String[] propertyColumnWriters = getPropertyColumnWriters(i);
					boolean[] propertyNullness = types[i].toColumnNullness(oldFields[i], getFactory());
					for (int k = 0; k < propertyNullness.length; k++) {
						if (propertyNullness[k]) {
							update.addWhereColumn(propertyColumnNames[k], "=" + propertyColumnWriters[k]);
						} else {
							update.addWhereColumn(propertyColumnNames[k], " is null");
						}
					}
				}
			}

		}

		if (getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
			update.setComment("update " + getEntityName());
		}

		return hasColumns ? update.toStatementString() : null;
	}
	private boolean checkVersion(final boolean[] includeProperty) {
		EntityMetamodel entityMetamodel = this.getEntityMetamodel();
		return includeProperty[getVersionProperty()] || entityMetamodel.isVersionGenerated();
	}
	private boolean isAllOrDirtyOptLocking() {
		EntityMetamodel entityMetamodel = this.getEntityMetamodel();
		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY || entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
	}
	//	@Override
	//	public void delete(Object id, Object version, int j, Object object, String sql, SharedSessionContractImplementor session, Object[] loadedState) throws HibernateException {
	//		// TODO Auto-generated method stub
	//		super.delete(id, version, j, object, sql, session, loadedState);
	//	}
	//	public void delete(Object id, Object version, Object object, SharedSessionContractImplementor session) throws HibernateException {
	//		// TODO Auto-generated method stub
	//		super.delete(id, version, object, session);
	//	};
	//	@Override
	//	protected Update createUpdate() {
	//		return new AmiUpdate(getFactory().getJdbcServices().getDialect());
	//	}
	//	@Override
	//	protected Delete createDelete() {
	//		return new AmiDelete();
	//	}
	//	@Override
	//	protected String[] generateSQLDeleteStrings(Object[] loadedState) {
	//		// TODO Auto-generated method stub
	//		return super.generateSQLDeleteStrings(loadedState);
	//	}
	//	@Override
	//	public String[] getSQLDeleteStrings() {
	//		// TODO Auto-generated method stub
	//		return super.getSQLDeleteStrings();
	//	}
	//	@Override
	//	public void applyWhereRestrictions(Consumer<Predicate> predicateConsumer, TableGroup tableGroup, boolean useQualifier, SqlAstCreationState creationState) {
	//		// TODO Auto-generated method stub
	//		super.applyWhereRestrictions(predicateConsumer, tableGroup, useQualifier, creationState);
	//	}

}
