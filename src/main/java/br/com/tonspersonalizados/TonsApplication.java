package br.com.tonspersonalizados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = "br.com.tonspersonalizados")
public class TonsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TonsApplication.class, args);
    }
}
