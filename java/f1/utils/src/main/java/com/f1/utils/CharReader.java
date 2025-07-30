package com.f1.utils;

import com.f1.utils.impl.CharMatcher;
import com.f1.utils.string.ExpressionParserException;

public interface CharReader {
	int EOF = -1;

	/**
	 * @return the next char or {@link #EOF} if end has been reached
	 */
	public int readCharOrEof();

	/**
	 * @return the next char
	 * @throws IndexOutOfBoundsException
	 *             if eof reached
	 * @see #readCharOrEof()
	 */
	public char readChar();

	/**
	 * read next series of chars and store in out param. number of chars read is determined by length of out param
	 * 
	 * @return number of chars actually read, zero if at EOF
	 */
	public int readChars(char[] out);

	/**
	 * read next series of chars and store in out param
	 * 
	 * @param out
	 *            the sink for chars read
	 * @param offset
	 *            where to start start storing chars in out param
	 * @param length
	 *            the number of chars to read
	 * @return number of chars actually read, zero if at EOF
	 */
	public int readChars(char[] out, int offset, int length);

	/**
	 * return the next char that will be read (with out advancing the position)
	 * 
	 * @return the next char
	 * @throws IndexOutOfBoundsException
	 *             if at EOF
	 */
	public char peak();

	public int peak(char sink[]);

	/**
	 * return the next char that will be read
	 * 
	 * @return the next char or {@link #EOF} if at EOF
	 */
	public int peakOrEof();

	/**
	 * read the next char and throw an exception if it does not match supplied char
	 * 
	 * @param c
	 *            the char to expect
	 * @return value of c
	 * @throws RuntimeException
	 *             if the next char does not match c
	 * @throws IndexOutOfBoundsException
	 *             if at EOF
	 */
	public char expect(int c);

	/**
	 * advance the cursor to the first char that does not match c or EOF is reached
	 * 
	 * @param c
	 *            char to skip
	 * @return number of chars skipped. 0 if none of EOF
	 */
	public int skip(char c);
	public int skipAny(int[] whiteSpace);

	/**
	 * advance the cursor to the first char that does not exist in the supplied set of chars or EOF is reached
	 * 
	 * @param chars
	 *            set chars to skip
	 * @return number of chars skipped. 0 if none of EOF
	 */
	public int skip(CharMatcher chars);

	/**
	 * read the next char and throw an exception if the char is not in the list of supplied chars. Please note, if {@link #EOF} is in the list and the cursor is at EOF, then an
	 * exception is not thrown but the curson is not progressed (because it is already at the EOF)
	 * 
	 * @param c
	 *            the char to expect
	 * @return value of the next char read(which will obviously be in supplied c array). If cursor is at EOF and {@link #EOF} is supplied in array then {@link #EOF} will be
	 *         returned
	 * @throws RuntimeException
	 *             if the next char does not match c
	 * @throws IndexOutOfBoundsException
	 *             if at EOF
	 */
	public int expectAny(int c[]);

	/**
	 * expect the following string of chars after the cursor will match supplied array c (in the same order). Upon success, the cursor will be advanced the length of c array,
	 * otherwise the cursor will be advanced to this first mismatched char
	 * 
	 * @param c
	 *            sequence of chars to expect
	 * @throws RuntimeException
	 *             if the expected sequence is not found
	 * @throws IndexOutOfBoundsException
	 *             if the EOF is reached
	 */
	public void expectSequence(char c[]);
	public boolean expectSequenceNoThrow(char c[]);
	public boolean expectSequenceNoThrow(String s);

	/**
	 * @return number of chars read so far
	 */
	public int getCountRead();

	/**
	 * see {@link #readUntilAny(int[], char,StringBuilder)}
	 * 
	 * @param includeEOF
	 *            if false and EOF is reached, throw a RuntimeException
	 */
	public int readUntilAny(CharMatcher chars, boolean includeEOF, char escape, StringBuilder sink);
	public int readUntilAnySkipEscaped(CharMatcher chars, char escape, StringBuilder sink);

	/**
	 * see {@link #readUntilAny(int[], StringBuilder)}
	 * 
	 * @param includeEOF
	 *            if false and EOF is reached, throw a RuntimeException
	 */
	public int readUntilAny(CharMatcher chars, boolean includeEOF, StringBuilder sink);

