package com.f1.ami.relay.fh.aeron.fix;
import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class fields {
	@JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<field> field;
    
    public fields() {}
}