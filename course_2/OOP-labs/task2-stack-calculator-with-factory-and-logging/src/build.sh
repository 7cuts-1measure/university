#!/bin/bash
CP="lib/commons-cli-1.11.0.jar"
SRC="ru/nsu/ccfit/gerasimov2/a/src"
OUT="out"

mkdir -p "$OUT"

javac -cp "$CP" -d "$OUT" "$SRC"/jcalc/*.java
