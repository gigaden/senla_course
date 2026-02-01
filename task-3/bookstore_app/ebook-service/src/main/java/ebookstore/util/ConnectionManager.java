package ebookstore.util;

import ebookstore.exception.DatabaseException;
import ebookstore.exception.message.DataBaseErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class ConnectionManager {


    @Value("${jdbc.url}")
    private String URL;
    @Value("${jdbc.user}")
    private String USER;
    @Value("${jdbc.password}")
    private String PASSWORD;
    @Value("${db.pool.size}")
    private Integer POOL_SIZE;
    private final int DEFAULT_POOL_SIZE = 10;
    private BlockingQueue<Connection> pool;

    //@PostConstruct
    private void init() {
        initConnectionPool();
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(DataBaseErrorMessages.DRIVER_ERROR);
        }
    }

    private void initConnectionPool() {
        int size = POOL_SIZE == null ? DEFAULT_POOL_SIZE : POOL_SIZE;
        pool = new ArrayBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            Connection connection = open();
            var proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close") ? pool.add((Connection) proxy) : method.invoke(connection, args));
            pool.add(proxyConnection);
        }
    }

    private Connection open() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException(DataBaseErrorMessages.CONNECTION_ERROR);
        }
    }

    public Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            System.out.println("Ошибка получения connection из пула");
            throw new RuntimeException(e);
        }
    }
}
