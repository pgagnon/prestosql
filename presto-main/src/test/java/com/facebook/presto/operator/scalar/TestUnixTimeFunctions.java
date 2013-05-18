/*
 * Copyright 2004-present Facebook. All Rights Reserved.
 */
package com.facebook.presto.operator.scalar;

import com.facebook.presto.sql.analyzer.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.chrono.ISOChronology;
import org.testng.annotations.Test;

import static com.facebook.presto.operator.scalar.FunctionAssertions.assertFunction;
import static com.facebook.presto.operator.scalar.FunctionAssertions.selectSingleValue;
import static com.facebook.presto.sql.analyzer.Session.DEFAULT_CATALOG;
import static com.facebook.presto.sql.analyzer.Session.DEFAULT_SCHEMA;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.testng.Assert.assertEquals;

public class TestUnixTimeFunctions
{
    private static final DateTimeField CENTURY_FIELD = ISOChronology.getInstance(DateTimeZone.UTC).centuryOfEra();

    @Test
    public void testCurrentTime()
    {
        long millis = new DateTime(2001, 1, 22, 3, 4, 5, 321, DateTimeZone.UTC).getMillis();
        Session session = new Session("user", "test", DEFAULT_CATALOG, DEFAULT_SCHEMA, null, null, millis);

        assertEquals((long) selectSingleValue("current_timestamp", session), fromMillis(millis));
        assertEquals((long) selectSingleValue("now()", session), fromMillis(millis));
    }

    @Test
    public void testPartFunctions()
    {
        DateTime dateTime = new DateTime(2001, 1, 22, 3, 4, 5, 321, DateTimeZone.UTC);
        long seconds = getSeconds(dateTime);

        assertFunction("second(" + seconds + ")", dateTime.getSecondOfMinute());
        assertFunction("minute(" + seconds + ")", dateTime.getMinuteOfHour());
        assertFunction("hour(" + seconds + ")", dateTime.getHourOfDay());
        assertFunction("dayOfWeek(" + seconds + ")", dateTime.dayOfWeek().get());
        assertFunction("dow(" + seconds + ")", dateTime.dayOfWeek().get());
        assertFunction("day(" + seconds + ")", dateTime.getDayOfMonth());
        assertFunction("day_of_month(" + seconds + ")", dateTime.getDayOfMonth());
        assertFunction("dayOfMonth(" + seconds + ")", dateTime.getDayOfMonth());
        assertFunction("dayOfYear(" + seconds + ")", dateTime.dayOfYear().get());
        assertFunction("doy(" + seconds + ")", dateTime.dayOfYear().get());
        assertFunction("week(" + seconds + ")", dateTime.weekOfWeekyear().get());
        assertFunction("week_of_year(" + seconds + ")", dateTime.weekOfWeekyear().get());
        assertFunction("weekOfYear(" + seconds + ")", dateTime.weekOfWeekyear().get());
        assertFunction("month(" + seconds + ")", dateTime.getMonthOfYear());
        assertFunction("quarter(" + seconds + ")", dateTime.getMonthOfYear() / 4 + 1);
        assertFunction("year(" + seconds + ")", dateTime.getYear());
    }

    @Test
    public void testExtract()
    {
        DateTime dateTime = new DateTime(2001, 1, 22, 3, 4, 5, 321, DateTimeZone.UTC);
        long seconds = getSeconds(dateTime);

        assertFunction("extract(day FROM " + seconds + ")", dateTime.getDayOfMonth());
        assertFunction("extract(doy FROM " + seconds + ")", dateTime.getDayOfYear());
        assertFunction("extract(hour FROM " + seconds + ")", dateTime.getHourOfDay());
        assertFunction("extract(minute FROM " + seconds + ")", dateTime.getMinuteOfHour());
        assertFunction("extract(month FROM " + seconds + ")", dateTime.getMonthOfYear());
        assertFunction("extract(quarter FROM " + seconds + ")", dateTime.getMonthOfYear() / 4 + 1);
        assertFunction("extract(second FROM " + seconds + ")", dateTime.getSecondOfMinute());
        assertFunction("extract(week FROM " + seconds + ")", dateTime.getWeekOfWeekyear());
        assertFunction("extract(year FROM " + seconds + ")", dateTime.getYear());
        assertFunction("extract(century FROM " + seconds + ")", dateTime.getCenturyOfEra());
    }

