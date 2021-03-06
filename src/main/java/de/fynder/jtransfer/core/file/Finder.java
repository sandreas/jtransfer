package de.fynder.jtransfer.core.file;

import de.fynder.jtransfer.interfaces.file.FinderFilterInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.stream.Stream;

class Finder {
    private Source source;
    private ArrayList<FinderFilterInterface> filters = new ArrayList<>();

    Stream<String> walk(Source src) throws IOException {
        source = src;
        Path path = Paths.get(src.getLocationAsString());
        return Files.find(path, getMaxDepth(), this::filter).map(Path::toString);
    }

    private int getMaxDepth() {
        return Integer.MAX_VALUE;
    }


    private boolean filter(Path path, BasicFileAttributes basicFileAttributes) {
        for(FinderFilterInterface f : filters) {
            if(!f.matches(path.toString(), basicFileAttributes, source.getPatternAsString())) {
                return false;
            }
        }

        return true;
    }

    void addFilter(FinderFilterInterface f) {
        filters.add(f);
    }
}
