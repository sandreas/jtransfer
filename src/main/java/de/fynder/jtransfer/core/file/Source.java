package de.fynder.jtransfer.core.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Source {

    private Pattern lookupPattern;
    private Path lookupPath;

    Source(String s) {
        parse(s);
    }

    private void parse(String findPattern) {
        String validPart = findPattern;
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
        return lookupPattern != null;
    }

    public boolean isFile() {
        return Files.isRegularFile(lookupPath);
    }

    Pattern getPattern() {
        return lookupPattern;
    }

    String getLocationAsString() {
        return lookupPath.toString();
    }
}