    @Test
    public void testDateAdd()
    {
        DateTime dateTime = new DateTime(2001, 1, 22, 3, 4, 5, 321, DateTimeZone.UTC);
        long seconds = getSeconds(dateTime);

        assertFunction("dateAdd('day', 3, " + seconds + ")", getSeconds(dateTime.plusDays(3)));
        assertFunction("dateAdd('doy', 3, " + seconds + ")", getSeconds(dateTime.plusDays(3)));
        assertFunction("dateAdd('hour', 3, " + seconds + ")", getSeconds(dateTime.plusHours(3)));
        assertFunction("dateAdd('minute', 3, " + seconds + ")", getSeconds(dateTime.plusMinutes(3)));
        assertFunction("dateAdd('month', 3, " + seconds + ")", getSeconds(dateTime.plusMonths(3)));
        assertFunction("dateAdd('quarter', 3, " + seconds + ")", getSeconds(dateTime.plusMonths(3 * 3)));
        assertFunction("dateAdd('second', 3, " + seconds + ")", getSeconds(dateTime.plusSeconds(3)));
        assertFunction("dateAdd('week', 3, " + seconds + ")", getSeconds(dateTime.plusWeeks(3)));
        assertFunction("dateAdd('year', 3, " + seconds + ")", getSeconds(dateTime.plusYears(3)));
        assertFunction("dateAdd('century', 3, " + seconds + ")", fromMillis(CENTURY_FIELD.add(dateTime.getMillis(), 3)));
    }

    @Test
    public void testDateDiff()
    {
        DateTime dateTime1 = new DateTime(1960, 1, 22, 3, 4, 5, 0, DateTimeZone.UTC);
        long seconds1 = getSeconds(dateTime1);
        DateTime dateTime2 = new DateTime(2011, 5, 1, 7, 2, 9, 0, DateTimeZone.UTC);
        long seconds2 = getSeconds(dateTime2);

        assertFunction("dateDiff('day', " + seconds1 + ", " + seconds2 + ")", Days.daysBetween(dateTime1, dateTime2).getDays());
        assertFunction("dateDiff('doy', " + seconds1 + ", " + seconds2 + ")", Days.daysBetween(dateTime1, dateTime2).getDays());
        assertFunction("dateDiff('hour', " + seconds1 + ", " + seconds2 + ")", Hours.hoursBetween(dateTime1, dateTime2).getHours());
        assertFunction("dateDiff('minute', " + seconds1 + ", " + seconds2 + ")", Minutes.minutesBetween(dateTime1, dateTime2).getMinutes());
        assertFunction("dateDiff('month', " + seconds1 + ", " + seconds2 + ")", Months.monthsBetween(dateTime1, dateTime2).getMonths());
        assertFunction("dateDiff('quarter', " + seconds1 + ", " + seconds2 + ")", Months.monthsBetween(dateTime1, dateTime2).getMonths() / 4 + 1);
        assertFunction("dateDiff('second', " + seconds1 + ", " + seconds2 + ")", Seconds.secondsBetween(dateTime1, dateTime2).getSeconds());
        assertFunction("dateDiff('week', " + seconds1 + ", " + seconds2 + ")", Weeks.weeksBetween(dateTime1, dateTime2).getWeeks());
        assertFunction("dateDiff('year', " + seconds1 + ", " + seconds2 + ")", Years.yearsBetween(dateTime1, dateTime2).getYears());
        assertFunction("dateDiff('century', " + seconds1 + ", " + seconds2 + ")", fromMillis(CENTURY_FIELD.getDifference(dateTime1.getMillis(), dateTime2.getMillis())));
    }

    @Test
    public void testParseDatetime()
    {
        DateTimeZone timeZone = DateTimeZone.forOffsetHours(5);

        assertFunction("parsedatetime('1960/01/22 03:04', 'YYYY/MM/DD HH:mm')", getSeconds(new DateTime(1960, 1, 22, 3, 4, 0, 0, DateTimeZone.UTC)));
        assertFunction("parsedatetime('1960/01/22 03:04 Asia/Oral', 'YYYY/MM/DD HH:mm ZZZZZ')", getSeconds(new DateTime(1960, 1, 22, 3, 4, 0, 0, timeZone)));
        assertFunction("parsedatetime('1960/01/22 03:04 +0500', 'YYYY/MM/DD HH:mm Z')", getSeconds(new DateTime(1960, 1, 22, 3, 4, 0, 0, timeZone)));
    }

    @Test
    public void testFormatDatetime()
    {
        DateTime dateTime = new DateTime(2001, 1, 22, 3, 4, 5, 321, DateTimeZone.UTC);
        long seconds = getSeconds(dateTime);

        assertFunction("formatDatetime(" + seconds + ", 'YYYY/MM/DD HH:mm')", "2001/01/22 03:04");
        assertFunction("formatDatetime(" + seconds + ", 'YYYY/MM/DD HH:mm ZZZZ')", "2001/01/22 03:04 UTC");
    }

