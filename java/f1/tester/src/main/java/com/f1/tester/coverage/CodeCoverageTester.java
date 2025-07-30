package com.f1.tester.coverage;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import com.f1.tester.JdiClassFinder;
import com.f1.tester.Tester;
import com.f1.tester.TesterListener;
import com.f1.utils.CH;
import com.f1.utils.ClassFinder;
import com.f1.utils.ClassFinderEntry;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.StreamPiper;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;
import com.f1.utils.structs.Tuple2;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;

public class CodeCoverageTester implements Tester, Runnable {

	private String main;
	private List<String> vmOptions = new ArrayList<String>();
	private List<String> vmArguments = new ArrayList<String>();
	private String classPath;
	private OutputStream stdout = System.out;
	private OutputStream stderr = System.err;
	private PrintStream commentout = System.err;
	private Set<String> skipInterfaces = new HashSet<String>();
	private boolean startShouldBlock;
	private long maxRuntime;
	private VirtualMachine vm;
	private Process process;
	private EventRequestManager eventRequestManager;
	private Map<Location, Tuple2<BreakpointRequest, CodeCoverageLine>> line2request = new HashMap<Location, Tuple2<BreakpointRequest, CodeCoverageLine>>();
	private Thread shutdownThread;
	private CodeCoverageResults results = new CodeCoverageResults();
	private Thread pumpThread;
	private long maxEndTime;
	private LaunchingConnector connector;
	private OutputStream stdin;
	private Set<String> targetClasses = null;

	private List<String> searchPaths = new ArrayList<String>();

	public void addSearchPaths(String... searchPath) {
		CH.l(this.searchPaths, searchPath);
	}

	public void addFilterOutBaseClasses(String... filters) {
		CH.s(this.skipInterfaces, filters);
	}

	public void prepareTargetClasses() {
		try {
			targetClasses = new LinkedHashSet<String>();
			ClassFinder cf = new ClassFinder().searchClasspath(ClassFinder.TYPE_ALL);
			Set<ClassFinderEntry> allEntries = new HashSet<ClassFinderEntry>();
			for (String searchPath : searchPaths)
				allEntries.addAll(cf.filterByPackage(searchPath).getEntries());
			cf = JdiClassFinder.toJdiClasses(new ClassFinder(allEntries), System.out, System.out);
			for (String filterOut : skipInterfaces)
				cf = cf.filterOutExtends(ReflectedClassMirror.valueOf(Class.forName(filterOut)));
			for (ClassMirror cm : cf.getClassMirrors())
				targetClasses.add(cm.getName());
		} catch (Exception e) {
			OH.toRuntime(e);
		}
	}

	public CodeCoverageResults getResults() {
		return results;
	}

	public CodeCoverageTester() {
		connector = Bootstrap.virtualMachineManager().defaultConnector();
	}

	public void setSourcePath(String sourcePath) {
		this.results.setSourcePath(sourcePath);
	}

	@Override
	public void setMain(String main) {
		if (SH.startsWith(main, '-'))
			throw new IllegalArgumentException(main);
		assertNotRunning();
		this.main = main;
	}

	@Override
	public void addVmOptions(String... vmOption) {
		for (String t : vmOption)
			if (t.indexOf("-cp") != -1 || t.indexOf("-classpath") != -1)
				throw new IllegalArgumentException("use setClassPath(...) for " + t);
		assertNotRunning();
		CH.l(vmOptions, vmOption);
	}

	@Override
	public void addArguments(String... vmArgument) {
		assertNotRunning();
		CH.l(vmArguments, vmArgument);
	}

	@Override
	public void setClassPath(String classPath) {
		assertNotRunning();
		this.classPath = classPath;
	}

	@Override
	public void setStdOut(OutputStream stdout) {
		assertNotRunning();
		this.stdout = stdout;
	}

	@Override
	public void setStdErr(OutputStream stderr) {
		assertNotRunning();
		this.stderr = stderr;
	}

