package com.vortex.sso;

import java.io.File;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.base.ObjectGenerator;
import com.f1.base.Password;
import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.OutputPort;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.email.EmailClient;
import com.f1.email.EmailClientConfig;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.CH;
import com.f1.utils.DBH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.db.Database;
import com.f1.utils.db.DbService;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.DbBackedIdGenerator;
import com.sso.messages.CreateSsoGroupRequest;
import com.sso.messages.CreateSsoGroupResponse;
import com.sso.messages.CreateSsoUserRequest;
import com.sso.messages.CreateSsoUserResponse;
import com.sso.messages.LoginSsoUserRequest;
import com.sso.messages.LoginSsoUserResponse;
import com.sso.messages.QuerySsoEventsRequest;
import com.sso.messages.QuerySsoEventsResponse;
import com.sso.messages.QuerySsoGroupRequest;
import com.sso.messages.QuerySsoGroupResponse;
import com.sso.messages.QuerySsoHistoryRequest;
import com.sso.messages.QuerySsoHistoryResponse;
import com.sso.messages.QuerySsoUserRequest;
import com.sso.messages.QuerySsoUserResponse;
import com.sso.messages.ResetPasswordRequest;
import com.sso.messages.ResetPasswordResponse;
import com.sso.messages.SendEmailToSsoUserRequest;
import com.sso.messages.SendEmailToSsoUserResponse;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupMemberRequest;
import com.sso.messages.UpdateSsoGroupMemberResponse;
import com.sso.messages.UpdateSsoGroupRequest;
import com.sso.messages.UpdateSsoGroupResponse;
import com.sso.messages.UpdateSsoUserRequest;
import com.sso.messages.UpdateSsoUserResponse;

public class SsoMain {

	private static final Logger log = Logger.getLogger(SsoMain.class.getName());

	public static final String OPTION_SQL_DIR = "sql.dir";
	public static final String OPTION_DBURL = "ssodb.url";
	public static final String OPTION_DBPASSWORD = "ssodb.password";
	public static final String OPTION_IDFOUNTAIN_BATCHSIZE = "idfountan.batchsize";
	private static final String OPTION_EMAIL_HOST = "email.host";
	private static final String OPTION_EMAIL_PORT = "email.port";
	private static final String OPTION_EMAIL_USER = "email.user";
	private static final String OPTION_EMAIL_PASS = "email.password";
	public static final String OPTION_KEYSTORE_FILE = "keystore.file";
	public static final String OPTION_KEYSTORE_PASS = "keystore.password";
	private static final String OPTION_SSL_PORT = "sso.ssl.port";

