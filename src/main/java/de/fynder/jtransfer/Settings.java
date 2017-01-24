package de.fynder.jtransfer;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import de.fynder.jtransfer.interfaces.LocationSettingsInterface;

import java.util.ArrayList;
import java.util.List;

@Parameters(separators = "=")
public class Settings implements LocationSettingsInterface {

    @Parameter(description = "source...pattern [destination...replacement]", required = true)
    List<String> patterns = new ArrayList<>();

    @Parameter(names = "--debug", description = "show debug info")
    boolean debug = false;

    @Parameter(names = "--quiet", description = "do not show any output")
    boolean quiet = false;

    @Parameter(names = "--follow-symlinks", description = "regard contents of symlinked folders")
    boolean followSymlinks = false;

    @Parameter(names = "--force-overwrite", description = "force overwrite of existing files that diff from source")
    boolean forceOverwrite = false;

    @Parameter(names = "--dry-run", description = "perform a dry run without transferring")
    boolean dryRun = false;

    @Parameter(names = "--move", description = "move instead of copy")
    boolean move = false;

    @Parameter(names = "--times", description = "preserves times")
    boolean keepTimes = false;

    @Parameter(names = "--perms", description = "preserves permissions")
    boolean keepPerms = false;

    @Parameter(names = "--owner", description = "preserve owner (super-user only)")
    boolean keepOwner = false;

    @Parameter(names = "--group", description = "preserve group")
    boolean keepGroup = false;

    @Parameter(names = "--archive", description = "preserves all file attributes, if possible")
    boolean archive = false;

    @Parameter(names = "--max-items", description = "Maximum number of matching items")
    private long maxItems;

    @Parameter(names = "--export-to", description = "export source listing to file, one line per source")
    String exportTo;

    @Parameter(names = "--files-from", description = "import source listing from file, one line per source")
    String filesFrom;

    @Parameter(names = "--min-age", description = "minimum age (e.g. -2 days, -8 weeks, 2015-10-10, etc.)")
    String minAge;

    @Parameter(names = "--max-age", description = "maximum age (e.g. -2 days, -8 weeks, 2015-10-10, etc.)")
    String maxAge;


//    @Parameter(names = "--enable-plugins", description = "enable plugins (number, id3, exif, etc.), which is slower")
//    boolean enablePlugins = true;

    @Override
    public boolean isFollowSymlinks() {
        return followSymlinks;
    }

    @Override
    public boolean isForceOverwrite() {
        return forceOverwrite;
    }

    @Override
    public boolean isDryRun() {
        return dryRun;
    }

    @Override
    public boolean isMove() {
        return move;
    }

    @Override
    public boolean isKeepTimes() {return archive || keepTimes;}

    @Override
    public boolean isKeepPerms() {
        return archive || keepPerms;
    }

    @Override
    public boolean isKeepOwner() {
        return archive || keepOwner;
    }

    @Override
    public boolean isKeepGroup() {
        return archive || keepGroup;
    }

    @Override
    public boolean isArchive() {
        return archive;
    }



    @Override
    public String getExportTo() {
        return exportTo;
    }

    @Override
    public String getFilesFrom() {return filesFrom;}

    @Override
    public String getMaxAge() {
        return maxAge;
    }

    @Override
    public String getMinAge() {
        return minAge;
    }

    @Override
    public long getMaxItems() {
        return maxItems;
    }
}