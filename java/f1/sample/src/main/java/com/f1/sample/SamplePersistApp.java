package com.f1.sample;

import java.io.File;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.OutputPort;
import com.f1.container.Partition;
import com.f1.container.PartitionResolver;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.StateGenerator;
import com.f1.container.Suite;
import com.f1.container.impl.AbstractContainerScope;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.BasicState;
import com.f1.containerpersist.ContainerPersister;
import com.f1.povo.standard.TextMessage;
import com.f1.speedlogger.impl.PrintStreamToSpeedLogger;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

// 1. Use the following options to VM arguments: 
//
//-Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager
//-Dproperty.f1.console.port=3333
//
// 2. For testing via the console, ensure putty is in your path and run:
//
// putty localhost 3333
//
// 3. in the putty session run, noticing that each time the number returned is incremented by 1 (see MySampleClass.java usage for details):
//
// call sample.showNext();
// call sample.showNext();
// call sample.showNext();
// call sample.showNext();
//
// 4. To see the processors that can be accessed:
//
// call container.showProcessors()
//
// 5. To call the SampleStateProcessor
//
// call container.callProcessor("SampleStateProcessor",1,"text","14");
// call container.callProcessor("SampleStateProcessor",1,"text","14");
// call container.callProcessor("SampleStateProcessor",1,"text","14");
//
// Notice that the value printed to stdout will be incremented by 14 each time (due to the param passed in).

public class SamplePersistApp extends AbstractContainerScope implements StateGenerator<Action, State> {

	public static void main(String... args) throws Exception {
		ContainerBootstrap cam = new ContainerBootstrap(SamplePersistApp.class, args);
		// at this point default values have been set...
		System.out.println("config dir: " + cam.getConfigDirProperty());

		// 3 different ways to override a property, for example these are all
		// equivalent:
		cam.setConfigDirProperty("myconfig");
		cam.setProperty("f1.conf.dir", "myconfig");
		System.setProperty("property.f1.conf.dir", "myconfig");

		PrintStreamToSpeedLogger.init();

		// now that the configuration dir / file have been set, lets init the
		// properties
		cam.readProperties();

		// lets override a setting or two...
		cam.setLogLevel(Level.FINE, Level.CONFIG, SamplePersistApp.class);
		cam.setProperty("myproperty", "myvalue");

		// how that we are happy with the properties, lets consume them.
		cam.processProperties();

		// now the properties are locked, but we can override other things, like
		// the converter
		cam.setConverter(new ObjectToJsonConverter());

		// all set, now lets startup
		cam.startup();

		// create the container
		Container mycontainer = new BasicContainer();
		mycontainer.setName("myContainer");

		// pass the container through the builder...
		cam.prepareContainer(mycontainer);

		// create and add three processors
		PartitionResolver<SampleMessage> partitionResolver = new BasicPartitionResolver<SampleMessage>(SampleMessage.class, "PARTITION1");
		Suite rootSuite = mycontainer.getRootSuite();

		rootSuite.applyPartitionResolver(partitionResolver, true, true);
		mycontainer.getPartitionController().registerStateGenerator(new SamplePersistApp());

		// wire the processors together

		// Sample for new state
		rootSuite.addChild(new SampleStateProcessor());
		mycontainer.getPartitionController().registerStateGenerator(new SampleStateGenerator());

		PersistProcessor persistProcessor = new PersistProcessor();
		IncAccessProcessor incProcessor = new IncAccessProcessor();
		// Grab a port to send messages into the chainProcessor.
		rootSuite.addChild(persistProcessor);
		rootSuite.addChild(incProcessor);
		OutputPort<Action> port = rootSuite.exposeInputPortAsOutput(persistProcessor, true);
		OutputPort<TextMessage> port2 = rootSuite.exposeInputPortAsOutput(incProcessor, true);

		ContainerPersister cp = new ContainerPersister(mycontainer.getPersistenceController());
		IOH.ensureDir(new File("data"));
		cp.addFileReplication(new File("data/persist"), false, 1024 * 1024);

		cam.registerConsoleObject("sample", new MySampleClass());
		cam.startupContainer(mycontainer);
		System.out.println(mycontainer.isStarted());

		// Send a message into the chaining processor
		SampleMessage action = mycontainer.nw(SampleMessage.class);

		// Let's create a sample acker and register it with the action
		// We don't really need to pass the action into the acker's
		// constructor... but in some cases it might be useful
		action.registerAcker(new SampleAcker(action, "its acked!"));
		action.setText("Hello world");
		port.send(action, null);

		// Let's keep the app alive.
		OH.sleep(100);
		cam.keepAlive(false);
		for (Object id : mycontainer.getPartitionController().getPartitions()) {
			TextMessage a = mycontainer.nw(TextMessage.class);
			a.setText(id.toString());
			port2.send(a, null);
		}
	}

	@Override
	public State createState(Partition partition, Action a, Processor<? extends Action, ?> processor) {
		State state = nw(BasicState.class);
		SampleAccount account = nw(SampleAccount.class);
		state.setPersistedRoot(account);
		return state;
	}

	@Override
	public Class getStateType() {
		return State.class;
	}
}
