package com.snapmocklib.utils;

import java.util.regex.Pattern;

public final class PatternRegexUtils {

    private PatternRegexUtils() {}

    private static final String DATE_FORMAT_YYYY_MM_DD =
      "^((19|2\\d)\\d{2})-(0[1-9]|10|11|12)-(0[1-9]|1\\d|2\\d|3[0-1])";

    private static final String TIME_FORMAT_HH_MM_SS = "(0\\d|1\\d|2[0-3]):([0-5]\\d):([0-5]\\d)";

    public static final Pattern ANY = Pattern.compile(".*");

    public static final Pattern UUID =
      Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})");

    public static final Pattern DATE_YYYY_MM_DD = Pattern.compile(DATE_FORMAT_YYYY_MM_DD);

    public static final Pattern TIME_HH_MM_SS = Pattern.compile(TIME_FORMAT_HH_MM_SS);

    public static final Pattern DATETIME_ISO =
      Pattern.compile(DATE_FORMAT_YYYY_MM_DD + "T" + TIME_FORMAT_HH_MM_SS + "$");

    public static final Pattern DATETIME_WITH_NANO_ISO =
      Pattern.compile(DATE_FORMAT_YYYY_MM_DD + "T" + TIME_FORMAT_HH_MM_SS + "(.\\d*)?" + "$");

    public static final Pattern DATETIME_WITH_NANO_ISO_Z =
      Pattern.compile(DATE_FORMAT_YYYY_MM_DD + "T" + TIME_FORMAT_HH_MM_SS + "(.\\d*)?Z$" + "$");

    public static final Pattern ANY_WITH_NEW_LINE = Pattern.compile("(?s).*\\R.*");

    public static final Pattern ALPHANUMERIC = Pattern.compile("^(\\w*)$");

    public static final Pattern ANY_TRIM_SPACES = Pattern.compile("^[\\s]*(.*?)[\\s]*$");

    public static final Pattern EMAIL = Pattern.compile("\\b[\\w.!#$%&’*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)*\\b");

    public static final Pattern DECIMAL_NUMBER = Pattern.compile("^(\\d*)[.,](\\d+)$");

    public static final Pattern INT_NUMBER = Pattern.compile("^(\\d+)$");

    public static final Pattern LITERALS = Pattern.compile("[a-zA-Z]");

    public static final Pattern DIGITS = Pattern.compile("\\d");

    public static final Pattern LOWERCASE = Pattern.compile("[a-z]");

    public static final Pattern UPPERCASE = Pattern.compile("[A-Z]");

    // TODO: other dates?
}
