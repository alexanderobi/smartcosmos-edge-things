package net.smartcosmos.edge.things.rest.template;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class SmartCosmosRequestTest {

    @Test
    public void buildRequest() {

        final HttpMethod expectedMethod = HttpMethod.GET;
        final String expectedBody = "body";
        final String serviceName = "serviceName";
        final String url = "url";

        String uriString = String.format("http://%s/%s", serviceName, url);
        final URI expectedUri = URI.create(uriString);

        SmartCosmosRequest<String> request = SmartCosmosRequest.builder()
            .requestBody(expectedBody)
            .serviceName(serviceName)
            .httpMethod(expectedMethod)
            .url(url)
            .build();

        RequestEntity<?> requestEntity = request.buildRequest();

        assertEquals(expectedMethod, requestEntity.getMethod());
        assertEquals(expectedBody, requestEntity.getBody());
        assertEquals(expectedUri, requestEntity.getUrl());
    }

    @Test
    public void buildRequestRemovesTrailingSlash() {

        final HttpMethod expectedMethod = HttpMethod.GET;
        final String expectedBody = "body";
        final String serviceName = "serviceName";
        final String url = "url";

        String uriString = String.format("http://%s/%s", serviceName, url);
        final URI expectedUri = URI.create(uriString);

        SmartCosmosRequest<String> request = SmartCosmosRequest.builder()
            .requestBody(expectedBody)
            .serviceName(serviceName)
            .httpMethod(expectedMethod)
            .url("/" + url)
            .build();

        RequestEntity<?> requestEntity = request.buildRequest();

        assertEquals(expectedMethod, requestEntity.getMethod());
        assertEquals(expectedBody, requestEntity.getBody());
        assertEquals(expectedUri, requestEntity.getUrl());
    }
}
