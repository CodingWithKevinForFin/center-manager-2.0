
package com.f1.vdb.bdb;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.ReplicaConsistencyPolicy;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.je.rep.NodeType;
import com.sleepycat.je.rep.ReplicatedEnvironment;
import com.sleepycat.je.rep.ReplicationConfig;
import com.sleepycat.je.rep.TimeConsistencyPolicy;
import com.f1.base.ObjectGenerator;
import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.SH;
import com.f1.vdb.VdbException;
import com.f1.vdb.impl.AbstractVdbDatabase;

@SuppressWarnings("unchecked")
public class BdbVdbDatabase extends AbstractVdbDatabase<BdbVdbTransaction>
{
  private static final Logger log = Logger.getLogger(BdbVdbDatabase.class.getName());

  private final File location;
  private Environment environment;
  private final Map<String, BdbVdbTable<?>> tables = new TreeMap<String, BdbVdbTable<?>>(SH.COMPARATOR);
  private final ObjectGenerator generator;
  private final OfflineConverter converter;

  private final boolean isMaster;
  private final boolean isSingle;

  private final String groupName;

  private final String localHostName;

  private final String nodeName;

  private final int serverPort;

  private final int slavePort;

  private final String serverHost;

  private final String helperHost;

  private final int helperPort;

  private final Durability durability;

  private final long driftTimeMs;

  private long timeoutSeconds;
  private long flushSeconds;



  public BdbVdbDatabase(String name, boolean isMaster, File location, ObjectGenerator generator,
      OfflineConverter converter, String groupName, String nodeName, int serverPort, int slavePort,
      String serverHostOrNull, String localHostName, String helperHostName, int helperPort, long driftTimeMs,
      String durabilityMode, long timeoutSeconds, long flushSeconds)
  {
    super(name);
    this.timeoutSeconds = timeoutSeconds;
    this.flushSeconds = flushSeconds;
    isSingle = false;
    localHostName = OH.noNull(localHostName, EH.getLocalHost());
    this.isMaster = isMaster;
    try
      {
        IOH.ensureDir(location);
      }
    catch (IOException e)
      {
        throw OH.toRuntime(e);
      }
    this.location = location;
    this.generator = generator;
    this.converter = converter;
    this.groupName = groupName;
    this.localHostName = localHostName;
    this.nodeName = nodeName;
    this.serverPort = serverPort;
    this.slavePort = slavePort;
    this.serverHost = OH.noNull(serverHostOrNull, localHostName);
    this.helperHost = helperHostName;
    this.helperPort = helperPort;
    this.durability = SH.isnt(durabilityMode) ? Durability.COMMIT_NO_SYNC : Durability.parse(durabilityMode);
    this.driftTimeMs = driftTimeMs;
    open();
  }

  public boolean isReplica()
  {
    // if (isMaster) throw new IllegalStateException(
    // "can only call this method if this is a secondary instance, not primary");
    if (isSingle) throw new IllegalStateException(
        "can only call this method if this is a primary or secondary instance, not single");
    return (((ReplicatedEnvironment)environment).getState().isReplica());
  }

  private void setEnvironment(Environment env)
  {
    this.environment = env;
  }

  public BdbVdbDatabase(String name, File location, ObjectGenerator generator, OfflineConverter converter,
      String durabilityMode)
  {
    super(name);
    isSingle = true;
    isMaster = true;
    try
      {
        IOH.ensureDir(location);
      }
    catch (IOException e)
      {
        throw OH.toRuntime(e);
      }
    this.location = location;
    this.generator = generator;
    this.converter = converter;
    this.durability = SH.isnt(durabilityMode) ? Durability.COMMIT_NO_SYNC : Durability.parse(durabilityMode);
    this.slavePort = -1;
    this.driftTimeMs = -1;
    this.groupName = null;
    this.nodeName = null;
    this.localHostName = null;
    this.serverHost = null;
    this.serverPort = -1;
    this.helperHost = null;
    this.helperPort = -1;
    open();
  }

  @Override
  protected <V extends Valued> BdbVdbTable<V> createTableInner(String dbName, Class<V> clazz, String... keys)
  {
    ValuedSchema<V> schema = (ValuedSchema<V>)generator.nw(clazz).askSchema();
    BdbVdbTable<V> r = new BdbVdbTable<V>(dbName, this, schema, keys, converter);
    CH.putOrThrow(tables, r.getTableName(), r);
    return r;
  }

