package com.f1.ami.web.dm;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebFormula;
import com.f1.base.ToStringable;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;

public class AmiWebDmLinkWhereClause implements ToStringable {
	public static final String WHERE_CLAUSE = "clause_";
	public static final String DEFAULT_PREFIX = "((";
	public static final String DEFAULT_JOIN = ") or (";
	public static final String DEFAULT_SUFFIX = "))";
	public static final String DEFAULT_TRUE_OVERRIDE = "";
	public static final String DEFAULT_FALSE_OVERRIDE = "";
	public static final String DEFAULT_TRUE_CONST = "true";
	public static final String DEFAULT_FALSE_CONST = "false";
	private String varName;
	private AmiWebFormula whereClause;
	private String prefix;
	private String suffix;
	private String join;
	private String trueOverride;
	private String falseOverride;
	private String trueConst;
	private String falseConst;
	final private AmiWebDmLink owner;

	public AmiWebDmLinkWhereClause(AmiWebDmLink owner, String varname, String whereClause) {
		this(owner, varname, whereClause, DEFAULT_PREFIX, DEFAULT_JOIN, DEFAULT_SUFFIX, DEFAULT_TRUE_CONST, DEFAULT_FALSE_CONST, DEFAULT_TRUE_OVERRIDE, DEFAULT_FALSE_OVERRIDE);
	}
	public AmiWebDmLinkWhereClause(AmiWebDmLink owner, String varname, String whereClause, String prefix, String join, String suffix, String trueConst, String falseConst,
			String trueOverride, String falseOverride) {
		this(owner);
		OH.assertNotNull(varname);
		this.varName = varname;
		this.whereClause = owner.getFormulas().addFormulaTemplate(WHERE_CLAUSE + this.getVarName());
		this.whereClause.setFormula(whereClause, false);
		this.setPrefix(prefix);
		this.setJoin(join);
		this.setSuffix(suffix);
		this.setTrueConst(trueConst);
		this.setFalseConst(falseConst);
		this.setTrueOverride(trueOverride);
		this.setFalseOverride(falseOverride);
	}

	public AmiWebDmLinkWhereClause copy(AmiWebDmLink newOwner) {
		return new AmiWebDmLinkWhereClause(newOwner, varName, whereClause.getFormula(false), prefix, join, suffix, trueConst, falseConst, trueOverride, falseOverride);
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(varName).append('=').append(prefix).append(whereClause).append(join).append("...").append(suffix);
		return sink;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public AmiWebDmLinkWhereClause(AmiWebDmLink owner, Map<String, Object> link, StringBuilder warningsSink) {
		this(owner);
		this.init(link, warningsSink);
	}
	public AmiWebDmLinkWhereClause(AmiWebDmLink owner) {
		OH.assertNotNull(owner);
		this.owner = owner;
	}
	public void init(Map<String, Object> link, StringBuilder warningsSink) {
		this.varName = CH.getOrThrow(Caster_String.INSTANCE, link, "vn");
		this.whereClause = owner.getFormulas().addFormulaTemplate(WHERE_CLAUSE + this.getVarName());
		String wc = CH.getOr(Caster_String.INSTANCE, link, "wc", null);
		if (wc != null) {
			wc = removeLegacyTarget(wc);
		} else {
			wc = CH.getOrThrow(Caster_String.INSTANCE, link, "wh");
		}
		this.whereClause.initFormula(wc);
		this.setPrefix(CH.getOrThrow(Caster_String.INSTANCE, link, "pf"));
		this.setJoin(CH.getOrThrow(Caster_String.INSTANCE, link, "j"));
		this.setSuffix(CH.getOrThrow(Caster_String.INSTANCE, link, "sf"));
		this.setTrueConst(CH.getOrThrow(Caster_String.INSTANCE, link, "tr"));
		this.setFalseConst(CH.getOrThrow(Caster_String.INSTANCE, link, "fl"));
		this.setTrueOverride(CH.getOr(Caster_String.INSTANCE, link, "to", null));
		this.setFalseOverride(CH.getOr(Caster_String.INSTANCE, link, "fo", null));
	}

	static private String removeLegacyTarget(String wc) {

		String r = wc.replaceAll("\\$\\{`Target_([^`]*)\\`}", "$1");
		r = r.replaceAll("\\$\\{Target_(\\w*)\\}", "$1");
		return r;
	}
	//	public static void main(String a[]) {
	//		String s = removeLegacyTarget("this = ${Target_test123} and that = ${`Target_test 456`} this = ${Target_test789} and that = ${`Target_test abc`}");
	//		System.out.println(s);
	//	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("vn", varName);
		m.put("wh", whereClause.getFormulaConfig());
		m.put("pf", prefix);
		m.put("j", join);
		m.put("sf", suffix);
		m.put("tr", trueConst);
		m.put("fl", falseConst);
		m.put("to", trueOverride);
		m.put("fo", falseOverride);
		return m;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		if (OH.eq(this.varName, varName))
			return;
		this.varName = varName;
		AmiWebFormula old = owner.getFormulas().removeFormula(this.whereClause.getFormulaId());
		this.whereClause = owner.getFormulas().addFormulaTemplate(WHERE_CLAUSE + this.getVarName());
		if (old != null)
			this.whereClause.setFormula(old.getFormula(false), false);
	}
	public AmiWebFormula getWhereClause() {
		return whereClause;
	}
	//	public void setWhereClause(String whereClause) {
	//		this.whereClause = whereClause;
	//	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getJoin() {
		return join;
	}
	public void setJoin(String join) {
		this.join = join;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getTrueConst() {
		return trueConst;
	}
	public void setTrueConst(String trueConst) {
		this.trueConst = trueConst;
	}
	public String getFalseConst() {
		return falseConst;
	}
	public void setFalseConst(String falseConst) {
		this.falseConst = falseConst;
	}
	public String getTrueOverride() {
		return trueOverride;
	}
	public void setTrueOverride(String trueOverride) {
		this.trueOverride = trueOverride;
	}
	public String getFalseOverride() {
		return falseOverride;
	}
	public void setFalseOverride(String falseOverride) {
		this.falseOverride = falseOverride;
	}

}
