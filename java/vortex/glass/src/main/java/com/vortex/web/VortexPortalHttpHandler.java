/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.vortex.web;

import com.f1.container.ContainerTools;
import com.f1.suite.web.PortletManagerFactory;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.text.TestFastTextPortlet;
import com.vortex.web.portlet.forms.VortexWebAuditTrailRuleFormPortlet;
import com.vortex.web.portlet.tables.VortexEyeJournalReportTablePortlet;
import com.vortex.web.portlet.tables.VortexWebAuditRulesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebAuditTrailEventsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebBackupDestinationTablePortlet;
import com.vortex.web.portlet.tables.VortexWebBackupFilesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebBackupTablePortlet;
import com.vortex.web.portlet.tables.VortexWebBuildProcedureTablePortlet;
import com.vortex.web.portlet.tables.VortexWebBuildResultTablePortlet;
import com.vortex.web.portlet.tables.VortexWebCloudInterfaceTablePortlet;
import com.vortex.web.portlet.tables.VortexWebCloudMachinesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebCronTabTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDatabaseColumnsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDatabaseObjectTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDatabasePrivilegesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDatabaseServersTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDatabaseTablesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDatabasesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDeploymentSetTablePortlet;
import com.vortex.web.portlet.tables.VortexWebDeploymentTablePortlet;
import com.vortex.web.portlet.tables.VortexWebExpectationsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebEyeClientEventsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebF1AppConnectionsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebF1AppInstanceTablePortlet;
import com.vortex.web.portlet.tables.VortexWebF1AppLoggerTablePortlet;
import com.vortex.web.portlet.tables.VortexWebF1AppPartitionsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebF1AppProcessorsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebF1AppPropertiesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebF1AppThreadsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebMachineConnectionsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebMachineFileSystemTablePortlet;
import com.vortex.web.portlet.tables.VortexWebMachineInstancesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebMachineNetAddressesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebMachineNetLinksTablePortlet;
import com.vortex.web.portlet.tables.VortexWebMachineProcessesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebMetadataFieldsTablePortlet;
import com.vortex.web.portlet.tables.VortexWebScheduledTasksTablePortlet;
import com.vortex.web.portlet.trees.VortexWebAuditTrailEventsTreePortlet;
import com.vortex.web.portlet.trees.VortexWebConnectionTreePortlet;
import com.vortex.web.portlet.trees.VortexWebDatabasesTreePortlet;
import com.vortex.web.portlet.trees.VortexWebF1AppsTreePortlet;
import com.vortex.web.portlet.visuals.VortexClockPortlet;
import com.vortex.web.portlet.visuals.VortexTerminalPortlet;
import com.vortex.web.portlet.visuals.VortexWebF1AppGraphPortlet;
import com.vortex.web.portlet.visuals.VortexWebMachineProcessesTreemapPortlet;
import com.vortex.web.portlet.visuals.VortexWebNetConnectionsGraphPortlet;

public class VortexPortalHttpHandler extends PortletManagerFactory {

	//final private RequestOutputPort<Message, AgentSnapshot> snapshotRequestPort = newRequestOutputPort(Message.class, AgentSnapshot.class);

	public VortexPortalHttpHandler(ContainerTools tools) {
		super(tools);
		//		super.setCallback("/portal/portal.ajax");
		//		this.setPortletBackend(backend);
	}

	@Override
	public void applyServices(PortletManager portletManager) {
		super.applyServices(portletManager);
		VortexWebEyeService agentSnapshotService = new VortexWebEyeService(portletManager);
		portletManager.registerService(agentSnapshotService);
	}
	@Override
	public void applyBuilders(PortletManager portletManager) {
		super.applyBuilders(portletManager);
		String path = "Vortex.Monitoring";
		String apath = "Vortex.Auditing";
		String mpath = path + ".Machines";
		String dbpath = path + ".Databases";
		String f1path = path + ".F1 Enabled Applications";
		String dpath = "Vortex.Deployment";
		portletManager.addPortletBuilder(new VortexWebCronTabTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebMachineProcessesTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebMachineProcessesTreemapPortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebMachineConnectionsTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebMachineFileSystemTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebMachineInstancesTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebMachineNetAddressesTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebMachineNetLinksTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebDatabasesTablePortlet.Builder().setPath(dbpath));
		portletManager.addPortletBuilder(new VortexWebDatabaseTablesTablePortlet.Builder().setPath(dbpath));
		portletManager.addPortletBuilder(new VortexWebDatabaseObjectTablePortlet.Builder().setPath(dbpath));
		portletManager.addPortletBuilder(new VortexWebDatabasePrivilegesTablePortlet.Builder().setPath(dbpath));
		portletManager.addPortletBuilder(new VortexWebDatabaseColumnsTablePortlet.Builder().setPath(dbpath));
		portletManager.addPortletBuilder(new VortexWebDatabaseServersTablePortlet.Builder().setPath(dbpath));
		portletManager.addPortletBuilder(new VortexWebF1AppPropertiesTablePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebF1AppsTreePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebF1AppGraphPortlet.Builder().setPath(f1path));
		//portletManager.addPortletBuilder(new AgentF1TablePortlet.Builder());
		portletManager.addPortletBuilder(new VortexWebF1AppInstanceTablePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebF1AppLoggerTablePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebF1AppPartitionsTablePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebF1AppProcessorsTablePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebF1AppThreadsTablePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebF1AppConnectionsTablePortlet.Builder().setPath(f1path));
		portletManager.addPortletBuilder(new VortexWebConnectionTreePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexWebDatabasesTreePortlet.Builder().setPath(dbpath));
		portletManager.addPortletBuilder(new VortexWebAuditRulesTablePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebAuditTrailRuleFormPortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebAuditTrailEventsTablePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebAuditTrailEventsTreePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebAuditTrailEventsTreePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebExpectationsTablePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebBuildProcedureTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebBuildResultTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebDeploymentSetTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebDeploymentTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebBackupDestinationTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebBackupFilesTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebBackupTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebScheduledTasksTablePortlet.Builder().setPath(dpath));
		//portletManager.addPortletBuilder(new TestTilesPortlet.Builder().setPath(dpath));
		//portletManager.addPortletBuilder(new TestGraphPortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebNetConnectionsGraphPortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new VortexEyeJournalReportTablePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebMetadataFieldsTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebEyeClientEventsTablePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new TestFastTextPortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new VortexWebCloudInterfaceTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexWebCloudMachinesTablePortlet.Builder().setPath(dpath));
		portletManager.addPortletBuilder(new VortexTerminalPortlet.Builder().setPath(path));
		portletManager.addPortletBuilder(new VortexClockPortlet.Builder().setPath("Extras"));
		//portletManager.addPortletBuilder(new TestTreeMapPortlet.Builder().setPath(dpath));
		//portletManager.addPortletBuilder(new VortexExpectationFormPortlet.Builder().setPath(apath));
	}

}
