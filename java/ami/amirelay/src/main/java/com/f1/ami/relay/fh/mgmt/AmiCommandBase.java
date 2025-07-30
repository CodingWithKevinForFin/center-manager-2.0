package com.f1.ami.relay.fh.mgmt;

import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public abstract class AmiCommandBase implements AmiCommand {
	protected static final Logger log = LH.get();

	protected String id = "NOT DEFINED";
	protected String title;
	protected String help;
	protected int permLevel;
	protected String whereClause;
	protected String args;
	protected PropertyController props;
	protected String enabled;
	protected int priority;
	protected String style;
	protected String selectMode;
	protected String fields;
	protected String filterClause;
	protected int callbacksMask;

	@Override
	public void init(PropertyController props) {
		this.props = props;
		iniFromProps();
	}

	private void iniFromProps() {
		//try to get as much as possible from props...but allow subclasses to do what they want
		id = props.getOptional("I");
		title = props.getOptional("N");
		permLevel = props.getOptional("L", Caster_Integer.PRIMITIVE);
		whereClause = props.getOptional("W");
		filterClause = props.getOptional("T");
		help = props.getOptional("H");
		args = props.getOptional("A");
		enabled = props.getOptional("E");
		priority = SH.parseInt(props.getOptional("P", "-1"));
		style = props.getOptional("S");
		selectMode = props.getOptional("M");
		fields = props.getOptional("F");
		callbacksMask = props.getOptional("C", AmiRelayCommandDefMessage.CALLBACK_USER_CLICK);
	}
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getHelp() {
		return help;
	}

	@Override
	public int getPermissionLevel() {
		return permLevel;
	}

	@Override
	public String getWhereClause() {
		return whereClause;
	}

	@Override
	public String getArgs() {
		return args;
	}

	@Override
	public String getEnabledClause() {
		return enabled;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public String getStyle() {
		return style;
	}

	@Override
	public String getSelectMode() {
		return selectMode;
	}

	@Override
	public String getFields() {
		return fields;
	}

	@Override
	public String getFilterClause() {
		return filterClause;
	}

	@Override
	public int getCallbacksMask() {
		return callbacksMask;
	}

	@Override
	public String getScript() {
		return null;
	}
}