	/**
	 * reads a sequence of characters and appends them to sink until a char inside supplied chars is found. If a char matching supplied escape char is reached, then the following
	 * char is not inspected against the supplied list of supplied stop chars, note both the escape char and char will be appended to supplied sink
	 * <P>
	 * Please note, use {@link #readUntilAny(CharMatcher, boolean,char, StringBuilder)} for better performance
	 * 
	 * <Pre>
	 * For example:  
	 *   1. assume the chars following the cursor is "this is a |,test, here"
	 *   2. assume we call readUntil(new char[]{',','.'},'|',sink);
	 *   3. the resulting string will be appened to the sink:  "this is a|,test"
	 * </pre>
	 * 
	 * @param chars
	 *            the list of chars to stop reading at
	 * @param sink
	 *            where read chars are appended to
	 * @return the next char to be read(aka the char that was encountered in the supplied chars)
	 * @throws IndexOutOfBoundsException
	 *             if end of file is reached and {@link #EOF} is not in supply chared array
	 * 
	 */
	public int readUntilAny(int chars[], char escape, StringBuilder sink);

	/**
	 * reads a sequence of characters and appends them to sink until a char inside supplied chars is found or the EOF is reached.
	 * <P>
	 * Please note, use {@link #readUntilAny(CharMatcher, boolean, StringBuilder)} for better performance
	 * <P>
	 * 
	 * @param chars
	 *            the list of chars to stop reading at
	 * @param sink
	 *            where read chars are appended to
	 * @return the next char to be read(aka the char that was encountered in the supplied chars)
	 * @throws IndexOutOfBoundsException
	 *             if end of file is reached and {@link #EOF} is not in supply chared array
	 * 
	 */
	public int readUntilAny(int chars[], StringBuilder sink);
	public int readWhileAny(int chars[], StringBuilder sink);
	public int readWhileAny(CharSequence expecting, StringBuilder sink);
	public int readWhileAny(CharMatcher expecting, StringBuilder sink);

	/**
	 * reads a sequence of characters and appends them to sink until char c is found or the EOF is reached. If a char matching supplied escape char is reached, then the following
	 * char is not inspected against the supplied list of supplied stop chars, note both the escape char and char will be appended to supplied sink
	 * <P>
	 * Please note, use {@link #readUntilAny(CharMatcher, boolean,char, StringBuilder)} for better performance
	 * 
	 * <Pre>
	 * For example:  
	 *   1. assume the chars following the cursor is "this is a |,test, here"
	 *   2. assume we call readUntil(',','|',sink);
	 *   3. the resulting string will be appened to the sink:  "this is a|,test"
	 * </pre>
	 * 
	 * @param c
	 *            the char to stop reading at
	 * @param escape
	 *            an escape char(indicating the following char should just be blindly read)
	 * @param sink
	 *            where read chars are appended to
	 * @return the number of chars read
	 * @throws IndexOutOfBoundsException
	 *             if end of file is reached and {@link #EOF} is not in supply chared array
	 * 
	 */
	public int readUntil(int c, char escape, StringBuilder sink);

	/**
	 * set the char at the current cursor to c and rewind the cursor by 1 char
	 * 
	 * @param c
	 *            the char to put at the cursor position
	 */
	public void pushBack(char c);

	/**
	 * similar to {@link #expectSequence(char[])} except the cursor is not changed and will return false instead of throwing an exception
	 * 
	 * @param string
	 *            the string of chars to expect at the cursor
	 * @return true iff matches
	 */
	public boolean peakSequence(char[] string);

	/**
	 * remeber the current cursor location, useful for {@link #returnToMark()}
	 */
	public void mark();

	/**
	 * move the cursor back to where it was when {@link #mark()} was called. If {@link #mark()} has never been called then return to the begining
	 */
	public void returnToMark();

	public int readUntil(int c, StringBuilder sink);

	public int readChars(int i, StringBuilder sink);

	/**
	 * skip i number of chars
	 */
	public int skipChars(int i);

	public void reset(String string);
	public void expectSequence(CharSequence text);
	public boolean peakSequence(CharSequence string);
	public boolean read(CharSequence string, CharMatcher followedBy);
	public boolean read(char[] string, CharMatcher followedBy);

	public int readUntilSkipEscaped(int c, char d, StringBuilder sink);

	public int readUntilSequence(char sequence[], StringBuilder sink);
	public int readUntilSequence(CharSequence sequence, StringBuilder sink);

	public int readUntilSequenceAndSkip(String sequence, StringBuilder sink);

	public boolean isEof();

	public ExpressionParserException newExpressionParserException(String message);

	public ExpressionParserException newExpressionParserException(String text, Exception e);

	public boolean getCaseInsensitive();
	public void setCaseInsensitive(boolean ignoreCase);

	boolean expectNoThrow(int c);

	/**
	 * @return char that caused stop (next char)
	 */
	int readUntilAny(CharSequence chars, boolean includeEOF, StringBuilder sink);
	int readUntilSequenceAndSkip(char[] sequence, StringBuilder sink);

	public String substring(int start, int end);

	public String getText();

}
