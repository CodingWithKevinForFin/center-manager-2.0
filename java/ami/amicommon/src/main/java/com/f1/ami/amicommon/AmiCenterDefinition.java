package com.f1.ami.amicommon;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.f1.base.Password;
import com.f1.container.ContainerTools;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.EncoderUtils;

public class AmiCenterDefinition {
	private final byte id;
	private final String name;
	private final String host;
	private final int port;
	private final String description;
	private final String hostPort;
	private final File sslKeyFile;
	private final byte[] sslKeyData;
	private final Password sslKeyPassword;

	public AmiCenterDefinition(byte id, String name, String host, int port, File sslKeyFile, byte[] sslKeyData, Password sslKeyPassword) {
		super();
		OH.assertGe(id, 0);
		this.id = id;
		this.name = name;
		this.host = host;
		this.port = port;
		this.hostPort = host + ":" + port;
		String s = this.host + ":" + this.port;
		this.description = SH.equals(s, name) ? name : (name + " (" + s + ")");
		this.sslKeyFile = sslKeyFile;
		this.sslKeyData = sslKeyData;
		this.sslKeyPassword = sslKeyPassword;
	}

	@Override
	public String toString() {
		return "Center-" + id + "[" + host + ":" + port + "]" + (isSecure() ? "(secure)" : "(not secure)");
	}
	public AmiCenterDefinition(AmiCenterDefinition def) {
		OH.assertGe(def.id, 0);
		this.id = def.id;
		this.name = def.name;
		this.host = def.host;
		this.port = def.port;
		this.hostPort = def.hostPort;
		this.description = def.description;
		this.sslKeyFile = def.sslKeyFile;
		this.sslKeyData = def.sslKeyData;
		this.sslKeyPassword = def.sslKeyPassword;
	}

	public boolean isSecure() {
		return this.sslKeyData != null || this.sslKeyFile != null;
	}
	public byte getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}

	public boolean isPrimary() {
		return id == 0;
	}

	public MsgDirectConnection newMsgDirectConnection(String name, String topic1, String topic2) {
		MsgDirectConnectionConfiguration config = new MsgDirectConnectionConfiguration(name);
		if (isSecure()) {
			config.setForceSsl(true);
			if (this.sslKeyData != null)
				config.setKeystore(this.sslKeyData, Password.valueFrom(this.sslKeyPassword));
			else
				config.setKeystore(this.sslKeyFile, Password.valueFrom(this.sslKeyPassword));
		}
		MsgDirectTopicConfiguration c1 = new MsgDirectTopicConfiguration(topic1, getHost(), getPort());
		MsgDirectTopicConfiguration c2 = new MsgDirectTopicConfiguration(topic2, getHost(), getPort());
		final MsgDirectConnection connection = new MsgDirectConnection(config);
		connection.addTopic(c1);
		connection.addTopic(c2);
		return connection;
	}

	public static AmiCenterDefinition[] parse(ContainerTools props) {
		String centersString = props.getOptional(AmiCommonProperties.OPTION_AMI_CENTERS, "");

		if (SH.isnt(centersString)) {
			final String amiCenterHost = props.getRequired(AmiCommonProperties.PROPERTY_AMI_CENTER_HOST, String.class);
			final Integer amiCenterPort = props.getRequired(AmiCommonProperties.PROPERTY_AMI_CENTER_PORT, Integer.class);
			centersString = amiCenterHost + ":" + amiCenterPort;
		}
		String[] parts = SH.split(',', centersString);
		AmiCenterDefinition[] r = new AmiCenterDefinition[parts.length];
		if (parts.length > 100)
			throw new RuntimeException("Too many centers, max 100");
		byte id = 0;
		Set<String> names = new HashSet<String>();
		for (String s : parts) {
			s = SH.trim(s);
			String name = SH.trim(SH.beforeFirst(s, '=', s));
			String namespace = AmiCommonProperties.PROPERTY_AMI_CENTER_PREFIX + name;
			String suffix = SH.trim(SH.afterFirst(s, '=', s));
			String host = SH.trim(SH.beforeFirst(suffix, ':', null));
			String port = SH.trim(SH.afterFirst(suffix, ':', null));
			String sslKeyFileName = props.getOptional(namespace + AmiCommonProperties.PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_FILE);
			String sslKeyText = props.getOptional(namespace + AmiCommonProperties.PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_TEXT_BAS64);
			String sslKeyPassword = props.getOptional(namespace + AmiCommonProperties.PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_PASSWORD);
			byte[] sslKeyData;
			File sslKeyFile;
			if (SH.is(sslKeyText)) {
				if (SH.is(sslKeyFileName))
					throw new RuntimeException(namespace + AmiCommonProperties.PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_TEXT_BAS64 + " and " + namespace
							+ AmiCommonProperties.PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_FILE + " are mutually exclusive");
				sslKeyData = EncoderUtils.decodeCert(sslKeyText);
				sslKeyFile = null;
			} else if (SH.is(sslKeyFileName)) {
				sslKeyData = null;
				sslKeyFile = new File(sslKeyFileName);
			} else {
				sslKeyData = null;
				sslKeyFile = null;
			}
			if (SH.startsWith(name, "@@"))
				throw new RuntimeException("Invalid Center name, @@ prefix is reserved: " + name);
			if (SH.isnt(name) || SH.isnt(host) || SH.isnt(port) || !SH.areBetween(port, '0', '9'))
				throw new RuntimeException("Invalid Center name, should be in format host:port or name=host:port ==> " + s);
			if (!names.add(name))
				throw new RuntimeException("Duplicate center name: " + name);
			r[id] = new AmiCenterDefinition(id, name, host, SH.parseInt(port), sslKeyFile, sslKeyData, Password.valueOf(sslKeyPassword));
			id++;
		}
		return r;
	}
	public String getDescription() {
		return this.description;
	}
	public String getHostPort() {
		return hostPort;
	}

}
