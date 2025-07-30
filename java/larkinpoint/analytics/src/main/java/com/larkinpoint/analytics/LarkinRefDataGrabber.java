package com.larkinpoint.analytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.utils.IOH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ftp.client.FtpClient;
import com.f1.utils.ftp.client.FtpConstants;
import com.f1.utils.ftp.client.FtpUtils;

public class LarkinRefDataGrabber {

	public static void main(String args[]) throws IOException {
		final ContainerBootstrap bs = new ContainerBootstrap(LarkinRefDataGrabber.class, args);
		bs.setConfigDirProperty("src/main/config");

		final PropertyController props = bs.getProperties();
		final String host = props.getRequired("data.ftp.host");
		final int port = props.getOptional("data.ftp.port", FtpConstants.FTP_PORT);
		final String user = props.getRequired("data.ftp.user");
		final String pass = props.getRequired("data.ftp.pass");
		final String remoteFilesMask = props.getOptional("data.ftp.remote.filesmask", "*");
		final String remoteDirectory = props.getOptional("data.ftp.remote.path");
		final File localDirectory = props.getRequired("data.local.dir", File.class);

		IOH.ensureDir(localDirectory);

		final FtpClient client = new FtpClient(host, port, user, pass);
		final List<File> changedFiles = new ArrayList<File>();
		FtpUtils.syncFilesFrom(client, remoteDirectory, SH.m(remoteFilesMask), localDirectory, FtpUtils.SYNC_OPTION_RECURSE, changedFiles);
		IOH.close(client);

		System.err.println("Done with ftp sync from: " + host + ":" + port);
		for (File file : changedFiles) {
			System.err.println("Need to process new file: " + file);

			List<File> files = IOH.expandFile(file, localDirectory, true);
		}
	}
}
