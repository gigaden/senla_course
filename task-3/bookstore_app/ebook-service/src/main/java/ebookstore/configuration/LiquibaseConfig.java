package ebookstore.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog.xml");
        liquibase.setContexts("development,production");
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);
        liquibase.setDefaultSchema("public");

        return liquibase;
    }
}