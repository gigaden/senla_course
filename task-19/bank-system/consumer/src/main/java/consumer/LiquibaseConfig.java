package consumer;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {

        boolean enabled = Boolean.parseBoolean(
                System.getenv().getOrDefault("LIQUIBASE_ENABLED", "true")
        );

        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("db/changelog/changelog-master.xml");
        liquibase.setShouldRun(enabled);

        return liquibase;
    }
}