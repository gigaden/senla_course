#!/bin/bash
cd "$(dirname "$0")"
echo "Компилим config-module..."
find src -name "*.java" > sources.txt
mkdir -p build
javac -d build @sources.txt
jar cvf config-module.jar -C build .
echo "config-module скомпилин"
rm -rf build
rm sources.txt