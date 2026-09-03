package com.example.taskflowapi;

import org.springframework.boot.SpringApplication;

public class TestTaskflowapiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TaskflowapiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
