package com.jerrybowman.jhipster.jdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class SqlJdlCreateApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SqlJdlCreateApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SqlJdlCreateApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }
}
