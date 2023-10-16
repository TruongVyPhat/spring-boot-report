package com.example.springbootreport;

import com.example.springbootreport.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ ApplicationProperties.class })
public class SpringBootReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReportApplication.class, args);
    }

}
