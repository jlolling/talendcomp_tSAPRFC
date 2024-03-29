/**
 * Copyright 2015 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jlo.talendcomp.sap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;

public final class TypeUtil {
	
	private static final Map<String, DecimalFormat> numberformatMap = new HashMap<String, DecimalFormat>();
	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String DEFAULT_LOCALE = "en_UK";
	
	public TypeUtil() {}
	
	public static DecimalFormat getNumberFormat(String localeStr) {
		DecimalFormat nf = numberformatMap.get(localeStr);
		if (nf == null) {
			Locale locale = new Locale(localeStr);
			nf = (DecimalFormat) NumberFormat.getInstance(locale);
			numberformatMap.put(localeStr, nf);
		}
		return nf;
	}
	
	public static boolean isSupportedDataType(String dataType) {
		if ("String".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("BigDecimal".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Boolean".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Date".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Double".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Float".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Int".equalsIgnoreCase(dataType) || "Integer".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Long".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Short".equalsIgnoreCase(dataType)) {
			return true;
		} else if ("Timestamp".equalsIgnoreCase(dataType)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Object convertToDatatype(String value, String dataType, String options) throws Exception {
		if (value != null && value.trim().isEmpty() == false) {
			value = value.trim();
			if ("String".equalsIgnoreCase(dataType)) {
				return value;
			} else if ("BigDecimal".equalsIgnoreCase(dataType)) {
				return convertToBigDecimal(value);
			} else if ("Boolean".equalsIgnoreCase(dataType)) {
				return convertToBoolean(value);
			} else if ("Date".equalsIgnoreCase(dataType)) {
				return convertToDate(value, options);
			} else if ("Double".equalsIgnoreCase(dataType)) {
				return convertToDouble(value);
			} else if ("Float".equalsIgnoreCase(dataType)) {
				return convertToFloat(value);
			} else if ("Int".equalsIgnoreCase(dataType) || "Integer".equalsIgnoreCase(dataType)) {
				return convertToInteger(value);
			} else if ("Long".equalsIgnoreCase(dataType)) {
				return convertToLong(value);
			} else if ("Short".equalsIgnoreCase(dataType)) {
				return convertToLong(value);
			} else if ("Timestamp".equalsIgnoreCase(dataType)) {
				return convertToTimestamp(value, options);
			} else {
				throw new Exception("Unsupported dataType:" + dataType);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * converts the string format into a Date
	 * @param value
	 * @param pattern
	 * @return the resulting Date
	 */
	public static Date convertToDate(String value, String pattern) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (pattern == null) {
			pattern = DEFAULT_DATE_PATTERN;
		}
		try {
			FastDateFormat sdf = FastDateFormat.getInstance(pattern);
			Date date = null;
			try {
				date = sdf.parse(value);
			} catch (ParseException pe) {
				date = GenericDateUtil.parseDate(value);
			}
			return date;
		} catch (Throwable t) {
			throw new Exception("Failed to convert string to date:" + t.getMessage(), t);
		}
	}
	
	public static Timestamp convertToTimestamp(String value, String pattern) throws Exception {
		Date date = convertToDate(value, pattern);
		if (date != null) {
			return new Timestamp(date.getTime());
		} else {
			return null;
		}
	}

	public static Boolean convertToBoolean(String value) throws Exception {
		if (value == null) {
			return null;
		}
		value = value.toLowerCase();
		if ("true".equals(value)) {
			return Boolean.TRUE;
		} else if ("false".equals(value)) {
			return Boolean.FALSE;
		} else if ("1".equals(value)) {
			return Boolean.TRUE;
		} else if ("0".equals(value)) {
			return Boolean.FALSE;
		} else if ("yes".equals(value)) {
			return Boolean.TRUE;
		} else if ("y".equals(value)) {
			return Boolean.TRUE;
		} else if ("sí".equals(value)) {
			return Boolean.TRUE;
		} else if ("да".equals(value)) {
			return Boolean.TRUE;
		} else if ("no".equals(value)) {
			return Boolean.FALSE;
		} else if ("нет".equals(value)) {
			return Boolean.FALSE;
		} else if ("n".equals(value)) {
			return Boolean.FALSE;
		} else if ("ja".equals(value)) {
			return Boolean.TRUE;
		} else if ("j".equals(value)) {
			return Boolean.TRUE;
		} else if ("nein".equals(value)) {
			return Boolean.FALSE;
		} else if ("oui".equals(value)) {
			return Boolean.TRUE;
		} else if ("non".equals(value)) {
			return Boolean.FALSE;
		} else if ("ok".equals(value)) {
			return Boolean.TRUE;
		} else if ("x".equals(value)) {
			return Boolean.TRUE;
		} else if (value != null) {
			throw new Exception("Value: " + value + " cannot be parsed to a boolean");
		} else {
			return null;
		}
	}

	public static Double convertToDouble(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		DecimalFormat decfrm = getNumberFormat(DEFAULT_LOCALE);
		decfrm.setParseBigDecimal(false);
		return decfrm.parse(value).doubleValue();
	}

	public static Integer convertToInteger(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		DecimalFormat decfrm = getNumberFormat(DEFAULT_LOCALE);
		decfrm.setParseBigDecimal(false);
		return decfrm.parse(value).intValue();
	}
	
	public static Short convertToShort(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return Short.parseShort(value);
	}

	public static Character convertToChar(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return value.charAt(0);
	}

	public static String convertToString(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		value = value.replace("\\n", "\n").replace("\\\"", "\"");
		return value;
	}

	public static Float convertToFloat(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return Float.parseFloat(value);
	}

	public static Long convertToLong(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return Long.parseLong(value);
	}

	public static BigDecimal convertToBigDecimal(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			DecimalFormat decfrm = getNumberFormat(DEFAULT_LOCALE);
			decfrm.setParseBigDecimal(true);
			ParsePosition pp = new ParsePosition(0);
			return (BigDecimal) decfrm.parse(value, pp);
		} catch (RuntimeException e) {
			throw new Exception("convertToBigDecimal:" + value + " failed:" + e.getMessage(), e);
		}
	}

	public static BigInteger convertToBigInteger(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			return new BigInteger(value);
		} catch (RuntimeException e) {
			throw new Exception("convertToBigDecimal:" + value + " failed:" + e.getMessage(), e);
		}
	}

	public static double roundScale(double value, int scale) {
    	double d = Math.pow(10, scale);
        return Math.round(value * d) / d;
    }
 
}
