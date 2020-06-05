package br.com.douglasffilho.webclientpagination.client

import br.com.douglasffilho.webclientpagination.model.DataWarehouseClientResponse
import br.com.douglasffilho.webclientpagination.model.DataWarehouseOrder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Component
class DataWarehouseClient {
    private static final String apiUrl = '<API>'
    private static final String DYNAMIC_AUTH_TOKEN = '<TOKEN>'

    private static final Integer DEFAULT_INVOICED_DAYS = 10
    private static final Integer DEFAULT_REQUEST_SIZE = 100

    private static final Integer DEFAULT_ORGANIZATION_ID = 1

    private final WebClient client

    DataWarehouseClient() {
        this.client = WebClient
                .builder()
                .baseUrl(API_URL)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build()
    }

    Mono<List<DataWarehouseOrder>> findInvoicedPaginated(final Integer page) {
        return this.client
                .get()
                .uri { uriBuilder ->
                    return uriBuilder
                            .path('/datawarehouse/orders/invoice-delayed')
                            .queryParam('days', DEFAULT_INVOICED_DAYS)
                            .queryParam('size', DEFAULT_REQUEST_SIZE)
                            .queryParam('page', page)
                            .build([:])
                }
                .header('Authorization', DYNAMIC_AUTH_TOKEN) //melhor quando é colocado por request interceptor
                .header('x-blz-organization', "$DEFAULT_ORGANIZATION_ID")
                .exchange()
                .flatMap { response ->
                    return response
                            .bodyToMono(DataWarehouseClientResponse)
                            .map { dataWarehouseClientResponse ->
                                return dataWarehouseClientResponse.items
                            }
                }
    }

}