	public static void main(String a[]) throws Exception {
		ContainerBootstrap bs = new ContainerBootstrap(SsoMain.class, a);
		bs.setConfigDirProperty("./src/main/config");
		bs.setLoggingOverrideProperty("info");
		bs.startup();
		bs.registerMessagesInPackages("com.sso.messages");
		PropertyController props = bs.getProperties();

		final BasicContainer container = new BasicContainer();
		final int port = props.getRequired("sso.port", Integer.class);
		final int broadcastPort = props.getRequired("sso.broadcastport", Integer.class);
		final String dburl = props.getOptional(OPTION_DBURL);
		final Database dbsource;
		SsoDbService dbservice;
		if (SH.isnt(dburl)) {
			dbsource = null;
			dbservice = null;
			System.out.println("No db specified, limited funcionality.  Please specify property: " + OPTION_DBURL);
		} else {
			final String password = props.getRequired(OPTION_DBPASSWORD);
			final File sqlDir = props.getRequired(OPTION_SQL_DIR, File.class);
			dbsource = DBH.createPooledDataSource(dburl, password);
			container.getServices().addDatabase("SSO", dbsource);
			dbservice = new SsoDbService(dbsource, bs.getCodeGenerator(), container.getServices());
			dbservice.add(sqlDir, ".sql");
			container.getServices().putService("DB", dbservice);
		}

		String emailHost = props.getOptional(OPTION_EMAIL_HOST);
		if (SH.isnt(emailHost)) {
			System.out.println("No email server specified.  Not sending email notifications. Please specify property: " + OPTION_EMAIL_HOST);
		} else {
			EmailClientConfig config = new EmailClientConfig();
			config.setHost(emailHost);
			config.setPort(props.getRequired(OPTION_EMAIL_PORT, Integer.class));
			config.setEnableAuthentication(true);
			config.setEnableStartTLS(true);
			config.setEnableSSL(true);
			//config.setUsername("test@3forge.com");
			//config.setPassword("Simple1423");
			config.setUsername(props.getRequired(OPTION_EMAIL_USER));
			config.setPassword(Password.valueOf(props.getRequired(OPTION_EMAIL_PASS)));
			final EmailClient emailClient = new EmailClient(config);
			container.getServices().putService("EMAIL", emailClient);
		}

		MsgDirectConnectionConfiguration config = new MsgDirectConnectionConfiguration("name");
		MsgDirectTopicConfiguration ssoToClientConf = new MsgDirectTopicConfiguration("sso.to.client", port);
		MsgDirectTopicConfiguration clientToSsoConf = new MsgDirectTopicConfiguration("client.to.sso", port);
		MsgDirectTopicConfiguration ssoToBroadcastConf = new MsgDirectTopicConfiguration("sso.to.broadcast", port);
		final String store = props.getOptional(OPTION_KEYSTORE_FILE);
		if (store != null) {
			final int sslPort = props.getRequired(OPTION_SSL_PORT, Integer.class);
			final String pass = props.getRequired(OPTION_KEYSTORE_PASS);
			config.setKeystore(new File(store), pass);
			ssoToBroadcastConf.setSslPort(sslPort);
			ssoToClientConf.setSslPort(sslPort);
			clientToSsoConf.setSslPort(sslPort);
		}

		MsgDirectConnection connection = new MsgDirectConnection(config);
		connection.addTopic(ssoToClientConf);
		connection.addTopic(clientToSsoConf);
		connection.addTopic(ssoToBroadcastConf);
		String uniqueSuffix = Bootstrap.getProcessUid();
		bs.prepareContainer(container);
		MsgSuite msgSuite = new MsgSuite("MSG", connection, "client.to.sso", "sso.to.client", uniqueSuffix);
		MsgSuite broadcastSuite = new MsgSuite("BDCAST", connection, null, "sso.to.broadcast", uniqueSuffix);
		Suite rs = container.getRootSuite();

		CreateSsoUserProcessor createUserProcessor = new CreateSsoUserProcessor();
		CreateSsoGroupProcessor createGroupProcessor = new CreateSsoGroupProcessor();
		ResetPasswordProcessor resetPasswordProcessor = new ResetPasswordProcessor();
		SendEmailToSsoUserProcessor sendEmailProcessor = new SendEmailToSsoUserProcessor();
		UpdateSsoUserProcessor updateUserProcessor = new UpdateSsoUserProcessor();
		UpdateSsoGroupProcessor updateGroupProcessor = new UpdateSsoGroupProcessor();
		UpdateSsoGroupMemberProcessor updateGroupMemberProcessor = new UpdateSsoGroupMemberProcessor();
		LoginSsoUserProcessor loginUserProcessor = new LoginSsoUserProcessor();
		QuerySsoUserProcessor queryUserProcessor = new QuerySsoUserProcessor();
		QuerySsoHistoryProcessor queryHistoryProcessor = new QuerySsoHistoryProcessor(dbsource);
		QuerySsoGroupProcessor queryGroupProcessor = new QuerySsoGroupProcessor(dbsource);
		QuerySsoEventsProcessor querySsoEventsProcessor = new QuerySsoEventsProcessor();
		InsertSsoEventProcessor insertSsoEventProcessor = new InsertSsoEventProcessor();

		ClassRoutingProcessor<Message> routingProcessor = new ClassRoutingProcessor<Message>(Message.class);
		rs.addChildren(msgSuite, broadcastSuite, createUserProcessor, createGroupProcessor, routingProcessor, loginUserProcessor, updateUserProcessor, updateGroupProcessor,
				updateGroupMemberProcessor, resetPasswordProcessor, queryUserProcessor, queryGroupProcessor, queryHistoryProcessor, insertSsoEventProcessor,
				querySsoEventsProcessor, sendEmailProcessor);
		rs.wire(msgSuite.inboundOutputPort, routingProcessor, true);
		//request response
		rs.wire(routingProcessor.newRequestOutputPort(CreateSsoUserRequest.class, CreateSsoUserResponse.class), createUserProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(CreateSsoGroupRequest.class, CreateSsoGroupResponse.class), createGroupProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(ResetPasswordRequest.class, ResetPasswordResponse.class), resetPasswordProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(SendEmailToSsoUserRequest.class, SendEmailToSsoUserResponse.class), sendEmailProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(UpdateSsoUserRequest.class, UpdateSsoUserResponse.class), updateUserProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(UpdateSsoGroupRequest.class, UpdateSsoGroupResponse.class), updateGroupProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(UpdateSsoGroupMemberRequest.class, UpdateSsoGroupMemberResponse.class), updateGroupMemberProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(LoginSsoUserRequest.class, LoginSsoUserResponse.class), loginUserProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(QuerySsoUserRequest.class, QuerySsoUserResponse.class), queryUserProcessor.getInputPort(), true);
		rs.wire(routingProcessor.newRequestOutputPort(QuerySsoHistoryRequest.class, QuerySsoHistoryResponse.class), queryHistoryProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(QuerySsoGroupRequest.class, QuerySsoGroupResponse.class), queryGroupProcessor, true);
		rs.wire(routingProcessor.newRequestOutputPort(QuerySsoEventsRequest.class, QuerySsoEventsResponse.class), querySsoEventsProcessor, true);
		//something
		rs.wire(loginUserProcessor.updatePort, updateUserProcessor, false);
		rs.wire(resetPasswordProcessor.updatePort, updateUserProcessor, false);
		//broadcasting
		rs.wire(createUserProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);
		rs.wire(createGroupProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);
		rs.wire(resetPasswordProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);
		rs.wire(sendEmailProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);
		rs.wire(updateUserProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);
		rs.wire(updateGroupProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);
		rs.wire(updateGroupMemberProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);
		rs.wire(loginUserProcessor.broadcastPort, insertSsoEventProcessor.getInputPort(), true);

		rs.wire(insertSsoEventProcessor.broadcastPort, broadcastSuite.outboundInputPort, true);

		BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new DbBackedIdGenerator.Factory(dbsource, "Id_Fountains", "next_id", "namespace"), 100);
		container.getServices().setUidGenerator(new BasicNamespaceIdGenerator<Long>(fountain));
		rs.wire(updateUserProcessor.updateGroupPort, updateGroupProcessor, false);

