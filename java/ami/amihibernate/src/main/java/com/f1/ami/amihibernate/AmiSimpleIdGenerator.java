package com.f1.ami.amihibernate;

import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Long;

public class AmiSimpleIdGenerator implements IdentifierGenerator {

	private Type type;

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		this.type = type;
	}

	@Override
	public Object generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
		String query = String.format("select max(%s) as max_id from %s", session.getEntityPersister(obj.getClass().getName(), obj).getIdentifierPropertyName(),
				obj.getClass().getSimpleName());

		Object lastId = session.createQuery(query).getSingleResultOrNull();
		Long nextIdl = Caster_Long.INSTANCE.castOr(lastId, 0L) + 1;

		return OH.cast(nextIdl, type.getReturnedClass());
	}

	//	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
	//
	//		String query = String.format("select %s from %s", session.getEntityPersister(obj.getClass().getName(), obj).getIdentifierPropertyName(), obj.getClass().getSimpleName());
	//
	//		Stream ids = session.createQuery(query).stream();
	//
	//		Long max = ids.map(o -> o.replace(prefix + "-", "")).mapToLong(Long::parseLong).max().orElse(0L);
	//
	//		return prefix + "-" + (max + 1);
	//	}
}
