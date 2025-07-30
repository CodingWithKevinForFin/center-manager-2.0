package com.f1.stringmaker;

import java.util.Set;

public interface StringMaker {
	public void toString(StringMakerSession session);
	public Set<String> getReferences(Set<String> sink);
}
