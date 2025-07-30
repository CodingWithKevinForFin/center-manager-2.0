package com.f1.tester;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.request.ThreadStartRequest;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.CH;
import com.f1.utils.ClassFinder;
import com.f1.utils.ClassFinderEntry;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.StreamPiper;

public class JdiClassFinder {

	public static void main(String a[]) throws IOException {
		Set<ClassMirror> t = new HashSet<ClassMirror>();
		ClassFinder cf = new ClassFinder().searchClasspath(ClassFinder.TYPE_ALL).filterByPackage("f1.lib");
		System.err.println("Starting");
		toJdiClasses(cf, System.out, System.err);
		System.err.println("Done");
	}

	public static ClassFinder toJdiClasses(ClassFinder targetClasses, PrintStream stdout, PrintStream stderr) {
		try {
			Set<ClassFinderEntry> r = new LinkedHashSet<ClassFinderEntry>();
			LaunchingConnector connector = Bootstrap.virtualMachineManager().defaultConnector();
			Map<String, Argument> args = connector.defaultArguments();
			final String options = "-cp " + SH.join(File.pathSeparator, EH.getJavaClassPath());
			CH.getOrThrow(args, "options").setValue(options);
			CH.getOrThrow(args, "suspend").setValue("true");
			CH.getOrThrow(args, "main").setValue(String.class.getName());
			VirtualMachine tvm = connector.launch(args);

			ThreadStartRequest cpr = tvm.eventRequestManager().createThreadStartRequest();
			cpr.setSuspendPolicy(cpr.SUSPEND_ALL);
			cpr.enable();

			new Thread(new StreamPiper(tvm.process().getInputStream(), stdout, 1024)).start();
			new Thread(new StreamPiper(tvm.process().getErrorStream(), stderr, 1024)).start();

			tvm.resume();

			outer : while (true) {
				for (Event e : tvm.eventQueue().remove()) {
					if (e instanceof ThreadStartEvent) {
						ThreadStartEvent e2 = (ThreadStartEvent) e;
						ThreadReference thread = e2.thread();
						ClassType javaLangClassLoader = (ClassType) tvm.classesByName(ClassLoader.class.getName()).get(0);
						Method m2 = javaLangClassLoader.methodsByName("getSystemClassLoader").get(0);
						Value scl = javaLangClassLoader.invokeMethod(thread, m2, Collections.EMPTY_LIST, 0);
						ClassType javaLangClass = (ClassType) tvm.classesByName(Class.class.getName()).get(0);
						Method forName = javaLangClass.methodsByName("forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;").get(0);
						for (ClassFinderEntry cfe : targetClasses.getEntries()) {
							ClassMirror cm = cfe.getClassMirror();
							try {
								javaLangClass.invokeMethod(e2.thread(), forName, CH.l(tvm.mirrorOf(cm.getName()), tvm.mirrorOf(true), scl), 0).toString();

								ReferenceType result = tvm.classesByName(cm.getName()).get(0);
								r.add(new ClassFinderEntry(cfe.getType(), cfe.getClassPath(), toMirror(result)));
							} catch (Exception e_) {
								stderr.println("error with " + cm);
								e_.printStackTrace(stderr);
								r.add(cfe);
							}
						}
						cpr.disable();
						thread.resume();
						tvm.dispose();
						break outer;
					}
				}
			}

			return new ClassFinder(r);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	private static JdiClassMirror toMirror(ReferenceType r) {
		if (r == null)
			return null;
		else if (r instanceof ClassType) {
			ClassType ct = (ClassType) r;
			List<InterfaceType> jdiInterfaces = ct.interfaces();
			JdiClassMirror[] interfaces = new JdiClassMirror[jdiInterfaces.size()];
			int i = 0;
			for (InterfaceType it : jdiInterfaces)
				interfaces[i++] = toMirror(it);
			return new JdiClassMirror(r.name(), toMirror(ct.superclass()), interfaces, JdiClassMirror.TYPE_CLASS);
		} else if (r instanceof InterfaceType) {
			InterfaceType ct = (InterfaceType) r;
			List<InterfaceType> jdiInterfaces = ct.superinterfaces();
			JdiClassMirror[] interfaces = new JdiClassMirror[jdiInterfaces.size()];
			int i = 0;
			for (InterfaceType it : jdiInterfaces)
				interfaces[i++] = toMirror(it);
			return new JdiClassMirror(r.name(), null, interfaces, JdiClassMirror.TYPE_INTERFACE);
		} else
			throw new RuntimeException("can't handle: " + r);
	}
}
