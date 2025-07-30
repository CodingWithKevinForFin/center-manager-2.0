package com.f1.ami.amihibernate;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.JoinWalker;
import org.hibernate.loader.entity.AbstractEntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class AmiCascadeEntityLoader extends AbstractEntityLoader {

	public AmiCascadeEntityLoader(OuterJoinLoadable persister, CascadingAction action, SessionFactoryImplementor factory) throws MappingException {
		//		super(persister, action, factory);

		super(persister, persister.getIdentifierType(), factory, LoadQueryInfluencers.NONE);

		JoinWalker walker = new AmiCascadeEntityJoinWalker(persister, action, factory);
		initFromWalker(walker);

		postInstantiate();

		if (LOG.isDebugEnabled()) {
			LOG.debugf("Static select for action %s on entity %s: %s", action, entityName, getSQLString());
		}
	}
	//	private void initStatementString(final String condition, final String orderBy, final LockOptions lockOptions) throws MappingException {
	//		initStatementString(null, condition, orderBy, "", lockOptions);
	//	}
	//
	//	private void initStatementString(final String projection, final String condition, final String orderBy, final String groupBy, final LockOptions lockOptions)
	//			throws MappingException {
	//
	//		this.g
	//		final int joins = countEntityPersisters(associations);
	//		suffixes = BasicLoader.generateSuffixes(joins + 1);
	//
	//		JoinFragment ojf = mergeOuterJoins(associations);
	//
	//		Select select = new Select(getDialect()).setLockOptions(lockOptions)
	//				.setSelectClause(projection == null ? persister.selectFragment(alias, suffixes[joins]) + selectString(associations) : projection)
	//				.setFromClause(getDialect().appendLockHint(lockOptions, persister.fromTableFragment(alias)) + persister.fromJoinFragment(alias, true, true))
	//				.setWhereClause(condition).setOuterJoins(ojf.toFromFragmentString(), ojf.toWhereFragmentString() + getWhereFragment())
	//				.setOrderByClause(orderBy(associations, orderBy)).setGroupByClause(groupBy);
	//
	//		if (getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
	//			select.setComment(getComment());
	//		}
	//		sql = select.toStatementString();
	//	}

}
