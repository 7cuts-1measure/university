# Practice 1. Simple words counter

## How to use
Just type `java -jar freq.jar [input text] [output csv]` and you will get statistics of your text in format: `word,frequency,frequency%`

## Example
If input text looks like this:
```
fiz
baz
fiz
fiz
baz
fiz
fizbaz
```
The ouput will be:
```
fiz,4,57.14%
baz,2,28.57%
fizbaz,1,14.29%

```

## How to compile
```bash
javac -d out src/freq/*.java
```
Now you can run program from `out` folder:
```
java freq/Main
```


If you want to create jar file, do this:
```bash
cd out
jar cfe freq.jar freq.Main freq/*.class
```