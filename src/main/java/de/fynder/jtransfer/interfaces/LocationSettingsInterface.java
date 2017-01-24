package de.fynder.jtransfer.interfaces;

public interface LocationSettingsInterface {
    boolean isFollowSymlinks();

    boolean isForceOverwrite();

    boolean isDryRun();

    boolean isMove();

    boolean isKeepTimes();

    boolean isKeepPerms();

    boolean isKeepOwner();

    boolean isKeepGroup();

    boolean isArchive();

    long getMaxItems();

    String getExportTo();

    String getFilesFrom();

    String getMaxAge();

    String getMinAge();

}
