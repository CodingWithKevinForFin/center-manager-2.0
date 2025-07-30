/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.container.Suite;
import com.f1.container.SuiteController;

public class BasicSuiteController extends AbstractContainerScope implements SuiteController {

	private Suite suite;

	public BasicSuiteController() {
		suite = new BasicSuite();
		suite.setName("rootSuite");
		addChildContainerScope(suite);
	}

	@Override
	public Suite getRootSuite() {
		return suite;
	}

}
