package de.fynder.jtransfer;

import com.beust.jcommander.JCommander;

import de.fynder.jtransfer.interfaces.ProgressListenerInterface;
import de.fynder.jtransfer.core.location.AbstractTransferLocation;
import de.fynder.jtransfer.core.location.AbstractTransferLocationFactory;
import de.fynder.jtransfer.util.MaxSizeHashMap;
import de.fynder.jtransfer.util.OperatingSystemDetector;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.*;

public class Transfer implements ProgressListenerInterface {
    private static final int STATUS_OK = 0;
    private static final int STATUS_ERROR = 1;

    private int statusCode = 0;
    private Settings settings;

    private double lastProgressUpdate = 0;
    private String bandwidthOutput = "";
    private static int outputLength;

    // parameter variables
    private String sourcePattern;
    private String destinationPattern;


    private boolean notifySingleQuotes = false;
    private List<String> importLines = new ArrayList<>();

    int getStatusCode() {
        return statusCode;
    }

    void initSettings(Settings settings) {
        this.settings = settings;
    }

    private TransferShutdownHook shutdownHook = new TransferShutdownHook(this);

    private MaxSizeHashMap<Long, Long> bandwidthStatistics = new MaxSizeHashMap<>(2);

    void run(String[] args) throws Exception {
//        Path p = Paths.get("C:\\Users\\aschroden\\Documents\\_PRI\\__PROJEKTE\\spielerschutz.sql.bz2");
//        Map<String,Object> attr = Files.readAttributes(p, "*");

        if (!parseCommandLineArguments(args)) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(shutdownHook);
        ensureValidCommandLineParameters();
        loadPatternParameters();
        performRequestedOperation();
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
        statusCode = STATUS_OK;
    }


    private boolean parseCommandLineArguments(String[] args) {
        JCommander argumentsParser = new JCommander(settings);
        try {
            argumentsParser.setProgramName("jtransfer");
            argumentsParser.parse(args);

            if(settings.debug) {
                println("arguments:");
                for(String arg : args) {
                    println("  >" + arg);
                }
            }
            return true;
        } catch (Exception e) {
            if (args.length == 0 || Arrays.asList(args).contains("--help")) {
                argumentsParser.usage();

                println("Examples:");
                println("");

                println("  List directory using regex:");
                if (OperatingSystemDetector.isWindows()) {
                    println("    jtransfer \"/tmp/(.*)\\.jpg\"");
                } else {
                    println("    jtransfer '/tmp/(.*)\\.jpg'");
                }
                println("");
                println("  Copy recursively using regex replace:");
                if (OperatingSystemDetector.isWindows()) {
                    println("    jtransfer \"/tmp/(.*)\\.jpeg\" \"/tmp/$1\\.jpg\"");
                } else {
                    println("    jtransfer '/tmp/(.*)\\.jpeg' '/tmp/$1\\.jpg'");
                }

                println("");
                println("  Move using regex replace:");
                if (OperatingSystemDetector.isWindows()) {
                    println("    jtransfer \"/tmp/(.*)\\.jpeg\" \"/tmp/$1\\.jpg\" --move");
                } else {
                    println("    jtransfer '/tmp/(.*)\\.jpeg' '/tmp/$1\\.jpg' --move");
                }
                println("");

                if (OperatingSystemDetector.isWindows()) {
                    println("Notes for Windows:");
                    println("");
                    println("  Use double quotes (\") to enclose patterns, not single quotes (')");
                } else {
                    println("Notes for Unix:");
                    println("");
                    println("  Use single quotes ('), not double quotes (\") to enclose patterns, so that variables like $1 are not accidentally resolved");
                }
            } else {
                System.out.println(e.getMessage());
                System.out.println("use --help to print usage");
                statusCode = STATUS_ERROR;
            }
        }

        if (settings.debug) {
            println("Called with args:");
            for (String arg : args) {
                println("  " + arg + "");
            }
        }

        return false;
    }


    private void ensureValidCommandLineParameters() {
        if (settings.patterns.size() < 1 || settings.patterns.size() > 2) {
            String errorMessage = "you must specify sourceLocation and optional a target";
            if (OperatingSystemDetector.isWindows()) {
                errorMessage += " - perhaps you used single quotes (') instead of double quotes (\") to qualify your pattern?";
            }
            throw new Error(errorMessage);
        }
    }

