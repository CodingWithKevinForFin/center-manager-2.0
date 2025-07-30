package com.f1.office.spreadsheet;

import com.f1.base.Clearable;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.utils.xml.XmlElement;

public abstract class SpreadSheetResource implements Lockable, Clearable {

	private int id = -1;
	private boolean locked = false;
	private XmlElement e = null;

	final public XmlElement getXml() {
		return this.e;
	}
	
	final public void setXml(final XmlElement e) {
		this.e = e;
	}
	
	final public int getId() {
		if (id == -1)
			throw new IllegalStateException("id not set");
		return id;
	}

	final public void setId(int id) {
		assertNotLocked();
		this.id = id;
	}

	final public void assertNotLocked() {
		LockedException.assertNotLocked(this);
	}

	@Override
	final public void lock() {
		this.locked = true;
	}
	@Override
	final public boolean isLocked() {
		return this.locked;
	}

}
