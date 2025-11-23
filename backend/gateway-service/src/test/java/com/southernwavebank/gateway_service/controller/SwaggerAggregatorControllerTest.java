package com.southernwavebank.gateway_service.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

//import com.bankofindia.gateway_service.controller.SwaggerAggregatorController;

import reactor.core.publisher.Mono;

//@WebFluxTest(SwaggerAggregatorController.class)
//@Import(SwaggerAggregatorController.class)
//class SwaggerAggregatorControllerTest {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @MockitoBean
//    private DiscoveryClient discoveryClient;
//
//    @MockitoBean
//    private WebClient.Builder webClientBuilder;
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
//  
//    @Mock 
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpec;
//
////    @BeforeEach
////    void setup() {
////        when(webClientBuilder.build()).thenReturn(webClient);
////    }
//
//    @Test
//    void testGetServices() {
//        List<String> services = List.of("user-service", "account-service");
//        when(discoveryClient.getServices()).thenReturn(services);
//
//        webTestClient.get()
//            .uri("/swagger/services")
//            .exchange()
//            .expectStatus().isOk()
//            .expectBodyList(List.class) // Expecting a list of lists
//            .hasSize(1) // Expecting the outer list to contain one element
//            .value(response -> {
//                assertTrue(response instanceof List);
//                List<String> innerList = (List<String>) response.get(0); // Unwrap the inner list
//                assertEquals(2, innerList.size()); // Expecting the inner list to have 2 elements
//                assertTrue(innerList.contains("user-service"));
//                assertTrue(innerList.contains("account-service"));
//            });
//    }
//
//
//
//    @Test
//    void testGetSwaggerDocs_Success() {
//        when(discoveryClient.getServices()).thenReturn(List.of("user-service"));
//
//        Map<String, Object> mockSwaggerDoc = new HashMap<>();
//        mockSwaggerDoc.put("openapi", "3.0.1");
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//
//        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
//            .thenReturn(Mono.just(mockSwaggerDoc));
//
//        webTestClient.get()
//            .uri("/swagger/docs")
//            .exchange()
//            .expectStatus().isOk()
//            .expectBodyList(Map.class)
//            .consumeWith(result -> {
//                List<Map> docs = result.getResponseBody();
//                assertNotNull(docs);
//                assertEquals(1, docs.size());
//                assertEquals("3.0.1", docs.get(0).get("openapi"));
//                assertEquals("user-service", docs.get(0).get("service"));
//            });
//    }
//
//   @Test
//    void testGetSwaggerDocs_ServiceFailsGracefully() {
//        when(discoveryClient.getServices()).thenReturn(List.of("failing-service"));
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//
//        // Simulate failure when retrieving the docs
//        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
//            .thenReturn(Mono.error(new RuntimeException("Service down")));
//
//        webTestClient.get()
//            .uri("/swagger/docs")
//            .exchange()
//            .expectStatus().isOk()
//            .expectBodyList(Map.class)
//            .consumeWith(result -> {
//                List<Map> docs = result.getResponseBody();
//                assertNotNull(docs);
//                assertTrue(docs.isEmpty(), "Expected no swagger docs due to service error");
//            });
//    }
//
//}
