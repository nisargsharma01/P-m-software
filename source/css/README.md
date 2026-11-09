# CSS and Java source

This folder keeps the stylesheet and Java implementation together under `source/`, separated by language:

- CSS: [`css/app.css`](css/app.css) imports the existing pharmacy stylesheet at [`../../app.css`](../../app.css), so there is one source of truth for the current design.
- Java: [`java/Main.java`](java/Main.java) is the standalone command-line inventory manager.

The existing browser app at the repository root already links its stylesheet with `<link rel="stylesheet" href="app.css">`. To use the shared stylesheet entry point from a page inside `source/`, link `css/app.css` from that page. CSS styles the browser interface; the Java program runs separately from the command line.

Compile and run Java from the repository root:

```sh
javac source/java/Main.java
java -cp source/java Main
```
