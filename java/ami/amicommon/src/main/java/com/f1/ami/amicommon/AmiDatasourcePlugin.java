package com.f1.ami.amicommon;

import java.util.List;
import java.util.Map;

import com.f1.utils.CH;

/**
 * 
 * Represents a type of datasource. This should not be confused with an instance of a datasource. This plugin is a factory for producing datasources of a given type. One of these
 * plugins will be created per entry in the ami.datasource.plugins property. The important functions are getPluginId(),getDatasourceDescription(),createDatasourceAdapter()
 * 
 */
public interface AmiDatasourcePlugin extends AmiPlugin {

	public final static String OPERATOR_KEY_EQUAL_TO = "eq";
	public final static String OPERATOR_KEY_NOT_EQUAL_TO = "ne";
	public final static String OPERATOR_KEY_LESS_THAN = "lt";
	public final static String OPERATOR_KEY_GREATER_THAN = "gt";
	public final static String OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO = "lte";
	public final static String OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO = "gte";
	public final static String OPERATORS = "operators";
	public final static String WHERE_SYNTAX = "where_syntax";
	public final static String WHERE_SYNTAX_PREFIX = "prefix";
	public final static String WHERE_SYNTAX_JOIN = "join";
	public final static String WHERE_SYNTAX_SUFFIX = "suffix";
	public final static String WHERE_SYNTAX_TRUE = "true";
	public final static String WHERE_SYNTAX_FALSE = "false";
	public final static String QUOTES = "quotes";
	public final static List<String> OPERATOR_KEY_LIST = CH.l(OPERATOR_KEY_EQUAL_TO, OPERATOR_KEY_NOT_EQUAL_TO, OPERATOR_KEY_LESS_THAN, OPERATOR_KEY_GREATER_THAN,
			OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO, OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO);
	public final static String HELP = "help";
	public final static String HELP_URL = "ur";
	public final static String HELP_PASSWORD = "pw";
	public final static String HELP_ADAPTER = "ad";
	public final static String HELP_NAME = "nm";
	public final static String HELP_OPTIONS = "op";
	public final static String HELP_USER = "us";
	public final static String HELP_GENERAL = "gen";

	/**
	 * @return A well-known, universally unique datasource type identifier, should be "human legible." Preferred Syntax is UPPER_CASE similar to the convention for java constants.
	 *         For custom company-specific plugins it is recommended to prefix with company name to avoid future naming conflicts. Ex: ACME_XMLREADER is preferred vs XMLREADER
	 */
	public String getPluginId();

	/**
	 * @return human-readable description of the datasource,Ex: Acme XmlReader
	 */
	public String getDatasourceDescription();

	/**
	 * @return a new datasource of the type as defined by this plugin. This gets called each time a query is done. The AmiDatasourceAdapter is "stateless". Any connection pooling
	 *         should be handled externally.
	 */
	public AmiDatasourceAdapter createDatasourceAdapter();

	/**
	 * @return Name of image file containing datasource icon, ex: "generic-jdbc.svg"
	 */
	public String getDatasourceIcon();

	/**
	 * @return A string that represents how strings should be wrapped, usually either double or single quotes. Ex: "'"
	 */
	public String getDatasourceQuoteType();

	/**
	 * @return A Map of Strings representing operators used by a specific datasource type. This is to help end users building relationships knoe the types of operators supported
	 */
	public Map<String, Object> getDatasourceOperators();

	/**
	 * @return A Map of Strings representing syntax of how WHERE clauses are build. Keys should use the const WHERE_SYNTAX_PREFIX, WHERE_JOIN, WHERE_SUFFIX, WHERE_TRUE, WHERE_FALSE
	 */
	public Map<String, Object> getDatasourceWhereClauseSyntax();

	/**
	 * @return A map containing help documentation for certaion properties. Keys should use the const HELP,HELP_URL, HELP_PASSWORD, HELP_ADAPTER, HELP_NAME, HELP_OPTIONS,
	 *         HELP_USER, HELP_GENERAL
	 */
	public Map<String, Object> getDatasourceHelp();

	/**
	 * @return A map containing available options, key is the option name, value is the help. These are the options found under the advanced tab of the attach-datasoruce-wizard
	 */
	public Map<String, String> getAvailableOptions();
}
