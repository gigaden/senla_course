#!/bin/bash

FILE="ebookstore.jar"
if [ -f "$FILE" ]; then
  echo "файл существует, запускаю..."
  java -jar ebookstore.jar
else
  echo "файла не существует, сейчас сделаю..."
  chmod +x compile.sh
  ./compile.sh
  java -jar ebookstore.jar
fi