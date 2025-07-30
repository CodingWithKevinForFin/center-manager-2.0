package org.junit;

import java.util.concurrent.atomic.AtomicLong;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.internal.ExactComparisonCriteria;
import org.junit.internal.InexactComparisonCriteria;

public class Assert {
	static private final AtomicLong counts = new AtomicLong();
	public static long getAndResetAssertCounts() {
		return counts.getAndSet(0);
	}

	static private void i() {
		counts.incrementAndGet();
	}
	protected Assert() {
	}

	static public void assertTrue(String message, boolean condition) {
		i();
		if (!condition)
			fail(message);
	}

	static public void assertTrue(boolean condition) {
		//i();
		assertTrue(null, condition);
	}

	static public void assertFalse(String message, boolean condition) {
		//i();
		assertTrue(message, !condition);
	}
	static public void assertFalse(boolean condition) {
		//i();
		assertFalse(null, condition);
	}
	static public void fail(String message) {
		if (message == null)
			throw new AssertionError();
		throw new AssertionError(message);
	}

	static public void fail() {
		fail(null);
	}

	static public void assertEquals(String message, Object expected, Object actual) {
		i();
		if (expected == null && actual == null)
			return;
		if (expected != null && isEquals(expected, actual))
			return;
		else if (expected instanceof String && actual instanceof String) {
			String cleanMessage = message == null ? "" : message;
			throw new ComparisonFailure(cleanMessage, (String) expected, (String) actual);
		} else
			failNotEquals(message, expected, actual);
	}

	private static boolean isEquals(Object expected, Object actual) {
		return expected.equals(actual);
	}

	static public void assertEquals(Object expected, Object actual) {
		//i();
		assertEquals(null, expected, actual);
	}

	public static void assertArrayEquals(String message, Object[] expecteds, Object[] actuals) throws ArrayComparisonFailure {
		//i();
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(Object[] expecteds, Object[] actuals) {
		//i();
		assertArrayEquals(null, expecteds, actuals);
	}

	public static void assertArrayEquals(String message, byte[] expecteds, byte[] actuals) throws ArrayComparisonFailure {
		//i();
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(byte[] expecteds, byte[] actuals) {
		//i();
		assertArrayEquals(null, expecteds, actuals);
	}

	public static void assertArrayEquals(String message, char[] expecteds, char[] actuals) throws ArrayComparisonFailure {
		//i();
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(char[] expecteds, char[] actuals) {
		//i();
		assertArrayEquals(null, expecteds, actuals);
	}

	public static void assertArrayEquals(String message, short[] expecteds, short[] actuals) throws ArrayComparisonFailure {
		//i();
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(short[] expecteds, short[] actuals) {
		//i();
		assertArrayEquals(null, expecteds, actuals);
	}

	public static void assertArrayEquals(String message, int[] expecteds, int[] actuals) throws ArrayComparisonFailure {
		//i();
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(int[] expecteds, int[] actuals) {
		//i();
		assertArrayEquals(null, expecteds, actuals);
	}

	public static void assertArrayEquals(String message, long[] expecteds, long[] actuals) throws ArrayComparisonFailure {
		//i();
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(long[] expecteds, long[] actuals) {
		//i();
		assertArrayEquals(null, expecteds, actuals);
	}

	public static void assertArrayEquals(String message, double[] expecteds, double[] actuals, double delta) throws ArrayComparisonFailure {
		i();
		new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(double[] expecteds, double[] actuals, double delta) {
		//i();
		assertArrayEquals(null, expecteds, actuals, delta);
	}

	public static void assertArrayEquals(String message, float[] expecteds, float[] actuals, float delta) throws ArrayComparisonFailure {
		i();
		new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals);
	}

	public static void assertArrayEquals(float[] expecteds, float[] actuals, float delta) {
		//i();
		assertArrayEquals(null, expecteds, actuals, delta);
	}

	private static void internalArrayEquals(String message, Object expecteds, Object actuals) throws ArrayComparisonFailure {
		i();
		new ExactComparisonCriteria().arrayEquals(message, expecteds, actuals);
	}

	static public void assertEquals(String message, double expected, double actual, double delta) {
		i();
		if (Double.compare(expected, actual) == 0)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			failNotEquals(message, new Double(expected), new Double(actual));
	}

	static public void assertEquals(long expected, long actual) {
		//i();
		assertEquals(null, expected, actual);
	}

	static public void assertEquals(String message, long expected, long actual) {
		//i();
		assertEquals(message, (Long) expected, (Long) actual);
	}

	@Deprecated
	static public void assertEquals(double expected, double actual) {
		assertEquals(null, expected, actual);
	}

	@Deprecated
	static public void assertEquals(String message, double expected, double actual) {
		fail("Use assertEquals(expected, actual, delta) to compare floating-point numbers");
	}

	static public void assertEquals(double expected, double actual, double delta) {
		assertEquals(null, expected, actual, delta);
	}

	static public void assertNotNull(String message, Object object) {
		//i();
		assertTrue(message, object != null);
	}

	static public void assertNotNull(Object object) {
		//i();
		assertNotNull(null, object);
	}

	static public void assertNull(String message, Object object) {
		//i();
		assertTrue(message, object == null);
	}

	static public void assertNull(Object object) {
		//i();
		assertNull(null, object);
	}

	static public void assertSame(String message, Object expected, Object actual) {
		i();
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	static public void assertSame(Object expected, Object actual) {
		//i();
		assertSame(null, expected, actual);
	}

	static public void assertNotSame(String message, Object unexpected, Object actual) {
		i();
		if (unexpected == actual)
			failSame(message);
	}

	static public void assertNotSame(Object unexpected, Object actual) {
		//i();
		assertNotSame(null, unexpected, actual);
	}

	static private void failSame(String message) {
		String formatted = "";
		if (message != null)
			formatted = message + " ";
		fail(formatted + "expected not same");
	}

	static private void failNotSame(String message, Object expected, Object actual) {
		String formatted = "";
		if (message != null)
			formatted = message + " ";
		fail(formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
	}

	static private void failNotEquals(String message, Object expected, Object actual) {
		//NO i()
		fail(format(message, expected, actual));
	}

	static String format(String message, Object expected, Object actual) {
		String formatted = "";
		if (message != null && !message.equals(""))
			formatted = message + " ";
		String expectedString = String.valueOf(expected);
		String actualString = String.valueOf(actual);
		if (expectedString.equals(actualString))
			return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: " + formatClassAndValue(actual, actualString);
		else
			return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
	}

	private static String formatClassAndValue(Object value, String valueString) {
		String className = value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}

	@Deprecated
	public static void assertEquals(String message, Object[] expecteds, Object[] actuals) {
		//i();
		assertArrayEquals(message, expecteds, actuals);
	}

	@Deprecated
	public static void assertEquals(Object[] expecteds, Object[] actuals) {
		//i();
		assertArrayEquals(expecteds, actuals);
	}

	public static <T> void assertThat(T actual, Matcher<T> matcher) {
		//i();
		assertThat("", actual, matcher);
	}

	public static <T> void assertThat(String reason, T actual, Matcher<T> matcher) {
		i();
		if (!matcher.matches(actual)) {
			Description description = new StringDescription();
			description.appendText(reason);
			description.appendText("\nExpected: ");
			description.appendDescriptionOf(matcher);
			description.appendText("\n     got: ");
			description.appendValue(actual);
			description.appendText("\n");
			throw new java.lang.AssertionError(description.toString());
		}
	}
}
