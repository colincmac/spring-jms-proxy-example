package com.example.servicebus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;


@SpringBootApplication
@EnableJms
public class ServicebusApplication implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicebusApplication.class);
	private static final String TOPIC_NAME = "testtopic";
	private static final String SUBSCRIPTION_NAME = "demo-subscription";

	// @Autowired
	// private ServiceBusHelper serviceBusHelper;

	@Autowired
	private JmsTemplate template;


	public static void main(String[] args) {
		SpringApplication.run(ServicebusApplication.class, args);
	}

		@Override
		public void run(String... args) {
				LOGGER.info("Sending message");
				template.convertAndSend(TOPIC_NAME, "Hello, World!");
		}

		@JmsListener(destination = TOPIC_NAME, containerFactory = "topicJmsListenerContainerFactory",
		subscription = SUBSCRIPTION_NAME)
		public void receiveMessage(String message) {
			LOGGER.info("Message received: {}", message);
		}
}
