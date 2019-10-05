# LaTeX Scanner

## Installation

Run:

```
sbt stage
```

## Usage

This gives you this executable:
```
target/universal/stage/bin/cmdscanner
```

Which you can then call from the directory with your `.tex` sources.
It also looks for `.tex` files in subdirectories.
By default it will look for `\todo{}`s:

```
cmdscanner
```

but you can also give it some other command name to look for.
For instance, you can have it tell you about duplicate uses of `\emph{}`:

```
cmdscanner emph | sort | uniq -di
```

## Features

### It's multi-line safe!

```latex
This is \emph{very
great} indeed!

\emph{very

absolutely

awesome}.
```

produces (when invoked with emph as parameter):

```text
very great
very absolutely awesome
```

### Speed

It’s scanning all your .tex files in parallel, taking full advantage of your CPU cores. Which is completely irrelevant because 99% is JVM startup time. Which could be alleviated by compiling to a native jvm-free image with sbt graalvm-native-image:packageBin except that it currently does not work because I’m using Scala 2.13.1 and they don’t yet support that.