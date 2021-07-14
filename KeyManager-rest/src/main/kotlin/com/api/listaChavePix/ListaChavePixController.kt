package com.api.listaChavePix

import com.api.ListaChavesPixRequest
import com.api.ListaPixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/v1/clientes/{clienteId}")
class ListaChavePixController(
    private val listaGrpc: ListaPixKeyServiceGrpc.ListaPixKeyServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("/pix")
    fun listaChave(clienteId: UUID): MutableHttpResponse<Any>? {
        logger.info("Buscando chaves cliente: $clienteId")
        val request = ListaChavesPixRequest.newBuilder()
            .setClienteId(clienteId.toString())
            .build()
        val response = listaGrpc.listar(request)
        val chavesListadas = response.chavesList.map{ListaChavesResponse(it,clienteId)}

        return HttpResponse.ok(chavesListadas)
    }
}