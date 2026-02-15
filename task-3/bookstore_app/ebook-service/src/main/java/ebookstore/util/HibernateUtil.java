package ebookstore.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * Создаёт соединение с БД с помощью хибера и выдаёт сессии
 */
@Service
@DependsOn("liquibase")
public class HibernateUtil {

    private final SessionFactory sessionFactory;

    public HibernateUtil(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Получаем сессию для работы с БД
     *
     * @return - сессия
     */
    public Session getCurrentSession() throws HibernateException {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Закрываем фабрику сессий
     */
    public void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
