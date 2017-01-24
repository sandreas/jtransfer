package de.fynder.jtransfer.core.location;

import de.fynder.jtransfer.toolbox.location.FileTransferLocation;
import junit.framework.TestCase;

public class AbstractTransferLocationFactoryTest extends TestCase {

    public void testFactoryWithFileUri() throws Exception {

        AbstractTransferLocation t = AbstractTransferLocationFactory.factory("fixtures/de/fynder");
        assertEquals(FileTransferLocation.class, t.getClass());
    }

    public void testFactoryWithInvalidUri() throws Exception {
        try {
            AbstractTransferLocationFactory.factory("invalid://no/valid/scheme");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Unsupported scheme: invalid");
        }
    }

}