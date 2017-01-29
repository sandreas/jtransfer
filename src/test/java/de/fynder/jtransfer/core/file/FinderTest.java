package de.fynder.jtransfer.core.file;

import de.fynder.jtransfer.core.filter.MatchPatternFilter;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.mockito.Mockito.when;


public class FinderTest extends TestCase {
    private Finder subject = new Finder();
    private Source sourceMock = Mockito.mock(Source.class);

    private String fixturesPath = "fixtures/de/fynder/jtransfer/core/file/Finder";


    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(sourceMock.getLocationAsString()).thenReturn(fixturesPath);
    }

    public void testWithoutFilter() throws IOException {

        String[] expected = new String[]{
                "fixtures/de/fynder/jtransfer/core/file/Finder",
                "fixtures/de/fynder/jtransfer/core/file/Finder/0-9.txt",
                "fixtures/de/fynder/jtransfer/core/file/Finder/0-9a-z.txt",
                "fixtures/de/fynder/jtransfer/core/file/Finder/a-z0-9a-z.txt",
                "fixtures/de/fynder/jtransfer/core/file/Finder/subdir",
                "fixtures/de/fynder/jtransfer/core/file/Finder/subdir/subfile.ini",
                "fixtures/de/fynder/jtransfer/core/file/Finder/subdir/subsubdir",
                "fixtures/de/fynder/jtransfer/core/file/Finder/subdir/subsubdir/0-9.log"
        };
        Assert.assertArrayEquals(expected, subject.walk(sourceMock).toArray());
    }

    public void testWithPatternForLogFiles() throws IOException {
        when(sourceMock.getLocationAsString()).thenReturn(fixturesPath);
        when(sourceMock.hasPattern()).thenReturn(true);
        when(sourceMock.getPattern()).thenReturn(mockLogFilePattern());

        subject.addFilter(new MatchPatternFilter());

        String[] expected = new String[]{
                "fixtures/de/fynder/jtransfer/core/file/Finder/subdir/subsubdir/0-9.log"
        };
        Assert.assertArrayEquals(expected, subject.walk(sourceMock).toArray());
    }

    private Pattern mockLogFilePattern() {
        return Pattern.compile(fixturesPath + "/(.*)\\.log");
    }


}