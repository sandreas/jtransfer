package de.fynder.jtransfer.util;

import junit.framework.TestCase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtilityTest extends TestCase {


    private final Date reference = new Date(1485114625612L);

    public DateUtilityTest(String name) {
        super(name);
    }

    public void testStrToDate() throws IOException {
        assertEquals("2016-12-23T20:50:25.612Z", reformatStrToDate("-30 days"));
        assertEquals("2017-01-21T20:50:25.612Z", reformatStrToDate("-1 day"));
        assertEquals("2017-01-22T20:50:25.612Z", reformatStrToDate("now"));
        assertEquals("2017-01-23T20:50:25.612Z", reformatStrToDate("+1 day"));
        assertEquals("2017-02-21T20:50:25.612Z", reformatStrToDate("+30 days"));
        assertEquals("2017-02-21T20:50:25.612Z", reformatStrToDate("2017-02-21T20:50:25.612Z"));
        assertEquals("2017-02-21T00:00:00.000Z", reformatStrToDate("2017-02-21"));
    }

    public void testStrToAge() throws IOException {
        assertEquals("2016-12-23T20:50:25.612Z", reformatStrToAge("30 days"));
        assertEquals("2017-01-21T20:50:25.612Z", reformatStrToAge("1 day"));
        assertEquals("2017-01-22T20:50:25.612Z", reformatStrToAge("now"));
        assertEquals("2017-01-23T20:50:25.612Z", reformatStrToAge("-1 day"));
        assertEquals("2017-02-21T20:50:25.612Z", reformatStrToAge("-30 days"));
        assertEquals("2017-02-21T20:50:25.612Z", reformatStrToAge("2017-02-21T20:50:25.612Z"));
        assertEquals("2017-02-21T00:00:00.000Z", reformatStrToAge("2017-02-21"));

    }


    private String reformatStrToDate(final String date) {
        Date d = DateUtility.strToDate(date, reference);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return f.format(d);
    }

    private String reformatStrToAge(String date) {
        Date d = DateUtility.strToAge(date, reference);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return f.format(d);
    }

}
