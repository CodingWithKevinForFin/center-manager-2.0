package com.f1.ami.web;

import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.suite.web.portal.PortletConfig;

/**
 * Manages the creation and configuration of an AmiWebPluginPortlet
 * 
 */
public interface AmiWebPanelPlugin extends AmiPlugin {

	/**
	 * @return the name shown in the visualization creation wizard.
	 */
	public String getDisplayName();

	/**
	 * @return file name of the image to show in the vizualization creation wizard
	 */
	public String getDisplayIconFileName();

	/**
	 * @return the style to show in the vizualization creation wizard
	 */
	public String getCssClassName();

	/**
	 * returns the unique plugin id for this type of visualization. Note, this should not change overtime, as this is what is saved in the dashboard configuration.
	 */
	@Override
	public String getPluginId();

	/**
	 * @return html to be included when the page is loaded. This is called each time the page is refreshed exactly once. This is typically where you could reference css, javascript
	 *         and other resources that are needed by the browser for this plugin
	 */
	public String getBootstrapHtml();

	/**
	 * 
	 * @return the style object used for defining the styling properties that this plugins visualizations can be decorated with
	 */
	public AmiWebStyleType getStyleType();

	/**
	 * Generates a new panel
	 * 
	 * @param config
	 *            Each panel has a unique id, specified in the config's portletId
	 * @return
	 */
	AmiWebPluginPortlet createPanel(PortletConfig config);

	/**
	 * During the creation of the visualization, the user navigates through a creation "wizard". When the user elects to use this type of visualization, this method is called and
	 * should return a "vizwiz" for providing a sample to the user.
	 * 
	 * @param service
	 *            the service for the users session
	 * @param target
	 *            the existing portlet
	 * @return
	 */
	AmiWebVizwiz createVizwiz(AmiWebService service, AmiWebPluginPortlet target);

	public List<String> extraceUsedDms(Map<String, Object> portletConfig);

	public void replaceUsedDmAt(Map<String, Object> portletConfig, int position, String name);

}
