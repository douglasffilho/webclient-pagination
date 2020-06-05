package br.com.douglasffilho.webclientpagination.service

import br.com.douglasffilho.webclientpagination.client.DataWarehouseClient
import br.com.douglasffilho.webclientpagination.model.DataWarehouseOrder
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Slf4j
@Service
class DataWarehouseService {
    private static final Integer DEFAULT_FIRST_REQUEST_PAGE = 1
    private static final Integer DEFAULT_REQUEST_SIZE = 100
    private static final Integer DELAYED_INVOICED_TEN_DAYS = 10

    private final DataWarehouseClient dataWarehouseClient

    DataWarehouseService(final DataWarehouseClient dataWarehouseClient) {
        this.dataWarehouseClient = dataWarehouseClient
    }

    Flux<DataWarehouseOrder> findAllInvoiced10Days() {
        return this.findAllInvoicedPaginated(DEFAULT_FIRST_REQUEST_PAGE, DEFAULT_REQUEST_SIZE, DELAYED_INVOICED_TEN_DAYS)
    }

    private Flux<DataWarehouseOrder> findAllInvoicedPaginated(
            final Integer page,
            final Integer size,
            final Integer invoicedDelayedDays
    ) {
        log.info('getting invoiced orders for page={}', page)
        return this.dataWarehouseClient
                .findInvoicedPaginated(page, size, invoicedDelayedDays)
                .flux()
                .flatMap { dataWareHouseOrders ->
                    final Flux<DataWarehouseOrder> next = !dataWareHouseOrders
                            ? Flux.defer { Flux.empty() as Flux<DataWarehouseOrder> }
                            : Flux.defer { findAllInvoicedPaginated(page + 1, size, invoicedDelayedDays) }

                    return Flux.fromIterable(dataWareHouseOrders).concatWith(next)
                }
    }

}
