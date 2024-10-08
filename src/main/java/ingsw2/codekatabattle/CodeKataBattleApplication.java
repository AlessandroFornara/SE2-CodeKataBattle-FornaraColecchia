package ingsw2.codekatabattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class of the Spring application
 * @version 1.0-RELEASE
 */
@SpringBootApplication
@EnableScheduling
public class CodeKataBattleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeKataBattleApplication.class, args);
	}

}
