package com.vortex.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.f1.utils.LocalToolkit;
import com.f1.utils.PropertiesHelper;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;

public class VortexClientBuildProcedure extends VortexClientEntity<VortexBuildProcedure> {

	private LongKeyMap<VortexClientBuildResult> buildResults = new LongKeyMap<VortexClientBuildResult>();

	public VortexClientBuildProcedure(VortexBuildProcedure data) {
		super(VortexAgentEntity.TYPE_BUILD_PROCEDURE, data);
	}

	public Set<String> getVariables() {
		LocalToolkit tk = new LocalToolkit();
		VortexBuildProcedure t = getData();
		Set<String> variableNames = new HashSet<String>();
		findVariables(t.getTemplateCommand(), variableNames, tk);
		findVariables(t.getTemplateResultFile(), variableNames, tk);
		findVariables(t.getTemplateResultVerifyFile(), variableNames, tk);
		findVariables(t.getTemplateResultName(), variableNames, tk);
		findVariables(t.getTemplateResultVersion(), variableNames, tk);
		findVariables(t.getTemplateStdin(), variableNames, tk);
		findVariables(t.getTemplateUser(), variableNames, tk);
		return variableNames;
	}

	public static void findVariables(String text, Set<String> variablesSink, LocalToolkit tk) {
		if (SH.isnt(text))
			return;
		try {
			final ArrayList<String> sink = new ArrayList<String>();
			final StringBuilder buf = tk.borrowStringBuilder();
			PropertiesHelper.splitVariables(text, sink, buf);
			for (int i = 1; i < sink.size(); i += 2)
				variablesSink.add(sink.get(i));
		} finally {
			tk.returnAll();
		}
	}

	public void addBuildResult(VortexClientBuildResult exp) {
		buildResults.put(exp.getId(), exp);
	}

	public VortexClientBuildResult removeBuildResult(VortexClientBuildResult exp) {
		return buildResults.remove(exp.getId());
	}

	public Iterable<VortexClientBuildResult> getBuildResults() {
		return buildResults.values();
	}

}
