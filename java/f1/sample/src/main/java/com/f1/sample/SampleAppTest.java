package com.f1.sample;

public class SampleAppTest {
	/*
	 * @Test public void testMain() { ContainerAppMain cam = new
	 * ContainerAppMain(SampleApp.class, null); // at this point default values
	 * have been set... System.out.println("config dir: " +
	 * cam.getConfigDirProperty());
	 * 
	 * // 3 different ways to override a property, for example these are all
	 * equivalent: cam.setConfigDirProperty("myconfig");
	 * cam.setProperty("f1.conf.dir", "myconfig");
	 * System.setProperty("property.f1.conf.dir", "myconfig");
	 * 
	 * // now that the configuration dir / file have been set, lets init the
	 * properties cam.readProperties();
	 * 
	 * // lets override a setting or two... cam.setLogLevel(Level.FINE,
	 * Level.CONFIG, SampleApp.class); cam.setProperty("myproperty", "myvalue");
	 * 
	 * // how that we are happy with the properties, lets consume them.
	 * cam.processProperties();
	 * 
	 * // now the properties are locked, but we can override other things, like
	 * the converter cam.setConverter(new ObjectToJsonConverter());
	 * 
	 * // all set, now lets startup cam.startup();
	 * 
	 * // create the container Container mycontainer = new BasicContainer();
	 * mycontainer.setName("myContainer");
	 * 
	 * // pass the container through the builder...
	 * cam.prepareContainer(mycontainer);
	 * 
	 * // create and add three processors PartitionResolver<Action>
	 * partitionResolver = new BasicPartitionResolver<Action>(Action.class,
	 * "PARTITION1");
	 * 
	 * SampleChainingProcessor chainingProcessor = new
	 * SampleChainingProcessor(); SampleEndOfChainProcessor endofChainProcessor
	 * = new SampleEndOfChainProcessor(); SampleReplyProcessor replyProcessor =
	 * new SampleReplyProcessor(); SampleResponseProcessor responseProcessor =
	 * new SampleResponseProcessor();
	 * 
	 * Suite rootSuite = mycontainer.getRootSuite();
	 * rootSuite.addChildren(chainingProcessor, endofChainProcessor,
	 * replyProcessor, responseProcessor);
	 * rootSuite.applyPartitionResolver(partitionResolver, true, true);
	 * 
	 * // wire the processors together rootSuite.wire(chainingProcessor.out,
	 * endofChainProcessor, false); rootSuite.wire(chainingProcessor.out2,
	 * replyProcessor, false); rootSuite.wire(chainingProcessor.resultOut,
	 * responseProcessor, false); // Grab a port to send messages into the
	 * chainProcessor. Port<SampleMessage> port =
	 * rootSuite.exposeInputPortAsOutput(chainingProcessor, true);
	 * 
	 * cam.startupContainer(mycontainer);
	 * 
	 * // Send a message into the chaining processor SampleMessage action =
	 * mycontainer.nw(SampleMessage.class); SampleTypeMessage action2 =
	 * mycontainer.nw(SampleTypeMessage.class); boolean bool=false; byte
	 * b=44,bt=18,s=4,in=11,d=24,f=40,c=15,o=38;
	 * 
	 * for(int i=0;i<1000;i++){ bool=!bool; action2.setBoolean(bool); if(i%2==0)
	 * OH.assertTrue(action2.getBoolean()); else
	 * OH.assertFalse(action2.getBoolean()); action2.put("boolean", !bool);
	 * OH.assertEq(!bool,action2.ask("boolean")); // action2.putBoolean(b,
	 * bool); // OH.assertEq(bool, action2.askBoolean(b)); // action2.put(b,
	 * !bool); // OH.assertEq(!bool, action2.ask(b));
	 * 
	 * action2.setByte((byte)i); OH.assertEq((byte)i,action2.getByte());
	 * action2.put("byte",(byte) (i+1));
	 * OH.assertEq((byte)(i+1),action2.ask("byte")); // action2.putByte(bt,
	 * (byte)(i)); // OH.assertEq((byte)(i+1),action2.askByte(bt)); //
	 * action2.put(bt, (byte)(i)); // OH.assertEq((byte)(i+1),action2.ask(bt));
	 * 
	 * action2.setShort((short) i); OH.assertEq((short)i,action2.getShort());
	 * action2.put("short", (short)(i+1));
	 * OH.assertEq((short)(i+1),action2.ask("short")); // action2.putShort(s,
	 * (short)i); // OH.assertEq((short)(i),action2.askShort(s)); //
	 * action2.put(s, (short)(i+1)); //
	 * OH.assertEq((short)(i+1),action2.ask(s));
	 * 
	 * action2.setInt(i); OH.assertEq(i,action2.getInt()); action2.put("int",
	 * i+1); OH.assertEq(i+1,action2.ask("int")); // action2.putInt(in, i); //
	 * OH.assertEq(i,action2.askInt(in)); // action2.put(in, i+1); //
	 * OH.assertEq(i+1,action2.ask(in));
	 * 
	 * action2.setDouble((double) i);
	 * OH.assertEq((double)i,action2.getDouble()); action2.put("double",
	 * (double)(i+1)); OH.assertEq((double)(i+1),action2.ask("double")); //
	 * action2.putDouble(d, (double)i); //
	 * OH.assertEq((double)(i),action2.askDouble(d)); // action2.put(d,
	 * (double)(i+1)); // OH.assertEq((double)(i+1),action2.ask(d));
	 * 
	 * action2.setFloat((float)i); OH.assertEq((float)i,action2.getFloat());
	 * action2.put("float", (float)(i+1));
	 * OH.assertEq((float)(i+1),action2.ask("float")); // action2.putFloat(f,
	 * (float)i); // OH.assertEq((float)(i),action2.askFloat(f)); //
	 * action2.put(f,(float)(i+1)); // OH.assertEq((float)(i+1),action2.ask(f));
	 * 
	 * action2.setChar((char) i); OH.assertEq((char)i,action2.getChar());
	 * action2.put("char", (char)(i+1));
	 * OH.assertEq((char)(i+1),action2.ask("char")); // action2.putChar(c,
	 * (char)i); // OH.assertEq((char)(i),action2.askChar((byte)15)); //
	 * action2.put(c, (char)(i+1)); //
	 * OH.assertEq((char)(i+1),action2.ask((byte)15));
	 * 
	 * action2.setObject(i); OH.assertEq((Object)i,action2.getObject());
	 * action2.put("object", (Object)(i+1));
	 * OH.assertEq((Object)(i+1),action2.ask("object")); //
	 * action2.put(o,(Object)i); //
	 * OH.assertEq((Object)i,action2.ask((byte)38));
	 * 
	 * 
	 * } // Let's create a sample acker and register it with the action // We
	 * don't really need to pass the action into the acker's constructor... but
	 * in some cases it might be useful action.registerAcker(new
	 * SampleAcker(action, "its acked!")); action.setText("Hello world");
	 * port.send(action, null);
	 * 
	 * // Let's keep the app alive. cam.keepAlive(); rec(mycontainer,false);
	 * mycontainer.stop(); rec(mycontainer,true); }
	 * 
	 * public void rec(ContainerScope container, boolean stopped){ if(!stopped)
	 * OH.assertTrue(container.isStarted()); else
	 * OH.assertFalse(container.isStarted());
	 * System.out.println(container.getName() + (!stopped ? " is running" :
	 * " is stopped")); List<ContainerScope> temp=
	 * container.getChildContainerScopes(); for(ContainerScope cs : temp){
	 * rec(cs,stopped); } }
	 */

}
