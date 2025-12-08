#!/bin/bash
cd "$(dirname "$0")"
echo "Компиляция основного приложения..."

# Используем абсолютные пути
BASE_DIR="$(cd .. && pwd)"
CONFIG_JAR="$BASE_DIR/config-module/config-module.jar"
DI_JAR="$BASE_DIR/di-module/di-module.jar"

echo "BASE_DIR: $BASE_DIR"
echo "CONFIG_JAR: $CONFIG_JAR"
echo "DI_JAR: $DI_JAR"

# Проверяем зависимости
if [ ! -f "$CONFIG_JAR" ]; then
    echo "Ошибка: config-module.jar не найден!"
    echo "Выполните: cd ../config-module && ./compile.sh"
    exit 1
fi

if [ ! -f "$DI_JAR" ]; then
    echo "Ошибка: di-module.jar не найден!"
    echo "Выполните: cd ../di-module && ./compile.sh"
    exit 1
fi

# Находим все Java файлы
find src/main/java -name "*.java" > sources.txt

# Создаем директорию для скомпилированных классов
mkdir -p build

# Компилируем с учетом зависимостей
javac -cp "$CONFIG_JAR:$DI_JAR" \
      -d build \
      @sources.txt

# Проверяем успешность компиляции
if [ $? -eq 0 ]; then
    echo "Компиляция завершена успешно!"
else
    echo "Ошибка компиляции!"
    exit 1
fi
