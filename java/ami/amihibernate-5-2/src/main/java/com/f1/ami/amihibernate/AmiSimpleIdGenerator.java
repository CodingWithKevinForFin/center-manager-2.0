package com.f1.ami.amihibernate;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Long;

public class AmiSimpleIdGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
		EntityPersister persister = session.getEntityPersister(obj.getClass().getName(), obj);
		String identifierPropertyName = persister.getIdentifierPropertyName();
		Type identifierType = persister.getIdentifierType();
		String query = String.format("select max(%s) as max_id from %s as t", identifierPropertyName, obj.getClass().getSimpleName());

		Object lastId = session.createQuery(query).getSingleResult();
		Long nextIdl = Caster_Long.INSTANCE.castOr(lastId, 0L) + 1;

		Serializable ret = (Serializable) OH.cast(nextIdl, identifierType.getReturnedClass());
		return ret;
	}

}
