package de.fynder.jtransfer.toolbox.location;

import de.fynder.jtransfer.core.file.FilteredWalker;
import de.fynder.jtransfer.util.DateUtility;
import de.fynder.jtransfer.util.Hasher;
import de.fynder.jtransfer.core.location.AbstractTransferLocation;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileTransferLocation extends AbstractTransferLocation {
    private final FilteredWalker walker;
    private HashMap<String, String> destinationMapping = new HashMap<>();

    private Path lookupPath;
    private Pattern lookupPattern;
    private Hasher hasher;
    private PrintWriter exportToWriter;
    private Date minAge;
    private Date maxAge;

    public FileTransferLocation(String patternUri) {

        super(patternUri);
        walker = new FilteredWalker();
    }

    HashMap<String, String> getDestinationMap() {
        return destinationMapping;
    }

    public void find() throws IOException {
        initExport();
        walk().forEach(this::findHandler);
        finishExport();
    }

    private void initExport() {
        if (settings.getExportTo() != null) {
            if (settings.getFilesFrom() != null) {
                this.notifyMessage("--from-files and -export-to cannot be combined, --export-to is ignored");
                return;
            }
            try {
                exportToWriter = new PrintWriter(settings.getExportTo());
            } catch (IOException e) {
                this.notifyMessage("Could not open file for --export-to: " + settings.getExportTo());
            }
        }
    }

    Stream<String> walk() throws IOException {
        parseUri();
        if (settings.getFilesFrom() != null) {
            return Files.lines(Paths.get(settings.getFilesFrom()));
        }

        /*
        Files.find(p,maxDepth,(path, basicFileAttributes) -> {
            if (String.valueOf(path).equals("workspace")) {
                System.out.println("FOUND : " + path);
                return true;
            }
            System.out.println("\tNOT VALID : " + path);
            return false;
        });
         */

/*
    private boolean filterPattern(Path path) {
        // /tmp/(.*) matches /tmp
        return lookupPattern.matcher(normalizeDirectorySeparatorsToSlashes(path.toString())).matches();
    }


 */
        // add lookupPaternMatcher
        if (hasPattern()) {
            walker.addFilter(this::filterPattern);
        }


        if (settings.getMinAge() != null) {
            minAge = DateUtility.strToDate(settings.getMinAge());
            if (minAge != null) {
                walker.addFilter(this::filterMinAge);
            } else {
                this.notifyMessage("invalid value for --min-age: " + settings.getMinAge());
            }

        }

        if (settings.getMaxAge() != null) {
            maxAge = DateUtility.strToDate(settings.getMaxAge());
            if (maxAge != null) {
                walker.addFilter(this::filterMaxAge);
            } else {
                this.notifyMessage("invalid value for --max-age: " + settings.getMaxAge());
            }
        }

        Stream<String> w = walker.walk(lookupPath, FileVisitOption.FOLLOW_LINKS);
        if(settings.getMaxItems() > 0) {
            w = w.limit(settings.getMaxItems());
        }
        if (settings.isFollowSymlinks()) {
            return w;
        }
        return w;
        /*
        Stream<Path> walker;
        if (settings.isFollowSymlinks()) {
            walker = Files.walk(lookupPath, FileVisitOption.FOLLOW_LINKS);
        } else {
            walker = Files.walk(lookupPath);
        }
        if (hasPattern()) {
            walker = walker.filterPattern(this::filterPattern);
        }
        return walker.map(Path::toString);
        */

        /*
        Stream<Path> walker;
        if (settings.isFollowSymlinks()) {
            walker = Files.walk(lookupPath, FileVisitOption.FOLLOW_LINKS);
        } else {
            walker = Files.walk(lookupPath);
        }
        if (hasPattern()) {
            walker = walker.filterPattern(this::filterPattern);
        }
        return walker.map(Path::toString);
        */
    }

    private void finishExport() {
        if (exportToWriter != null) {
            exportToWriter.close();
        }
    }

    private void parseUri() {
        String validPart = uri;
        int lastSlashPos;
        lookupPattern = null;
        String lookupPatternAsString = "";
        lookupPath = null;
        do {
            try {
                lookupPath = Paths.get(validPart);
                if (lookupPath.toFile().exists()) {
                    break;
                }
            } catch (Exception ignored) {
            }
            lastSlashPos = normalizeDirectorySeparatorsToSlashes(validPart).lastIndexOf("/");
            if (lastSlashPos > 0) {
                lookupPatternAsString = validPart.substring(lastSlashPos) + lookupPatternAsString;
                validPart = validPart.substring(0, lastSlashPos);
            }
        } while (lastSlashPos > 0);

        if (lookupPatternAsString.length() > 0) {
            lookupPatternAsString = lookupPatternAsString.substring(1);
            lookupPattern = Pattern.compile(normalizeDirectorySeparatorsToSlashes(lookupPath.toString()) + "/" + lookupPatternAsString, Pattern.CASE_INSENSITIVE);
        } else {
            lookupPattern = null;
        }
    }

    private boolean hasPattern() {
        return lookupPattern != null;
    }

    private String normalizeDirectorySeparatorsToSlashes(String pathString) {
        final String maskedDollar = "\\$";

        if (!pathString.contains(maskedDollar)) {
            return pathString.replaceAll("\\\\", "/");
        }

        StringBuilder result = new StringBuilder();
        int start = 0;
        int index = pathString.indexOf(maskedDollar);
        while (index >= 0) {
            result.append(pathString.substring(start, index).replaceAll("\\\\", "/")).append(maskedDollar);
            start = index + 2;
            index = pathString.indexOf(maskedDollar, index + 1);
        }
        if (pathString.length() >= start) {
            result.append(pathString.substring(start));
        }
        return result.toString();
    }

    public void transferTo(AbstractTransferLocation dst) throws IOException, NoSuchAlgorithmException {
        mapDestination((FileTransferLocation) dst);
        initHasher();
        transferMappedFiles();
    }

    void mapDestination(FileTransferLocation dst) throws IOException {
        this.notifyMessage("Mapping " + uri + " => " + dst.getUri());

        initExport();

        walk().forEach(s -> replaceSrc(s, dst));

        finishExport();
    }

    private void initHasher() throws NoSuchAlgorithmException {
        hasher = new Hasher(MessageDigest.getInstance("SHA1"));
    }

    private void transferMappedFiles() throws IOException {
        Iterator it = destinationMapping.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            handleTransfer((String) pair.getKey(), (String) pair.getValue());
            it.remove();
        }
    }

    private void replaceSrc(String from, FileTransferLocation dst) {
        Path fromPath = Paths.get(from);
        String replacedDestinationPattern = normalizeDirectorySeparatorsToSlashes(dst.getUri());
        if (lookupPattern != null) {
            replacedDestinationPattern = lookupPattern.matcher(normalizeDirectorySeparatorsToSlashes(from)).replaceFirst(replacedDestinationPattern);
        } else {
            replacedDestinationPattern = from.replace(normalizeDirectorySeparatorsToSlashes(lookupPath.toString()), replacedDestinationPattern);
        }

        Path toPath = Paths.get(replacedDestinationPattern);
        if (fromPath.toString().equals(toPath.toString())) {
            return;
        }

        if (Files.isDirectory(fromPath) && Files.isDirectory(toPath)) {
            return;
        }

        exportLine(fromPath.toString());

        destinationMapping.put(fromPath.toString(), toPath.toString());
    }

    private void handleTransfer(String srcPathString, String dstPathString) throws IOException {
        Path fromPath = Paths.get(srcPathString);
        Path toPath = Paths.get(dstPathString);

        this.notifyMessage(srcPathString + " => " + dstPathString);

        if (settings.isDryRun()) {
            return;
        }

        if (Files.isDirectory(fromPath)) {
            if (!Files.exists(toPath) && !toPath.toFile().mkdirs()) {
                this.notifyMessage("Could not create directory " + toPath.toString());
            }
            return;
        }


        if (!Files.exists(toPath)) {
            transfer(fromPath, toPath, 0);
            return;
        }


        long fromSize = Files.size(fromPath);
        long toSize = Files.size(toPath);

        if (!shouldResume(fromPath, fromSize, toPath, toSize)) {
            overwriteOrSkip(fromPath, toPath);
            return;
        }

        if (toSize < fromSize) {
            transfer(fromPath, toPath, toSize);
        }
    }

    private void transfer(Path from, Path to, long offset) throws IOException {
        if (Files.isRegularFile(from) && !Files.exists(to.getParent())) {
            if (!to.getParent().toFile().mkdirs()) {
                this.notifyMessage("failed to create directory " + to.getParent().toString());
                return;
            }
        }
        try {
            if (settings.isMove()) {
                if (settings.isForceOverwrite()) {
                    Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(from, to);
                }
                return;
            }
        } catch (Exception e) {
            this.notifyMessage("could not move " + from.toString() + " to " + to.toString());
            this.notifyMessage("trying to copy and delete after instead");
        }

        boolean append = (offset > 0);
        copy(new FileInputStream(from.toFile()).getChannel(), new FileOutputStream(to.toFile(), append).getChannel(), offset);


//        BasicFileAttributeView – Provides a view of basic attributes that are required to be supported by all file system implementations.
//        DosFileAttributeView – Extends the basic attribute view with the standard four bits supported on file systems that support the DOS attributes.
//        PosixFileAttributeView – Extends the basic attribute view with attributes supported on file systems that support the POSIX family of standards, such as UNIX. These attributes include file owner, group owner, and the nine related access permissions.
//        FileOwnerAttributeView – Supported by any file system implementation that supports the concept of a file owner.
//        AclFileAttributeView – Supports reading or updating a file's Access Control Lists (ACL). The NFSv4 ACL model is supported. Any ACL model, such as the Windows ACL model, that has a well-defined mapping to the NFSv4 model might also be supported.
//        UserDefinedFileAttributeView – Enable

        // extended before basic, because of times

        // copyExtendedAttributes(from, to);

        if (settings.isKeepPerms()) {
            copyAcl(from, to);
        }

        if (settings.isKeepOwner()) {
            copyOwner(from, to);
        }

        if (settings.isArchive()) {
            copyDosAttributes(from, to);
        }

        if (settings.isKeepGroup() || settings.isKeepPerms()) {
            copyPosixAttributes(from, to);
        }

        if (settings.isArchive()) {
            copyUserDefinedAttributes(from, to);
        }

        if (settings.isKeepTimes()) {
            copyTimes(from, to);
        }

        if (settings.isMove()) {
            Files.delete(from);
        }
    }

    private boolean shouldResume(Path fromPath, long fromSize, Path toPath, long toSize) throws IOException {
        if (toSize > fromSize) {
            return false;
        }

        int hashSize = 1024 * 1024;
        long maxHashSize = Math.min(hashSize, Math.min(fromSize, toSize));
        ArrayList<Long> hashOffsets = new ArrayList<>();
        hashOffsets.add(0L);
        if (toSize > 2 * hashSize) {
            hashOffsets.add(toSize - hashSize);
        }

        for (long o : hashOffsets) {
            String fromHash = hasher.hashFilePart(fromPath, o, maxHashSize);
            String toHash = hasher.hashFilePart(toPath, o, maxHashSize);
            if (!fromHash.equals(toHash)) {
                return false;
            }
        }
        return true;
    }

    private void overwriteOrSkip(Path from, Path to) throws IOException {
        if (settings.isForceOverwrite()) {
            transfer(from, to, 0);
            this.notifyMessage("destination file " + to + " already exists and differs from source => overwrite");
        } else {
            this.notifyMessage("destination file " + to + " already exists and differs from source => skip");
        }
    }

    private void copy(FileChannel in, FileChannel out, long offset) throws IOException {

        long bytesTransferred = offset;

        long size = in.size();
        long bytesToTransfer = size - offset;
        long chunkSizeTmp;

        while (bytesTransferred < size) {
            chunkSizeTmp = Math.min(chunkSize, bytesToTransfer);
            bytesTransferred += in.transferTo(bytesTransferred, chunkSizeTmp, out);
            bytesToTransfer -= chunkSizeTmp;
            this.notifyProgress(bytesTransferred, bytesToTransfer);
        }

        in.close();
        out.close();
        this.notifyFinished(bytesTransferred);
    }

    private void copyAcl(Path source, Path target) {
        try {
            AclFileAttributeView acl = Files.getFileAttributeView(source, AclFileAttributeView.class);
            if (acl != null) {
                Files.getFileAttributeView(target, AclFileAttributeView.class).setAcl(acl.getAcl());
            }
        } catch (Throwable e) {
            this.notifyMessage("Could not transfer acl file attributes from " + source + " to " + target + " (" + e.getMessage() + ")");
        }
    }

    private void copyOwner(Path source, Path target) {
        try {
            FileOwnerAttributeView ownerAttrs = Files.getFileAttributeView(source, FileOwnerAttributeView.class);
            if (ownerAttrs != null) {
                FileOwnerAttributeView targetOwner = Files.getFileAttributeView(target, FileOwnerAttributeView.class);
                targetOwner.setOwner(ownerAttrs.getOwner());
            }
        } catch (Throwable e) {
            this.notifyMessage("Could not transfer owner file attributes from " + source + " to " + target + " (" + e.getMessage() + ")");
        }
    }

    private void copyDosAttributes(Path source, Path target) {
        try {
            DosFileAttributeView dosAttrs = Files.getFileAttributeView(source, DosFileAttributeView.class);
            if (dosAttrs != null) {
                DosFileAttributes sourceDosAttrs = dosAttrs.readAttributes();
                DosFileAttributeView targetDosAttrs = Files.getFileAttributeView(target, DosFileAttributeView.class);
                targetDosAttrs.setArchive(sourceDosAttrs.isArchive());
                targetDosAttrs.setHidden(sourceDosAttrs.isHidden());
                targetDosAttrs.setReadOnly(sourceDosAttrs.isReadOnly());
                targetDosAttrs.setSystem(sourceDosAttrs.isSystem());
            }
        } catch (Throwable e) {
            this.notifyMessage("Could not transfer dos file attributes from " + source + " to " + target + " (" + e.getMessage() + ")");
        }
    }

    private void copyPosixAttributes(Path source, Path target) {
        try {
            PosixFileAttributeView posixAttrs = Files.getFileAttributeView(source, PosixFileAttributeView.class);
            if (posixAttrs != null) {
                PosixFileAttributes sourcePosix = posixAttrs.readAttributes();
                PosixFileAttributeView targetPosix = Files.getFileAttributeView(target, PosixFileAttributeView.class);
                if (settings.isKeepPerms()) {
                    targetPosix.setPermissions(sourcePosix.permissions());
                }
                if (settings.isKeepGroup()) {
                    targetPosix.setGroup(sourcePosix.group());
                }
            }
        } catch (Throwable e) {
            this.notifyMessage("Could not transfer posix file attributes from " + source + " to " + target + " (" + e.getMessage() + ")");
        }
    }


