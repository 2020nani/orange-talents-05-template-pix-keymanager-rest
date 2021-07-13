package com.api.registraChavePix

import com.api.KeyManagerGrpcRequest
import com.api.KeyManagerGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class RegistraChavePixController(
    @Inject val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun registraChavePix(
        clienteId: UUID,
        @Valid @Body request: NovaChavePixForm
    ): HttpResponse<Any> {
        logger.info("Registrando a chave para $request.chave")

        val responseGrpc = grpcClient.cadastraPixKey(request.converteGrpc(clienteId))

        logger.info("Chave '${request.chave}' registrada com sucesso")

        return HttpResponse.created(
            URI.create("/api/v1/clientes/$clienteId/pix/${responseGrpc.pixId}")
        )

    }
}