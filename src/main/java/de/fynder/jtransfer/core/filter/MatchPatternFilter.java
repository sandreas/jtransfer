package de.fynder.jtransfer.core.filter;

import de.fynder.jtransfer.interfaces.file.FinderFilterInterface;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchPatternFilter implements FinderFilterInterface {
    @Override
    public boolean matches(String path, BasicFileAttributes basicAttributes, Pattern pattern) {
        if (pattern != null) {
            Matcher m = pattern.matcher(path);
            if (!m.matches()) {
                return false;
            }
        }
        return true;
    }
}
