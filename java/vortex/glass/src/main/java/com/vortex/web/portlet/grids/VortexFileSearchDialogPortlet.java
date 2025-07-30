package com.vortex.web.portlet.grids;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.Tuple3;

public class VortexFileSearchDialogPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener {

	private FormPortletButton searchButton;
	private FormPortletButton resetButton;
	private IntKeyMap<Tuple3<FormPortletTextField, FormPortletColorField, FormPortletSelectField<Byte>>> fields = new IntKeyMap<Tuple3<FormPortletTextField, FormPortletColorField, FormPortletSelectField<Byte>>>();
	private FormPortlet typeForm;
	private FormPortlet colorForm;
	private FormPortlet saveForm;
	private FormPortlet textForm;
	private FormPortlet lowrForm;
	private VortexFileTextPortlet portlet;
	private FormPortletCheckboxField caseSensitiveField;
	private FormPortletSelectField<String> saveSelectField;
	private FormPortletButton saveButton;
	private FormPortletButton deleteButton;

	private Map<String, SearchTemplate> templates = new TreeMap<String, SearchTemplate>();

	public VortexFileSearchDialogPortlet(VortexFileTextPortlet portlet) {
		super(portlet.getManager().generateConfig());
		this.portlet = portlet;
		this.templates.putAll(loadTemplates(portlet.getManager()));

		this.colorForm = new FormPortlet(generateConfig());
		this.typeForm = new FormPortlet(generateConfig());
		this.textForm = new FormPortlet(generateConfig());
		this.lowrForm = new FormPortlet(generateConfig());
		this.textForm.setLabelsWidth(0);
		this.colorForm.setLabelsWidth(0);
		for (int i = 0; i < 10; i++) {
			FormPortletSelectField<Byte> typeField = typeForm.addField(new FormPortletSelectField<Byte>(Byte.class, "#" + SH.toString(i + 1) + " "));
			typeField.addOption(SearchOptions.TYPE_HIGHLIGHT_WORD, "highlight word");
			typeField.addOption(SearchOptions.TYPE_HIGHLIGHT_LINE, "highlight line");
			typeField.addOption(SearchOptions.TYPE_FILTER_IN, "filter on");
			typeField.addOption(SearchOptions.TYPE_FILTER_OUT, "hide lines");
			FormPortletTextField textField = textForm.addField(new FormPortletTextField("").setWidth(FormPortletTextField.WIDTH_STRETCH)).setHeight(20);
			FormPortletColorField colorField = colorForm.addField(new FormPortletColorField("")).setValue(WebHelper.getUniqueColorNoBlack(i)).setWidth(20);
			typeField.setHeight(20);
			textField.setHeight(20);
			colorField.setHeight(18);
			fields.put(i, new Tuple3<FormPortletTextField, FormPortletColorField, FormPortletSelectField<Byte>>(textField, colorField, typeField));
		}
		this.saveForm = new FormPortlet(generateConfig());
		addChild(saveForm, 0, 0, 4, 1);
		addChild(typeForm, 0, 1);
		addChild(textForm, 1, 1);
		addChild(colorForm, 2, 1);
		addChild(lowrForm, 0, 2, 4, 1);
		addChild(new HtmlPortlet(generateConfig(), ""), 3, 1);
		setColSize(0, 150);
		setColSize(2, 30);
		setColSize(3, 10);
		setRowSize(0, 30);
		setRowSize(2, 70);
		this.saveSelectField = this.saveForm.addField(new FormPortletSelectField<String>(String.class, "Saved Templates:"));
		for (SearchTemplate i : this.templates.values()) {
			this.saveSelectField.addOption(i.templateName, i.templateName);
		}
		this.saveSelectField.addOption("", "<empty template>");
		this.saveSelectField.setValue("");
		caseSensitiveField = lowrForm.addField(new FormPortletCheckboxField("Case Sensitive"));
		this.searchButton = lowrForm.addButton(new FormPortletButton("SEARCH"));
		this.resetButton = lowrForm.addButton(new FormPortletButton("reset"));
		this.saveButton = lowrForm.addButton(new FormPortletButton("save as"));
		this.deleteButton = new FormPortletButton("delete");
		if (this.templates.size() > 1)
			lowrForm.addButton(this.deleteButton);

		this.textForm.addFormPortletListener(this);
		this.lowrForm.addFormPortletListener(this);
		this.saveForm.addFormPortletListener(this);
		setSuggestedSize(410, 350);
	}
	public static Map<String, SearchTemplate> loadTemplates(PortletManager manager) {
		String value = manager.getUserConfigStore().loadFile("vortex_search_templates");
		Map<String, SearchTemplate> r = new TreeMap<String, VortexFileSearchDialogPortlet.SearchTemplate>();
		if (value != null) {
			List<Map<String, Object>> templatesJson = (List<Map<String, Object>>) new ObjectToJsonConverter().stringToObject(value);
			for (Map<String, Object> i : templatesJson) {
				String name = (String) i.get("name");
				boolean caseSensitive = (Boolean) i.get("caseSensitive");
				List<Map<String, Object>> searches = (List<Map<String, Object>>) i.get("searches");
				List<SearchOptions> searchOptions = new ArrayList<SearchOptions>();
				for (Map<String, Object> srch : searches) {
					SearchOptions so = new SearchOptions((String) srch.get("search"), (String) srch.get("color"), (String) srch.get("fgcolor"),
							((Integer) srch.get("type")).byteValue(), (Integer) srch.get("pos"));
					searchOptions.add(so);
				}
				r.put(name, new SearchTemplate(name, searchOptions, caseSensitive));
			}
		}
		return r;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.resetButton) {
			reset();
			this.saveSelectField.setValue("");
		} else if (button == this.searchButton) {
			//			boolean whiteSpaceAlert = false;
			//			List<SearchOptions> searchs = new ArrayList<SearchOptions>();
			//			Set<String> searchTexts = new HashSet<String>();
			//			int pos = 0;
			//			for (Tuple3<FormPortletTextField, FormPortletColorField, FormPortletSelectField<Byte>> i : fields.values()) {
			//				String text = i.getA().getValue();
			//				String color = i.getB().getValue();
			//				byte type = i.getC().getValue();
			//				if (text == null || text.length() == 0)
			//					continue;
			//				if (!text.trim().equals(text)) {
			//					whiteSpaceAlert = true;
			//				}
			//				if (!searchTexts.add(text)) {
			//					getManager().showAlert("Duplicate search not allowed: " + text);
			//					return;
			//				}
			//				Color col = WebHelper.getContrastingColor(WebHelper.parseColor(color));
			//				searchs.add(new SearchOptions(text, color, WebHelper.toString(col), type, pos++));
			//			}
			//			//			if (searchs.isEmpty()) {
			//			//				getManager().showAlert("Please enter search text");
			//			//				return;
			//			//			}
			//			if (whiteSpaceAlert)
			//				getManager().showAlert("Note, your search contains trailing or preceding white space. This may unexpectedly reduce your search results");
			//			//this.portlet.doSearch(this.saveSelectField.getValue(), searchs, this.caseSensitiveField.getBooleanValue());
			SearchTemplate st = getSearchTemplate();
			if (st != null)
				this.portlet.doSearch(st.templateName, st.searchOptions, st.isCaseSensitive);
			close();
		} else if (button == this.saveButton) {
			SearchTemplate st = getSearchTemplate();
			if (st == null)
				return;
			FormPortletField<String> field = new FormPortletTextField("").setWidth(200);
			field.setValue(this.saveSelectField.getValue());
			getManager().showDialog("Save",
					new ConfirmDialogPortlet(generateConfig(), "Save template as: ", ConfirmDialogPortlet.TYPE_OK_CANCEL, this, field).setCorrelationData("SAVE"));
		} else if (button == this.deleteButton) {
			//TODO: delete dialog
			String template = this.saveSelectField.getValue();
			if (SH.isnt(template))
				return;
			getManager().showDialog(
					"Save",
					new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete: <B>" + template, ConfirmDialogPortlet.TYPE_YES_NO, this, null)
							.setCorrelationData("DELETE"));
		}

	}
	private SearchTemplate getSearchTemplate() {
		List<SearchOptions> searches = new ArrayList<SearchOptions>();
		Set<String> searchTexts = new HashSet<String>();
		int pos = 0;
		for (Tuple3<FormPortletTextField, FormPortletColorField, FormPortletSelectField<Byte>> i : fields.values()) {
			String text = i.getA().getValue();
			String color = i.getB().getValue();
			byte type = i.getC().getValue();
			if (text == null || text.length() == 0)
				continue;
			if (!searchTexts.add(text)) {
				getManager().showAlert("Duplicate search not allowed: " + text);
				return null;
			}
			Color col = WebHelper.getContrastingColor(WebHelper.parseColor(color));
			searches.add(new SearchOptions(text, color, WebHelper.toString(col), type, pos++));
		}
		return new SearchTemplate(this.saveSelectField.getValue(), searches, this.caseSensitiveField.getValue());
	}
	private void reset() {
		for (Node<Tuple3<FormPortletTextField, FormPortletColorField, FormPortletSelectField<Byte>>> i : fields) {
			i.getValue().getA().setValue("");
			i.getValue().getB().setValue(WebHelper.getUniqueColorNoBlack(i.getIntKey()));
		}

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.saveSelectField) {
			if (SH.isnt(this.saveSelectField.getValue())) {
				this.lowrForm.removeButton(this.deleteButton);
				reset();
			} else {
				if (!this.lowrForm.hasButton(this.deleteButton.getId()))
					this.lowrForm.addButton(this.deleteButton);
				SearchTemplate st = this.templates.get(this.saveSelectField.getValue());
				this.initSearch(st.templateName, st.searchOptions, st.isCaseSensitive);
			}
		}
	}
	public void initSearch(String templateName, Collection<SearchOptions> collection, boolean searchIsCaseSensitive) {
		reset();
		this.saveSelectField.setValue(templateName);
		for (SearchOptions e : collection) {
			Tuple3<FormPortletTextField, FormPortletColorField, FormPortletSelectField<Byte>> fieldTuple = this.fields.get(e.position);
			fieldTuple.getA().setValue(e.search);
			fieldTuple.getB().setValue(e.color);
			fieldTuple.getC().setValue(e.type);
		}
		this.caseSensitiveField.setValue(searchIsCaseSensitive);
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onButton(ConfirmDialogPortlet source, String id) {
		if ("DELETE".equals(source.getCorrelationData())) {
			if (OH.eq(id, ConfirmDialogPortlet.ID_NO))
				return true;
			String template = this.saveSelectField.getValue();
			this.saveSelectField.setValue("");
			if (SH.is(template)) {
				this.saveSelectField.removeOption(template);
				this.templates.remove(template);
				saveTemplates();
			}
			return true;
		} else if ("SAVE".equals(source.getCorrelationData())) {
			String templateNameToSave = SH.trim((String) source.getInputFieldValue());
			if (OH.eq(id, ConfirmDialogPortlet.ID_NO))
				return true;
			if (SH.isnt(templateNameToSave)) {
				getManager().showAlert("Template name required");
				return false;
			}
			if (!OH.eq(this.saveSelectField.getValue(), templateNameToSave)) {
				if (this.templates.containsKey(templateNameToSave)) {
					getManager().showAlert("Template name already exists, please delete it first");
					return false;
				}
				this.saveSelectField.addOption(templateNameToSave, templateNameToSave);
				this.saveSelectField.setValue(templateNameToSave);
			}

			this.templates.put(templateNameToSave, getSearchTemplate());
			if (SH.isnt(this.saveSelectField.getValue()))
				this.lowrForm.removeButton(this.deleteButton);
			else {
				if (!this.lowrForm.hasButton(this.deleteButton.getId()))
					this.lowrForm.addButton(this.deleteButton);
			}
			saveTemplates();
			return true;
		} else
			return false;
	}

	private void saveTemplates() {
		JsonBuilder jb = new JsonBuilder();
		jb.startList();
		for (SearchTemplate i : this.templates.values()) {
			jb.startMap();
			jb.addKeyValueQuoted("name", i.templateName);
			jb.addKeyValue("caseSensitive", i.isCaseSensitive);
			jb.addKey("searches");
			jb.startList();
			for (SearchOptions search : i.searchOptions) {
				jb.startMap();
				jb.addKeyValueQuoted("search", search.search);
				jb.addKeyValueQuoted("color", search.color);
				jb.addKeyValueQuoted("fgcolor", search.fgcolor);
				jb.addKeyValue("type", search.type);
				jb.addKeyValue("pos", search.position);
				jb.endMap();
			}
			jb.endList();
			jb.endMap();
		}
		jb.endList();
		this.getManager().getUserConfigStore().saveFile("vortex_search_templates", jb.toString());

		//getManager().showAlert("Template Changes");
		//		this.lowrForm.removeButton(this.deleteButton);
		//		if (this.templates.size() > 1)
		//			this.lowrForm.addButton(this.deleteButton);
		//		for (SearchTemplate i : this.templates.values()) {
		//			this.saveSelectField.addOption(i.templateName, i.templateName);
		//			this.saveSelectField.addOption("", "-- none --");
		//		}
	}

	public static class SearchTemplate {
		public SearchTemplate(String name, List<SearchOptions> searchOptions2, boolean caseSensitive) {
			this.templateName = name;
			this.searchOptions = searchOptions2;
			this.isCaseSensitive = caseSensitive;
		}
		public List<SearchOptions> searchOptions;
		public boolean isCaseSensitive;
		public String templateName;
	}

	public static class SearchOptions {
		public static final byte TYPE_HIGHLIGHT_WORD = 1;
		public static final byte TYPE_FILTER_IN = 2;
		public static final byte TYPE_FILTER_OUT = 3;
		public static final byte TYPE_HIGHLIGHT_LINE = 4;
		final public String search;
		final public String color;
		final public byte type;
		final public int position;
		final public String fgcolor;
		public SearchOptions(String search, String color, String fgcolor, byte type, int position) {
			this.search = search;
			this.color = color;
			this.fgcolor = fgcolor;
			this.type = type;
			this.position = position;
		}

	}

}
