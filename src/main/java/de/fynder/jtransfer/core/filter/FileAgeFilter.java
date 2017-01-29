package de.fynder.jtransfer.core.filter;


import de.fynder.jtransfer.interfaces.file.FinderFilterInterface;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class FileAgeFilter  implements FinderFilterInterface {
    final static int MIN_AGE = 1;
    final static int MAX_AGE = 2;

    private Date reference;
    private int type;

    FileAgeFilter(Date referenceAge, int filterType) {
        reference = referenceAge;
        type = filterType;
    }

    @Override
    public boolean matches(String path, BasicFileAttributes basicFileAttributes, String pattern) {
        try {
            Date lastModified = new Date(basicFileAttributes.lastModifiedTime().toMillis());
            if(type == MIN_AGE) {
                return lastModified.compareTo(reference) < 0;
            }

            if(type == MAX_AGE) {
                return lastModified.compareTo(reference) > 0;
            }

        } catch(Exception ignored) {

        }
        return false;
    }
}
