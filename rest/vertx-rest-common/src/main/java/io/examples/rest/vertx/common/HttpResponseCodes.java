package io.examples.rest.vertx.common;

/**
 * @author Gary Cheng
 */
public interface HttpResponseCodes {
    // Status code (200) indicating the request succeeded normally.
    int SC_OK = 200;

    //Status code (404) indicating that the requested resource is not available.
    int SC_NOT_FOUND = 404;

    // Status code (500) indicating an error inside the HTTP server which prevented it from fulfilling the request.
    int SC_INTERNAL_SERVER_ERROR = 500;
}
