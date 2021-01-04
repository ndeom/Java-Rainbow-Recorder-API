package com.rainbowrecorder.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RainbowRecorderApiApplication {

	public static void main(String[] args) { SpringApplication.run(RainbowRecorderApiApplication.class, args); }

	@Bean
	ObjectMapper includeTransientObjectMapper() {
		Hibernate5Module module = new Hibernate5Module();
		module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		return mapper;
	}

}
