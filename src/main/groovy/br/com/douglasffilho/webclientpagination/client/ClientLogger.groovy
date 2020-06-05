package br.com.douglasffilho.webclientpagination.client

import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

trait ClientLogger implements ExchangeFilterFunction {

    abstract void log(final ClientRequest request)

    Mono<ClientResponse> filter(final ClientRequest request, final ExchangeFunction next) {
        log(request)
        return next.exchange(request)
    }

}