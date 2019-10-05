# LaTeX Scanner

```
sbt stage
```

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