package br.com.douglasffilho.webclientpagination.client

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Slf4j
@Component
class UaaClient implements ExchangeFilterFunction {
    private static final String ORGANIZATION_HEADER = 'x-blz-organization'
    private static final String DEFAULT_ORGANIZATION = "1"
    private static final String AUTHORIZATION_HEADER = 'Authorization'
    private final WebClient client
    private final String clientId
    private final String clientSecret

    UaaClient(
            ClientLogger clientLogger,
            @Value('${api.gateway}') final String apiUrl,
            @Value('${security.oauth2.client-id}') final String clientId,
            @Value('${security.oauth2.client-secret}') final String clientSecret
    ) {
        this.clientId = clientId
        this.clientSecret = clientSecret

        this.client = WebClient
                .builder()
                .baseUrl("$apiUrl/uaa")
                .filter(clientLogger)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build()
    }

    @Override
    Mono<ClientResponse> filter(final ClientRequest request, final ExchangeFunction next) {
        final ClientRequest requestWithOrganizationHeader = appendOrganizationHeader(request)
        return appendOAuth2ClientToken(requestWithOrganizationHeader, next)
    }

    private static ClientRequest appendOrganizationHeader(final ClientRequest request) {
        return ClientRequest
                .from(request)
                .header(ORGANIZATION_HEADER, DEFAULT_ORGANIZATION)
                .build()
    }

    private Mono<ClientResponse> appendOAuth2ClientToken(final ClientRequest request, final ExchangeFunction next) {
        final String basicAuthToken = Base64Utils.encodeToString("$clientId:$clientSecret".bytes)

        return client
                .post()
                .uri('/oauth/token?grant_type=client_credentials')
                .header(AUTHORIZATION_HEADER, "Basic $basicAuthToken")
                .exchange()
                .flatMap { response ->
                    if (response.statusCode() != OK)
                        return next.exchange(request)

                    return response.bodyToMono(Map).flatMap { bodyAsMap ->
                        if (bodyAsMap['access_token']) {
                            final ClientRequest requestWithToken = ClientRequest
                                    .from(request)
                                    .header(AUTHORIZATION_HEADER, "Bearer ${bodyAsMap['access_token']}")
                                    .build()

                            return next.exchange(requestWithToken)
                        }

                        return next.exchange(request)
                    }
                }
    }

}
