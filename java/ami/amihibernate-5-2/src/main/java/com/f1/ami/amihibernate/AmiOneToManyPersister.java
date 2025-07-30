package com.f1.ami.amihibernate;

import org.hibernate.MappingException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.mapping.Collection;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.persister.spi.PersisterCreationContext;

public class AmiOneToManyPersister extends OneToManyPersister {

	public AmiOneToManyPersister(Collection collectionBinding, CollectionRegionAccessStrategy cacheAccessStrategy, PersisterCreationContext creationContext)
			throws MappingException, CacheException {
		super(collectionBinding, cacheAccessStrategy, creationContext);
		// TODO Auto-generated constructor stub
	}

}
