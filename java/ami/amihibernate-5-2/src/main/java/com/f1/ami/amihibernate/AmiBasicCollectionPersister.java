package com.f1.ami.amihibernate;

import org.hibernate.MappingException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.SubselectFetch;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.mapping.Collection;
import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.spi.PersisterCreationContext;

public class AmiBasicCollectionPersister extends BasicCollectionPersister {

	public AmiBasicCollectionPersister(Collection collectionBinding, CollectionRegionAccessStrategy cacheAccessStrategy, PersisterCreationContext creationContext)
			throws MappingException, CacheException {
		super(collectionBinding, cacheAccessStrategy, creationContext);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SharedSessionContractImplementor session) {
		// TODO Auto-generated method stub
		//		return super.createSubselectInitializer(subselect, session);
		String prepSubselectString = AmiHibernateHelper.prepareFrom(subselect.toSubselectString(getCollectionType().getLHSPropertyName()));
		return new AmiSubselectCollectionLoader(this, prepSubselectString, subselect.getResult(), subselect.getQueryParameters(), subselect.getNamedParameterLocMap(),
				session.getFactory(), session.getLoadQueryInfluencers());
	}
	@Override
	protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
		// TODO Auto-generated method stub
		return super.createCollectionInitializer(loadQueryInfluencers);
	}

}
