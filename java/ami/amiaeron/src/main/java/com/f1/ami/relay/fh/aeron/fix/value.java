package com.f1.ami.relay.fh.aeron.fix;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class value {
	@JacksonXmlProperty(isAttribute = true)
	public String _enum;
	@JacksonXmlProperty(isAttribute = true)
	public String description;
	
	public value() {}
}