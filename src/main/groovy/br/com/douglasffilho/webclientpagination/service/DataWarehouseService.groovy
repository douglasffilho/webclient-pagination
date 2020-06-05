package br.com.douglasffilho.webclientpagination.service

import br.com.douglasffilho.webclientpagination.client.DataWarehouseClient
import br.com.douglasffilho.webclientpagination.model.DataWarehouseOrder
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Slf4j
@Service
class DataWarehouseService {
    private final DataWarehouseClient dataWarehouseClient

    DataWarehouseService(final DataWarehouseClient dataWarehouseClient) {
        this.dataWarehouseClient = dataWarehouseClient
    }

    Flux<DataWarehouseOrder> findAllInvoiced10Days() {
        return this.findAllFromPage(1)
    }

    private Flux<DataWarehouseOrder> findAllFromPage(final Integer page) {
        log.info('getting invoiced orders for page={}', page)
        return this.dataWarehouseClient
                .findInvoicedPaginated(page)
                .flux()
                .flatMap { dataWareHouseOrders ->
                    final Flux<DataWarehouseOrder> next = !dataWareHouseOrders
                            ? Flux.defer { Flux.empty() as Flux<DataWarehouseOrder> }
                            : Flux.defer { findAllFromPage(page + 1) }

                    return Flux.fromIterable(dataWareHouseOrders).concatWith(next)
                }
    }

}
