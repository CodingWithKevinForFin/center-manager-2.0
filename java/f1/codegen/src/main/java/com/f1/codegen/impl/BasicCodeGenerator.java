/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.impl;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.CodeGenerated;
import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.codegen.CodeCompiler;
import com.f1.codegen.CodeGenerator;
import com.f1.codegen.CodeTemplate;
import com.f1.codegen.CodeableClass;
import com.f1.codegen.templates.MessageCodeTemplate;
import com.f1.codegen.templates.PartialMessageCodeTemplate;
import com.f1.codegen.templates.ValuedCodeTemplate;
import com.f1.codegen.templates.ValuedListenableCodeTemplate;
import com.f1.utils.BasicObjectGenerator;
import com.f1.utils.BasicObjectGeneratorForClass;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.Tuple2;

public class BasicCodeGenerator extends BasicObjectGenerator implements CodeGenerator {
	private static final Logger log = Logger.getLogger(BasicCodeGenerator.class.getName());

	final private CopyOnWriteHashMap<Class, ObjectGeneratorForClass> compiled = new CopyOnWriteHashMap<Class, ObjectGeneratorForClass>();
	private CodeCompiler codeCompiler;
	private List<CodeTemplate> templates = new ArrayList<CodeTemplate>();
	private Map<Class, CodeTemplate> templatesByClass = new HashMap<Class, CodeTemplate>();
	private ObjectGenerator simpleGenerator = new BasicObjectGenerator();

	public BasicCodeGenerator(CodeCompiler compiler, boolean init) throws IOException {
		this.codeCompiler = compiler;
		if (init) {
			addCodeTemplate(new PartialMessageCodeTemplate());
			addCodeTemplate(new ValuedListenableCodeTemplate());
			addCodeTemplate(new MessageCodeTemplate());
			addCodeTemplate(new ValuedCodeTemplate());
		}
	}

	@Override
	public void addCodeTemplate(CodeTemplate template) {
		CH.putOrThrow(templatesByClass, template.getType(), template);
		templates.add(template);
	}

