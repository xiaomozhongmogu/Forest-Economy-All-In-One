package edu.scau.forestproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan//SpringBoot对Servelet的支持
@SpringBootApplication
public class ForestProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForestProjectApplication.class, args);
    }

}
