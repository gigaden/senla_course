#!/bin/bash
cd "$(dirname "$0")"

# Используем абсолютные пути
BASE_DIR="$(cd .. && pwd)"
CONFIG_JAR="$BASE_DIR/config-module/config-module.jar"
DI_JAR="$BASE_DIR/di-module/di-module.jar"

# Проверяем, скомпилировано ли приложение
if [ ! -d build ]; then
    echo "Приложение не скомпилировано. Запускаю компиляцию..."
    ./compile.sh
    if [ $? -ne 0 ]; then
        echo "Ошибка компиляции!"
        exit 1
    fi
fi

echo "Запуск приложения..."
java -cp "build:$CONFIG_JAR:$DI_JAR" \
     ebookstore.EBookStoreAppConsole