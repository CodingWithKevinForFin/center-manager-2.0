/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.base.Ideable;
import com.f1.base.IdeableGenerator;
import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.utils.structs.LongKeyMap;

public class BasicIdeableGenerator extends ObjectGeneratorWrapper implements IdeableGenerator {

	final static private Logger log = Logger.getLogger(BasicIdeableGenerator.class.getName());

	final private Map<Long, Class> id2classes = new CopyOnWriteHashMap<Long, Class>();
	final private Map<String, Class> name2classes = new CopyOnWriteHashMap<String, Class>();
	private LongKeyMap<ObjectGeneratorForClass<?>> id2Generators = new LongKeyMap<ObjectGeneratorForClass<?>>();

	public BasicIdeableGenerator(ObjectGenerator inner) {
		super(inner);
	}

	@Override
	public <C> C nw(long ideableId) {
		ObjectGeneratorForClass<?> obgfc = id2Generators.get(ideableId);
		if (obgfc != null) {
			return (C) obgfc.nw();
		}
		Class c = id2classes.get(ideableId);
		if (c == null) {
			Map<String, Class<?>> existing = new HashMap<String, Class<?>>();
			for (Entry<Long, Class> e : id2classes.entrySet()) {
				existing.put(VidParser.fromLong(e.getKey()), e.getValue());
			}
			throw new DetailedException("VID not found (you need to register the corresponding class w/ this generator)").set("VID", VidParser.fromLong(ideableId))
					.set("VID (hex)", SH.toHex(ideableId)).set("registered VIDs", existing);
		}
		obgfc = getGeneratorForClass(c);
		if (obgfc == null)
			throw new DetailedException("Object Generator for class not found").set("VID (hex)", SH.toHex(ideableId));
		synchronized (this) {
			LongKeyMap<ObjectGeneratorForClass<?>> t = id2Generators.clone();
			t.put(ideableId, obgfc);
			this.id2Generators = t;
		}
		return (C) obgfc.nw();
	}

	@Override
	public <C> C nw(String ideableName) {
		Class c = name2classes.get(ideableName);
		if (c == null) {
			c = RH.getClass(ideableName);
			name2classes.put(ideableName, c);
		}
		return nw((Class<C>) c);
	}

	@Override
	public void register(final Class... classes) {
		Object[] instances;
		try {
			instances = nw(classes);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		for (int i = 0; i < classes.length; i++) {
			Class clazz = classes[i];
			Object instance = instances[i];
			if (instance != null) {
				registerInstance((Ideable) instance, clazz);
			} else {
				LH.severe(log, "could not instantiate ", clazz);
			}
		}
	}

	private void registerInstance(Ideable instance, Class clazz) {
		if (instance.askVid() != Ideable.NO_IDEABLEID)
			CH.putOrThrow(id2classes, instance.askVid(), clazz, "Duplicate VID");
		CH.putOrThrow(name2classes, instance.askIdeableName(), clazz);
	}

	@Override
	public <C> C nwCast(Class<C> classs_, Class[] argumentTypes_, Object[] constructorParameters_) {
		return null;
	}

	@Override
	public Iterable<Class> getRegistered() {
		return name2classes.values();
	}

}
