#!/bin/bash
echo "--- Компиляция config-module ---"
cd config-module
./compile.sh
cd ..

echo "--- Компиляция di-module ---"
cd di-module
./compile.sh
cd ..

echo "--- Компиляция основного приложения ---"
cd ebook-service
./compile.sh

echo "--- Запуск приложения ---"
./run.sh