package com.southernwavebank.gateway_service.filter;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.southernwavebank.gateway_service.filter.AuthenticationFilter;
import com.southernwavebank.gateway_service.reponse.Response;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    private WebClient mockWebClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;


    @Mock
    private ObjectMapper objectMapper;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        mockWebClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(mockWebClient);
        authenticationFilter = new AuthenticationFilter(webClientBuilder);
        ReflectionTestUtils.setField(authenticationFilter, "objectMapper", objectMapper);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "/auth/register", "/auth/login", "/auth/validate", "/auth/logout"
    })
    void shouldBypassAuthForPublicEndpoints(String path) {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, ex -> Mono.empty()))
                .verifyComplete();
    }

    @Test
    void shouldRequireAuthForOtherPaths() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/secure/feature").build());
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, ex -> Mono.empty()))
                .verifyComplete();

        // Expect 401 if no Authorization header
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldInjectUserHeadersWhenTokenValid() throws Exception {
        String validToken = "valid-token";
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .build());

        // Setup WebClient mock
        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("{\"data\":{\"role\":\"OFFICER\",\"userId\":\"user123\"}}"));

        // Setup ObjectMapper mock
        Response<Map<String, Object>> mockResponse = new Response<>();
        Map<String, Object> data = new HashMap<>();
        data.put("role", "OFFICER");
        data.put("userId", "user123");
        mockResponse.setData(data);
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(mockResponse);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, exchange1 -> {
                    HttpHeaders headers = exchange1.getRequest().getHeaders();
                    Assertions.assertEquals("OFFICER", headers.getFirst("X-User-Role"));
                    Assertions.assertEquals("user123", headers.getFirst("X-User-Id"));
                    return Mono.empty();
                }))
                .verifyComplete();
    }


    @Test
    void shouldNotAddUserIdWhenNull() throws Exception {
        String validToken = "valid-token";

        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .build());

        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("{\"data\":{\"role\":\"OFFICER\"}}"));

        Response<Map<String, Object>> mockResponse = new Response<>();
        Map<String, Object> data = new HashMap<>();
        data.put("role", "OFFICER");
        mockResponse.setData(data);
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(mockResponse);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, exchange1 -> {
                    HttpHeaders headers = exchange1.getRequest().getHeaders();
                    Assertions.assertNull(headers.getFirst("X-User-Id"));
                    return Mono.empty();
                }))
                .verifyComplete();
    }

    @Test
    void shouldHandleNullResponseFromAuthService() throws Exception {
        String token = "valid-token";

        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build());

        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("{}")); // will deserialize to null

        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(null);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, exchange1 -> Mono.empty()))
                .verifyComplete();
    }

    @Test
    void shouldHandleResponseWithNullData() throws Exception {
        String token = "valid-token";

        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build());

        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("{}"));

        Response<Map<String, Object>> mockResponse = new Response<>();
        mockResponse.setData(null); // simulate null data
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(mockResponse);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, exchange1 -> Mono.empty()))
                .verifyComplete();
    }

    @Test
    void shouldReturnResponseFails() throws Exception {
        String validToken = "valid-token";
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .build());

        // Setup WebClient mock
        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.just("{invalid-json-response}"));

        // Simulate exception during parsing
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenThrow(new RuntimeException("Parsing failed"));

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, ex -> Mono.empty()))
                .verifyComplete();

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
    }


    @Test
    void shouldReturnUnauthorizedWhenAuthServiceFails() {
        String failingToken = "failing-token";
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + failingToken)
                .build());

        // Setup WebClient mock for .get(), .uri(), .header()
        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Simulate Auth Service failure here
        when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.error(new RuntimeException("Auth Service down")));

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, exchange1 -> Mono.empty()))
                .verifyComplete();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }


}


