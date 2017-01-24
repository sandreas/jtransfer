package de.fynder.jtransfer.util;

import de.fynder.jtransfer.util.Hasher;
import junit.framework.TestCase;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HasherTest extends TestCase {
    private String fixturePath = "fixtures/de/fynder/jtransfer/toolbox/file/HasherTest";
    private Hasher subject;

    public HasherTest(String name) {
        super(name);

        try {
            subject = new Hasher(MessageDigest.getInstance("SHA-256"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void testHashFile() throws IOException {
        assertEquals("84d89877f0d4041efb6bf91a16f0248f2fd573e6af05c19f96bedb9f882f7882", subject.hashFile(Paths.get(fixturePath + "/0-9.txt")));
        assertEquals("40471774ebe0b7361753f55c9999a3ffb91818d3c8ad6ba7c7c67cd9f1b4075d", subject.hashFile(Paths.get(fixturePath + "/0-9a-z.txt")));
    }

    public void testHashFilePart() throws IOException {
        assertEquals("84d89877f0d4041efb6bf91a16f0248f2fd573e6af05c19f96bedb9f882f7882", subject.hashFilePart(Paths.get(fixturePath + "/0-9a-z.txt"), 0, 10));
        assertEquals("84d89877f0d4041efb6bf91a16f0248f2fd573e6af05c19f96bedb9f882f7882", subject.hashFilePart(Paths.get(fixturePath + "/a-z0-9a-z.txt"), 27, 10));
    }
}
