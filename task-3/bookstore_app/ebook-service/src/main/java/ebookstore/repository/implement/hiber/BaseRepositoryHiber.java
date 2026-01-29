package ebookstore.repository.implement.hiber;

import ebookstore.exception.DatabaseException;
import ebookstore.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public abstract class BaseRepositoryHiber<T, PK extends Serializable> {

    private final Class<T> type;
    private static final Logger log = LoggerFactory.getLogger(BaseRepositoryHiber.class);

    protected BaseRepositoryHiber(Class<T> type) {
        this.type = type;
    }

    protected T save(T entity) {
        try {
            Session session = HibernateUtil.getCurrentSession();
            session.persist(entity);
            return entity;
        } catch (HibernateException e) {
            log.error("Ошибка базы данных при сохранении {}: {}", type.getSimpleName(), entity, e);
            throw new DatabaseException("Ошибка сохранения сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected T update(T entity) {
        try {
            Session session = HibernateUtil.getCurrentSession();
            return session.merge(entity);
        } catch (HibernateException e) {
            log.error("Ошибка БД при обновлении {}: {}", type.getSimpleName(), entity, e);
            throw new DatabaseException("Ошибка обновления сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected void delete(PK id) {
        try {
            Session session = HibernateUtil.getCurrentSession();
            T entity = session.find(type, id);
            if (entity != null) {
                session.remove(entity);
            }
        } catch (HibernateException e) {
            log.error("Ошибка БД при удалении {} с id: {}", type.getSimpleName(), id, e);
            throw new DatabaseException("Ошибка удаления сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected List<T> findAll() {
        try {
            log.debug("Пытаюсь получить все записи {}", type.getSimpleName());
            Session session = HibernateUtil.getCurrentSession();
            return session.createQuery("FROM " + type.getSimpleName(), type)
                    .getResultList();
        } catch (HibernateException e) {
            log.error("Ошибка БД при получении записей {}", type.getSimpleName(), e);
            throw new DatabaseException("Ошибка получения списка " + type.getSimpleName() + " " + e);
        }
    }

    protected T find(PK id) {
        try {
            Session session = HibernateUtil.getCurrentSession();
            return session.find(type, id);
        } catch (HibernateException e) {
            log.error("Ошибка поиска сущности {} с id: {}", type.getSimpleName(), id, e);
            throw new DatabaseException("Ошибка поиска сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected boolean exists(PK id) {
        try {
            Session session = HibernateUtil.getCurrentSession();
            T entity = session.find(type, id);
            return entity != null;
        } catch (HibernateException e) {
            log.error("Ошибка БД при проверке существования {} с id: {}", type.getSimpleName(), id, e);
            throw new DatabaseException("Ошибка проверки существования сущности " + type.getSimpleName() + " " + e);
        }
    }
}
