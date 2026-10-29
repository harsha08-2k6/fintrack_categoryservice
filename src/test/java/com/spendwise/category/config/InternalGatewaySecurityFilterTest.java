package com.spendwise.category.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InternalGatewaySecurityFilterTest {

    @Test
    @DisplayName("Direct access without internal gateway key returns 403 Forbidden")
    void testDirectAccessWithoutKeyReturns403() throws ServletException, IOException {
        InternalGatewaySecurityFilter filter = new InternalGatewaySecurityFilter();
        ReflectionTestUtils.setField(filter, "internalGatewayKey", "prod-secret-key-999");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/categories");
        request.addHeader("X-User-Id", "10");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("Direct microservice access denied"));
    }

    @Test
    @DisplayName("Access with valid internal gateway key passes filter")
    void testAccessWithKeyPasses() throws ServletException, IOException {
        InternalGatewaySecurityFilter filter = new InternalGatewaySecurityFilter();
        ReflectionTestUtils.setField(filter, "internalGatewayKey", "prod-secret-key-999");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/categories");
        request.addHeader("X-Internal-Gateway-Key", "prod-secret-key-999");
        request.addHeader("X-User-Id", "10");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Actuator health check passes through without internal key")
    void testActuatorPassesThrough() throws ServletException, IOException {
        InternalGatewaySecurityFilter filter = new InternalGatewaySecurityFilter();
        ReflectionTestUtils.setField(filter, "internalGatewayKey", "prod-secret-key-999");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
    }
}

