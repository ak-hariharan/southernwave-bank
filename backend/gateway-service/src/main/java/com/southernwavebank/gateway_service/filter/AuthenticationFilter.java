package com.southernwavebank.gateway_service.filter;

import static com.southernwavebank.gateway_service.util.ServicePathUtils.*;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.southernwavebank.gateway_service.reponse.Response;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
	
	@Autowired
    private RouteValidator routeValidator;
	
	@Autowired
	private ObjectMapper objectMapper;

	private final WebClient webClient;
	
	public static class Config {
	}

	public AuthenticationFilter(WebClient.Builder webClientBuilder) {
		super(Config.class); 
		this.webClient = webClientBuilder.build();
	}
	
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String path = exchange.getRequest().getURI().getPath();
			log.debug("Incoming request path");

			// Allow public endpoints without authentication
			if (routeValidator.isPublic(path)) {
				log.debug("Bypassing authentication for public access endpoint");
				return chain.filter(exchange);
			}

			// Extract token
			String token = extractToken(exchange);
			if (token == null) {
				log.warn("Missing Authorization token in request headers");
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			log.debug("Validating token with Auth Service");
			return webClient
					.get()
					.uri("http://auth-service/swb/auth/validate")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.exchangeToMono(response -> {
						if(response.statusCode().is2xxSuccessful()) {
							log.debug("Token validation successful");
							String targetService = getTargetServiceFromPath(path);
							if (targetService == null) {
								log.warn("Target service could not be identified from path: {}", path);
								
								String msg = "Target service could not be identified from path";
								return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, msg);
							}
							log.debug("Request is going to: {}", targetService);
							
							return webClient.get()
									.uri("http://auth-service/swb/auth/delegate-token?aud=" + targetService)
									.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
									.retrieve()
									.onStatus(HttpStatusCode::isError, clientResponse -> { 
										log.warn("Delegation failed with status: {}", clientResponse.statusCode());
										return Mono.error(new RuntimeException("Delegation failed with status: " + clientResponse.statusCode()));
									})
									.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
									.flatMap(map -> {
										String delegatedToken = map.get("token");
	
										if (delegatedToken == null) {
											log.warn("Delegated token is null from Auth Service");
											HttpStatusCode status = response.statusCode();
											String msg = "Delegated token is null from Auth Service and it returned: " 
				                                     + status.toString();
											return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, msg);
										}
	
										ServerHttpRequest.Builder modifiedRequestBuilder = exchange.getRequest().mutate()
												.header(HttpHeaders.AUTHORIZATION, "Bearer " + delegatedToken);

	
										log.debug("Forwarding request with delegated token to: {}", targetService);
										return chain
												.filter(exchange.mutate().request(modifiedRequestBuilder.build()).build());
									});
							
						}
						else {
	                        log.warn("Token validation failed: {}", response.statusCode());
	                        HttpStatusCode status = response.statusCode();
	                        String msg = "Token validation failed. Auth service returned: " 
	                                     + status.toString();
	                        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, msg);
	                    }
						
					})
					
					.onErrorResume(error -> {
						log.error("Token validation or delegation token failed: {}", error.getMessage(), error);
						return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token validation or delegation token failed");

					});
		};
	}


	private String extractToken(ServerWebExchange exchange) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
			return null;
		}
		return headers.getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
	}


	private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
		log.info("Writing error response");
	    exchange.getResponse().setStatusCode(status);
	    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

	    Response<Object> errorResponse = Response.builder()
	            .message(message)
	            .data(null)
	            .build();

	    try {
	        byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
	        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
	        return exchange.getResponse().writeWith(Mono.just(buffer));
	    } catch (JsonProcessingException e) {
	        // fallback to plain text
	    	log.info("Error while writing the writer response");
	        DataBuffer buffer = exchange.getResponse().bufferFactory()
	                .wrap(("Serialization error: " + e.getMessage()).getBytes());
	        return exchange.getResponse().writeWith(Mono.just(buffer));
	    }
	}
}
