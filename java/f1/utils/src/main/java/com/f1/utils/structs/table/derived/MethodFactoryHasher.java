package com.f1.utils.structs.table.derived;

import java.util.Arrays;

import com.f1.utils.Hasher;
import com.f1.utils.OH;

public class MethodFactoryHasher implements Hasher<MethodFactory> {

	public static final Hasher<MethodFactory> INSTANCE = new MethodFactoryHasher();

	@Override
	public int hashcode(MethodFactory o) {
		if (o == null)
			return 0;
		ParamsDefinition def = o.getDefinition();
		int r = OH.hashCode(def.getMethodName());
		for (Class type : def.getParamTypes())
			r = OH.hashCode(r, type);
		return r;
	}

	@Override
	public boolean areEqual(MethodFactory l, MethodFactory r) {
		if (l == r)
			return true;
		else if (l == null || r == null)
			return false;
		final ParamsDefinition ld = l.getDefinition(), rd = r.getDefinition();
		return OH.eq(ld.getMethodName(), rd.getMethodName()) && Arrays.equals(ld.getParamTypes(), rd.getParamTypes());
	}
}
