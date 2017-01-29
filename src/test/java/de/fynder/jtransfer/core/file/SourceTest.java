package de.fynder.jtransfer.core.file;

import junit.framework.TestCase;
import org.junit.Before;

import java.io.IOException;


public class SourceTest extends TestCase {
    private Source subject;


    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testPathWithEmptyPattern() throws IOException {
        subject = new Source("fixtures/de/fynder/jtransfer/core/file/Source/");
        assertFalse(subject.hasPattern());
        assertFalse(subject.isFile());
        assertNull(subject.getPattern());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/Source", subject.getLocationAsString());
    }

    public void testFile() throws IOException {
        subject = new Source("fixtures/de/fynder/jtransfer/core/file/Source/dummy-file.txt");
        assertFalse(subject.hasPattern());
        assertTrue(subject.isFile());
        assertNull(subject.getPattern());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/Source/dummy-file.txt", subject.getLocationAsString());
    }

    public void testPathWithPattern() throws IOException {
        subject = new Source("fixtures/de/fynder/jtransfer/core/file/Source/(.*)");
        assertTrue(subject.hasPattern());
        assertFalse(subject.isFile());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/Source/(.*)", subject.getPattern().toString());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/Source", subject.getLocationAsString());
    }

    public void testFileWithPattern() throws IOException {
        subject = new Source("fixtures/de/fynder/jtransfer/core/file/Source/dummy-(.*)\\.txt");
        assertTrue(subject.hasPattern());
        assertFalse(subject.isFile());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/Source/dummy-(.*)\\.txt", subject.getPattern().toString());
        assertEquals("fixtures/de/fynder/jtransfer/core/file/Source", subject.getLocationAsString());
    }

}