//    private void copyExtendedAttributes(Path source, Path target) {
//        try {
//
////            -p, --perms                 preserve permissions
////            --executability         preserve executability
////            --chmod=CHMOD           affect file and/or directory permissions
////            -o, --owner                 preserve owner (super-user only)
////            -g, --group                 preserve group
////            --devices               preserve device files (super-user only)
////                    --specials              preserve special files
////            -D                          same as --devices --specials
////                    -t, --times                 preserve times
//
//
//
//
//
//
//
//
//
//        } catch (Throwable e) {
//            this.notifyMessage("Could not transfer extended file attributes (ownership, permissions, etc.) from " + source + " to " + target + " (" + e.getMessage() + ")");
//        }
//    }

    private void copyUserDefinedAttributes(Path source, Path target) {
        try {
            UserDefinedFileAttributeView userAttrs = Files.getFileAttributeView(source, UserDefinedFileAttributeView.class);
            if (userAttrs != null) {
                UserDefinedFileAttributeView targetUser = Files.getFileAttributeView(target, UserDefinedFileAttributeView.class);
                for (String key : userAttrs.list()) {
                    ByteBuffer buffer = ByteBuffer.allocate(userAttrs.size(key));
                    userAttrs.read(key, buffer);
                    buffer.flip();
                    targetUser.write(key, buffer);
                }
            }
        } catch (Throwable e) {
            this.notifyMessage("Could not transfer posix file attributes from " + source + " to " + target + " (" + e.getMessage() + ")");
        }
    }

    private boolean copyTimes(Path source, Path target) {
        LinkOption[] linkOptions = (settings.isFollowSymlinks()) ? new LinkOption[0] :
                new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        try {
            BasicFileAttributes attrs = Files.readAttributes(source,
                    BasicFileAttributes.class,
                    linkOptions);
            if (attrs.isSymbolicLink()) {
                this.notifyMessage("File " + source + " is a symbolic link, file attributes are not transferred");
                return false;
            }
            BasicFileAttributeView view =
                    Files.getFileAttributeView(target, BasicFileAttributeView.class);
            view.setTimes(attrs.lastModifiedTime(),
                    attrs.lastAccessTime(),
                    attrs.creationTime());
            return true;
        } catch (Throwable e) {
            this.notifyMessage("Could not transfer basic file attributes (lastModifiedTime, accessTime, creationTime) from " + source + " to " + target + " (" + e.getMessage() + ")");
        }
        return false;
    }

