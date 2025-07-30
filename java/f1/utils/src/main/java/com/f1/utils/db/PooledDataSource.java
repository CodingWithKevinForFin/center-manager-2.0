package com.f1.utils.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class PooledDataSource extends AbstractDataSource implements Database {
	private static final Logger log = Logger.getLogger(PooledDataSource.class.getName());

	public static final byte MAX_POLICY_NEW_CONNECTION = 1;
	public static final byte MAX_POLICY_BLOCK = 2;

	private int minPoolSize = 1, maxPoolSize = 5;
	private DataSource inner;
	private boolean autoCommit = true;
	private byte maxPolicy = MAX_POLICY_NEW_CONNECTION;

	private String url;
	private int validateTimeout = 0;
	private int outCount = 0;
	private List<PooledConnection> in = new ArrayList<PooledConnection>();
	private List<PooledConnection> connections = new CopyOnWriteArrayList<PooledConnection>();

	private long blockPeriodMs = 60 * 1000;

	private boolean skipAutoCommit = false;

	public PooledDataSource(DataSource inner, String url) {
		this.inner = inner;
		this.url = url;
		if (!setMinPoolSize(minPoolSize))
			throw new RuntimeException("Could not connect/login to: " + url);
	}

	public boolean setMinPoolSize(int minPoolSize) {
		if (minPoolSize > maxPoolSize)
			throw new IllegalArgumentException("min > max: " + minPoolSize + " > " + maxPoolSize + ". Url=" + url);
		this.minPoolSize = minPoolSize;
		int add = 0;
		synchronized (this) {
			add = minPoolSize - outCount - in.size();
		}

		List<PooledConnection> connections = new ArrayList<PooledConnection>();
		for (int i = 0; i < add; i++) {
			try {
				PooledConnection connection = new PooledConnection(inner.getConnection(), this);
				connections.add(connection);
			} catch (Exception e) {
				LH.severe(log, "Could not create connections to meet min level. Url=", url, e);
				return false;
			}
		}
		this.connections.addAll(connections);
		synchronized (this) {
			for (PooledConnection c : connections) {
				c.setInPool(true);
				in.add(c);
			}
		}
		return true;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		if (minPoolSize > maxPoolSize)
			throw new IllegalArgumentException("min > max: " + minPoolSize + " > " + maxPoolSize + ". Url=" + url);
		synchronized (in) {
			for (int i = in.size() - 1; i >= maxPoolSize; i--)
				IOH.close(in.remove(i));
		}
		this.maxPoolSize = maxPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	@Override
	public PooledConnection getConnection() throws SQLException {
		for (;;) {
			boolean existingConnection = false;
			PooledConnection r = null;
			synchronized (in) {
				if (in.size() > 0) {
					outCount++;
					r = in.remove(in.size() - 1);
					r.setInPool(false);
				}
			}
			if (r == null) {
				if (maxPolicy == MAX_POLICY_BLOCK) {
					boolean createConnection = false;
					synchronized (in) {
						if (in.size() != 0)
							continue;//try again
						if (outCount >= maxPoolSize) {//max connections reached
							if (log.isLoggable(Level.FINE))
								LH.fine(log, "No Connections and block policy set for ", blockPeriodMs, ", Waiting for freed connection...");
							if (!OH.wait(in, blockPeriodMs))
								throw new SQLException("Interrupted while waiting for connection");
							if (in.size() > 0) {
								r = in.remove(in.size() - 1);
								r.setInPool(false);
							} else
								continue;
						} else
							createConnection = true;
						outCount++;
					}
					if (createConnection) {
						try {
							r = new PooledConnection(inner.getConnection(), this);
							this.connections.add(r);
							r.setInPool(false);
						} catch (RuntimeException e) {
							synchronized (in) {
								outCount--;
								in.notify();
							}
							throw e;
						} catch (SQLException e) {
							synchronized (in) {
								outCount--;
								in.notify();
							}
							throw e;
						}
						existingConnection = false;
					} else
						existingConnection = true;
				} else {
					existingConnection = true;
					r = new PooledConnection(inner.getConnection(), this);
					this.connections.add(r);
					r.setInPool(false);
					synchronized (in) {
						outCount++;
					}
				}
			}
			if (!isValid(r)) {
				log.info("Closing invalid database connection");
				connections.remove(r);
				r.setConnectionLost(true);
				IOH.close(r);
				synchronized (in) {
					outCount--;
				}
				continue;
			}
			try {
				if (!skipAutoCommit)
					r.setAutoCommit(isAutoCommit());
			} catch (SQLException e) {
				if (!existingConnection)
					throw e;
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Closing expired database connection", e);
				else
					LH.info(log, "Closing expired database connection");
				connections.remove(r);
				r.setConnectionLost(true);
				synchronized (in) {
					outCount--;
				}
				IOH.close(r);
				continue;
			}
			return r;
		}
	}
	private boolean isValid(PooledConnection r) {
		if (validateTimeout == 0)
			return true;
		try {
			return r.isValid(validateTimeout);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return inner.getConnection(username, password);
	}
	public DataSource getInnerDatasource() {
		return inner;
	}

	protected void returnConnection(PooledConnection pooledConnection) throws SQLException {
		if (pooledConnection.getDatasource() != this)
			throw new IllegalArgumentException("not a connection from this pool. Url=" + url);
		if (pooledConnection.getIsInPool())
			throw new IllegalStateException("already closed (and in pool). Url=" + url);
		if (pooledConnection.getLastException() != null) {
			synchronized (in) {
				connections.remove(pooledConnection);
				outCount--;
				in.notify();
			}
			LH.info(log, "Not returning connection to pool due to exception: ", pooledConnection.getLastException());
			IOH.close(pooledConnection.getInner());
			return;
		}
		if (!pooledConnection.getAutoCommit())
			pooledConnection.commit();
		pooledConnection.clearWarnings();
		boolean needsClosing = false;
		synchronized (in) {
			pooledConnection.setInPool(true);
			outCount--;
			if (in.size() <= maxPoolSize) {
				in.add(pooledConnection);
			} else {
				connections.remove(pooledConnection);
				needsClosing = true;
			}
			in.notify();
		}
		if (needsClosing)
			pooledConnection.closeConnectionNoThrow();
	}
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public String toString() {
		return getClass().getSimpleName() + ":" + url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public List<PooledConnection> getDatabaseConnections() {
		return this.connections;
	}

	private List<DatabaseListener> listeners = new CopyOnWriteArrayList<DatabaseListener>();

	private boolean monitorForConnectionLeaks = false;

	private Watcher watcher;

	@Override
	public void addDatabaseListener(DatabaseListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removedDatabaseListener(DatabaseListener listener) {
		listeners.remove(listener);
	}

	@Override
	public List<DatabaseListener> getDatabaseListeners() {
		return listeners;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	public int getValidateTimeoutSeconds() {
		return validateTimeout;
	}

	/**
	 * The amount of time to try and validate a connection for (in seconds). Zero to skip validation
	 * 
	 * @param validateTimeout
	 */
	public void setValidateTimeoutSeconds(int validateTimeout) {
		OH.assertGe(validateTimeout, 0);
		this.validateTimeout = validateTimeout;
	}

	public void setMaxPoolSizePolicy(byte maxPolicy) {
		this.maxPolicy = maxPolicy;
	}

	public long getBlockPeriodMs() {
		return blockPeriodMs;
	}

	public void setBlockPeriodMs(long blockPeriodMs) {
		this.blockPeriodMs = blockPeriodMs;
	}

	public boolean isSkipAutoCommit() {
		return skipAutoCommit;
	}

	public void setSkipAutoCommit(boolean skipAutoCommit) {
		this.skipAutoCommit = skipAutoCommit;
	}

	synchronized public boolean getMonitorForConnectionLeaks() {
		return monitorForConnectionLeaks;
	}
	public long getMonitorLeakPeriodMs() {
		return monitorPeriodMs;
	}

	public void setMonitorLeakPeriodMs(long monitorPeriodMs) {
		this.monitorPeriodMs = monitorPeriodMs;
	}

	public long getConnectionIsLeakPeriodMs() {
		return connectionIsLeakPeriodMs;
	}

	public void setConnectionIsLeakPeriodMs(long connectionIsLeakPeriodMs) {
		this.connectionIsLeakPeriodMs = connectionIsLeakPeriodMs;
	}

	private long monitorPeriodMs = 10000;
	private long connectionIsLeakPeriodMs = 30000;

	synchronized public void setMonitorForConnectionLeaks(boolean monitorForConnectionLeaks) {
		if (this.monitorForConnectionLeaks == monitorForConnectionLeaks)
			return;
		this.monitorForConnectionLeaks = monitorForConnectionLeaks;
		if (this.monitorForConnectionLeaks) {
			this.watcher = new Watcher();
			this.watcher.start();
		} else {
			this.watcher.running = false;
			this.watcher = null;
		}
	}

	private class Watcher extends Thread {
		private boolean running = true;

		public Watcher() {
			super("PooledDataSourceLeakMon");
			super.setDaemon(true);
		}

		@Override
		public void run() {
			for (;;) {
				try {
					OH.sleep(monitorPeriodMs);
					if (!running)
						break;
					long cutoff = System.currentTimeMillis() - connectionIsLeakPeriodMs;
					if (log.isLoggable(Level.FINE))
						LH.fine(log, "Monitoring ", connections.size(), " connection(s) for leaks");
					for (PooledConnection i : connections)
						i.monitorForLeak(cutoff);
				} catch (Exception e) {
					LH.warning(log, "Error while monitoring for leaks", e);
				}
			}
		}

	}

}
