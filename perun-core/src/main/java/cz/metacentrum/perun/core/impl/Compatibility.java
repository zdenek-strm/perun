package cz.metacentrum.perun.core.impl;

import cz.metacentrum.perun.core.api.BeansUtils;
import cz.metacentrum.perun.core.api.exceptions.InternalErrorException;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * This class provide support for compatibility issues.
 * For example for covering differences between oracle and postgree DB.
 */
public class Compatibility {

	public static boolean isOracle() throws InternalErrorException {
		return "oracle".equals(getDbType());
	}

	public static boolean isPostgreSql() throws InternalErrorException {
		return "postgresql".equals(getDbType());
	}

	public static boolean isHSQLDB() throws InternalErrorException {
		return "hsqldb".equals(getDbType());
	}

	public static String getTrue() throws InternalErrorException {
		switch (getDbType()) {
			case "oracle":
				return "'1'";
			case "postgresql":
				return "TRUE";
			case "hsqldb":
				return "TRUE";
			default:
				throw new InternalErrorException("unknown DB type");
		}
	}

	public static String getSysdate() throws InternalErrorException {
		switch (getDbType()) {
			case "oracle":
				return "sysdate";
			case "postgresql":
				return "statement_timestamp()";
			case "hsqldb":
				return "current_date";
			default:
				throw new InternalErrorException("unknown DB type");
		}
	}

	static Object getDate(long dateInMiliseconds) throws InternalErrorException {
		switch (getDbType()) {
			case "oracle":
				return new Date(dateInMiliseconds);
			case "postgresql":
				return new Timestamp(dateInMiliseconds);
			case "hsqldb":
				return new Timestamp(dateInMiliseconds);
			default:
				throw new InternalErrorException("unknown DB type");
		}
	}

	static String castToVarchar() {

		try {
			switch (getDbType()) {
				case "oracle":
					return "";
				case "postgresql":
					return "::varchar(128)";
				default:
					return "";
			}
		} catch (InternalErrorException ex) {
			return "";
		}

	}

	static String castToInteger() {

		try {
			switch (getDbType()) {
				case "oracle":
					return "";
				case "postgresql":
					return "::integer";
				default:
					return "";
			}
		} catch (InternalErrorException ex) {
			return "";
		}

	}

	private static String getDbType() throws InternalErrorException {
		return BeansUtils.getCoreConfig().getDbType();
	}

	static String getAsAlias(String aliasName) {

		try {
			String dbType = getDbType();
			switch (dbType) {
				case "oracle":
					return " " + aliasName;
				case "postgresql":
					return "as " + aliasName;
				default:
					return " " + aliasName;
			}
		} catch (InternalErrorException ex) {
			return "";
		}

	}

	static String getRowNumberOver() {
		try {
			if ("hsqldb".equals(getDbType())) {
				return ",row_number() over () as rownumber";
			} else {
				return ",row_number() over (ORDER BY id DESC) as rownumber";
			}
		} catch (InternalErrorException e) {
			return ",row_number() over (ORDER BY id DESC) as rownumber";
		}
	}

	static String orderByBinary(String columnName) {

		try {
			switch (getDbType()) {
				case "oracle":
					return "NLSSORT(" + columnName + ",'NLS_SORT=BINARY_AI')";
				case "postgresql":
					return columnName + " USING ~<~";
				default:
					return columnName;
			}
		} catch (InternalErrorException ex) {
			return columnName;
		}

	}

	static String convertToAscii(String columnName) {

		try {
			switch (getDbType()) {
				case "oracle":
					// convert column type to VARCHAR2 from (N)VARCHAR2 and modify encoding from UTF to US7ASCII
					return "to_char(convert(" + columnName + ", 'US7ASCII', 'UTF8'))"; // DESTINATION / SOURCE

				case "postgresql":
					return "unaccent(" + columnName + ")";
				case "hsqldb":
					return "translate(" + columnName + ", 'ÁÇÉÍÓÚÀÈÌÒÙÚÂÊÎÔÛÃÕËÜŮŘřáçéíóúàèìòùâêîôûãõëüů', 'ACEIOUUAEIOUAEIOUAOEUURraceiouaeiouaeiouaoeuu')";
				default:
					return "unaccent(" + columnName + ")";
			}
		} catch (InternalErrorException ex) {
			return "unaccent(" + columnName + ")";
		}

	}

	public static String toDate(String value, String format) {
		try {
			switch (getDbType()) {
				case "oracle":
					return "to_date(" + value + ", " + format + ")";
				case "postgresql":
					return "to_timestamp(" + value + ", " + format + ")";
				default:
					return "to_date(" + value + ", " + format + ")";
			}
		} catch (InternalErrorException ex) {
			return "to_date(" + value + ", " + format + ")";
		}

	}

}
