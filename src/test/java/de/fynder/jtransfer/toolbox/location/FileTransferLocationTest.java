package de.fynder.jtransfer.toolbox.location;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by aschroden on 12.01.2017.
 */
public class FileTransferLocationTest extends TestCase {
    ArrayList<String> uriList = new ArrayList<String>();
    String destinationPath = "fixtures/tmp/FileTransferLocation";


    private void cleanUp() throws IOException {
        uriList.clear();


        Path rootPath = Paths.get(destinationPath);
        if(!Files.exists(rootPath)) {
            return;
        }

        Files.walk(rootPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }


    public void testWalkPath() throws Exception {
        cleanUp();
        FileTransferLocation f = new FileTransferLocation("fixtures/de/fynder/jtransfer/toolbox/location/FileTransferLocation/");
        f.walk().forEach(u -> uriList.add(u));
        assertEquals(8, uriList.size());
    }

    public void testWalkFile() throws Exception {
        cleanUp();
        FileTransferLocation f = new FileTransferLocation("fixtures/de/fynder/jtransfer/toolbox/location/FileTransferLocation/0-9.txt");
        f.walk().forEach(u -> uriList.add(u));
        assertEquals(1, uriList.size());
    }

    public void testWalkRegex() throws Exception {
        cleanUp();
        FileTransferLocation f = new FileTransferLocation("fixtures/de/fynder/jtransfer/toolbox/location/FileTransferLocation/(.*)subfile\\.txt");
        f.walk().forEach(u -> uriList.add(u));
        assertEquals(1, uriList.size());
    }

    public void testMapDestination() throws Exception {
        cleanUp();
        FileTransferLocation f = new FileTransferLocation("fixtures/de/fynder/jtransfer/toolbox/location/FileTransferLocation/");
        f.mapDestination(new FileTransferLocation(destinationPath));
        HashMap<String, String> map = f.getDestinationMap();
        assertEquals(8, map.size());


        Iterator it = map.entrySet().iterator();

        Map.Entry pair = (Map.Entry) it.next();
        assertEquals("fixtures/de/fynder/jtransfer/toolbox/location/FileTransferLocation/subdir/subsubdir/0-9.txt", pair.getKey());
        assertEquals(destinationPath + "/subdir/subsubdir/0-9.txt", pair.getValue());

        pair = (Map.Entry) it.next();
        assertEquals("fixtures/de/fynder/jtransfer/toolbox/location/FileTransferLocation/subdir", pair.getKey());
        assertEquals(destinationPath + "/subdir", pair.getValue());

    }

    public void testCopyTo() throws IOException, NoSuchAlgorithmException {
        cleanUp();
        FileTransferLocation f = new FileTransferLocation("fixtures/de/fynder/jtransfer/toolbox/location/FileTransferLocation/");
        f.transferTo(new FileTransferLocation(destinationPath));
    }


}