package com.f1.ami.web.amiscript;

import com.f1.ami.amicommon.AmiScriptDerivedCellParser;
import com.f1.container.ContainerTools;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.structs.table.derived.BasicExternFactoryManager;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;

public class AmiWebAmiScriptDerivedCellParser extends AmiScriptDerivedCellParser {

	static public class AmiWebDeclaredMethodFactory extends DeclaredMethodFactory {

		private String layoutAlias;

		public AmiWebDeclaredMethodFactory(Class<?> returnType, String methodName, String[] argumentNames, Class[] argumentTypes, byte modifiers, String layoutAlias) {
			super(returnType, methodName, argumentNames, argumentTypes, modifiers);
			this.layoutAlias = layoutAlias;
		}

		public String getLayoutAlias() {
			return this.layoutAlias;
		}

	}

	private String layoutAlias;

	public AmiWebAmiScriptDerivedCellParser(ExpressionParser parser, SqlProcessor sqlProcessor, ContainerTools tools, BasicExternFactoryManager externFactory, String layoutAlias,
			boolean optimized) {
		super(parser, sqlProcessor, tools, externFactory, optimized);
		this.layoutAlias = layoutAlias;
	}

	@Override
	protected DeclaredMethodFactory newDeclaredMethodFactory(Class<?> returnType, String methodName, String[] argumentNames, Class[] argumentTypes, byte modifiers) {
		AmiWebDeclaredMethodFactory r = new AmiWebDeclaredMethodFactory(returnType, methodName, argumentNames, argumentTypes, modifiers, this.layoutAlias);
		r.setInnerLabel("CUSTOM_METHODS:" + this.layoutAlias);
		return r;
	}

}