    @Test
    public void testDateFormat()
    {
        DateTime dateTime = new DateTime(2001, 1, 9, 13, 4, 5, 0, DateTimeZone.UTC);
        long seconds = getSeconds(dateTime);

        assertFunction("date_format(" + seconds + ", '%a')", "Tue");
        assertFunction("date_format(" + seconds + ", '%b')", "Jan");
        assertFunction("date_format(" + seconds + ", '%c')", "1");
        assertFunction("date_format(" + seconds + ", '%d')", "09");
        assertFunction("date_format(" + seconds + ", '%e')", "9");
        assertFunction("date_format(" + seconds + ", '%f')", "000000");
        assertFunction("date_format(" + seconds + ", '%H')", "13");
        assertFunction("date_format(" + seconds + ", '%h')", "01");
        assertFunction("date_format(" + seconds + ", '%I')", "01");
        assertFunction("date_format(" + seconds + ", '%i')", "04");
        assertFunction("date_format(" + seconds + ", '%j')", "009");
        assertFunction("date_format(" + seconds + ", '%k')", "13");
        assertFunction("date_format(" + seconds + ", '%l')", "1");
        assertFunction("date_format(" + seconds + ", '%M')", "January");
        assertFunction("date_format(" + seconds + ", '%m')", "01");
        assertFunction("date_format(" + seconds + ", '%p')", "PM");
        assertFunction("date_format(" + seconds + ", '%r')", "01:04:05 PM");
        assertFunction("date_format(" + seconds + ", '%S')", "05");
        assertFunction("date_format(" + seconds + ", '%s')", "05");
        assertFunction("date_format(" + seconds + ", '%T')", "13:04:05");
        assertFunction("date_format(" + seconds + ", '%v')", "02");
        assertFunction("date_format(" + seconds + ", '%W')", "Tuesday");
        assertFunction("date_format(" + seconds + ", '%w')", "2");
        assertFunction("date_format(" + seconds + ", '%Y')", "2001");
        assertFunction("date_format(" + seconds + ", '%y')", "01");
        assertFunction("date_format(" + seconds + ", '%%')", "%");
        assertFunction("date_format(" + seconds + ", 'foo')", "foo");
        assertFunction("date_format(" + seconds + ", '%g')", "g");
        assertFunction("date_format(" + seconds + ", '%4')", "4");
    }

    @Test
    public void testDateParse()
    {
        assertFunction("date_parse('2013', '%Y')", getSeconds(new DateTime(2013, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC)));
        assertFunction("date_parse('2013-05', '%Y-%m')", getSeconds(new DateTime(2013, 5, 1, 0, 0, 0, 0, DateTimeZone.UTC)));
        assertFunction("date_parse('2013-05-17', '%Y-%m-%d')", getSeconds(new DateTime(2013, 5, 17, 0, 0, 0, 0, DateTimeZone.UTC)));
        assertFunction("date_parse('2013-05-17 12:35:10', '%Y-%m-%d %h:%i:%s')", getSeconds(new DateTime(2013, 5, 17, 0, 35, 10, 0, DateTimeZone.UTC)));
        assertFunction("date_parse('2013-05-17 12:35:10 PM', '%Y-%m-%d %h:%i:%s %p')", getSeconds(new DateTime(2013, 5, 17, 12, 35, 10, 0, DateTimeZone.UTC)));
        assertFunction("date_parse('2013-05-17 12:35:10 AM', '%Y-%m-%d %h:%i:%s %p')", getSeconds(new DateTime(2013, 5, 17, 0, 35, 10, 0, DateTimeZone.UTC)));

        assertFunction("date_parse('2013-05-17 00:35:10', '%Y-%m-%d %H:%i:%s')", getSeconds(new DateTime(2013, 5, 17, 0, 35, 10, 0, DateTimeZone.UTC)));
        assertFunction("date_parse('2013-05-17 23:35:10', '%Y-%m-%d %H:%i:%s')", getSeconds(new DateTime(2013, 5, 17, 23, 35, 10, 0, DateTimeZone.UTC)));
        assertFunction("date_parse('abc 2013-05-17 fff 23:35:10 xyz', 'abc %Y-%m-%d fff %H:%i:%s xyz')", getSeconds(new DateTime(2013, 5, 17, 23, 35, 10, 0, DateTimeZone.UTC)));

        assertFunction("date_parse('2013 14', '%Y %y')", getSeconds(new DateTime(2014, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC)));
    }


    private static long getSeconds(DateTime dateTime)
    {
        return MILLISECONDS.toSeconds(dateTime.getMillis());
    }

    private static long fromMillis(long millis)
    {
        return MILLISECONDS.toSeconds(millis);
    }
}
