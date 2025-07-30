package com.f1.ami.center.dbo;

public interface AmiDbo {

	public void startAmiDbo(AmiDboBinding binding) throws Throwable;
	public AmiDboBinding getAmiDboBinding();
	public void closeAmiDbo();
}
