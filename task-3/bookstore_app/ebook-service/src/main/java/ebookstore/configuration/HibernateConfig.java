package ebookstore.configuration;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;
import java.util.Properties;

@org.springframework.context.annotation.Configuration
public class HibernateConfig {

    @Bean
    @DependsOn("liquibase")
    public SessionFactory sessionFactory(DataSource dataSource) {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(ebookstore.model.Client.class);
        configuration.addAnnotatedClass(ebookstore.model.Book.class);
        configuration.addAnnotatedClass(ebookstore.model.Order.class);
        configuration.addAnnotatedClass(ebookstore.model.BookRequest.class);

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "validate");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.current_session_context_class", "thread");

        configuration.setProperties(props);

        configuration.getProperties().put("hibernate.connection.datasource", dataSource);

        return configuration.buildSessionFactory();
    }
}
