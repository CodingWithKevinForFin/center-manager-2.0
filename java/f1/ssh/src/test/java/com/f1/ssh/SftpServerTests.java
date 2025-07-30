package com.f1.ssh;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Session;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.auth.UserAuthPublicKey;
import org.apache.sshd.server.auth.UserAuthPublicKey.Factory;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;

import com.f1.utils.CH;
import com.f1.utils.OH;

public class SftpServerTests implements PasswordAuthenticator, FileSystemFactory {
	static {
		System.out.println("asdf");
	}

	public static void main(String a[]) throws IOException {
		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(2222);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshd.setCommandFactory(new ScpCommandFactory());
		sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>> asList(new SftpSubsystem.Factory()));
		List<Factory> userAuthFactories = Arrays.asList(new UserAuthPublicKey.Factory());
		sshd.setUserAuthFactories((List) CH.l(new UserAuthPublicKey.Factory(), new UserAuthPassword.Factory()));
		sshd.setPasswordAuthenticator(new SftpServerTests());
		sshd.setFileSystemFactory(new SftpServerTests());
		sshd.start();
		while (true)
			OH.sleep(1000);
	}
	@Override
	public boolean authenticate(String username, String password, ServerSession session) {
		System.out.println("user:" + username);
		System.out.println("password:" + password);
		return true;
	}
	@Override
	public FileSystemView createFileSystemView(Session session) throws IOException {
		System.out.println("creating session");
		NativeFileSystemView r = new NativeFileSystemView(session.getUsername());
		r.setCurrDir("/tmp");
		return r;
	}
}
