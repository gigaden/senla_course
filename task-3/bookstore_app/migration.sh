#!/bin/bash

echo "Установка базы данных ebookstore"

DB_CONTAINER_NAME="postgres-senla"
DB_NAME="bookstore"
DB_USER="postgres"
DDL_FILE_PATH="ebook-service/src/main/resources/db/changelog/001-create-tables.sql"
DML_FILE_PATH="ebook-service/src/main/resources/db/changelog/002-fill-tables.sql"

if [ ! -f $DDL_FILE_PATH ]; then
    echo "Ошибка: Файл 001-create-tables.sql не найден!"
    exit 1
fi

if [ ! -f $DML_FILE_PATH ]; then
    echo "Ошибка: Файл 002-fill-tables.sql не найден!"
    exit 1
fi

if ! docker ps | grep -q "postgres-senla"; then
    echo "Ошибка: Контейнер 'postgres-senla' не запущен!"
    echo "Запустите контейнер командой: docker-compose up -d"
    exit 1
fi

echo "1. Выполнение DDL скриптов..."
docker exec -i "$DB_CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" < $DDL_FILE_PATH

if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении DDL скриптов!"
    exit 1
fi

echo "2. Заполнение тестовыми данными..."
docker exec -i "$DB_CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" < "$DML_FILE_PATH"

if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении DML скриптов!"
    exit 1
fi

echo "База данных успешно установлена!"