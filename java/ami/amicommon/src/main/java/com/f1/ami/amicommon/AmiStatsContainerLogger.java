package com.f1.ami.amicommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.container.Container;
import com.f1.container.ContainerScope;
import com.f1.container.DispatchController;
import com.f1.container.impl.ContainerHelper;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.container.impl.dispatching.RootPartitionStats;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.TopMap;

public class AmiStatsContainerLogger implements Runnable {
	public static int MAX_PARTITIONS_PRINT = 64;

	private static final Logger amilog = Logger.getLogger("AMI_STATS.CONTAINER");

	private static final Comparator<RootPartitionActionRunner> RPAR_COMPARATOR = new Comparator<RootPartitionActionRunner>() {

		@Override
		public int compare(RootPartitionActionRunner o1, RootPartitionActionRunner o2) {
			return OH.compare(o1.getActionsAdded(), o2.getActionsAdded());
		}
	};

	private Container container;
	private List<MsgConnection> connections = new ArrayList<MsgConnection>();
	private DispatchController dispatchController;
	private final RootPartitionStats sink = new RootPartitionStats();
	private final HashMap<Object, RootPartitionStats> partitionStats = new HashMap<Object, RootPartitionStats>();
	private final IdentityHashMap<MsgTopic, MessageStats> messageStats = new IdentityHashMap<MsgTopic, MessageStats>();
	private final TopMap<RootPartitionActionRunner> tm = new TopMap<RootPartitionActionRunner>(MAX_PARTITIONS_PRINT, RPAR_COMPARATOR);
	private long modCount = 0;

	public AmiStatsContainerLogger(Container container) {
		this.container = container;
		List<ContainerScope> sink = new ArrayList<ContainerScope>();
		ContainerHelper.getAllChildren(container, sink);
		for (ContainerScope cs : sink) {
			if (cs instanceof MsgSuite) {
				MsgSuite ms = (MsgSuite) cs;
				if (!this.connections.contains(ms.getConnection()))
					this.connections.add(ms.getConnection());
			}
		}
		this.dispatchController = this.container.getDispatchController();
	}
	@Override
	public void run() {
		modCount++;
		Collection<RootPartitionActionRunner> runners = this.dispatchController.getRootParititionRunners();

		int count = runners.size();
		boolean atMax = count >= MAX_PARTITIONS_PRINT;
		if (atMax) {
			tm.clear();
		}

		removeInactivePartitionStats();

		for (RootPartitionActionRunner i : runners) {
			if (!atMax) {
				if (getStats(i, sink))
					log(i, sink);
			} else {
				tm.add(i);
			}
		}
		if (atMax) {
			for (RootPartitionActionRunner i : tm) {
				if (getStats(i, sink))
					log(i, sink);
			}
			tm.clear();
		}
		int cnt = 0;
		for (MsgConnection i : connections) {
			String cName = i.getConfiguration().getName();
			for (MsgInputTopic s : i.getInputTopics()) {
				MessageStats existing = messageStats.get(s);
				if (existing == null)
					messageStats.put(s, existing = new MessageStats(true, cName, s.getFullTopicName()));
				cnt++;
				existing.update(modCount, s.getReceivedMessagesCount(), 0, s.getExternalConnections().size());
			}
			for (MsgOutputTopic s : i.getOutputTopics()) {
				MessageStats existing = messageStats.get(s);
				if (existing == null)
					messageStats.put(s, existing = new MessageStats(false, cName, s.getFullTopicName()));
				cnt++;
				existing.update(modCount, s.getSentMessagesCount(), s.getSendQueueSize(), s.getExternalConnections().size());
			}
		}
		if (cnt < messageStats.size())
			for (Entry<MsgTopic, MessageStats> i : CH.l(this.messageStats.entrySet()))
				if (i.getValue().modCount != modCount)
					this.messageStats.remove(i.getKey());
	}

	private static class MessageStats {
		public long modCount;
		public boolean in;
		public String cName;
		public String name;
		long messagesCount = -1;//-1 indicates in 'temp' mode
		long queueSize;
		long connectionsCount;

		public MessageStats(boolean in, String cName, String name) {
			OH.assertEq(messagesCount, -1);
			this.in = in;
			this.cName = cName;
			this.name = name;
		}

		public boolean update(long modCount, long messagesCount, long queueSize, int connectionsCount) {
			this.modCount = modCount;
			if (this.messagesCount == messagesCount && this.queueSize == queueSize && this.connectionsCount == connectionsCount)
				return false;
			this.messagesCount = messagesCount;
			this.queueSize = queueSize;
			this.connectionsCount = connectionsCount;
			if (in)
				AmiProcessStatsLogger.log(amilog, "Topic", "dir", "in", "connection", cName, "name", name, "msgCount", messagesCount, "connections", connectionsCount);
			else
				AmiProcessStatsLogger.log(amilog, "Topic", "dir", "out", "connection", cName, "name", name, "msgCount", messagesCount, "queued", queueSize, "connections",
						connectionsCount);
			return true;
		}

		@Override
		public int hashCode() {
			return OH.hashCode(in, cName, name);
		}

		@Override
		public boolean equals(Object obj) {
			MessageStats other = (MessageStats) obj;
			return OH.eq(other.in, this.in) && OH.eq(other.cName, this.cName) && OH.eq(other.name, this.name);
		}
	}

	private void removeInactivePartitionStats() {
		int partitionRunnersCount = this.dispatchController.getRootParititionRunners().size();
		if (partitionRunnersCount == this.partitionStats.size())
			return;
		for (Object partitionId : CH.l(this.partitionStats.keySet())) {
			RootPartitionActionRunner partition = this.dispatchController.getRootPartitionRunner((String) partitionId);
			if (partition == null) {
				this.partitionStats.remove(partitionId);
				if (partitionRunnersCount == this.partitionStats.size())
					break;
			}
		}
	}
	/*
	 * 	Get the stats for each partition, updates active partitions
	 *	Returns true if the stats for the partition has changed 
	 */
	private boolean getStats(RootPartitionActionRunner i, RootPartitionStats sink) {
		i.getStats(sink);
		Object partitionId = i.getPartitionId();
		RootPartitionStats rootPartitionStats = this.partitionStats.get(partitionId);
		if (rootPartitionStats == null) {
			this.partitionStats.put(partitionId, new RootPartitionStats(sink));
			return true;
		} else if (rootPartitionStats.hasChanged(sink)) {
			rootPartitionStats.copyStats(sink);
			return true;
		}
		return false;
	}

	private void log(RootPartitionActionRunner i, RootPartitionStats sink) {
		AmiProcessStatsLogger.log(amilog, "Partition", "added", sink.getActionsAdded(), "processed", sink.getActionsProcessed(), "queued",
				sink.getActionsAdded() - sink.getActionsProcessed(), "totExecTime", sink.getTimeSpentMs(), "execs", sink.getQueueRuns(), "inThreadPool", sink.getInThreadPool(),
				"name", SH.s(i.getPartitionId()), "startTime", i.getStartTime());

	}

}
