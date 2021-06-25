package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.codec.StringDecoder;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@SpringBootApplication
public class ServiceApplication {

	// <1>
	private final String CLIENT_ID_HEADER = "client-id";

	private final String CLIENT_ID_VALUE = "messaging/x.bootiful."
		+ CLIENT_ID_HEADER;

	private final MimeType CLIENT_ID = MimeType.valueOf(CLIENT_ID_VALUE);

	@Bean
	RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
		return strategies -> strategies//
			.metadataExtractorRegistry(registry -> {
				registry.metadataToExtract(CLIENT_ID, String.class, CLIENT_ID_HEADER);
			})//
			.decoders(decoders -> decoders.add(StringDecoder.allMimeTypes()));
	}

	private Mono<Void> enumerate(Map<String, Object> headers) {
		headers.forEach((header, value) -> System.out.println( header + ':' + value));
		return Mono.empty();
	}

	@MessageMapping("hello")
	Mono<Void> message(@Header(CLIENT_ID_HEADER) String clientId,
																				@Headers Map<String, Object> metadata) {
		System.out.println("## message for " + CLIENT_ID_HEADER + ' ' + clientId);
		return enumerate(metadata);
	}


	@MessageMapping("hello.{name}")
	String hello(@DestinationVariable String name) {
		return "Hello, " + name + "!";
	}

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

}
