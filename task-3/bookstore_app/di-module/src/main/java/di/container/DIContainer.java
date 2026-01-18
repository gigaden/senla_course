package di.container;

import di.annotation.Autowired;
import di.annotation.PostConstruct;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс - контейнер, который занимается управлением нашими бинами и их внедрением
 */
public class DIContainer {
    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();
    private static DIContainer instance;

    private DIContainer() {
    }

    public static DIContainer getInstance() {
        if (instance == null) {
            instance = new DIContainer();
        }
        return instance;
    }

    /**
     * Регистрирует наши бины
     *
     * @param beanType - принимает класс(тип) бина
     * @param bean     - сам объекта класса, который нужно зарегать
     */
    public void registerBean(Class<?> beanType, Object bean) {
        singletonBeans.put(beanType, bean);

        // Регистрируем по интерфейсам, если они есть
        for (Class<?> interfaceClass : beanType.getInterfaces()) {
            singletonBeans.put(interfaceClass, bean);
        }
    }

    /**
     * Получаем бин по типу класса
     *
     * @param <T> - объект запрашиваемого класса
     */
    public <T> T getBean(Class<T> beanType) {
        return (T) singletonBeans.get(beanType);
    }

    /**
     * внедряем зависимости
     */
    public void initialize() {
        for (Object bean : singletonBeans.values()) {
            injectFieldDependencies(bean);
        }

        for (Object bean : singletonBeans.values()) {
            invokePostConstruct(bean);
        }
    }

    /**
     * внедряем нужные реализации в аннотированные поля через рефлексию
     */
    private void injectFieldDependencies(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                try {
                    field.setAccessible(true);
                    Object dependency = getBean(field.getType());
                    if (dependency == null) {
                        throw new RuntimeException("Не найдена зависимость для поля " +
                                                   field.getName() + " в классе " + clazz.getName());
                    }
                    field.set(bean, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Ошибка внедрения зависимости в поле " +
                                               field.getName() + " класса " + clazz.getName(), e);
                }
            }
        }
    }

    private void invokePostConstruct(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {

                if (method.getParameterCount() != 0) {
                    throw new RuntimeException(
                            "@PostConstruct метод должен быть без параметров: "
                            + clazz.getName() + "." + method.getName()
                    );
                }

                try {
                    method.setAccessible(true);
                    method.invoke(bean);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Ошибка при вызове @PostConstruct метода: "
                            + clazz.getName() + "." + method.getName(), e
                    );
                }
            }
        }
    }


    /**
     * Получаем все бины
     */
    public Map<Class<?>, Object> getBeansMap() {
        return new HashMap<>(singletonBeans);
    }
}