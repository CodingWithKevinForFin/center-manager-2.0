package com.f1.ami.amicommon.msg;

import java.util.List;
import java.util.Map;

import com.f1.utils.structs.table.derived.TimeoutController;

/**
 * an upload to run. Consider the AMI Script Example, this would result in one entry:
 * 
 * <PRE>
 *     use _OPTN="myoption" INSERT INTO mytable(col1,col2,col3) FROM SELECT * FROM SOMETABLE;
 *         \______________/             \__________________________________________________/
 *               |                                   |
 *               |                                   +-> data (note, AMISCRIPT syntax currently only support one table at a time)
 *               |
 *               +-------------------------------------> directives:  {OPTN = "myoption"}
 * </PRE>
 */
public interface AmiCenterUpload {
	public List<AmiCenterUploadTable> getData();
	public Map<String, Object> getDirectives();
	public TimeoutController getTimeout();
}
