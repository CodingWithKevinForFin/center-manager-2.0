package com.f1.ami.web.functions;

import java.util.logging.Logger;

import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.auth.AmiSsoSession;
import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator0;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionAccessToken extends AbstractMethodDerivedCellCalculator0 {

	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("accessToken", String.class, "");
	static {
		VERIFIER.addDesc("Returns the current access token as supplied by the sso plugin.");
	}
	private AmiSsoSession ss;

	public AmiWebFunctionAccessToken(int position, AmiSsoSession amiWebSsoSession) {
		super(position);
		this.ss = amiWebSsoSession;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		return String.class;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiWebFunctionAccessToken(getPosition(), this.ss);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private AmiWebService service;

		public Factory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionAccessToken(position, this.service.getVarsManager().getSsoSession());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	@Override
	public Object eval() {
		return ss.getAccessToken();
	}

}
