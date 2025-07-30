package com.f1.ami.relay.fh.aeron;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;

public class AmiAeronFH extends AmiFHBase{
	
	private static final Logger log = LH.get();
	
	private static final String PROP_CHANNEL = "channel";
	private static final String PROP_STREAM_ID = "stream.id";
	private static final String PROP_TABLE_NAME = "tablename";
	private static final String PROP_DEBUG_ENABLED = "debug.enabled";
	private static final String PROP_FRAGMENT_COUNT_LIMIT = "fragment.count.limit";
	private static final String PROP_MSG_PARSER_CLASS = "message.parser.class";
	
	private Boolean debugEnabled;
	private String tableName;
	private AmiAeronMessageParser msgParser;
	
	//Aeron objects
	private MediaDriver driver;
	private Subscription subscription;
	private FragmentHandler fragmentHandler;
	private AtomicBoolean running;
	private int fragmentCountLimit;
	
	public AmiAeronFH() {}
	
	@Override
	public void onCenterConnected(String centerId) {
		super.onCenterConnected(centerId);
		if (this.debugEnabled)
			LH.info(log, "Center ", centerId, " is ready. Starting Aeron consumption.");
		new Thread(new Connector(), "Aeron Connector").start();
	}
		
	@Override
	public void start() {	
		super.start();
	}
		
	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		this.tableName = props.getRequired(PROP_TABLE_NAME);
		final String msgParserClass = props.getRequired(PROP_MSG_PARSER_CLASS);
		try {
			@SuppressWarnings("unchecked")
			Class<AmiAeronMessageParser> parserClass = (Class<AmiAeronMessageParser>) Class.forName(msgParserClass);
			this.msgParser = parserClass.newInstance();
			this.msgParser.init(props);
		} catch (Exception e) {
			LH.warning(log, "ERROR: Problem faced with loading parser class: " + e.getMessage());
			return;
		}
		final String channel = props.getRequired(PROP_CHANNEL);
		final Integer streamID = Integer.parseInt(props.getRequired(PROP_STREAM_ID));
		this.debugEnabled = props.getOptional(PROP_DEBUG_ENABLED, false);
		if (this.debugEnabled)
			LH.info(log, "Debug mode enabled for Aeron Feedhandler");
		
		this.fragmentCountLimit = Integer.parseInt(props.getOptional(PROP_FRAGMENT_COUNT_LIMIT, "10"));
		
		LH.info(log, "Subscribing to " + channel + " on stream id " + streamID + " for table " + this.tableName);
		
		this.fragmentHandler = new AmiAeronFragmentHandler();
        this.running = new AtomicBoolean(true);
		
		this.driver = MediaDriver.launchEmbedded();
        final Aeron.Context ctx = new Aeron.Context()
            .availableImageHandler(AmiAeronHelper::printAvailableImage)
            .unavailableImageHandler(AmiAeronHelper::printUnavailableImage);
		
        ctx.aeronDirectoryName(driver.aeronDirectoryName());
        
        final Aeron aeron = Aeron.connect(ctx);
	    this.subscription = aeron.addSubscription(channel, streamID);
		
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}
	
	public class AmiAeronFragmentHandler implements FragmentHandler{
		@Override
		public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
			if (AmiAeronFH.this.debugEnabled)
	            LH.info(log, "MSG RECEIVED");
			AmiRelayMapToBytesConverter converter = AmiAeronFH.this.msgParser.parseMessage(buffer, offset, length, header);
            publishObjectToAmi(-1, null, AmiAeronFH.this.tableName, 0, converter.toBytes());
		}
	}
		
	public class Connector implements Runnable {
		@Override
		public void run() {
            AmiAeronHelper.subscriberLoop(fragmentHandler, AmiAeronFH.this.fragmentCountLimit, running).accept(AmiAeronFH.this.subscription);
            LH.info(log, "Shutting down Aeron FH...");
            
            CloseHelper.close(AmiAeronFH.this.driver);
		}
	}
}
