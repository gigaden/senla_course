echo "чистим предыдущую версию..."
rm -rf target
echo "почистили"
echo "создаём директорию"
mkdir -p target
echo "компилим файлы"
javac -d target **/*.java
echo "файлы скомпилены"
cd target
echo "переходим в каталог и создаём джарник"
jar cfe ../ebookstore.jar ebookstore.EBookStoreApp .
echo "джарник создан"
echo "если позвонят из Пентагона, то меня нет дома..."
cd ..
rm -rf target