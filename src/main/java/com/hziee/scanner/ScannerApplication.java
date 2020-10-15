package com.hziee.scanner;

import com.hziee.scanner.domain.tools.Test4;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScannerApplication.class, args);
        Test4 t = new Test4();
        String port = t.init(9600);

        try {
            t.connect(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
