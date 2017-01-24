package de.fynder.jtransfer.core.file;


import de.fynder.jtransfer.interfaces.FilterInterface;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FilteredWalker implements FilterInterface {
    private ArrayList<FilterInterface> filters = new ArrayList<>();
    public void addFilter(FilterInterface filter) {
        filters.add(filter);
    }

    public Stream<String> walk(Path p, FileVisitOption... options) throws IOException {
        Stream<String> walker = Files.walk(p, options).map(Path::toString);
        if(filters.size() > 0) {
            walker = walker.filter(this::matches);
        }
        return walker;
    }

    @Override
    public boolean matches(String s) {
        for(FilterInterface filter : filters) {
            if(!filter.matches(s)) {
                return false;
            }
        }
        return true;
    }
}
