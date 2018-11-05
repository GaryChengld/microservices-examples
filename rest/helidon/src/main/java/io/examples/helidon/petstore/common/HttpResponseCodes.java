package io.examples.helidon.petstore.common;

/**
 * HTTP server status code, see RFC 2068
 *
 * @author Gary Cheng
 */
public interface HttpResponseCodes {
    int SC_OK = 200;
    int SC_BAD_REQUEST = 400;
    int SC_NOT_FOUND = 404;
    int SC_INTERNAL_SERVER_ERROR = 500;
    int SC_SERVICE_UNAVAILABLE = 503;
}