		final SsoState state = initState(dbservice, bs.getCodeGenerator());
		routingProcessor.bindToPartition("SSOSTATE");
		resetPasswordProcessor.bindToPartition("SSOSTATE");
		sendEmailProcessor.bindToPartition("SSOSTATE");
		createUserProcessor.bindToPartition("SSOSTATE");
		createGroupProcessor.bindToPartition("SSOSTATE");
		updateUserProcessor.bindToPartition("SSOSTATE");
		updateGroupProcessor.bindToPartition("SSOSTATE");
		updateGroupMemberProcessor.bindToPartition("SSOSTATE");
		loginUserProcessor.bindToPartition("SSOSTATE");
		queryUserProcessor.bindToPartition("SSOSTATE");
		queryHistoryProcessor.bindToPartition("SSOSTATE");
		queryGroupProcessor.bindToPartition("SSOSTATE");
		querySsoEventsProcessor.bindToPartition("SSOSTATE");
		insertSsoEventProcessor.bindToPartition("SSOSTATE");
		container.getPartitionController().putState("SSOSTATE", state);

		OutputPort<?> p = rs.exposeInputPortAsOutput(queryGroupProcessor, true);
		bs.registerConsoleObject("sso", new SsoConsole(container));
		bs.startupContainer(container);
		bs.keepAlive();
	}
	private static SsoState initState(DbService dbservice, ObjectGenerator generator) throws Exception {
		Connection connection = dbservice.getConnection();
		SsoState r = null;
		try {
			List<SsoUser> users = dbservice.executeQuery("query_ssousers", CH.m(), SsoUser.class, connection);
			List<SsoGroup> groups = dbservice.executeQuery("query_ssogroup", CH.m(), SsoGroup.class, connection);
			List<SsoUpdateEvent> events = dbservice.executeQuery("query_ssoupdateevents", CH.m(), SsoUpdateEvent.class, connection);
			List<SsoGroupAttribute> groupAttributes = dbservice.executeQuery("query_group_attributes", CH.m(), SsoGroupAttribute.class, connection);
			List<SsoGroupMember> groupMembers = dbservice.executeQuery("query_ssogroupmembers", Collections.EMPTY_MAP, SsoGroupMember.class, connection);
			r = new SsoState(generator);
			for (SsoGroup group : groups) {
				try {
					r.addGroup(group);
				} catch (Exception e) {
					LH.warning(log, "Error adding group on startup", e);
				}
			}
			for (SsoUser user : users) {
				try {
					r.addUser(user);
				} catch (Exception e) {
					LH.warning(log, "Error adding user on startup", e);
				}
			}
			for (SsoGroupMember member : groupMembers) {
				try {
					if (r.getGroup(member.getGroupId()) == null)
						LH.warning(log, "Skipping group member w/ unknown group: ", member);
					else if (r.getGroup(member.getMemberId()) == null)
						LH.warning(log, "Skipping group member w/ unknown member: ", member);
					else
						r.addGroupMember(member);
				} catch (Exception e) {
					LH.warning(log, "Error adding group member on startup", e);
				}
			}
			for (SsoGroupAttribute groupAttribute : groupAttributes) {
				try {
					if (r.getGroup(groupAttribute.getGroupId()) == null)
						LH.warning(log, "Skipping group attribute w/ unknown group: ", groupAttribute);
					else
						r.addGroupAttribute(groupAttribute);
				} catch (Exception e) {
					LH.warning(log, "Error adding group attribute on startup", e);
				}
			}
			r.setEvents(events);
		} finally {
			connection.close();
		}
		return r;

	}
}
