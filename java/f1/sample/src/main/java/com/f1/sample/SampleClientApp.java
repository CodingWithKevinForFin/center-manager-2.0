package com.f1.sample;

import java.util.logging.Level;

import com.f1.base.Message;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.msg.MsgConnectionConfiguration;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.povo.standard.TextMessage;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.GuidHelper;
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

public class SampleClientApp {

	public static void main(String... args) {
		ContainerBootstrap cam = new ContainerBootstrap(SampleClientApp.class, args);
		// at this point default values have been set...
		System.out.println("config dir: " + cam.getConfigDirProperty());

		// 3 different ways to override a property, for example these are all
		// equivalent:
		cam.setConfigDirProperty("myconfig");
		cam.setProperty("f1.conf.dir", "myconfig");
		System.setProperty("property.f1.conf.dir", "myconfig");

		// now that the configuration dir / file have been set, lets init the
		// properties
		cam.readProperties();

		// lets override a setting or two...
		cam.setLogLevel(Level.FINE, Level.CONFIG, SampleClientApp.class);
		cam.setProperty("myproperty", "myvalue");

		// how that we are happy with the properties, lets consume them.
		cam.processProperties();

		// now the properties are locked, but we can override other things, like
		// the converter
		cam.setConverter(new ObjectToJsonConverter());
		cam.registerMessages(TextMessage.class);

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

		MsgConnectionConfiguration config = new BasicMsgConnectionConfiguration("connection1");
		MsgDirectConnection connection = new MsgDirectConnection(config);
		connection.addTopic(new MsgDirectTopicConfiguration("channel1", "localhost", 4567, "channel1"));
		connection.addTopic(new MsgDirectTopicConfiguration("channel2", "localhost", 4567, "channel2"));
		String suffix = GuidHelper.getGuid();
		MsgSuite msgSuite = new MsgSuite("partition1", connection, "channel2", "channel1", suffix);
		rootSuite.addChildren(msgSuite);
		RequestOutputPort<Message, Message> port = rootSuite.exposeInputPortAsOutput(msgSuite.getOutboundRequestInputPort(), true);
		connection.init();

		// wire the processors together

		// Sample for new state
		rootSuite.addChild(new SampleStateProcessor());
		mycontainer.getPartitionController().registerStateGenerator(new SampleStateGenerator());
		cam.startupContainer(mycontainer);

		// Let's keep the app alive.
		cam.keepAlive();
		int i = 0;
		while (true) {
			TextMessage tm = mycontainer.nw(TextMessage.class);
			tm.setText("what " + (i++));
			ResultActionFuture<Message> result = port.requestWithFuture(tm, null);
			System.out.println("The result is: " + ((TextMessage) result.getResult().getAction()).getText());
			OH.sleep(100);

		}
	}

}
