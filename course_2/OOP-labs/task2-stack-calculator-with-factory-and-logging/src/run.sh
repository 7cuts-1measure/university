#!/bin/bash
SRC="ru/nsu/ccfit/gerasimov2/a"
CP=$SRC/jcalc/lib/commons-cli-1.11.0/commons-cli-1.11.0.jar
java -cp "$CP" "$SRC"/jcalc/Main.java "$@"