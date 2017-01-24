package de.fynder.jtransfer.core.location;

import de.fynder.jtransfer.interfaces.ProgressListenerInterface;
import junit.framework.TestCase;
import org.junit.Before;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class AbstractTransferLocationTest extends TestCase {

    private final String testUrl = "dummy/url/for/testing";
    private AbstractTransferLocation location;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        location = new AbstractTransferLocation(testUrl) {
            @Override
            public void find() throws IOException {

            }

            @Override
            public void transferTo(AbstractTransferLocation location) throws IOException, NoSuchAlgorithmException {

            }
        };
    }

    public void testGetUrl() {
        assertEquals(testUrl, location.getUri());
        AbstractTransferLocation mockLocation = new AbstractTransferLocation(testUrl) {
            @Override
            public void find() throws IOException {

            }

            @Override
            public void transferTo(AbstractTransferLocation location) throws IOException, NoSuchAlgorithmException {

            }
        };
        ProgressListenerInterface mockListener = mock(ProgressListenerInterface.class);
        mockLocation.addListener(mockListener);
        mockLocation.notifyMessage("message");
        mockLocation.notifyProgress(1,2);
        mockLocation.notifyFinished(3);
        verify(mockListener, times(1)).notifyMessage("message");
        verify(mockListener, times(1)).notifyProgress(1,2);
        verify(mockListener, times(1)).notifyFinished(3);
    }

}