package de.fynder.jtransfer.core.file;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;


public class FilteredWalkerTest extends TestCase {
    private FilteredWalker walker;
    private Path fixturesPath = Paths.get("fixtures/de/fynder/jtransfer/core/file/FilteredWalker");
    @Before
    public void setUp() throws Exception {
        super.setUp();
        walker = new FilteredWalker();
    }

    public void testWalkWithoutFilter() throws IOException {
        String[] actual = walker.walk(fixturesPath).toArray(String[]::new);
        String[] expected = new String[] {
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker",
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/0-9.txt",
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/0-9a-z.txt",
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/a-z0-9a-z.txt",
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/subdir",
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/subdir/subfile.txt",
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/subdir/subsubdir",
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/subdir/subsubdir/0-9.txt"
        };

        Assert.assertArrayEquals(expected, actual);
    }

    public void testWalkWithSingleNegativeFilter() throws IOException {
        walker.addFilter(s -> false);

        String[] actual = walker.walk(fixturesPath).toArray(String[]::new);
        String[] expected = new String[] {};

        Assert.assertArrayEquals(expected, actual);
    }

    public void testWalkWithRegexFilter() throws IOException {
        Pattern p = Pattern.compile("fixtures/de/fynder/jtransfer/core/file/FilteredWalker/0-9\\.txt");
        walker.addFilter(s -> p.matcher(s).matches());

        String[] actual = walker.walk(fixturesPath).toArray(String[]::new);
        String[] expected = new String[] {
                "fixtures/de/fynder/jtransfer/core/file/FilteredWalker/0-9.txt",
        };

        assertTrue(walker.matches("fixtures/de/fynder/jtransfer/core/file/FilteredWalker/0-9.txt"));
        assertFalse(walker.matches("fixtures/de/fynder/jtransfer/core/file/FilteredWalker/dummy.txt"));

        Assert.assertArrayEquals(expected, actual);
    }

}