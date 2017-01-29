package de.fynder.jtransfer.core.filter;

import junit.framework.TestCase;
import org.junit.Before;

import java.io.IOException;
import java.util.regex.Pattern;


public class MatchPatternFilterTest extends TestCase {
    private MatchPatternFilter subject = new MatchPatternFilter();


    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testTxtPattern() throws IOException {
        String matchingPath = "de/fixtures/dummy-file.txt";
        Pattern pattern = Pattern.compile("de/fixtures/(.*)\\.txt");

        assertTrue(subject.matches(matchingPath, null, pattern));

        String nonMatchingPath = "de/fixtures/dummy-file.log";
        assertFalse(subject.matches(nonMatchingPath, null, pattern));
    }


}