package com.f1.ami.amihibernate;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;

public class AmiUnionSubclassEntityPersister extends UnionSubclassEntityPersister {

	public AmiUnionSubclassEntityPersister(PersistentClass persistentClass, EntityRegionAccessStrategy cacheAccessStrategy,
			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
		super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
		// TODO Auto-generated constructor stub
	}

}
