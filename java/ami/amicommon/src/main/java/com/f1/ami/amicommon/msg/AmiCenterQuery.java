package com.f1.ami.amicommon.msg;

import java.util.Map;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.utils.string.Node;

/**
 * a query to run. Consider the AMI Script execute Example, this would result in one entry:
 * 
 * <PRE>
 *     create table MYTABLE as use limit=123 _OPTN="myoption" execute SELECT * FROM SOMETABLE;
 *                                 \___/      \_______________/        \_____________________/
 *                                   |                 |                        |
 *                                   |                 |                        +-> query= "SELECT * FROM SOMETABLE"
 *                                   |                 |
 *                                   |                 + -------------------------> directives= {"_OPTN" = "myoption"}
 *                                   |
 *                                   +-----------------------------------------------> limit= 123
 * </PRE>
 */
@VID("F1.VE.ACQRY")
public interface AmiCenterQuery extends Message {

	@PID(1)
	public String getQuery();
	public void setQuery(String query);

	@PID(3)
	public void setLimit(int limit);
	public int getLimit();

	@PID(4)
	public void setDirectives(Map<String, Object> directives);
	public Map<String, Object> getDirectives();

	@PID(6)
	public void setParsedNode(Node parsedNode);
	public Node getParsedNode();

	@PID(7)
	public boolean getAllowSqlInjection();
	public void setAllowSqlInjection(boolean allowSqlInjection);

}