	@Override
	public void setStartShouldBlock(boolean should) {
		assertNotRunning();
		this.startShouldBlock = should;
	}

	@Override
	public void setMaxRuntime(long maxRuntime, TimeUnit timeUnit) {
		assertNotRunning();
		this.maxRuntime = timeUnit.toMillis(maxRuntime);
	}

	@Override
	public void startTest() {
		if (targetClasses == null)
			prepareTargetClasses();
		assertNotRunning();
		try {
			// loadClasses();

			synchronized (this) {
				// Prepare Arguments for VM
				Map<String, Argument> args = connector.defaultArguments();
				System.out.println(args);
				CH.getOrThrow(args, "main").setValue(main + " " + SH.join(' ', vmArguments));
				CH.getOrThrow(args, "suspend").setValue("true");
				final String options = "-cp " + getClassPath() + " " + SH.join(" ", vmOptions);
				CH.getOrThrow(args, "options").setValue(options);
				comment("VM OPTIONS: " + args.values());

				// Prepare VM
				Runtime.getRuntime().addShutdownHook(shutdownThread = new ShutdownThread());
				vm = connector.launch(args);
				process = vm.process();
				new Thread(new StreamPiper(process.getInputStream(), stdout, 1024)).start();
				new Thread(new StreamPiper(process.getErrorStream(), stderr, 1024)).start();
				stdin = process.getOutputStream();

				// Add listener for new classes and prepare existing classes
				eventRequestManager = vm.eventRequestManager();
				ClassPrepareRequest classRequest = eventRequestManager.createClassPrepareRequest();
				classRequest.enable();
				for (ReferenceType rt : vm.allClasses())
					onClassPrepare(rt);

				// Start the VM
				if (maxRuntime != 0)
					maxEndTime = EH.currentTimeMillis() + maxRuntime;
				else
					maxEndTime = 0;
				if (startShouldBlock)
					run();
				else
					new Thread(this).start();
			}

		} catch (Exception e) {
			safeDisposeVm();
			throw OH.toRuntime(e);
		}
	}

	private String getClassPath() {
		if (classPath == null)
			return SH.join(File.pathSeparator, EH.getJavaClassPath());
		return classPath;
	}

	@Override
	public void run() {
		try {
			synchronized (this) {
				for (TesterListener listener : listeners)
					listener.onStarted(this);
			}

			pumpEvents();
		} catch (Throwable e) {
			e.printStackTrace(commentout);
		}
	}

	private void pumpEvents() {
		pumpThread = Thread.currentThread();
		try {
			while (!pumpThread.isInterrupted() && pumpEvent() && !endTimeReached());
			List<ReferenceType> classes;
		} finally {
			safeDisposeVm();
			pumpThread = null;
			Thread.interrupted();// clear
		}
	}

	private boolean endTimeReached() {
		return maxEndTime != 0 && maxEndTime < EH.currentTimeMillis();
	}

	private void safeDisposeVm() {
		synchronized (this) {

			if (vm != null)
				try {
					vm.exit(1);
				} catch (Exception e2) {
				}
			this.vm = null;
			this.eventRequestManager = null;
			this.process = null;
			this.line2request.clear();
			if (this.shutdownThread != null)
				try {
					Runtime.getRuntime().removeShutdownHook(this.shutdownThread);
				} catch (Exception e) {
				}
			this.shutdownThread = null;
			for (TesterListener listener : listeners)
				listener.onStarted(this);
		}
	}

	private boolean pumpEvent()// return true=keep running
	{
		EventSet eventSet;
		try {
			eventSet = vm.eventQueue().remove(1000);
			if (eventSet == null)
				return true;
		} catch (InterruptedException e) {
			return false;
		}
		for (Event e : eventSet) {
			if (e instanceof BreakpointEvent)
				onBreakPointEvent((BreakpointEvent) e);
			else if (e instanceof ClassPrepareEvent)
				onClassPrepare(((ClassPrepareEvent) e).referenceType());
			else if (e instanceof VMDisconnectEvent || e instanceof VMDeathEvent)
				return false;
			else
				comment("Unknown Event=" + e);
			vm.resume();
		}
		return true;
	}

