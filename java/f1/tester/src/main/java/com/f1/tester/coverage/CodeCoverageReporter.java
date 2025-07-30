package com.f1.tester.coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class CodeCoverageReporter {
	private CodeCoverageResults group;

	public CodeCoverageReporter(CodeCoverageResults group) {
		this.group = group;
	}

	public String report() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, CodeCoverageClass> e : group.getClasses().entrySet()) {
			final String className = e.getKey();
			final CodeCoverageClass ccClass = e.getValue();
			if (ccClass == null)
				sb.append("CLASS_NOT_LOADED: " + className).append(SH.NEWLINE);
			else if (ccClass.isCovered())
				continue;
			else if (ccClass.isMissed())
				sb.append("CLASS_MISSED: " + className).append(SH.NEWLINE);
			else {
				sb.append("CLASS_PARTIALLY_COVERED: " + className).append(SH.NEWLINE);
				for (CodeCoverageMethod m : ccClass.getMethods()) {
					if (m.isCovered())
						continue;
					else if (m.isMissed())
						sb.append("  METHOD_MISSED: " + m.getName()).append(SH.NEWLINE);
					else {
						sb.append("  METHOD_PARTIALLY_COVERED: " + m.getName()).append(SH.NEWLINE);
						List<Tuple2<CodeCoverageItem, CodeCoverageItem>> spans = new ArrayList<Tuple2<CodeCoverageItem, CodeCoverageItem>>();
						m.getSpans(spans);
						for (Tuple2<CodeCoverageItem, CodeCoverageItem> span : spans) {
							final int start = span.getA().getLineNumber(), end = span.getB().getLineNumber();
							if (start == end)
								sb.append("    LINE_MISSED: " + start).append(SH.NEWLINE);
							else
								sb.append("    LINES_MISSED: " + start + "-" + end).append(SH.NEWLINE);
							if (ccClass.getSourceCode() == null)
								sb.append("      SOURCE_NOT_AVAIABLE: " + ccClass.getSourceFile()).append(SH.NEWLINE);
							else
								for (String line : ccClass.getSourceCode(start, end))
									sb.append("      >> " + line).append(SH.NEWLINE);
						}
					}
				}
			}
		}

		return sb.toString();
	}

}
