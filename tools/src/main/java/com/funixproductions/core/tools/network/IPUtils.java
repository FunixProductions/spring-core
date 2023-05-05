package com.funixproductions.core.tools.network;

import com.funixproductions.core.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RequiredArgsConstructor
public class IPUtils {

    public static final String HEADER_X_FORWARDED = "X-FORWARDED-FOR";

    private final boolean appProxied;

    /**
     * Can access to the API. If is present in whitelist
     * @param ip ip string fetrched from request
     * @return bool access or not
     * @throws ApiException if ip is not valid
     */
    public boolean isLocalClient(final String ip) throws ApiException {
        try {
            final InetAddress inetAddress = InetAddress.getByName(ip);
            return isIpLocalAddress(inetAddress);
        } catch (UnknownHostException e) {
            throw new ApiException(String.format("L'adresse ip entrée %s est invalide.", ip), e);
        }
    }

    /**
     * Get the client IP address from the request.
     * @param request http request to fetch ip
     * @return IP in string
     */
    @NonNull
    public String getClientIp(final HttpServletRequest request) {
        final String addressHeader = request.getHeader(HEADER_X_FORWARDED);
        final String remoteAddress;

        if (!appProxied) {
            remoteAddress = request.getRemoteAddr();
        } else {
            if (Strings.isEmpty(addressHeader)) {
                remoteAddress = request.getRemoteAddr();
            } else {
                final String[] addresses = addressHeader.split(",");

                if (addresses.length > 0) {
                    remoteAddress = addresses[0].replace(" ", "");
                } else {
                    remoteAddress = request.getRemoteAddr();
                }
            }
        }

        return remoteAddress;
    }

    private boolean isIpLocalAddress(final InetAddress ip) throws ApiException {
        return ip.isLoopbackAddress() || ip.isSiteLocalAddress();
    }

}
