package com.f1.fix2ami;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.fix2ami.processor.AmiPublishProcessor;
import com.f1.fix2ami.processor.CancelRejectProcessor;
import com.f1.fix2ami.processor.CancelRequestProcessor;
import com.f1.fix2ami.processor.ExecutionReportProcessor;
import com.f1.fix2ami.processor.MsgRoutingProcessor;
import com.f1.fix2ami.processor.NewOrderProcessor;
import com.f1.fix2ami.processor.ReplaceRequestProcessor;
import com.f1.fix2ami.processor.UnsupportMsgProcessor;
import com.f1.transportManagement.SessionManager;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;

/*
 * -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager
 *
 */
public class Fix2AmiMain {
	static BasicPartitionResolver<Fix2AmiEvent> pr = new BasicPartitionResolver<Fix2AmiEvent>(Fix2AmiEvent.class, null) {
		@Override
		public Object getPartitionId(Fix2AmiEvent action) {
			return action.getPartitionId();
		}
	};

	public static MsgRoutingProcessor setup(Container mycontainer, final PropertyController props) throws ConfigError {
		Suite rs = mycontainer.getSuiteController().getRootSuite();

		MsgRoutingProcessor msgRoutingProcessor = new MsgRoutingProcessor();
		AbstractFix2AmiProcessor newOrderProcessor = new NewOrderProcessor(props);
		AbstractFix2AmiProcessor unSupportMsgProcessor = new UnsupportMsgProcessor();
		ExecutionReportProcessor executionReportProcessor = new ExecutionReportProcessor(props);
		AbstractFix2AmiProcessor canelRejectProcessor = new CancelRejectProcessor(props);
		AbstractFix2AmiProcessor cancelRequestProcessor = new CancelRequestProcessor(props);
		AbstractFix2AmiProcessor replaceRequestProcessor = new ReplaceRequestProcessor(props);

		AmiPublishProcessor amiPublishProcessor = new AmiPublishProcessor(props);

		msgRoutingProcessor.setPartitionResolver(pr);
		newOrderProcessor.setPartitionResolver(pr);
		unSupportMsgProcessor.setPartitionResolver(pr);
		executionReportProcessor.setPartitionResolver(pr);
		canelRejectProcessor.setPartitionResolver(pr);
		cancelRequestProcessor.setPartitionResolver(pr);
		replaceRequestProcessor.setPartitionResolver(pr);

		amiPublishProcessor.bindToPartition("GLOBAL_STATE");

		rs.addChildren(msgRoutingProcessor, newOrderProcessor, unSupportMsgProcessor, executionReportProcessor, canelRejectProcessor, cancelRequestProcessor,
				replaceRequestProcessor, amiPublishProcessor);
		rs.wire(msgRoutingProcessor.newOrderPort, newOrderProcessor, true);
		rs.wire(msgRoutingProcessor.unsupportMessagePort, unSupportMsgProcessor, true);
		rs.wire(msgRoutingProcessor.executionReportPort, executionReportProcessor, true);
		rs.wire(msgRoutingProcessor.cancelRejectPort, canelRejectProcessor, true);
		rs.wire(msgRoutingProcessor.cancelRequestPort, cancelRequestProcessor, true);
		rs.wire(msgRoutingProcessor.replaceRequestPort, replaceRequestProcessor, true);

		rs.wire(newOrderProcessor.amiPublishPort, amiPublishProcessor, true);
		rs.wire(unSupportMsgProcessor.amiPublishPort, amiPublishProcessor, true);
		rs.wire(executionReportProcessor.amiPublishPort, amiPublishProcessor, true);
		rs.wire(executionReportProcessor.unsupportMessagePort, unSupportMsgProcessor, true);
		rs.wire(canelRejectProcessor.amiPublishPort, amiPublishProcessor, true);
		rs.wire(cancelRequestProcessor.amiPublishPort, amiPublishProcessor, true);
		rs.wire(replaceRequestProcessor.amiPublishPort, amiPublishProcessor, true);

		return msgRoutingProcessor;
	}

	public static void main(String[] args) throws Exception {
		final ContainerBootstrap cam = new ContainerBootstrap(Fix2AmiMain.class, args);
		cam.setConfigDirProperty("./src/main/config/fix2ami");
		cam.setTerminateFileProperty("${f1.conf.dir}/../." + cam.getMainClass().getSimpleName().toLowerCase() + ".prc");
		final PropertyController props = cam.getProperties();
		Container mycontainer = new BasicContainer();
		cam.prepareContainer(mycontainer);

		MsgRoutingProcessor msgRoutingProcessor = setup(mycontainer, props);

		cam.startupContainer(mycontainer);
		cam.keepAlive();

		SessionManager sessionManager = new SessionManager(mycontainer, msgRoutingProcessor, props);

		sessionManager.start();
	}
}
