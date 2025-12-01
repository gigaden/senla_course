#!/bin/bash

echo "чистим предыдущую версию..."
rm -rf target
rm -f ebookstore.jar
echo "почистили"
echo "создаём директорию"
mkdir -p target
echo "компилим файлы"
find src/main/java -name "*.java" > sources.txt
javac -d target @sources.txt
rm -f sources.txt
echo "файлы скомпилированы"
cd target
echo "переходим в каталог и создаём джарник"
jar cfe ../ebookstore.jar ebookstore.EBookStoreAppConsole .
echo "джарник создан"
echo "если позвонят из Пентагона, то меня нет дома..."
cd ..
rm -rf target