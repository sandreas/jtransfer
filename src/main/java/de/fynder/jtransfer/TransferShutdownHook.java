package de.fynder.jtransfer;

public class TransferShutdownHook extends Thread
{
    private final Transfer t;

    TransferShutdownHook(Transfer t) {
        this.t = t;
    }
    public void run()
    {
        t.println("");
    }
}