package ebookstore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "ebookstore")
@PropertySource("classpath:application.properties")
@Import({DatabaseConfig.class, LiquibaseConfig.class, HibernateConfig.class})
public class ConsoleConfig {

    @Bean
    public Scanner scanner() {
        return new Scanner((System.in));
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}