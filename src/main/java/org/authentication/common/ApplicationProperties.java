package org.authentication.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    private static String serviceUrlTransport;

    public static String getServiceUrlTransport() {
        return serviceUrlTransport;
    }

    @Value("${serviceUrl.transport}")
    public void setServiceUrlTransport(String serviceUrlTransport) {
        this.serviceUrlTransport = serviceUrlTransport;
    }
}
