package com.f1.email;

import java.util.Set;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class MimeType {

	final private String name;
	final private String catagory;
	final private String subCatagory;
	final private Set<String> fileExtensions;

	public MimeType(String catagory, String subCatagory, String fileExtensions) {
		this.catagory = catagory.toLowerCase();
		this.subCatagory = subCatagory.toLowerCase();
		this.name = this.catagory + '/' + this.subCatagory;
		this.fileExtensions = CH.s(SH.split(',', fileExtensions.toLowerCase()));
	}

	public String getName() {
		return name;
	}

	public String getCatagory() {
		return catagory;
	}

	public String getSubCatagory() {
		return subCatagory;
	}

	public Set<String> getFileExtensions() {
		return fileExtensions;
	}

	public String toString() {
		return getClass().getSimpleName() + ": " + getName();
	}

}
