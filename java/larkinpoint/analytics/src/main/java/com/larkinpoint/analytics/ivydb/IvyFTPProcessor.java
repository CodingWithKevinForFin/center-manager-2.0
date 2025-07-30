package com.larkinpoint.analytics.ivydb;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.f1.base.Action;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.Suite;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.ParamRoutingProcessor;
import com.f1.utils.BasicDay;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.ftp.client.FtpClient;
import com.f1.utils.ftp.client.FtpConstants;
import com.f1.utils.ftp.client.FtpUtils;
import com.larkinpoint.analytics.LarkinPointState;
import com.larkinpoint.messages.GetAvailableIvyFiles;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateRequest;
import com.larkinpoint.messages.LoadFileMessage;

public class IvyFTPProcessor extends BasicProcessor<GetAvailableIvyFiles, LarkinPointState> {

	private static OutputPort<LoadFileMessage> output;

	public IvyFTPProcessor() {
		super(GetAvailableIvyFiles.class, LarkinPointState.class);
	}

	@Override
	public void processAction(GetAvailableIvyFiles action,
			LarkinPointState state, ThreadScope threadScope) throws Exception {
		// TODO Auto-generated method stub
		final String host = getServices().getPropertyController().getRequired("data.ftp.host");
		final int port = getServices().getPropertyController().getOptional("data.ftp.port", FtpConstants.FTP_PORT);
		final String user = getServices().getPropertyController().getRequired("data.ftp.user");
		final String pass = getServices().getPropertyController().getRequired("data.ftp.pass");
		final String remoteFilesMask = getServices().getPropertyController().getOptional("data.ftp.remote.filesmask", "*");
		final String remoteDirectory = getServices().getPropertyController().getOptional("data.ftp.remote.path");
		final File localDirectory = getServices().getPropertyController().getRequired("data.local.dir", File.class);
		IOH.ensureDir(localDirectory);

		final FtpClient client = new FtpClient(host, port, user, pass);
		final List<File> changedFiles = new ArrayList<File>();
		FtpUtils.syncFilesFrom(client, remoteDirectory, SH.m(remoteFilesMask), localDirectory, FtpUtils.SYNC_OPTION_RECURSE, changedFiles);
		IOH.close(client);

		//Suite rs = getServices().getContainer().getRootSuite();
		//ParamRoutingProcessor router = (ParamRoutingProcessor) rs.getChild("ParamRoutingProcessor");
		//OutputPort<Action> output = rs.exposeInputPortAsOutput(router, true);
		
		System.out.println("Done with ftp sync from: " + host + ":" + port);
		for (File file : changedFiles) {
			if( file.getPath().contains("Tick")){
				System.out.println("Skipping " + file);
				continue;
			}
			System.out.println("Need to process new file: " + file);
			List<File> files = IOH.expandFile(file, localDirectory, true);
			for ( File f : files ){
				if (f.getName().contains("IVYOPPRC")  ){
					
					LoadFileMessage msg = nw(LoadFileMessage.class);
					msg.setLoadFilename(f.getName());
					msg.setLoadFiletype(LoadFileMessage.TYPE_DAILY_OPTION);
					
					output.send(msg, null);
				
				} else if (f.getName().contains("IVYSECPR") ){
					
					LoadFileMessage msg = nw(LoadFileMessage.class);
					msg.setLoadFilename(f.getName());
					msg.setLoadFiletype(LoadFileMessage.TYPE_SECURITY_PRICES);
					
					output.send(msg, null);
				} 
			}
			//I'm not sure what order the files will be processed or if there will be multiple days worth so process the
			//normal daily files first and then apply any patches.
			for ( File f : files ){
				if (f.getName().contains("ptcopprc")  ){
					
					LoadFileMessage msg = nw(LoadFileMessage.class);
					msg.setLoadFilename(f.getName());
					msg.setLoadFiletype(LoadFileMessage.TYPE_DAILY_OPTION);
					
					output.send(msg, null);
				
				} else if (f.getName().contains("ptcsecpr") ){
					
					LoadFileMessage msg = nw(LoadFileMessage.class);
					msg.setLoadFilename(f.getName());
					msg.setLoadFiletype(LoadFileMessage.TYPE_SECURITY_PRICES);
					
					output.send(msg, null);
				} 
			}
		}
		//Make sure that subsequent requests for Underlying data will read the new data
		state.getOptionDataRoot().refreshAll();
		
	}
	@Override
	public void init() {
		super.init();
//		basePath = getServices().getPropertyController().getRequired("options.directory", File.class);
	//	skipFirstRow = getServices().getPropertyController().getOptional("options.skipfirstrow", false);
		
		

	}

	public static OutputPort<LoadFileMessage> getOutput() {
		return output;
	}

	public static void setOutput(OutputPort<LoadFileMessage> output1) {
		IvyFTPProcessor.output = output1;
	}


}
