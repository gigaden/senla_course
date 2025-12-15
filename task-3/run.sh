#!/bin/bash
cd "$(dirname "$0")"

# Используем абсолютные пути
BASE_DIR="$(pwd)"
CONFIG_JAR="$BASE_DIR/config-module/config-module.jar"
DI_JAR="$BASE_DIR/di-module/di-module.jar"
APP_CLASSES="$BASE_DIR/ebook-service/build"

# Проверяем, скомпилировано ли приложение
if [ ! -d "$APP_CLASSES" ]; then
    echo "Приложение не скомпилировано. Запускаю компиляцию..."

    # Проверяем наличие модулей
    if [ ! -f "$CONFIG_JAR" ]; then
        echo "Компиляция config-module..."
        cd config-module
        ./compile.sh
        cd ..
    fi

    if [ ! -f "$DI_JAR" ]; then
        echo "Компиляция di-module..."
        cd di-module
        ./compile.sh
        cd ..
    fi

    # Компилируем основное приложение
    echo "Компиляция основного приложения..."
    cd ebook-service
    ./compile.sh
    cd ..

    if [ $? -ne 0 ]; then
        echo "Ошибка компиляции!"
        exit 1
    fi
fi

echo "Запуск приложения..."
java -cp "$APP_CLASSES:$CONFIG_JAR:$DI_JAR" \
     ebookstore.EBookStoreAppConsole