# LaTeX Scanner

## Installation

Run:

```
sbt stage
```

## Usage

This gives you these executables:
```
CmdScanner/target/universal/stage/bin/cmdscanner
RefScanner/target/universal/stage/bin/refscanner
```

Which you can then call from the directory with your `.tex` sources.
Both executables will look for `.tex` files in subdirectories.

## CmdScanner

Find all `todo`s:

```
cmdscanner todo
```

Find duplicate uses of `\emph{}`:

```
cmdscanner emph | sort -f | uniq -di
```

## RefScanner

Find all `\label`s that you did not reference:

```
refscanner cref Cref ref autoref
```

## Features

### Multi-line aware

```latex
This is \emph{very
great} indeed!

\emph{very

absolutely

awesome}.
```

produces:

```text
$ cmdscanner emph
very great
very absolutely awesome
```

### Speed

It’s scanning all your .tex files in parallel, taking full advantage of your CPU cores.
Which is completely irrelevant because 99% is JVM startup time.
This can be alleviated by compiling to a native jvm-free image with `sbt graalvm-native-image:packageBin`.