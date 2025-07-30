package com.f1.utils.xml;

import com.f1.base.ToStringable;

public interface XmlNode extends ToStringable {

	public byte VERSION_XML10 = 0;
	public byte VERSION_XML11 = 1;
	public byte VERSION_DEFAULT = VERSION_XML10;

	StringBuilder toLegibleString(StringBuilder sink, int i);

	StringBuilder toLegibleString(StringBuilder sink, int i, byte version);
	StringBuilder toString(StringBuilder sink, byte version);

}
