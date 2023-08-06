package me.jangjunha.ftgo.kitchen_service;

import io.eventuate.common.json.mapper.JSonMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KitchenServiceApplication {

	public static void main(String[] args) {
		JSonMapper.objectMapper.findAndRegisterModules();
		SpringApplication.run(KitchenServiceApplication.class, args);
	}

}
