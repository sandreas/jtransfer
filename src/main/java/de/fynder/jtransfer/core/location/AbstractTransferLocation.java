package de.fynder.jtransfer.core.location;

import de.fynder.jtransfer.interfaces.LocationSettingsInterface;
import de.fynder.jtransfer.interfaces.ProgressListenerInterface;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

//enum TransferError {
//    SHOW_MATCH_FOUND,
//    SHOW_REPLACEMENTS,
//    CREATE_DIRECTORY_FAILED,
//    MOVE_FAILED_TRY_COPY,
//    DESTINATION_EXISTS_OVERWRITE,
//    DESTINATION_EXISTS_SKIP
//}

public abstract class AbstractTransferLocation implements ProgressListenerInterface {
    protected String uri;
    protected long chunkSize = 1024 * 1024 * 32; // 32MB
    // ReplacementApi replacementApi;
    protected LocationSettingsInterface settings;
    private ArrayList<ProgressListenerInterface> listeners = new ArrayList<>();


    protected AbstractTransferLocation(String patternUri) {
        uri = patternUri;
        settings = new LocationSettingsInterface() {
            @Override
            public boolean isFollowSymlinks() {
                return false;
            }

            @Override
            public boolean isForceOverwrite() {
                return false;
            }

            @Override
            public boolean isDryRun() {
                return false;
            }

            @Override
            public boolean isMove() {
                return false;
            }

            @Override
            public boolean isKeepTimes() {
                return false;
            }

            @Override
            public boolean isKeepPerms() {
                return false;
            }

            @Override
            public boolean isKeepOwner() {
                return false;
            }

            @Override
            public boolean isKeepGroup() {
                return false;
            }

            @Override
            public boolean isArchive() {
                return false;
            }

            @Override
            public long getMaxItems() {
                return 0;
            }

            @Override
            public String getExportTo() {
                return null;
            }

            @Override
            public String getFilesFrom() {
                return null;
            }

            @Override
            public String getMaxAge() {
                return null;
            }

            @Override
            public String getMinAge() {
                return null;
            }
        };
    }



    public void setSettings(LocationSettingsInterface s) {
        settings = s;
    }

/*
    public void setReplacementApi(ReplacementApi api) {
        replacementApi = api;
    }
*/

    protected String getUri() {
        return uri;
    }

    public abstract void find() throws IOException;

    public abstract void transferTo(AbstractTransferLocation location) throws IOException, NoSuchAlgorithmException;

    public void addListener(ProgressListenerInterface l) {
        listeners.add(l);
    }

    public void notifyMessage(String message) {
        for (ProgressListenerInterface i : listeners) {
            i.notifyMessage(message);
        }
    }

    public void notifyProgress(long bytesTransferred, long bytesToTransfer) {
        for (ProgressListenerInterface i : listeners) {
            i.notifyProgress(bytesTransferred, bytesToTransfer);
        }
    }

    public void notifyFinished(long bytesTransferred) {
        for (ProgressListenerInterface i : listeners) {
            i.notifyFinished(bytesTransferred);
        }
    }
}
