package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.charts.AmiWebSurfaceRenderingLayer;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_SurfacePanelLayer extends AmiWebScriptBaseMemberMethods<AmiWebSurfaceRenderingLayer> {

	private AmiWebScriptMemberMethods_SurfacePanelLayer() {
		super();
		addMethod(GET_POSITION, "position");
		addMethod(GET_DATAMODEL, "datamodel");
	}

	@Override
	public String getVarTypeName() {
		return "SurfacePanelLayer";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Surface Panel Layer";
	}

	@Override
	public Class<AmiWebSurfaceRenderingLayer> getVarType() {
		return AmiWebSurfaceRenderingLayer.class;
	}

	@Override
	public Class<AmiWebSurfaceRenderingLayer> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebSurfaceRenderingLayer> GET_POSITION = new AmiAbstractMemberMethod<AmiWebSurfaceRenderingLayer>(
			AmiWebSurfaceRenderingLayer.class, "getPosition", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfaceRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSeries().getPosition();
		}

		@Override
		protected String getHelp() {
			return "Returns the position of the layer (zero index based).";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebSurfaceRenderingLayer> GET_DATAMODEL = new AmiAbstractMemberMethod<AmiWebSurfaceRenderingLayer>(
			AmiWebSurfaceRenderingLayer.class, "getDatamodel", AmiWebDm.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfaceRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getService().getDmManager().getDmByAliasDotName(targetObject.getSeries().getDmAliasDotName());
		}

		@Override
		protected String getHelp() {
			return "Returns the underlying datamodel used by the layer.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final static AmiWebScriptMemberMethods_SurfacePanelLayer INSTANCE = new AmiWebScriptMemberMethods_SurfacePanelLayer();
}
