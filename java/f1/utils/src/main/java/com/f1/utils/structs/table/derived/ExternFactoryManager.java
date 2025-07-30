package com.f1.utils.structs.table.derived;

import java.util.Set;

public interface ExternFactoryManager {

	public Extern getExternNoThrow(String language);
	public Set<String> getSupportedLanguages();

}
