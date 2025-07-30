package com.f1.base;

public interface Caster<C> {

	public Class<C> getCastToClass();
	public C cast(Object o, boolean required, String description);
	public C castNoThrow(Object o);//cast(o,false,false);

	public C cast(Object o);//cast(o,false,true);
	public C cast(Object o, boolean required);//cast(o,required,true);
	public C cast(Object o, boolean required, boolean throwExceptionOnError);

	public String getName();
	public String getSimpleName();
}
