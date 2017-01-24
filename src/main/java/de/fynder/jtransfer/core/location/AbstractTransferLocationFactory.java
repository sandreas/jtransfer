package de.fynder.jtransfer.core.location;

import de.fynder.jtransfer.toolbox.location.FileTransferLocation;


public abstract class AbstractTransferLocationFactory {
    public static AbstractTransferLocation factory(String uri) throws Exception {
        String scheme = "file";
        int schemeEnd = uri.indexOf("://");
        if(schemeEnd > 0) {
            scheme = uri.substring(0, schemeEnd);
        }
        if(scheme.equals("file")) {
            return new FileTransferLocation(uri);
        }
        throw new Exception("Unsupported scheme: " + scheme);
    }
}
