#!/bin/bash
cd "$(dirname "$0")"

# Используем абсолютные пути
BASE_DIR="$(pwd)"
CONFIG_JAR="$BASE_DIR/config-module/config-module.jar"
DI_JAR="$BASE_DIR/di-module/di-module.jar"
POSTGRES_DRIVER="$BASE_DIR/lib/postgresql.jar"
APP_CLASSES="$BASE_DIR/ebook-service/build"

# Проверяем наличие драйвера PostgreSQL
if [ ! -f "$POSTGRES_DRIVER" ]; then
    echo "ОШИБКА: PostgreSQL драйвер не найден в $POSTGRES_DRIVER!"
    echo "Скачайте драйвер командой:"
    echo "  wget -O lib/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.7.8.jar"
    echo "Или запустите compile.sh в ebook-service для автоматической загрузки"
    exit 1
fi

# Проверяем, что драйвер - валидный JAR файл
if ! jar -tf "$POSTGRES_DRIVER" > /dev/null 2>&1; then
    echo "ОШИБКА: PostgreSQL драйвер поврежден или не является JAR файлом!"
    echo "Удалите и скачайте заново:"
    echo "  rm -f lib/postgresql.jar"
    echo "  wget -O lib/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.7.8.jar"
    exit 1
fi

echo "Драйвер PostgreSQL найден и валиден: $POSTGRES_DRIVER"

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
echo "Пробую поднять БД в докере..."

# Останавливаем существующий контейнер (если есть) и запускаем заново
docker compose down > /dev/null 2>&1
docker compose up -d

# Ждем запуска базы данных
echo "Ожидание запуска базы данных (10 секунд)..."
sleep 10

echo "База в докере поднята"
echo "Запускаю скрипты создания и наполнения таблиц"
chmod +x migration.sh
./migration.sh
echo "Миграции выполнены"

echo "Запуск Java приложения..."
echo "=========================================="
echo "Classpath для Java:"
echo "  - APP_CLASSES: $APP_CLASSES"
echo "  - CONFIG_JAR:  $CONFIG_JAR"
echo "  - DI_JAR:      $DI_JAR"
echo "  - DRIVER:      $POSTGRES_DRIVER"
echo "=========================================="
echo ""

# Проверяем наличие всех файлов в classpath
if [ ! -d "$APP_CLASSES" ]; then
    echo "ОШИБКА: APP_CLASSES не найден: $APP_CLASSES"
    exit 1
fi

if [ ! -f "$CONFIG_JAR" ]; then
    echo "ОШИБКА: CONFIG_JAR не найден: $CONFIG_JAR"
    exit 1
fi

if [ ! -f "$DI_JAR" ]; then
    echo "ОШИБКА: DI_JAR не найден: $DI_JAR"
    exit 1
fi

java -cp "$APP_CLASSES:$CONFIG_JAR:$DI_JAR:$POSTGRES_DRIVER" \
     ebookstore.EBookStoreAppConsole