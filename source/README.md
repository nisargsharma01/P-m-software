# Language versions

The repository's main pharmacy manager remains the browser app in `index.html`. These standalone command-line versions provide the same basic inventory workflow in separate language folders.

## Python

From the repository root, run:

```sh
python3 source/python/main.py
```

## Java

From the repository root, compile and run:

```sh
javac source/java/Main.java
java -cp source/java Main
```

Both programs save inventory to `inventory.tsv` in the current working directory. Use the menu to list stock, add a medicine, record a sale, or quit. Keep the file in a trusted folder; it contains your locally saved inventory.
