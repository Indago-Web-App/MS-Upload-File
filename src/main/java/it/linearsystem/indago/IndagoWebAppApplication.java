package it.linearsystem.indago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableFeignClients
public class IndagoWebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndagoWebAppApplication.class, args);
    }

}
