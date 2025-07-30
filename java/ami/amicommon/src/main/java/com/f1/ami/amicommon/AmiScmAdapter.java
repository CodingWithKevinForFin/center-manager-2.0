package com.f1.ami.amicommon;

import java.util.List;
import java.util.Map;

import com.f1.container.ContainerTools;

public interface AmiScmAdapter {
	byte STATUS_PRIVATE = 1; //Untracked
	byte STATUS_CHECKED_IN = 2; //Tracked
	byte STATUS_CHECKED_OUT = 3; // Changed //Modified and staged
	byte STATUS_MARKED_FOR_ADD = 4; //Added and Staged
	byte STATUS_MARKED_FOR_DELETE = 5; //Removed and Staged
	byte STATUS_CONFLICTING = 6; //Conflicting
	byte STATUS_MODIFIED = 7; // Modified 
	byte STATUS_DIRECTORY = -1;
	byte STATUS_HISTORY = -2;
	byte TYPE_TEXT = 10;
	byte TYPE_BINARY = 11;

	void init(ContainerTools tools, String url, String client, String username, char[] password, String basePath, String options) throws AmiScmException;
	void addFiles(List<String> fileName, byte type) throws AmiScmException;
	void editFiles(List<String> fileName) throws AmiScmException;
	void deleteFiles(List<String> fileName) throws AmiScmException;
	void revertFiles(List<String> fileName) throws AmiScmException;
	void commitFiles(List<String> files, String comment) throws AmiScmException;
	List<String> getFileNames(String directory) throws AmiScmException;
	byte[] getFile(String fileName, String changelist) throws AmiScmException;//null is latest
	List<AmiScmRevision> getHistory(String fileName) throws AmiScmException;
	Map<String, Byte> getFileStatus(List<String> files) throws AmiScmException;//filename to status
	Map<String, String> getCurrentFileChangelists(List<String> files) throws AmiScmException;//filename to local changelist
	void syncToChangelists(Map<String, String> files) throws AmiScmException;//filename to local changelist, if changelist is null then latest
	void syncDirectories(List<String> directories) throws AmiScmException;//filename to local changelist, if changelist is null then latest
	String getRootDirectory() throws AmiScmException;
	String getStatusName(byte statusType);

}
