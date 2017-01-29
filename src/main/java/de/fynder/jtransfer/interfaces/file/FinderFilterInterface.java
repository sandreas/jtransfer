package de.fynder.jtransfer.interfaces.file;

import java.nio.file.attribute.BasicFileAttributes;

public interface FinderFilterInterface {
    boolean matches(String path, BasicFileAttributes basicAttributes, String pattern);
}
