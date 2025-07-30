package com.f1.utils.code;

import java.util.Set;
import com.f1.base.ObjectGenerator;
import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class CodeWriter {

	private ObjectGenerator generator;

	public CodeWriter(ObjectGenerator g) {
		this.generator = g;
	}

	public StringBuilder writeCopy(Class<? extends Valued> lc, Class<? extends Valued> rc, String leftVar, String rightVar, StringBuilder sb) {
		ValuedSchema<? extends Valued> l = this.generator.nw(lc).askSchema();
		ValuedSchema<? extends Valued> r = this.generator.nw(rc).askSchema();
		Set<String> ls = CH.s(l.askParams());
		Set<String> rs = CH.s(r.askParams());
		for (String s : CH.comm(ls, rs, false, true, false)) {
			sb.append(rightVar).append(".get").append(SH.uppercaseFirstChar(s)).append("();//TODO").append(SH.NEWLINE);
		}
		for (String s : CH.comm(ls, rs, false, false, true)) {
			sb.append(leftVar).append(".set").append(SH.uppercaseFirstChar(s)).append("(");
			sb.append(rightVar).append(".get").append(SH.uppercaseFirstChar(s)).append("()");
			sb.append(");").append(SH.NEWLINE);
		}
		for (String s : CH.comm(ls, rs, true, false, false)) {
			sb.append(leftVar).append(".set").append(SH.uppercaseFirstChar(s)).append("(");
			sb.append(OH.getDefaultValueString(l.askClass(s)));
			sb.append(");//TODO").append(SH.NEWLINE);
		}
		return sb;
	}
}
