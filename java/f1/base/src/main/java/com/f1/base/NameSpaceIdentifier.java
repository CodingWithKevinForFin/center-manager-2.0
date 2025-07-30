package com.f1.base;

public class NameSpaceIdentifier {

	final private String namespace;
	final private String varname;

	public NameSpaceIdentifier(String namespace, String varname) {
		if (namespace == null)
			throw new NullPointerException("namespace");
		if (varname == null)
			throw new NullPointerException("varname");
		this.namespace = namespace;
		this.varname = varname;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getVarName() {
		return varname;
	}

	@Override
	public String toString() {
		return namespace + "." + varname;
	}

	@Override
	public int hashCode() {
		return varname.hashCode() ^ namespace.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NameSpaceIdentifier other = (NameSpaceIdentifier) obj;
		return varname.equals(other.varname) && namespace.equals(other.namespace);
	}

}
