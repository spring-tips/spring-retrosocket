package com.example.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.retrosocket.EnableRSocketClients;
import org.springframework.retrosocket.RSocketClient;
import reactor.core.publisher.Mono;

@EnableRSocketClients
@SpringBootApplication
public class ClientApplication {

	@Bean
	RSocketRequester two(RSocketRequester.Builder builder) {
		return builder.tcp("localhost", 9001);
	}

	@Bean
	@Qualifier("one")
	RSocketRequester one(RSocketRequester.Builder builder) {
		return builder.tcp("localhost", 9001);
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Bean
	ApplicationRunner runOne (GreetingClient gc) {
		return events -> gc.greet("World").subscribe(System.out::println);
	}
	@Bean
	ApplicationRunner runTwo (GreetingClient gc) {
		return events -> gc.greetWithClientId( "jlong","World").subscribe(System.out::println);
	}

}

@Qualifier("one")
@RSocketClient
interface GreetingClient {

	@MessageMapping("hello.{name}")
	Mono<String> greet(@DestinationVariable String name);

	@MessageMapping("hello")
	Mono<String> greetWithClientId(
		@Header("messaging/x.bootiful.client-id") String clientId,
		@Payload  String  name
	);

}