package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebService;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.BasicMethodFactory;

public class AmiWebScriptMemberMethods {

	@SuppressWarnings("rawtypes")
	static public void registerMethods(AmiWebService service, BasicMethodFactory mFactory) {
		List<AmiWebScriptBaseMemberMethods> outerMethodsList;
		outerMethodsList = CH.l(//
				AmiWebScriptMemberMethods_Session.INSTANCE//
				, AmiWebScriptMemberMethods_SsoSession.INSTANCE//
				, AmiWebScriptMemberMethods_Panel.INSTANCE//
				, AmiWebScriptMemberMethods_Relationship.INSTANCE//
				, AmiWebScriptMemberMethods_FormPanel.INSTANCE//
				, AmiWebScriptMemberMethods_FormField.INSTANCE//
				, AmiWebScriptMemberMethods_FormSelectField.INSTANCE//
				, AmiWebScriptMemberMethods_FormButtonField.INSTANCE//
				, AmiWebScriptMemberMethods_FormColorPickerField.INSTANCE//
				, AmiWebScriptMemberMethods_FormColorGradientPickerField.INSTANCE//
				, AmiWebScriptMemberMethods_TablePanel.INSTANCE//
				, AmiWebScriptMemberMethods_Datamodel.INSTANCE//
				, AmiWebScriptMemberMethods_Window.INSTANCE//
				, AmiWebScriptMemberMethods_MenuItem.INSTANCE//
				, AmiWebScriptMemberMethods_FormMultiCheckboxField.INSTANCE//
				, AmiWebScriptMemberMethods_FormMultiSelectField.INSTANCE//
				, AmiWebScriptMemberMethods_TabsPanel.INSTANCE//
				, AmiWebScriptMemberMethods_Tab.INSTANCE//
				, AmiWebScriptMemberMethods_CommandResponse.INSTANCE//
				, AmiWebScriptMemberMethods_FormUploadField.INSTANCE//
				, AmiWebScriptMemberMethods_RpcRequest.INSTANCE//
				, AmiWebScriptMemberMethods_RpcResponse.INSTANCE//
				, AmiWebScriptMemberMethods_FormRangeSliderField.INSTANCE//
				, AmiWebScriptMemberMethods_FormDateField.INSTANCE//
				, AmiWebScriptMemberMethods_FormTimeField.INSTANCE//
				, AmiWebScriptMemberMethods_FormDateRangeField.INSTANCE//
				, AmiWebScriptMemberMethods_FormTimeRangeField.INSTANCE//
				, AmiWebScriptMemberMethods_FormDateTimeField.INSTANCE//
				, AmiWebScriptMemberMethods_FormSliderField.INSTANCE//
				, AmiWebScriptMemberMethods_TablePanelColumnFilter.INSTANCE//
				, AmiWebScriptMemberMethods_PdfBuilder.INSTANCE// 
				, AmiWebScriptMemberMethods_PdfText.INSTANCE// 
				, AmiWebScriptMemberMethods_ChartPanel.INSTANCE//
				, AmiWebScriptMemberMethods_Layout.INSTANCE//
				, AmiWebScriptMemberMethods_DividerPanel.INSTANCE//
				, AmiWebScriptMemberMethods_FormTextField.INSTANCE//
				, AmiWebScriptMemberMethods_DevTools.INSTANCE//
				, AmiWebScriptMemberMethods_TreePanel.INSTANCE//
				, AmiWebScriptMemberMethods_FilterPanel.INSTANCE//
				, AmiWebScriptMemberMethods_SurfacePanel.INSTANCE//
				, AmiWebScriptMemberMethods_SurfacePanel.INSTANCE//
				, AmiWebScriptMemberMethods_SurfacePanelLayer.INSTANCE//
				, AmiWebScriptMemberMethods_SurfacePanelAxis.INSTANCE//
				, AmiWebScriptMemberMethods_ChartPanelAxis.INSTANCE//
				, AmiWebScriptMemberMethods_ChartPanelPlot.INSTANCE//
				, AmiWebScriptMemberMethods_ChartPanelLayer.INSTANCE//
				, AmiWebScriptMemberMethods_HeatmapPanel.INSTANCE//
				, AmiWebScriptMemberMethods_FormCheckboxField.INSTANCE//
				, AmiWebScriptMemberMethods_FormImageField.INSTANCE//
				, AmiWebScriptMemberMethods_StyleSet.INSTANCE//
				, AmiWebScriptMemberMethods_StyleOptionDefinition.INSTANCE//
				, AmiWebScriptMemberMethods_ScrollPanel.INSTANCE//
				, AmiWebScriptMemberMethods_DashboardResource.INSTANCE//
				, AmiWebScriptMemberMethods_FormRadioButtonField.INSTANCE//
				, AmiWebScriptMemberMethods_TablePanelColumn.INSTANCE //
				, AmiWebScriptMemberMethods_Processor.INSTANCE //
				, AmiWebScriptMemberMethods_TreePanelColumn.INSTANCE // 
				, AmiWebScriptMemberMethods_TreePanelGrouping.INSTANCE//
				, AmiWebScriptMemberMethods_SpreadSheetBuilder.INSTANCE //
				, AmiWebScriptMemberMethods_SpreadSheetWorksheet.INSTANCE //
				, AmiWebScriptMemberMethods_SpreadSheetFlexsheet.INSTANCE //
				, AmiWebScriptMemberMethods_Callback.INSTANCE//
				, AmiWebScriptMemberMethods_FileSystem.INSTANCE//
				, AmiWebScriptMemberMethods_FormPasswordField.INSTANCE //
				, AmiWebScriptMemberMethods_FormTextareaField.INSTANCE //
				, AmiWebScriptMemberMethods_KeyEvent.INSTANCE //
				, AmiWebScriptMemberMethods_MouseEvent.INSTANCE //
				, AmiWebScriptMemberMethods_FormRelationshipButton.INSTANCE //
				, AmiWebScriptMemberMethods_RealtimeEvent.INSTANCE //
				, AmiWebScriptMemberMethods_Formula.INSTANCE //
				, AmiWebScriptMemberMethods_ChartPanelLegend.INSTANCE //
				, AmiWebScriptMemberMethods_FormDivField.INSTANCE //
				, AmiWebScriptMemberMethods_BloombergPipeProcessor.INSTANCE);

		Class<?> dfltImpl = null;
		String description = null;
		String varTypeName;
		for (AmiWebScriptBaseMemberMethods<?> outer : outerMethodsList) {
			mFactory.addClassDebugInspector(outer);
			dfltImpl = outer.getVarDefaultImpl();
			varTypeName = outer.getVarTypeName();
			if (dfltImpl == null)
				mFactory.addVarType(varTypeName, outer.getVarType());
			else
				mFactory.addVarType(varTypeName, outer.getVarType(), dfltImpl);
			description = outer.getVarTypeDescription();
			if (description != null)
				mFactory.addVarTypeDescription(varTypeName, description);
			for (AmiAbstractMemberMethod<?> inner : outer.getMethods())
				mFactory.addMemberMethod(inner);
		}
	}
}