package com.f1.utils.structs.table.derived;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.concurrent.LinkedHasherMap.Node;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class BasicMethodFactory implements MethodFactoryManager, Lockable, ToStringable {

	private static final Map<String, Class<?>> DEFAULT_VAR_TYPES = CH.m(new HasherMap<String, Class<?>>(CaseInsensitiveHasher.INSTANCE), "Integer", Integer.class, "long",
			Long.class, "String", String.class, "float", Float.class, "double", Double.class, "short", Short.class, "byte", Byte.class, "char", Character.class, "Object",
			Object.class, "boolean", Boolean.class, "Number", Number.class, "Comparable", Comparable.class);
	final private OneToOne<String, Class<?>> varTypes;
	final private Map<String, Class<?>> varTypeAliases;
	final private Map<Class<?>, Class<?>> dfltImpls;
	private boolean locked = false;
	private List<MethodFactoryManager> inners = new ArrayList<MethodFactoryManager>();
	private BasicMultiMap.List<String, MethodFactory> factoriesByName = new BasicMultiMap.List<String, MethodFactory>();
	private Map<Class<?>, Caster<?>> casters = new HashMap<Class<?>, Caster<?>>();
	private Map<String, String> varTypeDescriptions = new HashMap<String, String>();
	private BasicMultiMap.List<String, DerivedCellMemberMethod<?>> methods = new BasicMultiMap.List<String, DerivedCellMemberMethod<?>>();
	private Map<Class, ClassDebugInspector<?>> classDebugInspectors = new HashMap<Class, ClassDebugInspector<?>>();
	private Map<Class, List<ClassDebugInspector<?>>> classDebugInspectorsCache = new HashMap<Class, List<ClassDebugInspector<?>>>();
	private HasherMap<CacheKey, DerivedCellMemberMethod> cached = new HasherMap<CacheKey, DerivedCellMemberMethod>();
	private HasherMap<Class<?>, Tuple2<Class, String>> forTypeCached = new HasherMap<Class<?>, Tuple2<Class, String>>();

	public BasicMethodFactory() {
		this(null);
	}
	public BasicMethodFactory(MethodFactoryManager inner) {
		this.varTypes = new OneToOne<String, Class<?>>(new HasherMap<String, Class<?>>(CaseInsensitiveHasher.INSTANCE));
		this.casters = new HasherMap<Class<?>, Caster<?>>();
		this.varTypeAliases = new HasherMap<String, Class<?>>(CaseInsensitiveHasher.INSTANCE);
		this.dfltImpls = new HashMap<Class<?>, Class<?>>();
		if (inner != null)
			this.inners.add(inner);
	}

	@Override
	public String forType(Class<?> clazz) {
		return findTypeTuple(clazz).getB();
	}
	@Override
	public Class findType(Class clazz) {
		return findTypeTuple(clazz).getA();
	}
	private Tuple2<Class, String> findTypeTuple(Class<?> clazz) {
		com.f1.utils.concurrent.HasherMap.Entry<Class<?>, Tuple2<Class, String>> e = this.forTypeCached.getEntry(clazz);
		if (e != null)
			return e.getValue();
		String r = this.varTypes.getKey(clazz);
		if (r != null) {
			Tuple2<Class, String> t;
			this.forTypeCached.put(clazz, t = new Tuple2<Class, String>(clazz, r));
			t.lock();
			return t;
		}
		String bestName = null;
		Class bestType = null;
		for (Entry<String, Class<?>> i : varTypes.getEntries()) {
			if (i.getValue().isAssignableFrom(clazz)) {
				if (bestType == null || bestType.isAssignableFrom(i.getValue())) {
					bestName = i.getKey();
					bestType = i.getValue();
				}
			}
		}
		for (Entry<String, Class<?>> i : DEFAULT_VAR_TYPES.entrySet()) {
			if (i.getValue().isAssignableFrom(clazz)) {
				if (bestType == null || (bestType != i.getValue() && bestType.isAssignableFrom(i.getValue()))) {
					bestName = i.getKey();
					bestType = i.getValue();
				}
			}
		}

		for (int i = 0; i < this.inners.size(); i++) {
			String name = inners.get(i).forType(clazz);
			if (name != null) {
				Class type;
				try {
					type = inners.get(i).forName(name);
				} catch (ClassNotFoundException e1) {
					throw OH.toRuntime(e1);
				}
				if (bestType == null || bestType.isAssignableFrom(type)) {
					bestName = name;
					bestType = type;
				}
			}
		}
		Tuple2<Class, String> value = new Tuple2<Class, String>(bestType, bestName);
		value.lock();
		this.forTypeCached.put(clazz, value);
		return value;
	}
	public void addVarType(String name, Class<?> type) {
		LockedException.assertNotLocked(this);
		addVarType(name, type, type);
	}
	public void addVarTypeDescription(String name, String description) {
		LockedException.assertNotLocked(this);
		this.varTypeDescriptions.put(name, description);
	}

	@Override
	public String getVarTypeDescription(String name) {
		String r = this.varTypeDescriptions.get(name);
		if (r == null)
			for (MethodFactoryManager i : this.inners)
				if ((r = i.getVarTypeDescription(name)) != null)
					break;
		return r;
	}
	public void addVarType(String name, Class<?> type, Class<?> dfltImpl) {
		LockedException.assertNotLocked(this);
		if (this.varTypes.containsValue(type))
			CH.putOrThrow(this.varTypeAliases, name, type);
		else
			this.varTypes.put(name, type);
		this.forTypeCached.clear();
		if (type != dfltImpl)
			this.dfltImpls.put(type, dfltImpl);
	}

	protected Class<?> getVarType(String type) {
		Class<?> r = this.varTypes.getValue(type);
		if (r != null)
			return r;
		r = this.varTypeAliases.get(type);
		if (r != null)
			return r;
		return DEFAULT_VAR_TYPES.get(type);
	}

	@Override
	public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack variables) {
		List<MethodFactory> factories = factoriesByName.get(methodName);
		if (factories != null) {
			MethodFactory mf = null;
			outer: for (MethodFactory f : factories) {
				if (f.getDefinition().canAccept(calcs)) {
					if (mf == null || isNarrower(mf, f))
						mf = f;
				}
			}
			if (mf != null)
				return mf.toMethod(position, methodName, calcs, variables);
		}
		DerivedCellCalculator r = null;
		for (int i = 0; r == null && i < this.inners.size(); i++)
			r = inners.get(i).toMethod(position, methodName, calcs, variables);
		return r;
	}
	private static boolean isNarrower(MethodFactory l, MethodFactory r) {
		Class[] lp = l.getDefinition().getParamTypes();
		Class[] rp = r.getDefinition().getParamTypes();
		for (int j = 0; j < lp.length && j < rp.length; j++) {
			if (lp[j] == rp[j])
				continue;
			if (lp[j].isAssignableFrom(rp[j]))
				return true;
			if (rp[j].isAssignableFrom(lp[j]))
				return false;
		}
		return false;
	}
	public void addFactory(MethodFactory factory) {
		this.factoriesByName.putMulti(factory.getDefinition().getMethodName(), factory);
	}
	public void removeFactory(MethodFactory factory) {
		this.factoriesByName.removeMulti(factory.getDefinition().getMethodName(), factory);
	}
	public void clearMethodFactories() {
		this.factoriesByName.clear();
	}

	@Override
	public Class<?> forNameNoThrow(String vartype) {
		Class<?> r = getVarType(vartype);
		for (int i = 0; r == null && i < this.inners.size(); i++)
			r = inners.get(i).forNameNoThrow(vartype);
		return r;
	}
	@Override
	public Class<?> forName(String vartype) throws ClassNotFoundException {
		Class<?> r = getVarType(vartype);
		for (int i = 0; r == null && i < this.inners.size(); i++)
			r = inners.get(i).forNameNoThrow(vartype);
		if (r == null)
			throw new ClassNotFoundException(vartype);
		return r;
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return this.locked;
	}

	private static class CacheKey {
		Class targetType, args[];
		String methodName;

		public void reset(Class targetType, String methodName, Class args[]) {
			this.targetType = targetType;
			this.methodName = methodName;
			this.args = args;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(args);
			if (methodName != null)
				result = prime * result + methodName.hashCode();
			result = prime * result + targetType.hashCode();
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			return Arrays.equals(args, other.args) && OH.eq(this.methodName, other.methodName) && OH.eq(targetType, other.targetType);
		}

		@Override
		public String toString() {
			return targetType.getName() + "::" + methodName + "(" + SH.join(',', args) + ")";
		}
	}

	private CacheKey tmp = new CacheKey();

	@Override
	public <T> DerivedCellMemberMethod<T> findMemberMethod(Class<? extends T> targetType, String methodName, Class<?>[] arguments) {
		tmp.reset(targetType, methodName, arguments);
		Entry<CacheKey, DerivedCellMemberMethod> t = this.cached.getEntry(tmp);
		if (t != null)
			return t.getValue();
		List<DerivedCellMemberMethod<?>> choices = getMemberMethods(targetType, methodName);
		DerivedCellMemberMethod<T> r = null;
		if (CH.isntEmpty(choices)) {
			outer: for (DerivedCellMemberMethod<?> i : choices) {
				if (!i.getTargetType().isAssignableFrom(targetType))
					continue;
				if (methodName == null && i.getMethodName() != null)
					continue;
				final Class<?>[] paramTypes = i.getParamTypes();
				final Class<?> varArgType = i.getVarArgType();
				if (varArgType == null ? arguments.length != paramTypes.length : arguments.length < paramTypes.length)
					continue;
				int j;
				for (j = 0; j < paramTypes.length; j++)
					if (arguments[j] != null && arguments[j] != Void.class && !OH.isAssignableFrom(paramTypes[j], arguments[j]))
						continue outer;
				for (; j < arguments.length; j++)
					if (arguments[j] != null && arguments[j] != Void.class && !OH.isAssignableFrom(varArgType, arguments[j]))
						continue outer;
				if (r == null || isNarrower(r, i)) {
					r = (DerivedCellMemberMethod<T>) i;
				}
			}
		}
		if (r == null)
			for (int i = 0; i < this.inners.size(); i++) {
				MethodFactoryManager inner = this.inners.get(i);
				r = inner.findMemberMethod(targetType, methodName, arguments);
				if (r != null)
					break;
			}
		CacheKey tmp2 = new CacheKey();
		tmp2.reset(targetType, methodName, arguments.clone());
		this.cached.put(tmp2, r);
		return r;
	}

	private static boolean isNarrower(DerivedCellMemberMethod l, DerivedCellMemberMethod r) {
		Class[] lp = l.getParamTypes();
		Class[] rp = r.getParamTypes();
		if (l.getVarArgType() != null)
			lp = AH.append(lp, l.getVarArgType());
		if (r.getVarArgType() != null)
			rp = AH.append(rp, r.getVarArgType());
		for (int j = 0; j < lp.length && j < rp.length; j++) {
			if (lp[j] == rp[j])
				continue;
			if (lp[j].isAssignableFrom(rp[j]))
				return true;
			if (rp[j].isAssignableFrom(lp[j]))
				return false;
		}
		return false;
	}

	private HasherMap<Tuple2<Class<?>, String>, List<DerivedCellMemberMethod<?>>> memberMethodsCache = new HasherMap<Tuple2<Class<?>, String>, List<DerivedCellMemberMethod<?>>>();
	private MethodFactoryManager factoryForVirtuals;

	private List<DerivedCellMemberMethod<?>> getMemberMethods(Class<?> targetType, String methodName) {
		Entry<Tuple2<Class<?>, String>, List<DerivedCellMemberMethod<?>>> entry = this.memberMethodsCache.getOrCreateEntry(new Tuple2<Class<?>, String>(targetType, methodName));
		if (entry.getValue() != null)
			return entry.getValue();
		List<DerivedCellMemberMethod<?>> r;
		if (targetType == null) {
			Iterable<DerivedCellMemberMethod<?>> items = methodName == null ? methods.valuesMulti() : methods.get(methodName);
			r = items == null ? Collections.EMPTY_LIST : CH.l(items);
		} else {
			LinkedHasherMap<ParamsDefinition, DerivedCellMemberMethod<?>> m = new LinkedHasherMap<ParamsDefinition, DerivedCellMemberMethod<?>>(
					ParamsDefinition.HASHER_DEF_IGNORE_RETURNTYPE);
			Iterable<DerivedCellMemberMethod<?>> mthds = methodName == null ? methods.valuesMulti() : methods.get(methodName);
			if (mthds == null)
				r = Collections.EMPTY_LIST;
			else {
				for (DerivedCellMemberMethod<?> i : mthds) {
					if (i.getTargetType().isAssignableFrom(targetType)) {
						Node<ParamsDefinition, DerivedCellMemberMethod<?>> e2 = m.getOrCreateEntry(i.getParamsDefinition());
						if (e2.getValue() == null)
							e2.setValue(i);
						else if (OH.isAssignableFrom(e2.getValue().getTargetType(), i.getTargetType()))
							e2.setValue(i);
					}

				}
				r = CH.l(m.valueIterator());
			}
		}
		entry.setValue(r);
		return r;
	}
	@Override
	public <T> void getMemberMethods(Class<? extends T> targetType, String methodName, List<DerivedCellMemberMethod<T>> sink) {
		sink.addAll((List) getMemberMethods(targetType, methodName));
		for (MethodFactoryManager i : this.inners)
			i.getMemberMethods(targetType, methodName, sink);
	}

	public <T> void addMemberMethod(DerivedCellMemberMethod<T> method) {
		LockedException.assertNotLocked(this);
		//ENABLE TO CHECK FOR DUPLICATES:
		//		List<DerivedCellMemberMethod<?>> existing = methods.get(method.getMethodName());
		//		if (existing != null && CH.indexOfIdentity(existing, method) != -1)
		//			throw new RuntimeException("Duplicate method: " + method.getTargetType() + "::" + method);
		methods.putMulti(method.getMethodName(), method);
		this.memberMethodsCache.clear();
		this.cached.clear();
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		for (Entry<String, Class<?>> i : this.varTypes.getEntries()) {
			sb.append(i.getKey()).append(" --> ");
			SH.getSimpleName(i.getValue(), sb);
			sb.append(SH.NEWLINE);
		}
		for (Entry<String, Class<?>> i : this.varTypeAliases.entrySet()) {
			sb.append(i.getKey()).append(" --> ");
			SH.getSimpleName(i.getValue(), sb);
			sb.append(SH.NEWLINE);
		}
		for (Entry<String, Class<?>> i : DEFAULT_VAR_TYPES.entrySet()) {
			if (this.varTypes.containsKey(i.getKey()))
				continue;
			sb.append(i.getKey()).append(" --> ");
			SH.getSimpleName(i.getValue(), sb);
			sb.append(SH.NEWLINE);
		}
		for (DerivedCellMemberMethod<?> i : this.methods.valuesMulti()) {
			i.toString(sb);
			sb.append(SH.NEWLINE);
		}
		return sb;

	}
	@Override
	public Class<?> getDefaultImplementation(Class<?> vartype) {
		Class<?> r = this.dfltImpls.get(vartype);
		return r == null ? vartype : r;
	}

	public void removeFactoryManager(MethodFactoryManager mf) {
		LockedException.assertNotLocked(this);
		this.inners.remove(mf);
		this.cached.clear();
		this.forTypeCached.clear();
	}
	public void addFactoryManager(MethodFactoryManager mf) {
		LockedException.assertNotLocked(this);
		if (mf == null)
			throw new NullPointerException();
		this.inners.add(mf);
	}
	public void clearFactoryManagers() {
		LockedException.assertNotLocked(this);
		this.inners.clear();
		this.cached.clear();
		this.classDebugInspectorsCache.clear();
		this.forTypeCached.clear();
	}
	@Override
	public void getAllMethodFactories(List<MethodFactory> sink) {
		for (MethodFactoryManager i : inners)
			i.getAllMethodFactories(sink);
		for (List<MethodFactory> i : this.factoriesByName.values())
			sink.addAll(i);
	}

	@Override
	public void getMethodFactories(Collection<MethodFactory> sink) {
		for (List<MethodFactory> i : this.factoriesByName.values())
			sink.addAll(i);
	}

	@Override
	public MethodFactory getMethodFactory(String name, Class[] args) {
		List<MethodFactory> r = this.factoriesByName.get(name);
		if (r != null)
			for (MethodFactory i : r)
				if (Arrays.equals(i.getDefinition().getParamTypes(), args))
					return i;
		for (MethodFactoryManager i : this.inners) {
			MethodFactory t = i.getMethodFactory(name, args);
			if (t != null)
				return t;
		}
		return null;
	}
	public void addClassDebugInspector(ClassDebugInspector<?> outer) {
		LockedException.assertNotLocked(this);
		this.classDebugInspectors.put(outer.getVarType(), outer);
		this.classDebugInspectorsCache.clear();
	}

	@Override
	public <T> List<ClassDebugInspector<?>> getClassDebugInepectors(Class<T> c) {
		List<ClassDebugInspector<?>> r = this.classDebugInspectorsCache.get(c);
		if (r == null) {
			r = new ArrayList<ClassDebugInspector<?>>();
			for (MethodFactoryManager i : this.inners)
				r.addAll(i.getClassDebugInepectors(c));
			for (ClassDebugInspector<?> i : this.classDebugInspectors.values())
				if (i.getVarType().isAssignableFrom(c))
					r.add(i);
			this.classDebugInspectorsCache.put(c, r);
		}
		return (List) r;

	}
	@Override
	public void getTypes(Set<Class<?>> sink) {
		sink.addAll(this.DEFAULT_VAR_TYPES.values());
		for (MethodFactoryManager i : this.inners)
			i.getTypes(sink);
		for (Class<?> i : this.varTypes.getValues())
			sink.add(i);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Caster getCaster(Class type) {
		Caster<?> r = this.casters.get(type);
		if (r == null) {
			for (MethodFactoryManager i : this.inners) {
				r = i.getCaster(type);
				if (r != null) {
					this.casters.put(type, r);
					return r;
				}
			}
			this.casters.put(type, r = OH.getCaster(type));
		}
		return r;
	}

	public void addCaster(Caster<?> c) {
		LockedException.assertNotLocked(this);
		this.casters.put(c.getCastToClass(), c);
	}
	@Override
	public MethodFactoryManager getFactoryForVirtuals() {
		return this.factoryForVirtuals;
	}
	public void setFactoryForVirtuals(MethodFactoryManager factoryForVirtuals) {
		this.factoryForVirtuals = factoryForVirtuals;
	}
	public MethodFactory findFactory(ParamsDefinition definition) {
		List<MethodFactory> candidates = this.factoriesByName.get(definition.getMethodName());
		if (CH.isntEmpty(candidates)) {
			for (int i = 0; i < candidates.size(); i++) {
				MethodFactory candidate = candidates.get(i);
				if (definition.equalsDefIgnoreReturnType(candidate.getDefinition()))
					return candidate;
			}
		}
		return null;
	}
}
