package com.f1.vortex.tester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TesterMain extends RunListener {

	private static final int EXIT_CODE_CRITICAL = 5;
	private static final int EXIT_CODE_FAILED = 4;
	private static final int EXIT_CODE_ASSERT_FAILURES = 3;
	private static final int EXIT_CODE_IGNORED = 2;
	private static final int EXIT_CODE_NO_TESTS = 1;
	private static final int EXIT_CODE_SUCCESS = 0;
	private static final String IGNORED = "IGNORED";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILURE = "FAILURE";
	private static final String ASSUMPTION_FAILURE = "ASSUMPTION_FAILURE";

	private PrintStream out;

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");

	private long startMs;

	private boolean failed;

	private Description description;

	private int ignoredCount;
	private int successCount;
	private int failuresCount;
	private int assumptionFailuresCount;
	private int totalsCount;

	public TesterMain(PrintStream out, List<String> headers) {
		this.out = out;
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		for (String s : headers)
			logHeader(s);
	}

	public static void main(String a[]) throws Throwable {
		List<String> clazzes = new ArrayList<String>();
		List<String> headers = new ArrayList<String>();
		String outFileName = null;
		for (int i = 0; i < a.length;) {
			String key = a[i++];
			String val = a[i++];
			if ("-out".equals(key))
				outFileName = val;
			else if ("-test".equals(key))
				clazzes.add(val);
			else if ("-header".equals(key))
				headers.add(val);
			else
				throw new RuntimeException("Unknown option: " + key);

		}
		if (outFileName == null)
			throw new RuntimeException("Missing -out <filename> option");

		//		PrintStream out = System.out;
		//		PrintStream err = System.err;

		PrintStream out = new PrintStream(new FileOutputStream(new File(outFileName)));
		TesterMain listener = new TesterMain(out, headers);
		JUnitCore uc = new JUnitCore();
		uc.addListener(listener);
		for (String clazz : clazzes) {
			try {
				final Class<?> c;
				c = Class.forName(clazz);
				if (!hasTestMethod(c))
					continue;
				Result result = uc.run(c);
			} catch (Throwable e) {
				listener.onCritical(e.getClass().getSimpleName(), clazz);
				e.printStackTrace();
				listener.exit(EXIT_CODE_CRITICAL);
			}
		}
		//		listener.done();
		if (listener.failuresCount > 0)
			listener.exit(EXIT_CODE_FAILED);
		else if (listener.assumptionFailuresCount > 0)
			listener.exit(EXIT_CODE_ASSERT_FAILURES);
		else if (listener.ignoredCount > 0)
			listener.exit(EXIT_CODE_IGNORED);
		else if (listener.successCount == 0)
			listener.exit(EXIT_CODE_NO_TESTS);
		else
			listener.exit(EXIT_CODE_SUCCESS);
	}
	private static boolean hasTestMethod(Class<?> c) {
		for (Method m : c.getMethods())
			for (Annotation a : m.getAnnotations())
				if (a.annotationType() == Test.class)
					return true;
		return false;
	}

	private void exit(int exitCode) {
		out.append(sdf.format(new Date(System.currentTimeMillis())));
		out.append('|').append("SUMMARY");
		out.append('|').append("TOTAL").append('=').append(Integer.toString(totalsCount));
		out.append(',').append(SUCCESS).append('=').append(Integer.toString(successCount));
		out.append(',').append(FAILURE).append('=').append(Integer.toString(failuresCount));
		out.append(',').append(ASSUMPTION_FAILURE).append('=').append(Integer.toString(assumptionFailuresCount));
		out.append(',').append(IGNORED).append('=').append(Integer.toString(ignoredCount));
		out.append('|').append(Integer.toString(exitCode));
		out.append('|').append(exitCodeToString(exitCode));
		out.println();
		out.flush();
		System.exit(exitCode);
	}
	static private String exitCodeToString(int exitCode) {
		switch (exitCode) {
			case EXIT_CODE_CRITICAL:
				return "EXIT_WITH_CRITICAL_ERROR";
			case EXIT_CODE_FAILED:
				return "EXIT_WITH_FAILURES";
			case EXIT_CODE_ASSERT_FAILURES:
				return "EXIT_WITH_ASSERT_FAILURES";
			case EXIT_CODE_IGNORED:
				return "EXIT_WITH_IGNORED";
			case EXIT_CODE_NO_TESTS:
				return "EXIT_WITH_NO_TESTS";
			case EXIT_CODE_SUCCESS:
				return "EXIT_WITH_SUCCESS";
			default:
				return Integer.toString(exitCode);
		}
	}
	@Override
	public void testIgnored(Description d) throws Exception {
		onTestStarted(d);
		ignoredCount++;
		onTestFinished(d, IGNORED);
	}

	@Override
	public void testStarted(Description d) throws Exception {
		onTestStarted(d);
	}

	@Override
	public void testFinished(Description d) throws Exception {
		if (!failed) {
			onTestFinished(d, SUCCESS);
			successCount++;
		}
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		failed = true;
		failuresCount++;
		logFailure(failure);
		onTestFinished(failure.getDescription(), FAILURE);
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		failed = true;
		assumptionFailuresCount++;
		logFailure(failure);
		onTestFinished(failure.getDescription(), ASSUMPTION_FAILURE);
	}

	private void logFailure(Failure failure2) {
		System.err.println(failure2.getDescription().getClassName() + "::" + failure2.getDescription().getMethodName() + " ==> ");
		failure2.getException().printStackTrace(System.err);
	}

	private void onTestStarted(Description d) {
		this.totalsCount++;
		this.failed = false;
		this.startMs = System.currentTimeMillis();
		this.description = d;
		Assert.getAndResetAssertCounts();
		out.append(sdf.format(new Date(startMs)));
		out.append('|').append("TEST");
		out.append('|').append(d.getClassName()).append("::").append(d.getMethodName());
	}
	private void onTestFinished(Description d, String status) {
		if (d != this.description)
			status = "ILLEGAL_STATE(" + d + " !=" + this.description + ")";
		this.description = null;
		long asserts = Assert.getAndResetAssertCounts();
		long dur = System.currentTimeMillis() - startMs;
		out.append('|').append(Long.toString(dur)).append(" ms");
		out.append('|').append(Long.toString(asserts) + " subtests");
		out.append('|').append(status);
		out.println();
	}
	private void onCritical(String string, String clazz) {
		out.println();
		out.append(sdf.format(new Date(startMs)));
		out.append('|').append("CRITICAL_ERROR").append(string);
		out.append('|').append(clazz);
		out.println();
	}

	private void logHeader(String s) {
		out.append(sdf.format(new Date(System.currentTimeMillis())));
		out.append('|').append("HEADER");
		out.append('|').append(s);
		out.println();
	}
}