	private void onBreakPointEvent(BreakpointEvent e) {
		Location location = e.location();
		Tuple2<BreakpointRequest, CodeCoverageLine> tuple = line2request.remove(location);
		if (tuple == null) {
			comment("Unknown line: " + location);
			return;
		}
		tuple.getB().setCovered(true);
		this.eventRequestManager.deleteEventRequest(tuple.getA());
	}

	private String getSource(Location l) {
		return null;
	}

	private void onClassPrepare(ReferenceType rt) {
		try {
			if (!(rt instanceof ClassType))
				return;
			ClassType ct = (ClassType) rt;
			String className = ct.classObject().reflectedType().name();
			if (results.getClass(className) == null) {
				if (!targetClasses.contains(className))
					return;
			}
			CodeCoverageClass ccClass = results.getClass(className);
			if (ccClass == null) {
				for (ClassType c = ct; c != null; c = c.superclass())
					for (InterfaceType i : c.allInterfaces()) {
						String interfaceName = i.classObject().reflectedType().name();
						if (skipInterfaces.contains(interfaceName)) {
							ccClass = new CodeCoverageClass(className, getSourcePath(ct), ct.allLineLocations().get(0).lineNumber());
							results.addClass(ccClass);
							return;
						}
					}
				ccClass = new CodeCoverageClass(className, getSourcePath(ct), ct.allLineLocations().get(0).lineNumber());
				for (Method m : ct.methods()) {
					try {
						if (m.isAbstract())
							continue;
						String name = m.returnTypeName() + " " + m.name() + "(" + SH.join(',', m.argumentTypeNames()) + ")";
						CodeCoverageMethod ccMethod = new CodeCoverageMethod(name, m.location().lineNumber());
						for (Location line : m.allLineLocations())
							ccMethod.addLine(new CodeCoverageLine(line.lineNumber()));
						ccClass.addMethod(ccMethod);
					} catch (Exception e) {
						throw new RuntimeException("error with method: " + m, e);
					}
				}
				results.addClass(ccClass);
			}
			for (Method m : ct.methods())
				for (Location line : m.allLineLocations()) {
					CodeCoverageLine ccLine = ccClass.getLineAt(line.lineNumber());
					if (ccLine.getStatus() != CodeCoverageClass.COVERED) {
						BreakpointRequest request = this.eventRequestManager.createBreakpointRequest(line);
						line2request.put(line, new Tuple2<BreakpointRequest, CodeCoverageLine>(request, ccLine));
						request.enable();
					}
				}

		} catch (Exception e) {
			throw new RuntimeException("error with class: " + rt, e);
		}
	}

	private String getSourcePath(ClassType ct) {
		try {
			return ct.sourcePaths(null).get(0);
		} catch (Exception e) {
			return null;
		}
	}

	private List<TesterListener> listeners = new CopyOnWriteArrayList<TesterListener>();

	private void comment(String comment) {
		commentout.println("#### " + comment);
	}

	@Override
	public void stopTest() {
		assertRunning();
		Thread t = pumpThread;
		if (t == null)
			return; // maybe it just existed on its own
		t.interrupt();
		while (pumpThread == t)
			OH.sleep(100);
	}

	@Override
	public boolean isRunning() {
		return pumpThread != null;
	}

	private void assertNotRunning() {
		if (isRunning())
			throw new IllegalStateException("already running");
	}

	private void assertRunning() {
		if (!isRunning())
			throw new IllegalStateException("not running");
	}

	public class ShutdownThread extends Thread {

		ShutdownThread() {
			super("CodeCoverageTester.ShutdownThread");
		}

		@Override
		public void run() {
			System.err.println("SHUTTING DOWN INNER JVM FROM SHUTDOWN HOOK");
			safeDisposeVm();
		}
	}

	@Override
	public void addListener(TesterListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(TesterListener listener) {
		listeners.remove(listener);
	};
}
