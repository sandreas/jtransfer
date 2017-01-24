package de.fynder.jtransfer.interfaces;


public interface ProgressListenerInterface {
    void notifyMessage(String message);

    void notifyProgress(long bytesTransferred, long bytesToTransfer);

    void notifyFinished(long bytesTransferred);
}