	@Override
	synchronized protected <C> ObjectGeneratorForClass<C> createGenerator(Class<C> clazz) {
		try {
			return generateCode(CH.l((Class) clazz)).get(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	synchronized public List<ObjectGeneratorForClass> generateCode(List<Class> classes) throws Exception {
		int size = classes.size();
		List<Boolean> done = new ArrayList<Boolean>(size);
		List<ObjectGeneratorForClass> r = new ArrayList<ObjectGeneratorForClass>(size);
		int missingCount = 0;
		for (Class c : classes) {
			if (c == null)
				throw new NullPointerException("classes may not be null");
			boolean found = compiled.containsKey(c);
			if (!found)
				missingCount++;
			done.add(found);
			r.add(compiled.get(c));
		}
		if (missingCount == 0)
			return r;
		Map<Class, ObjectGeneratorForClass> map = new HashMap<Class, ObjectGeneratorForClass>(compiled);
		// Take care of the easy ones
		for (int i = 0; i < size; i++) {
			if (done.get(i))
				continue;
			Class c = classes.get(i);
			if (!Modifier.isAbstract(c.getModifiers()) || CodeGenerated.class.isAssignableFrom(c) || c.isEnum()) {
				ObjectGeneratorForClass t = toObjectGeneratorForClass(c);
				map.put(c, t);
				r.set(i, t);
			}
		}

		List<CodeableClass> codeableClasses = new ArrayList<CodeableClass>(size);
		List<List<CodeTemplate>> templates = new ArrayList<List<CodeTemplate>>(size);
		boolean more = false;
		for (int i = 0; i < size; i++) {
			Class c = classes.get(i);
			List<CodeTemplate> orderedTemplates = done.get(i) ? Collections.EMPTY_LIST : getTemplates(c);
			if (orderedTemplates.size() == 0) {
				ObjectGeneratorForClass t = compiled.containsKey(c) ? compiled.get(c) : toObjectGeneratorForClass(c);
				map.put(c, t);
				r.set(i, t);
			} else
				more = true;
			codeableClasses.add(new BasicCodeableClass(c, c));
			templates.add(orderedTemplates);
		}
		List<CodeableClass> sourceCodeableClasses = new ArrayList<CodeableClass>(codeableClasses);
		while (more) {
			more = false;
			List<Tuple2<String, String>> classesAndCode = new ArrayList<Tuple2<String, String>>();
			for (int i = 0; i < size; i++) {
				List<CodeTemplate> orderedTemplates = templates.get(i);
				CodeableClass cc = codeableClasses.get(i);
				if (done.get(i) || orderedTemplates.size() == 0)
					continue;
				CodeTemplate template = orderedTemplates.remove(orderedTemplates.size() - 1);
				boolean makeAbstract = !orderedTemplates.isEmpty();
				if (makeAbstract)
					more = true;
				String code = template.createCode(cc, sourceCodeableClasses.get(i), makeAbstract);
				Tuple2<String, String> tuple = new Tuple2<String, String>(cc.getClassName(), code);
				classesAndCode.add(tuple);
			}
			List<Boolean> results;
			try {
				results = codeCompiler.compile(classesAndCode);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			int resultLoc = 0;
			for (int i = 0; i < size; i++) {
				if (done.get(i))
					continue;
				if (!results.get(resultLoc++)) {
					LH.severe(log, "compiling failed for: ", classesAndCode.get(resultLoc - 1).getA());
					done.set(i, true);
					r.set(i, null);
					continue;
				}
				Class c;
				try {
					c = Class.forName(codeableClasses.get(i).getClassName());
				} catch (Exception e) {
					throw new RuntimeException("Error loadding class: " + codeableClasses.get(i).getClassName(), e);
				}
				codeableClasses.set(i, new BasicCodeableClass(c, codeableClasses.get(i).getOrigClass()));
				if (templates.get(i).isEmpty()) {
					done.set(i, true);
					ObjectGeneratorForClass result = toObjectGeneratorForClass(c);
					r.set(i, result);
					map.put(classes.get(i), result);
					map.put(result.getClass(), result);
				}
			}
		}

		compiled.setInnerMap(map);
		return r;
	}

	private List<CodeTemplate> getTemplates(Class c) {
		Set<Class> r = new HashSet<Class>();
		CodeTemplate top = null;
		for (CodeTemplate t : templates)
			if (t.getType().isAssignableFrom(c)) {
				r.add(t.getType());
				if (top == null)
					top = t;
			}
		if (r.isEmpty())
			return Collections.EMPTY_LIST;
		return top.getHierarchy(r, templatesByClass);
	}

	@Override
	public CodeCompiler getCodeCompiler() {
		return codeCompiler;
	}

	@Override
	public void setCodeCompiler(CodeCompiler codeCompiler) {
		this.codeCompiler = codeCompiler;
	}

	@Override
	public Object[] nw(Class<?>... classes) {
		try {
			final List<ObjectGeneratorForClass> gens = generateCode((List) CH.l(classes));
			final Object[] r = new Object[classes.length];
			for (int i = 0; i < classes.length; i++) {
				final ObjectGeneratorForClass gen = gens.get(i);
				if (gen != null)
					r[i] = gen.nw();
			}
			return r;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public <C> C nwCast(Class<C> classs, Class[] argumentTypes, Object[] constructorParameters) {
		ObjectGeneratorForClass<C> ogfc;
		try {
			ogfc = generateCode(classs);
		} catch (Exception e) {
			throw new RuntimeException("error coding for " + classs, e);
		}
		if (ogfc == null)
			throw new NullPointerException("class was not compiled: " + classs);
		C r = ogfc.nwCast(argumentTypes, constructorParameters);
		return r;
	}

	@Override
	public <T> ObjectGeneratorForClass<T> generateCode(Class<T> clazz) throws Exception {
		ObjectGeneratorForClass<T> r = compiled.get(clazz);
		if (r != null)
			return r;
		if (compiled.containsKey(clazz))
			return compiled.get(clazz);
		return generateCode(CH.l((Class) clazz)).get(0);
	}

	private <C> ObjectGeneratorForClass<C> toObjectGeneratorForClass(Class<C> c) {
		if (ObjectGeneratorForClass.class.isAssignableFrom(c)) {
			try {
				return (ObjectGeneratorForClass<C>) c.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Error instantiating: " + c.getName(), e);
			}
		}
		return new BasicObjectGeneratorForClass<C>(simpleGenerator, this, c);
	}

}
