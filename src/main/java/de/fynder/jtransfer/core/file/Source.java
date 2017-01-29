package de.fynder.jtransfer.core.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Source {

    private Path lookupPath;
    private String lookupPatternAsString;

    Source(String s) {
        parse(s);
    }

    private void parse(String findPattern) {
        String validPart = findPattern;
        int lastSlashPos;
        lookupPatternAsString = "";
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
            lookupPatternAsString = normalizeDirectorySeparatorsToSlashes(lookupPath.toString()) + "/" + lookupPatternAsString.substring(1);
        } else {
            lookupPatternAsString = null;
        }
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

    boolean hasPattern() {
        return lookupPatternAsString != null;
    }

    public boolean isFile() {
        return Files.isRegularFile(lookupPath);
    }

    String getPatternAsString() {
        return lookupPatternAsString;
    }

    String getLocationAsString() {
        return lookupPath.toString();
    }
}
