package com.unir.gateway.decorator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.gateway.model.GatewayRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import java.net.URI;

/**
 * This class is a decorator for the GatewayRequest object for POST, PATCH, PUT, DELETE requests.
 * It extends the ServerHttpRequestDecorator class and overrides its methods to modify the request.
 * It uses the ObjectMapper to convert the body of the GatewayRequest object into bytes.
 */
@Slf4j
public class GeneralRequestDecorator extends ServerHttpRequestDecorator {

    private final GatewayRequest gatewayRequest;
    private final ObjectMapper objectMapper;
    private final HttpMethod httpMethod;

    public GeneralRequestDecorator(GatewayRequest gatewayRequest, ObjectMapper objectMapper, HttpMethod httpMethod) {
        super(gatewayRequest.getExchange().getRequest());
        this.gatewayRequest = gatewayRequest;
        this.objectMapper = objectMapper;
        this.httpMethod = httpMethod;
    }

    /**
     * This method overrides the getMethod method of the ServerHttpRequestDecorator class.
     * It returns the HTTP method of the request.
     *
     * @return the HTTP method of the request
     */
    @Override
    @NonNull
    public HttpMethod getMethod() {
        return httpMethod;
    }

    /**
     * This method overrides the getURI method of the ServerHttpRequestDecorator class.
     * It returns the URI of the request.
     *
     * @return the URI of the request
     */
    @Override
    @NonNull
    public URI getURI() {
        return UriComponentsBuilder.fromUri((URI) gatewayRequest.getExchange()
                .getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)).build().toUri();
    }

    /**
     * This method overrides the getHeaders method of the ServerHttpRequestDecorator class.
     * It returns the headers of the request.
     *
     * @return the headers of the request
     */
    @Override
    @NonNull
    public HttpHeaders getHeaders() {
        return gatewayRequest.getHeaders();
    }

    /**
     * This method overrides the getBody method of the ServerHttpRequestDecorator class.
     * It converts the body of the GatewayRequest object into bytes using the ObjectMapper, and returns it as a Flux of DataBuffers.
     *
     * @return a Flux of DataBuffers representing the body of the request
     */
    @Override
    @NonNull
    @SneakyThrows
    public Flux<DataBuffer> getBody() {
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        byte[] bodyData = objectMapper.writeValueAsBytes(gatewayRequest.getBody());
        DataBuffer buffer = bufferFactory.allocateBuffer(bodyData.length);
        buffer.write(bodyData);
        return Flux.just(buffer);
    }
}
