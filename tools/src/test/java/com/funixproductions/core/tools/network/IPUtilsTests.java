package com.funixproductions.core.tools.network;

import com.funixproductions.core.exceptions.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class IPUtilsTests {

    @Test
    void testGetClientIpNotProxied() throws Exception {
        final String ipClient = "182.10.6.2";
        final IPUtils ipUtils = new IPUtils(false);
        final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.setRemoteAddr(ipClient);
        assertEquals(ipClient, ipUtils.getClientIp(httpServletRequest));
    }

    @Test
    void testGetClientIpBehindOneProxy() throws Exception {
        final String proxyIp = "127.0.0.1";
        final String clientIp = "182.10.6.2";
        final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        final IPUtils ipUtilsProxied = new IPUtils(true);
        final IPUtils ipUtilsNotProxied = new IPUtils(false);

        httpServletRequest.setRemoteAddr(proxyIp);
        httpServletRequest.addHeader(IPUtils.HEADER_X_FORWARDED, clientIp);
        assertEquals(clientIp, ipUtilsProxied.getClientIp(httpServletRequest));
        assertEquals(proxyIp, ipUtilsNotProxied.getClientIp(httpServletRequest));
    }

    @Test
    void testGetClientIpBehindMultipleProxy() throws Exception {
        final String firstProxy = "127.0.0.1";
        final String secondProxy = "127.0.0.2";
        final String clientIp = "182.10.6.2";
        final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        final IPUtils ipUtilsProxied = new IPUtils(true);
        final IPUtils ipUtilsNotProxied = new IPUtils(false);

        httpServletRequest.setRemoteAddr(secondProxy);
        httpServletRequest.addHeader(IPUtils.HEADER_X_FORWARDED, String.format("%s, %s", clientIp, firstProxy));
        assertEquals(clientIp, ipUtilsProxied.getClientIp(httpServletRequest));
        assertEquals(secondProxy, ipUtilsNotProxied.getClientIp(httpServletRequest));
    }

    @Test
    void testLocalhostIp() throws Exception {
        final IPUtils ipUtils = new IPUtils(false);

        assertTrue(ipUtils.isLocalClient("127.10.5.3"));
        assertFalse(ipUtils.isLocalClient("120.10.5.3"));
        assertTrue(ipUtils.isLocalClient("10.0.1.2"));
        assertTrue(ipUtils.isLocalClient("172.18.0.1"));
        assertTrue(ipUtils.isLocalClient("172.18.0.10"));
        assertFalse(ipUtils.isLocalClient("8.8.8.8"));
        assertFalse(ipUtils.isLocalClient("9.9.9.9"));
    }

    @Test
    void testInvalidIp() {
        final IPUtils ipUtils = new IPUtils(false);

        try {
            assertTrue(ipUtils.isLocalClient("8.8.8.8.1.1.1.1.1."));
            fail("The ip should not wwork.");
        } catch (ApiException ignored) {
        }
    }

}
