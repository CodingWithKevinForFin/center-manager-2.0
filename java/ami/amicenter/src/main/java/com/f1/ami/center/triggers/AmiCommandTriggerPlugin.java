package com.f1.ami.center.triggers;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.table.AmiImdb;

/**
 * An {@link AmiCommandTrigger} that is delcared using properties. See {@link AmiCenterProperties#PROPERTY_AMI_COMMAND_TRIGGERS} for declaring inside properties file.
 */

@Deprecated
public interface AmiCommandTriggerPlugin extends AmiCommandTrigger, AmiPlugin {

	/**
	 * Called once at startup, this is a good spot to grab needed tables, columns, indexes, etc that you will use at runtime
	 * 
	 * @param imdb
	 */
	public void startup(AmiImdb imdb);

}