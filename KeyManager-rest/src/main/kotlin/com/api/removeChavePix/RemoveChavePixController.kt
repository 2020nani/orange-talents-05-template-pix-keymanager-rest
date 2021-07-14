package com.api.removeChavePix

import com.api.PixKeyChaveRequest
import com.api.RemovePixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class RemoveChavePixController(
    private val grpcClient: RemovePixKeyServiceGrpc.RemovePixKeyServiceBlockingStub

) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Delete("/pix/{pixId}")
    fun removeChavePix(clienteId: UUID, pixId: UUID): MutableHttpResponse<Any>? {
        logger.info("Iniciando requisicao de deletar chavePix: $pixId, $clienteId")

        grpcClient.excluiPixKey(
            PixKeyChaveRequest.newBuilder()
                .setIdCliente(clienteId.toString())
                .setPixId(pixId.toString())
                .build()
        )

        return HttpResponse.ok()
    }
}