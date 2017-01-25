#jtransfer

***jtransfer*** is a Java command line utility, that can list directories and transfer files via regular expressions.
It supports resuming partial transferred files, preserving file attributes and permissions as well as exporting and importing lists.

jtransfer started as a learning project and it still is, so much of the code could be vastly improved and may contain bugs, but for now it already is a useful tool that works well in most cases. 

## Requirements

***jtransfer*** uses Java streams and nio features to provide acceptable performance and
 therefore it requires
 
  ***Java 8***

## Usage

***jtransfer*** supports Windows, OSX and Linux, although the usage instructions might be different for each operating system.

### Basic Usage

***jtransfer*** is available as wrapper script, which includes shell instructions as well as the jar file in a binary or a valid windows binary called jtransfer:

```
jtransfer [options] source [destination]
```

If you do not use the binary wrapper or another script, you can always use:

```
java -jar jtransfer.jar [options] source [destination]
```

### Search

The destination pattern is optional, as well as the programm options. If you do not specify a destination pattern, ***jtransfer*** recursively lists all matching files in all subdirectories, so it can also be used as a search tool.


### Copy, Rename, Resume

***jtransfer*** copies recursively and resumes partially transferred files by default. If you would like to move or rename files instead, use the ---move option 


### Examples

Recursive listing of /tmp directory and its matchings using a simple regular expression:
 
```
jtransfer '/tmp/(.*)\.jpg'
```

Copy recursive using a simple regex replace:

```
jtransfer '/tmp/(.*)\.jpeg' '/tmp/$1\.jpg'
```

Move using regex replace

```
jtransfer '/tmp/(.*)\.jpeg' '/tmp/$1\.jpg' --move
```

Simulate move with relative source and destination path

```
jtransfer 'relative/path/(.*)\.jpeg' 'relative/target/path/$1\.jpg' --dry-run
```


#### Options
Following options are available:
```
    --archive
      preserves all file attributes, if possible
      Default: false
    --debug
      show debug info
      Default: false
    --dry-run
      perform a dry run without transferring
      Default: false
    --export-to
      export source listing to file, one line per source
    --files-from
      import source listing from file, one line per source
    --follow-symlinks
      regard contents of symlinked folders
      Default: false
    --force-overwrite
      force overwrite of existing files that diff from source
      Default: false
    --group
      preserve group
      Default: false
    --max-age
      maximum age (e.g. -2 days, -8 weeks, 2015-10-10, etc.)
    --max-items
      Maximum number of matching items
      Default: 0
    --min-age
      minimum age (e.g. -2 days, -8 weeks, 2015-10-10, etc.)
    --move
      move instead of copy
      Default: false
    --owner
      preserve owner (super-user only)
      Default: false
    --perms
      preserves permissions
      Default: false
    --quiet
      do not show any output
      Default: false
    --times
      preserves times
      Default: false
```
 



### OS Specific Notes


#### Linux / Unix / MacOS
Use single quotes ('), not double quotes (") to enclose patterns, so that shell variables like $1 are not resolved

***Example:***

```
jtransfer '/tmp/(.*)\.jpeg' '/tmp/$1\.jpg' --move
```


#### Windows
Use double quotes (") to enclose patterns and slashes as directory separator, because on Windows single quotes are no valid argument delimiters.
 
> Although single quotes and backslaches should work in many cases (jtransfer provides a workaround), they might lead to unexpected behaviour and should be avoided on windows. 

***Example:***

```
jtransfer "C:/Users/johndoe/pictures/(.*)\.jpeg" "C:/Users/johndoe/jpegs/$1\.jpg" --move
```


# jtransfer development

***jtransfer*** is developed in Java 8, so JDK 8 must be installed in order to create builds. Gradle is used as build tool, so use the following instructions to build all jtransfer targets:

```
git clone https://github.com/sandreas/jtransfer.git


cd jtransfer


./gradlew all
```

If the build is successful, the directory builds should contain executable binaries and jar files:

## Linux / Unix / MacOS

***jtransfer*** comes with a binary shell wrapper, that contains shell script AND jar in one file. Use chmod +x to make it executable:

build/libs/***jtransfer***

## Windows
***jtransfer*** comes with a native executable that works together with `build/launch4j/lib/jtransfer.jar`

build/launch4j/***jtransfer.exe*** 

## OS Independent
jar file only, can be run with `java -jar jtransfer.jar`

build/libs/***jtransfer.jar***

## IDE recommendation

***jtransfer*** is developed with JetBrains IntelliJ IDEA, so this is the recommended IDE