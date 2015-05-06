package com.cloudwatt.apis.bss.impl;

/**
 * Created by pierre souchay
 */
public class HttpStatusResolverEmpty {

    private final int httpStatus;

    /**
     * Constructor
     *
     * @param httpStatus
     */
    public HttpStatusResolverEmpty(final int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}