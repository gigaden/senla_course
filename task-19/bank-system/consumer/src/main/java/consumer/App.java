package consumer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
public class App {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();

        context.scan("consumer");
        context.register(
                KafkaConsumerConfig.class,
                DBConfig.class,
                LiquibaseConfig.class,
                ConsumerService.class
        );

        context.refresh();
    }
}