package de.fynder.jtransfer.interfaces.file;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

public interface FinderFilterInterface {
    boolean matches(String path, BasicFileAttributes basicAttributes, Pattern pattern);
}
