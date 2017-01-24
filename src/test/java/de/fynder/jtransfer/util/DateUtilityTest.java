package de.fynder.jtransfer.util;

import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtilityTest extends TestCase {


    private final Date reference = new Date(1485114625612L);

    public DateUtilityTest(String name) {
        super(name);
    }

    public void testStrToDAte() throws IOException {
        assertEquals("2016-12-23T20:50:25.612Z", reformatDateString("-30 days"));
        assertEquals("2017-01-21T20:50:25.612Z", reformatDateString("-1 day"));
        assertEquals("2017-01-22T20:50:25.612Z", reformatDateString("now"));
        assertEquals("2017-01-23T20:50:25.612Z", reformatDateString("+1 day"));
        assertEquals("2017-02-21T20:50:25.612Z", reformatDateString("+30 days"));
        assertEquals("2017-02-21T20:50:25.612Z", reformatDateString("2017-02-21T20:50:25.612Z"));
        assertEquals("2017-02-21T00:00:00.000Z", reformatDateString("2017-02-21"));
    }

    private String reformatDateString(final String date) {
        Date d = DateUtility.strToDate(date, reference);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return f.format(d);
    }

}
