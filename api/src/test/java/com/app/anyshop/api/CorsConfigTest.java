package com.app.anyshop.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.app.anyshop.api.config.CorsConfig;

import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorsConfigTest {

    private CorsConfig corsConfig;

    @Mock
    private ServerWebExchange mockExchange;

    @Mock
    private ServerHttpRequest mockRequest;

    @Mock
    private ServerHttpResponse mockResponse;

    @Mock
    private WebFilterChain mockFilterChain;

    @Mock
    private HttpHeaders mockHeaders;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        corsConfig = new CorsConfig();
        when(mockExchange.getRequest()).thenReturn(mockRequest);
        when(mockExchange.getResponse()).thenReturn(mockResponse);
        when(mockResponse.getHeaders()).thenReturn(mockHeaders);
    }

    @Test
    void testCorsFilter_AllowsCorsRequest() {
        // Simulate a CORS request
        when(CorsUtils.isCorsRequest(mockRequest)).thenReturn(true);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockFilterChain.filter(mockExchange)).thenReturn(Mono.empty());

        WebFilter corsFilter = corsConfig.corsWebFilter();
        corsFilter.filter(mockExchange, mockFilterChain).block();

        // Verify headers are set correctly
        verify(mockHeaders).set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        verify(mockHeaders).set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
        verify(mockHeaders).set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "authorization, Content-Type, Authorization, credential, X-API-SECRET-TOKEN, X-API-USER-ID");
        verify(mockHeaders).set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        verify(mockFilterChain).filter(mockExchange);
    }

    @Test
    void testCorsFilter_OptionsRequest_ReturnsEmpty() {
        // Simulate a CORS preflight request
        when(CorsUtils.isCorsRequest(mockRequest)).thenReturn(true);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.OPTIONS);

        WebFilter corsFilter = corsConfig.corsWebFilter();
        Mono<Void> result = corsFilter.filter(mockExchange, mockFilterChain);

        // Verify response status is set to OK and no further processing
        result.block();
        verify(mockResponse).setStatusCode(HttpStatus.OK);
        verify(mockFilterChain, never()).filter(mockExchange);
    }

    @Test
    void testCorsFilter_NonCorsRequest_DelegatesToFilterChain() {
        // Simulate a non-CORS request
        when(CorsUtils.isCorsRequest(mockRequest)).thenReturn(false);
        when(mockFilterChain.filter(mockExchange)).thenReturn(Mono.empty());

        WebFilter corsFilter = corsConfig.corsWebFilter();
        corsFilter.filter(mockExchange, mockFilterChain).block();

        // Verify headers are not set and filter chain proceeds
        verify(mockHeaders, never()).set(anyString(), anyString());
        verify(mockFilterChain).filter(mockExchange);
    }
}
