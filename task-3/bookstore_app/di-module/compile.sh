#!/bin/bash
cd "$(dirname "$0")"
echo "Компилим di-module..."

if [ ! -f "../config-module/config-module.jar" ]; then
    echo "Ошибка: сначала скомпилируйте config-module!"
    echo "Выполните: cd ../config-module && ./compile.sh"
    exit 1
fi

find src -name "*.java" > sources.txt
mkdir -p build
javac -cp "../config-module/config-module.jar" -d build @sources.txt
jar cvf di-module.jar -C build .
echo "di-module скомпилирован успешно!"