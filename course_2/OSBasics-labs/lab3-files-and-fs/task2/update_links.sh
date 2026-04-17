#!/usr/bin/env bash

rm -f programs/*.so
for file in programs/*; do ln --force main $file; done
