package com.f1.ami.amihibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.JoinWalker;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.CollectionLoader;
import org.hibernate.loader.collection.SubselectCollectionLoader;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

//public class AmiSubselectCollectionLoader extends SubselectCollectionLoader implements CollectionInitializer {
public class AmiSubselectCollectionLoader extends CollectionLoader implements CollectionInitializer {

	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, AmiSubselectCollectionLoader.class.getName());
	private SubselectCollectionLoader subSel;

	private final Serializable[] keys;
	private final Type[] types;
	private final Object[] values;
	private final Map<String, TypedValue> namedParameters;
	private final Map<String, int[]> namedParameterLocMap;

	public AmiSubselectCollectionLoader(QueryableCollection persister, String subquery, Collection entityKeys, QueryParameters queryParameters,
			Map<String, int[]> namedParameterLocMap, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
		super(persister, factory, loadQueryInfluencers);

		JoinWalker walker = new AmiBasicCollectionJoinWalker(persister, 1, subquery, factory, loadQueryInfluencers);
		initFromWalker(walker);

		postInstantiate();

		if (LOG.isDebugEnabled()) {
			LOG.debugf("Static select for collection %s: %s", persister.getRole(), getSQLString());
		}

		//		super( persister, 1, subquery, factory, loadQueryInfluencers );

		keys = new Serializable[entityKeys.size()];
		Iterator iter = entityKeys.iterator();
		int i = 0;
		while (iter.hasNext()) {
			keys[i++] = ((EntityKey) iter.next()).getIdentifier();
		}

		this.namedParameters = queryParameters.getNamedParameters();
		this.types = queryParameters.getFilteredPositionalParameterTypes();
		this.values = queryParameters.getFilteredPositionalParameterValues();
		this.namedParameterLocMap = namedParameterLocMap;

	}
	@Override
	public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
		loadCollectionSubselect(session, keys, values, types, namedParameters, getKeyType());
	}

	@Override
	public int[] getNamedParameterLocs(String name) {
		return namedParameterLocMap.get(name);
	}

}
