package br.com.douglasffilho.webclientpagination

import br.com.douglasffilho.webclientpagination.service.DataWarehouseService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

import static reactor.core.scheduler.Schedulers.elastic

@SpringBootApplication
class WebclientPaginationApplication {
    Logger log = LoggerFactory.getLogger(WebclientPaginationApplication)

    static void main(String[] args) {
        SpringApplication.run(WebclientPaginationApplication, args)
    }

    @Bean
    CommandLineRunner testGetOrdersInvoiced10Days(final DataWarehouseService dataWarehouseService) {
        return { args ->
            dataWarehouseService
                    .findAllInvoiced10Days()
                    .publishOn(elastic())
                    .subscribe { order ->
                        log.info('order: {}', order)
                    }
        }
    }

}