    private void loadPatternParameters() throws URISyntaxException {
        if (settings.patterns.size() > 0) {
            sourcePattern = trimSingleQuotes(settings.patterns.get(0));
        }
        if (settings.patterns.size() > 1) {
            destinationPattern = trimSingleQuotes(settings.patterns.get(1));
        }

        if (notifySingleQuotes) {
            println("Warning: You seem to use single quotes (') to qualify your pattern, on windows you should use double quotes (\"), e.g.:");
            println("    jtransfer \"C:/Users/jtransfer/(.*)\"");
            println("jtransfer trims single quotes by default, but using single quotes is not recommended and may result in unexpected behaviour");
            println("");
        }
    }

    private String trimSingleQuotes(String s) {
        if (!OperatingSystemDetector.isWindows()) {
            return s;
        }
        if (s.charAt(0) == '\'') {
            s = s.substring(1);
            notifySingleQuotes = true;
        }
        if (s.charAt(s.length() - 1) == '\'') {
            s = s.substring(0, s.length() - 1);
            notifySingleQuotes = true;
        }
        return s;
    }

    private void performRequestedOperation() throws Exception {

        AbstractTransferLocation sourceLocation = AbstractTransferLocationFactory.factory(sourcePattern);
        sourceLocation.addListener(this);
        sourceLocation.setSettings(settings);

        if (destinationPattern == null) {
            sourceLocation.find();
            return;
        }

        AbstractTransferLocation destination = AbstractTransferLocationFactory.factory(destinationPattern);
        // TODO add replacement api
//        ReplacementApi api = new ReplacementApi();
//        // api.register(new );
//        sourceLocation.setReplacementApi();


        sourceLocation.transferTo(destination);

    }

//    void error(String s) {
//        println(s);
//        statusCode = STATUS_ERROR;
//    }

    void println(String s) {
        if (settings.quiet) {
            return;
        }

        System.out.println(s);
    }

    private void clearAndPrint(String s) {
        if (settings.quiet) {
            return;
        }

        clear();
        outputLength += s.length();
        System.out.print(s);
    }

    private void clear() {
        if (outputLength > 0) {
            // n * Backspace + n * ' ' + n * Backspace
            // Also, zuerst zum Zeilenanfang bewegen, dann alles mit Leerzeichen
            // Überschreiben und wieder zur�ck zu Zeilenanfang.
            // Dies funktioniert nur, wenn noch kein Zeilenumbruch erfolgt ist.
            char buffer[] = new char[outputLength * 3];
            Arrays.fill(buffer, (char) 8);
            Arrays.fill(buffer, outputLength, outputLength * 2, ' ');
            System.out.print(buffer);
            outputLength = 0;
        }
    }

    @Override
    public void notifyMessage(String message) {
        println(message);
    }

    @Override
    public void notifyProgress(long bytesTransferred, long bytesToTransfer) {
        long size = bytesToTransfer + bytesTransferred;
        double progress = (double) bytesTransferred / (double) size;
        double roundOff = (double) Math.round(progress * 10000) / 100;

        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastProgressUpdate) > 1500 || bandwidthOutput.equals("")) {
            bandwidthOutput = updateBandwidthStatistics(bytesToTransfer);
            lastProgressUpdate = currentTime;
        }

        String progressMessage = String.format("%-20s", "progress: " + roundOff + "% ");
        if (!bandwidthOutput.equals("")) {
            progressMessage += String.format("%-10s", "[ " + bandwidthOutput + " ]");
        }
        clearAndPrint(progressMessage);
        if (bytesToTransfer < 1) {
            println("");
        }
    }


    @Override
    public void notifyFinished(long bytesTransferred) {
        if(settings.debug){
            println("transfer finished: " + bytesTransferred + " bytes");
        }
    }

    private String updateBandwidthStatistics(long bytesToTransfer) {
        bandwidthStatistics.put(System.currentTimeMillis(), bytesToTransfer);
        if (bandwidthStatistics.size() < 2) {
            return "";
        }
        Iterator it = bandwidthStatistics.entrySet().iterator();
        Map.Entry pair = (Map.Entry) it.next();

        long firstTime = (long) pair.getKey();
        long firstToTransfer = (long) pair.getValue();

        pair = (Map.Entry) it.next();

        long secondTime = (long) pair.getKey();
        long secondToTransfer = (long) pair.getValue();

        long duration = secondTime - firstTime;


        long transferred = firstToTransfer - secondToTransfer;

        double bytesPerSecond = (double) transferred / ((double) duration / 1000);

        return readableFileSize(Math.round(bytesPerSecond)) + "/s";

    }

    private String readableFileSize(long size) {
        if (size <= 0) return "0B";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
