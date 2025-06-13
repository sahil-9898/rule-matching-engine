package com.sahil.matcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RuleMatchingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleMatchingEngineApplication.class, args);
    }

}
