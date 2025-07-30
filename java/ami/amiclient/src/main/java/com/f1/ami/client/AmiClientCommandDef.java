package com.f1.ami.client;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiClientCommandDef {

	static final public String CONDITION_USER_LOGIN = "user_open_layout";
	static final public String CONDITION_USER_LOGOUT = "user_close_layout";
	static final public String CONDITION_USER_CLICK = "user_click";
	static final public String CONDITION_NOW = "now";

	private String commandId;
	private Integer level;
	private String whereClause;
	private String help;
	private String argumentsJson;
	private String name;
	private Integer priority;
	private String enabledExpression;
	private String style;
	private String selectMode;
	private String fields;
	private String filterClause;
	private String conditions;
	private String amiScript;

	public AmiClientCommandDef(String id) {
		this.commandId = id;
	}

	public String getCommandId() {
		return commandId;
	}
	public AmiClientCommandDef setCommandId(String commandId) {
		this.commandId = commandId;
		return this;
	}
	public Integer getLevel() {
		return level;
	}
	public AmiClientCommandDef setLevel(Integer level) {
		this.level = level;
		return this;
	}
	public String getWhereClause() {
		return whereClause;
	}
	public AmiClientCommandDef setWhereClause(String whereClause) {
		this.whereClause = whereClause;
		return this;
	}
	public String getHelp() {
		return help;
	}
	public AmiClientCommandDef setHelp(String help) {
		this.help = help;
		return this;
	}
	public String getArgumentsJson() {
		return argumentsJson;
	}
	public AmiClientCommandDef setArgumentsJson(String argumentsJson) {
		this.argumentsJson = argumentsJson;
		return this;
	}
	public String getName() {
		return name;
	}
	public AmiClientCommandDef setName(String name) {
		this.name = name;
		return this;
	}
	public Integer getPriority() {
		return priority;
	}
	public AmiClientCommandDef setPriority(Integer priority) {
		this.priority = priority;
		return this;
	}
	public String getEnabledExpression() {
		return enabledExpression;
	}
	public AmiClientCommandDef setEnabledExpression(String enabledExpression) {
		this.enabledExpression = enabledExpression;
		return this;
	}
	public String getStyle() {
		return style;
	}
	public AmiClientCommandDef setStyle(String style) {
		this.style = style;
		return this;
	}
	public String getSelectMode() {
		return selectMode;
	}
	public AmiClientCommandDef setSelectMode(Integer min, Integer max) {
		if (OH.eq(min, max))
			this.selectMode = min == null ? null : SH.toString(min);
		else if (min == null)
			this.selectMode = '-' + SH.toString(max);
		else if (max == null)
			this.selectMode = SH.toString(min) + '-';
		else
			this.selectMode = SH.toString(min) + '-' + SH.toString(max);
		return this;
	}
	public String getFields() {
		return fields;
	}
	public AmiClientCommandDef setFields(String fields) {
		this.fields = fields;
		return this;
	}
	public String getFilterClause() {
		return filterClause;
	}
	public AmiClientCommandDef setFilterClause(String filterClause) {
		this.filterClause = filterClause;
		return this;
	}
	public String getConditions() {
		return conditions;
	}
	public AmiClientCommandDef setConditions(String... conditions) {
		this.conditions = conditions.length == 0 ? null : SH.join(",", conditions);
		return this;
	}
	public String getAmiScript() {
		return amiScript;
	}
	public AmiClientCommandDef setAmiScript(String amiScript) {
		this.amiScript = amiScript;
		return this;
	}

}
