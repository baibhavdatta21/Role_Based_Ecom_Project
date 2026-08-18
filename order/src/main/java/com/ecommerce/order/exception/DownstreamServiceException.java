package com.ecommerce.order.exception;

public class DownstreamServiceException extends RuntimeException {

    private final String serviceName;

    public DownstreamServiceException(String serviceName, Throwable cause) {
        super(serviceName + " service is currently unavailable. Please try again later.", cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
