package br.com.douglasffilho.webclientpagination.client

import br.com.douglasffilho.webclientpagination.model.DataWarehouseClientResponse
import br.com.douglasffilho.webclientpagination.model.DataWarehouseOrder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Component
class DataWarehouseClient {
    private final WebClient client

    DataWarehouseClient(
            final UaaClient uaaClient,
            final ClientLogger clientLogger,
            @Value('${api.gateway}') final String apiUrl
    ) {
        this.client = WebClient
                .builder()
                .baseUrl("$apiUrl/datawarehouse")
                .filters { filters ->
                    filters.add(uaaClient)
                    filters.add(clientLogger)
                }
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build()
    }

    Mono<List<DataWarehouseOrder>> findInvoicedPaginated(
            final Integer page,
            final Integer size,
            final Integer invoiceDelayedDays) {
        return this.client
                .get()
                .uri { uriBuilder ->
                    return uriBuilder
                            .path('/orders/invoice-delayed')
                            .queryParam('days', invoiceDelayedDays)
                            .queryParam('size', size)
                            .queryParam('page', page)
                            .build([:])
                }
                .exchange()
                .flatMap { response ->
                    if (response.statusCode() == NOT_FOUND)
                        return Mono.just([])

                    return response
                            .bodyToMono(DataWarehouseClientResponse)
                            .map { dataWarehouseClientResponse ->
                                return dataWarehouseClientResponse.items
                            }
                }
    }

}
