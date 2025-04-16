package com.groo.asoccer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //Jpa Auditing <<< 활성화를 위함 (생설일 수정일 자동 주입)
@SpringBootApplication
public class AsoccerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsoccerApplication.class, args);
	}

}
