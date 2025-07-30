package com.f1.ami.amihibernate;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.internal.StandardPersisterClassResolver;
import org.hibernate.persister.spi.UnknownPersisterException;

public class AmiPersisterClassResolver extends StandardPersisterClassResolver {
	@Override
	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
		// TODO Auto-generated method stub
		//		return super.getEntityPersisterClass(metadata);
		// todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
		if (RootClass.class.isInstance(metadata)) {
			if (metadata.hasSubclasses()) {
				//If the class has children, we need to find of which kind
				metadata = (PersistentClass) metadata.getDirectSubclasses().next();
			} else {
				return singleTableEntityPersister();
			}
		}
		if (JoinedSubclass.class.isInstance(metadata)) {
			return joinedSubclassEntityPersister();
		} else if (UnionSubclass.class.isInstance(metadata)) {
			return unionSubclassEntityPersister();
		} else if (SingleTableSubclass.class.isInstance(metadata)) {
			return singleTableEntityPersister();
		} else {
			throw new UnknownPersisterException("Could not determine persister implementation for entity [" + metadata.getEntityName() + "]");
		}
	}
	@Override
	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
		return metadata.isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
		//		return super.getCollectionPersisterClass(metadata);
	}
	@Override
	public Class<? extends EntityPersister> joinedSubclassEntityPersister() {
		// TODO Auto-generated method stub
		//		return super.joinedSubclassEntityPersister();
		return AmiJoinedSubclassEntityPersister.class;
	}
	@Override
	public Class<? extends EntityPersister> unionSubclassEntityPersister() {
		// TODO Auto-generated method stub
		//		return super.unionSubclassEntityPersister();
		return AmiUnionSubclassEntityPersister.class;
	}
	@Override
	public Class<? extends EntityPersister> singleTableEntityPersister() {
		return AmiEntityPersister.class;
	}
	private Class<? extends OneToManyPersister> oneToManyPersister() {
		return AmiOneToManyPersister.class;

	}
	private Class<? extends BasicCollectionPersister> basicCollectionPersister() {
		return AmiBasicCollectionPersister.class;
	}
}
