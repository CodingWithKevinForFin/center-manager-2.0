package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.FSQ")
public interface VortexAgentFileSearchRequest extends VortexAgentRequest {

	byte PID_SEARCH_EXPRESSION = 1;
	byte PID_INCLUDE_CHECKSUM_EXPRESSION = 2;
	byte PID_INCLUDE_DATA_EXPRESSION = 3;
	byte PID_MAX_DATA_SIZE = 4;
	byte PID_ROOT_PATHS = 5;
	byte PID_JOB_ID = 6;
	byte PID_RECURSE = 7;
	byte PID_INCLUDE_SEARCH_POSITIONS_EXPRESSION = 8;
	byte PID_DATA_OFFSET = 9;
	byte PID_SEARCH_IN_FILE_EXPRESSIONS = 10;
	byte PID_IS_SEARCH_CASE_SENSITIVE = 11;

	@PID(PID_SEARCH_EXPRESSION)
	public String getSearchExpression();
	public void setSearchExpression(String searchExpression);

	@PID(PID_INCLUDE_CHECKSUM_EXPRESSION)
	public String getIncludeChecksumExpression();
	public void setIncludeChecksumExpression(String includeChecksumExpression);

	@PID(PID_INCLUDE_DATA_EXPRESSION)
	public String getIncludeDataExpression();
	public void setIncludeDataExpression(String includeDataExpression);

	@PID(PID_INCLUDE_SEARCH_POSITIONS_EXPRESSION)
	public String getIncludeSearchPositionsExpression();
	public void setIncludeSearchPositionsExpression(String includeDataExpression);

	@PID(PID_MAX_DATA_SIZE)
	public void setMaxDataSize(long maxDataSize);
	public long getMaxDataSize();

	@PID(PID_ROOT_PATHS)
	public void setRootPaths(List<String> rootPaths);
	public List<String> getRootPaths();

	@PID(PID_JOB_ID)
	public long getJobId();
	public void setJobId(long jobId);

	@PID(PID_RECURSE)
	public boolean getRecurse();
	public void setRecurse(boolean recurse);

	@PID(PID_DATA_OFFSET)
	public long getDataOffset();
	public void setDataOffset(long recurse);

	@PID(PID_SEARCH_IN_FILE_EXPRESSIONS)
	public List<String> getSearchInFileExpressions();
	public void setSearchInFileExpressions(List<String> searchExpression);

	@PID(PID_IS_SEARCH_CASE_SENSITIVE)
	public boolean getIsSearchCaseSensitive();
	public void setIsSearchCaseSensitive(boolean isSearchCaseSensitive);
}
