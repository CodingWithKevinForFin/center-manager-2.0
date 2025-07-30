package com.f1.ami.web;

import java.io.File;
import java.io.IOException;

import com.f1.utils.IOH;

public class AmiWebFile_Local implements AmiWebFile {

	private final File file;

	public AmiWebFile_Local(String basePath, String string) {
		this.file = new File(basePath, string);
	}
	public AmiWebFile_Local(AmiWebFile basePath, String string) {
		this.file = new File(basePath.getFullPath(), string);
	}
	public AmiWebFile_Local(File file) {
		this.file = file;
	}
	public AmiWebFile_Local(String string) {
		this.file = new File(string);
	}
	@Override
	public long length() {
		return file.length();
	}
	@Override
	public String getName() {
		return file.getName();
	}
	@Override
	public long lastModified() {
		return file.lastModified();
	}
	@Override
	public String getFullPath() {
		return IOH.getFullPath(file);
	}
	@Override
	public boolean exists() {
		return file.exists();
	}
	@Override
	public boolean canWrite() {
		return file.canWrite();
	}
	@Override
	public boolean canRead() {
		return file.canRead();
	}
	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}
	@Override
	public AmiWebFile[] listFiles() {
		final File[] files = file.listFiles();
		if (files == null)
			return null;
		final AmiWebFile_Local[] amiFiles = new AmiWebFile_Local[files.length];
		for (int i = 0; i < files.length; i++)
			amiFiles[i] = new AmiWebFile_Local(files[i]);
		return amiFiles;
	}
	@Override
	public String[] list() {
		return file.list();
	}
	@Override
	public boolean isFile() {
		return file.isFile();
	}
	@Override
	public boolean isHidden() {
		return file.isHidden();
	}
	@Override
	public boolean canExecute() {
		return file.canExecute();
	}
	@Override
	public AmiWebFile getParentFile() {
		File f = file.getParentFile();
		if (f == null)
			return null;
		return new AmiWebFile_Local(f);
	}
	@Override
	public String getParent() {
		return file.getParent();
	}
	@Override
	public void mkdirForce() throws IOException {
		IOH.ensureDir(this.file);
	}

	@Override
	public void appendText(String text) throws IOException {
		IOH.appendText(file, text);
	}
	@Override
	public void writeText(String text) throws IOException {
		IOH.writeText(file, text);
	}
	@Override
	public String readTextForce() throws IOException {
		if (IOH.isSymlink(file))
			return IOH.readText(new File(IOH.getFullPath(file)), true);
		else
			return IOH.readText(file, true);
	}

	@Override
	public void appendBytes(byte[] data) throws IOException {
		IOH.appendData(file, data);
	}
	@Override
	public void writeBytes(byte[] data) throws IOException {
		IOH.writeData(file, data);
	}
	@Override
	public byte[] readBytes() throws IOException {
		if (IOH.isSymlink(file))
			return IOH.readData(new File(IOH.getFullPath(file)));
		else
			return IOH.readData(file);
	}

	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	@Override
	public String getPath() {
		return file.getPath();
	}
	@Override
	public boolean delete() {
		return file.delete();
	}
	@Override
	public boolean move(AmiWebFile nuw) {
		return file.renameTo(new File(nuw.getPath()));
	}
	@Override
	public boolean mkdir() {
		return file.mkdir();
	}
	@Override
	public String readText() throws IOException {
		return IOH.readText(file);
	}
	@Override
	public boolean setWritable(boolean b) {
		return file.setWritable(b);
	}
	@Override
	public void deleteForce() throws IOException {
		IOH.delete(file);
	}
	@Override
	public void deleteForceRecursive() throws IOException {
		IOH.deleteForce(file);
	}
	@Override
	public void moveForce(AmiWebFile targetFile) {
		IOH.moveForce(file, new File(targetFile.getPath()));
	}

	@Override
	public File getLocalFile() {
		return this.file;
	}
	@Override
	public void refreshFlags() throws IOException {
	}

	@Override
	public String toString() {
		return this.file.toString();
	}
}