//    private boolean filterPattern(Path path) {
//        // /tmp/(.*) matches /tmp
//        return lookupPattern.matcher(normalizeDirectorySeparatorsToSlashes(path.toString())).matches();
//    }

    private boolean filterPattern(String path) {
        // /tmp/(.*) matches /tmp
        return lookupPattern.matcher(normalizeDirectorySeparatorsToSlashes(path)).matches();
    }

    private boolean filterMaxAge(String path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
            Date lastModified = new Date(attrs.lastModifiedTime().toMillis());
            return lastModified.compareTo(maxAge) >= 1;
        } catch(IOException ignored) {

        }
        return false;
    }

    private boolean filterMinAge(String path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
            Date lastModified = new Date(attrs.lastModifiedTime().toMillis());
            return lastModified.compareTo(minAge) <= -1;
        } catch(IOException ignored) {

        }
        return false;
    }

    private void findHandler(String s) {
        exportLine(s);

        this.notifyMessage(s);

        // /Users/andreas/Temp/bilder/(.*)(DSC.*.jpg)$
        if (hasPattern()) {
            Matcher m = lookupPattern.matcher(s);
            if (m.find()) {
                for (int i = 1; i <= m.groupCount(); i++) {
                    this.notifyMessage("    $" + String.valueOf(i) + " = '" + m.group(i) + "'");
                }
            }
        }
    }

    private void exportLine(String line) {
        if (exportToWriter != null) {
            exportToWriter.println(line);
        }
    }
}
