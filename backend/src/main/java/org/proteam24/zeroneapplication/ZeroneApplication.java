package org.proteam24.zeroneapplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEncryptableProperties
@EnableCaching
@EnableScheduling
public class ZeroneApplication {

	public static void main(String[] args){
		SpringApplication.run(ZeroneApplication.class, args);
	}
}
