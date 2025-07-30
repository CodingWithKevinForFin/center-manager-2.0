package com.f1.ami.relay.fh.aeron.fix;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class field {
	@JacksonXmlProperty(isAttribute = true)
	public String number;
	@JacksonXmlProperty(isAttribute = true)
	public String name;
	@JacksonXmlProperty(isAttribute = true)
	public String type;
	
	@JacksonXmlElementWrapper(useWrapping = false)
	public ArrayList<value> value = new ArrayList<value>();
	
	public field() {}
}