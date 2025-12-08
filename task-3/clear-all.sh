#!/bin/bash
echo "--- Очистка старых файлов ---"
rm -rf config-module/build config-module/config-module.jar config-module/sources.txt
rm -rf di-module/build di-module/di-module.jar di-module/sources.txt
rm -rf ebook-service/build ebook-service/sources.txt
echo "--- Очищено ---"