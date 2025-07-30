package com.f1.ami.web.charts;

import com.f1.suite.web.menu.WebMenu;

public interface AmiWebContextMenuFactory {
	void populateConfigMenu(WebMenu headMenu);
	void populateLowerConfigMenu(WebMenu headMenu);
}
