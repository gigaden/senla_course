### Приложение книжного магазина

#### README.md находится в ожидании, когда до него дойдут руки и сделают его более красивым и информативным...

### Stage - Task-13-JPA-Hibernate
- Добавлен Hibernate 7
- Созданы новые репозитории и переделаны сервисы под транзакции
- Логи пишутся в файл
- Очень много нужно поправить)

### Stage - Task-12-Maven-Logging
- проект пересажен на maven.
- добавлено логирование по слоям с помощью Slf4j и LogBack.

### Запуск приложения
С помощью maven:
```bash
maven clean package
java -jar ebook-service/target/ebookstore-app.jar
```
из корня(где расположен сам run.sh)
```bash
chmod +x run.sh
./run.sh
```