package com.f1.ami.amihibernate;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.internal.StandardPersisterClassResolver;

public class AmiPersisterClassResolver extends StandardPersisterClassResolver {
	@Override
	public Class<? extends EntityPersister> singleTableEntityPersister() {
		return AmiEntityPersister.class;
	}

}
