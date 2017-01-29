package de.fynder.jtransfer.core.file;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;


public class FindLocationTest extends TestCase {
    private FindLocation subject;


    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testPathWithEmptyPattern() throws IOException {
        subject = new FindLocation("fixtures/de/fynder/jtransfer/core/file/FindLocation/");
        assertFalse(subject.hasPattern());
        assertFalse(subject.isFile());
        assertNull(subject.getPattern());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/FindLocation", subject.getLocationAsString());
    }

    public void testFile() throws IOException {
        subject = new FindLocation("fixtures/de/fynder/jtransfer/core/file/FindLocation/dummy-file.txt");
        assertFalse(subject.hasPattern());
        assertTrue(subject.isFile());
        assertNull(subject.getPattern());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/FindLocation/dummy-file.txt", subject.getLocationAsString());
    }

    public void testPathWithPattern() throws IOException {
        subject = new FindLocation("fixtures/de/fynder/jtransfer/core/file/FindLocation/(.*)");
        assertTrue(subject.hasPattern());
        assertFalse(subject.isFile());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/FindLocation/(.*)", subject.getPattern().toString());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/FindLocation", subject.getLocationAsString());
    }

    public void testFileWithPattern() throws IOException {
        subject = new FindLocation("fixtures/de/fynder/jtransfer/core/file/FindLocation/dummy-(.*)\\.txt");
        assertTrue(subject.hasPattern());
        assertFalse(subject.isFile());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/FindLocation/dummy-(.*)\\.txt", subject.getPattern().toString());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/FindLocation", subject.getLocationAsString());
    }

}