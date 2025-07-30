package com.f1.ami.web.dm;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.utils.sql.Tableset;

public interface AmiWebDm extends AmiWebDmListener, AmiWebDomObject {

	public Tableset getResponseTableset();
	public Tableset getResponseTablesetBeforeFilter();

	public AmiWebDmRequest getRequestTableset();

	public String getDmUid();

	public AmiWebDmTablesetSchema getRequestInSchema();
	public AmiWebDmTablesetSchema getResponseOutSchema();
	public AmiWebAmiScriptCallbacks getCallbacks();

	public void fireDmDataChanged();

	public void addDmListener(AmiWebDmListener listener);
	public void removeDmListener(AmiWebDmListener listener);

	public Iterable<AmiWebDmListener> getDmListeners();

	public Set<String> getUpperDmAliasDotNames();//datamodels that us this datamodel

	public AmiWebDmManager getDmManager();

	public void processRequest(AmiWebDmRequest req, AmiDebugManager debugger);

	void close();
	String getDmName();
	boolean hasVisiblePortlet();

	public void resetStatistics();
	public long getStatisticEvals();
	public boolean hasRanOnStartUp();
	public long getStatisticEvalsTimeMillis();
	public long getStatisticEvalsAvgTimeMillis();
	public long getStatisticEvalsMinTimeMillis();
	public long getStatisticEvalsMaxTimeMillis();
	public long getStatisticErrors();
	public long getStatisticLastEvalTimeMillis();
	public long getStatisticNextEvalTimeMillis();
	public long getStatisticConsecutiveRequeriesCount();

	Map<String, Object> getVars();

	boolean isCurrentlyRunning();

	void reprocessFilters(String name);
	void addFilter(AmiWebDmFilter filter);
	void removeFilter(AmiWebDmFilter filter);
	List<AmiWebDmFilter> getFilters();
	void fireDmDataBeforeFilterChanged();

	public AmiWebDmRequest getInputDefaults();
	public void setAliasDotName(String linkFullAlias);

	@Override
	public String getAmiLayoutFullAlias();
	@Override
	public String getAmiLayoutFullAliasDotId();
	public void removeDmLinkToThisDm(AmiWebDmLink link);
	public void addDmLinkToThisDm(AmiWebDmLink link);
	public void removeDmLinkFromThisDm(AmiWebDmLink link);
	public void addDmLinkFromThisDm(AmiWebDmLink link);
	public Collection<AmiWebDmLink> getDmLinksFromThisDm();
	public Collection<AmiWebDmLink> getDmLinksToThisDm();
	public Map<String, Object> getConfiguration();
	public void init(String alias, Map<String, Object> val, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink);
	public void onInitDone();
	public boolean isReadonlyLayout();
	boolean canQueryAtThisTime(long now);

	//	int getLimit();
	//	int getTimeoutMs();

	AmiWebAmiScriptCallback getCallback_OnProcess();

	String getDefaultDatasource();

	public Set<String> getLowerDmAliasDotNames();//Datamodels used by this datamodel
	public Set<String> getUsedDatasources();

	boolean getQueryOnStartup();
	int getMinRequeryMs();
	int getMaxRequeryMs();

	void setIsPlay(boolean b);

	boolean isPlaying();

	byte getQueryOnMode();
	void setQueryOnMode(byte queryOnMode);
	public void verify();
	public void setResponseTableset(Tableset tableset);
	public String getCrontabTimezone();
	public String getCrontab();
	public void setCrontab(String crontab, String tz);
	boolean isInRequery();
}