  @Override
  public BdbVdbTransaction startTransaction()
  {
    try
      {
        TransactionConfig txnConfig = new TransactionConfig();
        txnConfig.setDurability(this.durability);
        ReplicaConsistencyPolicy conPolicy = new TimeConsistencyPolicy(1, TimeUnit.SECONDS, 100, TimeUnit.SECONDS);
        txnConfig.setConsistencyPolicy(conPolicy);
        return new BdbVdbTransaction(environment.beginTransaction(null, txnConfig));
      }
    catch (DatabaseException e)
      {
        throw new VdbException("error starting transaction", e);
      }
  }

  @Override
  public void commitTransaction(BdbVdbTransaction transaction)
  {
    try
      {
        if (transaction.getTransaction() != null) transaction.getTransaction().commit();
      }
    catch (DatabaseException e)
      {
        throw new VdbException("error committing transaction", e);
      }
  }

  @Override
  public void abortTransaction(BdbVdbTransaction txn)
  {
    try
      {
        if (txn.getTransaction() != null) txn.getTransaction().abort();
      }
    catch (DatabaseException e)
      {
        throw new VdbException("error committing transaction", e);
      }
  }

  public Environment getEnvironment()
  {
    return environment;
  }

  public void close()
  {
    for (String table : getTableNames())
      getTable(table).close();
    environment.close();
  }

  public void openSingle()
  {
    final EnvironmentConfig envCfg = new EnvironmentConfig();
    envCfg.setAllowCreate(true);
    envCfg.setTransactional(true);
    envCfg.setDurability(durability);

   LH.info(log,"*********************");
   LH.info(log,"*");
   LH.info(log,"* bdb mode: single");
   LH.info(log,"* Bdb location: " , location);
   LH.info(log,"* Bdb durablility-mode: " , envCfg.getDurability());
   LH.info(log,"* Bdb environment config: " , envCfg.toString());
   LH.info(log,"*");
   LH.info(log,"********************");
    this.environment = new Environment(this.location, envCfg);
  }

  public void open()
  {
    try
      {
        if (isSingle) openSingle();
        else openHa();
        for (String table : getTableNames())
          getTable(table).open();
      }
    catch (Exception e)
      {
        throw new VdbException("error creating environment for:" + location, e);
      }
  }

  public void openHa()
  {
    final EnvironmentConfig envCfg = new EnvironmentConfig();
    envCfg.setAllowCreate(true);
    envCfg.setSharedCache(true);
    envCfg.setTransactional(true);
    ReplicationConfig repCfg = new ReplicationConfig();
    repCfg.setGroupName(groupName);
    repCfg.setNodeName(this.nodeName);
    if (isMaster) repCfg.setNodeHostPort(serverHost + ':' + serverPort);
    else repCfg.setNodeHostPort(localHostName + ':' + slavePort);

    repCfg.setNodeType(NodeType.ELECTABLE);
    repCfg.setMaxClockDelta(driftTimeMs, TimeUnit.MILLISECONDS);
    repCfg.setHelperHosts(helperHost + ":" + helperPort);
    envCfg.setDurability(durability);

    repCfg.setConfigParam(ReplicationConfig.ENV_SETUP_TIMEOUT, timeoutSeconds + " s");
    repCfg.setConfigParam(ReplicationConfig.LOG_FLUSH_TASK_INTERVAL, flushSeconds + " s");

   LH.info(log,"*********************");
   LH.info(log,"*");
    if (this.isMaster)LH.info(log,"* bdb mode: master");
    elseLH.info(log,"* bdb mode: slave");
   LH.info(log,"* Bdb location: " , location);
   LH.info(log,"* Bdb Timeout Seconds: " , repCfg.getConfigParam(ReplicationConfig.ENV_SETUP_TIMEOUT));
   LH.info(log,"* Bdb Group-Name: " , repCfg.getGroupName());
   LH.info(log,"* Bdb Node-Name: " , repCfg.getNodeName());
   LH.info(log,"* Bdb Node-Host-Port: " , repCfg.getNodeHostPort());
   LH.info(log,"* Bdb Helper-Hosts: " , repCfg.getHelperHosts());
   LH.info(log,"* Bdb durablility-mode: " , envCfg.getDurability());
   LH.info(log,"* Bdb replication config: " , repCfg.toString());
   LH.info(log,"* Bdb environment config: " , envCfg.toString());
   LH.info(log,"*");
   LH.info(log,"********************");
    repCfg.setElectableGroupSizeOverride(1);
    ReplicatedEnvironment repenv = null;
    this.environment = new ReplicatedEnvironment(location, repCfg, envCfg);
   LH.info(log,"* Bdb ReplicatedEnvironment Instantiated");

  }

  @Override
  public BdbVdbTable getTable(String tableName)
  {
    return (BdbVdbTable)super.getTable(tableName);
  }

  public OfflineConverter getConverter()
  {
    return converter;
  }

}
