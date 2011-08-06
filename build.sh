#!/bin/sh

find src -name '*.java' >.src-files.lst
OUTDIR=bin
mkdir -p $OUTDIR
javac -extdirs ext-lib -d "$OUTDIR" @.src-files.lst -target 1.4 -source 1.4
cp -a src/* "$OUTDIR/"
