package com.f1.ami.web.dm;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebService;

public interface AmiWebDmManager {

	//managers
	AmiWebService getService();

	//listeners
	void addDmManagerListener(AmiWebDmManagerListener listener);
	void removeDmManagerListener(AmiWebDmManagerListener listener);

	//Datamodel getter/setters
	List<AmiWebDmsImpl> getDmsSorted(String layoutAlias);
	Set<String> getDmNames(String alias);
	Collection<AmiWebDmsImpl> getDatamodels();

	AmiWebDmsImpl getDmByAliasDotName(String datamodelName);

	void addDm(AmiWebDmsImpl datamodel);
	AmiWebDmsImpl removeDm(String adn);

	//	void linkDatamodels(AmiWebDm lower, AmiWebDm upper);
	//	void unlinkDatamodels(AmiWebDm lower, AmiWebDm upper);

	//links
	void addDmLink(AmiWebDmLink dmLink);
	AmiWebDmLink getDmLink(String linkId);
	AmiWebDmLink removeDmLink(String linkId);
	String getNextRelationshipId(String amiLayoutFullAlias, String relationshipId);

	//Active Query management
	int getCurrentlyRunningQueriesCount();
	public Set<AmiWebDmsImpl> getCurrentlyRunningDms();
	List<AmiWebDmsImpl> getAndPausePlayingDatamodels();

	//events
	void onPanelDmDependencyChanged(AmiWebDmPortlet target, String dmAliasDotName, String tableName, boolean isAdd);
	void onFilterDependencyChanged(AmiWebDmFilter target, String dmAliasDotName, String tableName, boolean isAdd);

	//Cross-noun links
	List<AmiWebDmLink> getDmLinksToDmAliasDotName(String dmid);
	List<AmiWebDmPortlet> getPanelsForDmAliasDotName(String dmid);
	Collection<AmiWebDmLink> getDmLinks();

	//Configuration
	Map<String, Object> getConfiguration(String fullAlias);
	void init(String fullAlias, Map<String, Object> val, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink);
	void onInitDone();
	void clear();

	AmiWebDmLink getDmLinkByAliasDotRelationshipId(String id);

	Set<String> getDmLinkIdsByFullAlias(String fullAlias);

	void verifyDms();

	void onFrontendCalled(boolean isFirst);

	public String getNextDmName(String name, String layoutAlias);
	public String getDmAri(String dmName, String layoutAlias);
	public boolean isDmRegistered(String dmName, String layoutAlias);
	public boolean canRunDm();
	public AmiWebDmsImpl importDms(String layoutAlias, Map<String, Object> config, StringBuilder warningsSink, boolean isTransient);
}
