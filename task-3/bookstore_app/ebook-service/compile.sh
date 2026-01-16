#!/bin/bash
cd "$(dirname "$0")"
echo "Компиляция основного приложения..."

# Используем абсолютные пути
BASE_DIR="$(cd .. && pwd)"
CONFIG_JAR="$BASE_DIR/config-module/config-module.jar"
DI_JAR="$BASE_DIR/di-module/di-module.jar"
LIB_DIR="$BASE_DIR/lib"
POSTGRES_DRIVER="$LIB_DIR/postgresql.jar"

echo "BASE_DIR: $BASE_DIR"
echo "CONFIG_JAR: $CONFIG_JAR"
echo "DI_JAR: $DI_JAR"

# Создаем директорию lib если ее нет
mkdir -p "$LIB_DIR"

# Функция для скачивания драйвера PostgreSQL
download_postgres_driver() {
    echo "Скачивание PostgreSQL JDBC драйвера..."

    # URL последней версии драйвера
    DRIVER_URL="https://jdbc.postgresql.org/download/postgresql-42.7.8.jar"

    # Проверяем наличие curl или wget
    if command -v curl &> /dev/null; then
        curl -L -o "$POSTGRES_DRIVER" "$DRIVER_URL"
    elif command -v wget &> /dev/null; then
        wget -O "$POSTGRES_DRIVER" "$DRIVER_URL"
    else
        echo "Ошибка: Не найден ни curl, ни wget. Установите один из них."
        echo "Или скачайте драйвер вручную с: https://jdbc.postgresql.org/download/"
        echo "И поместите в: $POSTGRES_DRIVER"
        exit 1
    fi

    if [ $? -eq 0 ] && [ -f "$POSTGRES_DRIVER" ]; then
        echo "Драйвер успешно скачан: $POSTGRES_DRIVER"
    else
        echo "Ошибка скачивания драйвера"
        exit 1
    fi
}

# Проверяем наличие драйвера PostgreSQL
if [ ! -f "$POSTGRES_DRIVER" ]; then
    echo "PostgreSQL JDBC драйвер не найден, скачиваю..."
    download_postgres_driver
else
    echo "PostgreSQL JDBC драйвер найден: $POSTGRES_DRIVER"

    # Проверяем размер файла (должен быть больше 1MB)
    FILE_SIZE=$(stat -f%z "$POSTGRES_DRIVER" 2>/dev/null || stat -c%s "$POSTGRES_DRIVER")
    if [ $FILE_SIZE -lt 1000000 ]; then
        echo "Внимание: Драйвер поврежден или неполный, перескачиваю..."
        rm -f "$POSTGRES_DRIVER"
        download_postgres_driver
    fi
fi

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

# Проверяем что драйвер действительно JAR файл
if ! jar -tf "$POSTGRES_DRIVER" &> /dev/null; then
    echo "Ошибка: $POSTGRES_DRIVER не является корректным JAR файлом"
    echo "Удаляю и перескачиваю..."
    rm -f "$POSTGRES_DRIVER"
    download_postgres_driver
fi

echo "Драйвер PostgreSQL проверен:"
jar -tf "$POSTGRES_DRIVER" | grep -i "driver" | head -5

# Находим все Java файлы
echo "Поиск исходных файлов..."
find src/main/java -name "*.java" > sources.txt

# Создаем директорию для скомпилированных классов
mkdir -p build

echo "Компиляция с зависимостями..."
echo "Classpath: $CONFIG_JAR:$DI_JAR:$POSTGRES_DRIVER"

# Компилируем с учетом всех зависимостей
javac -cp "$CONFIG_JAR:$DI_JAR:$POSTGRES_DRIVER" \
      -d build \
      @sources.txt

cp src/main/resources/application.properties build/

# Проверяем успешность компиляции
if [ $? -eq 0 ]; then
    echo "Компиляция завершена успешно!"
else
    echo "Ошибка компиляции!"
    exit 1
fi