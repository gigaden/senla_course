#!/bin/bash
echo "--- Очистка старых файлов ---"
rm -rf config-module/build config-module/config-module.jar config-module/sources.txt
rm -rf di-module/build di-module/di-module.jar di-module/sources.txt
rm -rf ebook-service/build ebook-service/sources.txt

echo "--- Компиляция config-module ---"
cd config-module
./compile.sh
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции config-module!"
    exit 1
fi
cd ..

echo "--- Компиляция di-module ---"
cd di-module
./compile.sh
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции di-module!"
    exit 1
fi
cd ..

echo "--- Компиляция основного приложения ---"
cd ebook-service
./compile.sh
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции основного приложения!"
    exit 1
fi

echo "--- Всё скомпилировано успешно! ---"
echo "Для запуска приложения выполните: cd ebook-service && ./run.sh"