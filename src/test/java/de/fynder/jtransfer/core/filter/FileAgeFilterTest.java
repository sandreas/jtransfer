package de.fynder.jtransfer.core.filter;

import junit.framework.TestCase;
import org.junit.Before;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;

import static org.mockito.Mockito.when;


public class FileAgeFilterTest extends TestCase {
    private FileAgeFilter subject;
    private BasicFileAttributes mockAttributes = Mockito.mock(BasicFileAttributes.class);
    private final long referenceTime = 1485114625612L;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testMinAgeMatch() throws IOException {
        FileTime mockFileTime = FileTime.fromMillis(referenceTime - 1);
        when(mockAttributes.lastModifiedTime()).thenReturn(mockFileTime);
        subject = new FileAgeFilter(new Date(referenceTime), FileAgeFilter.MIN_AGE);
        assertTrue(subject.matches("dummy.txt", mockAttributes, null));
    }

    public void testMinAgeNoMatch() throws IOException {
        FileTime mockFileTime = FileTime.fromMillis(referenceTime + 1);
        when(mockAttributes.lastModifiedTime()).thenReturn(mockFileTime);
        subject = new FileAgeFilter(new Date(referenceTime), FileAgeFilter.MIN_AGE);
        assertFalse(subject.matches("dummy.txt", mockAttributes, null));
    }

    public void testMaxAgeMatch() throws IOException {
        FileTime mockFileTime = FileTime.fromMillis(referenceTime - 1);
        when(mockAttributes.lastModifiedTime()).thenReturn(mockFileTime);
        subject = new FileAgeFilter(new Date(referenceTime), FileAgeFilter.MAX_AGE);
        assertFalse(subject.matches("dummy.txt", mockAttributes, null));
    }

    public void testMaxAgeNoMatch() throws IOException {
        FileTime mockFileTime = FileTime.fromMillis(referenceTime + 1);
        when(mockAttributes.lastModifiedTime()).thenReturn(mockFileTime);
        subject = new FileAgeFilter(new Date(referenceTime), FileAgeFilter.MAX_AGE);
        assertTrue(subject.matches("dummy.txt", mockAttributes, null));
    }

}