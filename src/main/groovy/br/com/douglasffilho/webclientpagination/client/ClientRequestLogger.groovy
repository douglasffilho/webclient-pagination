package br.com.douglasffilho.webclientpagination.client

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest

@Slf4j
@Component
class ClientRequestLogger implements ClientLogger {
    private final Boolean enabled

    ClientRequestLogger(
            @Value('${client-logger.enabled}') final Boolean clientLoggerEnabled
    ) {
        this.enabled = clientLoggerEnabled
    }

    @Override
    void log(final ClientRequest request) {
        if (enabled) {
            final StringBuilder sb = new StringBuilder()
            sb.append('\n')
            sb.append("--uri: ${request.url()}")

            request.headers().each { name, values ->
                values.each { value ->
                    sb.append('\n')
                    sb.append("--header: $name = $value")
                }
            }

            log.info(sb.toString())
        }
    }

}
