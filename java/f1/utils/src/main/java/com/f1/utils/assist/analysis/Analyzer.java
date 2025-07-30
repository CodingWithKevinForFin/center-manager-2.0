package com.f1.utils.assist.analysis;

public interface Analyzer {
	public void visitObject(AnalyzerSession session, Object obj, ClassAnalyzer clazz) throws Exception;
	public void visitArray(AnalyzerSession session, boolean[] array) throws Exception;
	public void visitArray(AnalyzerSession session, byte[] array) throws Exception;
	public void visitArray(AnalyzerSession session, char[] array) throws Exception;
	public void visitArray(AnalyzerSession session, short[] array) throws Exception;
	public void visitArray(AnalyzerSession session, int[] array) throws Exception;
	public void visitArray(AnalyzerSession session, float[] array) throws Exception;
	public void visitArray(AnalyzerSession session, double[] array) throws Exception;
	public void visitArray(AnalyzerSession session, long[] array) throws Exception;
	public void visitPrimitive(AnalyzerSession session, boolean primitive) throws Exception;
	public void visitPrimitive(AnalyzerSession session, byte primitive) throws Exception;
	public void visitPrimitive(AnalyzerSession session, char primitive) throws Exception;
	public void visitPrimitive(AnalyzerSession session, short primitive) throws Exception;
	public void visitPrimitive(AnalyzerSession session, int primitive) throws Exception;
	public void visitPrimitive(AnalyzerSession session, float primitive) throws Exception;
	public void visitPrimitive(AnalyzerSession session, double primitive) throws Exception;
	public void visitPrimitive(AnalyzerSession session, long primitive) throws Exception;
	public void visitNull(AnalyzerSession session);
}
