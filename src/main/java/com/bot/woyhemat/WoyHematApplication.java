package com.bot.woyhemat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WoyHematApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        System.out.println("JALAN MAIN NIH !");
        SpringApplication.run(WoyHematApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WoyHematApplication.class);
    }

}
