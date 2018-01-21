package com.ayoza.feline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableAspectJAutoProxy
public class FelineTrackerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FelineTrackerApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FelineTrackerApplication.class, args);
    }
}
