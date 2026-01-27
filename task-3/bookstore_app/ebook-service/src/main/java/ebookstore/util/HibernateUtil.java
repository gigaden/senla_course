package ebookstore.util;

import ebookstore.exception.DatabaseException;
import ebookstore.exception.message.DataBaseErrorMessages;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Создаёт соединение с БД с помощью хибера и выдаёт сессии
 */
public class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            log.error("Ошибка коннекта к БД через хибер: {}", e.getMessage(), e);
            throw new DatabaseException(DataBaseErrorMessages.HIBER_CONNECTION_ERROR + ": " + e);
        }

    }

    /**
     * Получаем сессию для работы с БД
     *
     * @return - сессия
     */
    public static Session getCurrentSession() throws HibernateException {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Закрываем фабрику сессий
     * */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

}
