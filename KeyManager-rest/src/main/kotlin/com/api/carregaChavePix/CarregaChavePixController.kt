package com.api.carregaChavePix


import com.api.CarregaChavePixRequest
import com.api.CarregaPixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject


@Controller("/api/v1/clientes/{clienteId}")
class CarregaChavePixController(
   @Inject val carregaGrpc: CarregaPixKeyServiceGrpc.CarregaPixKeyServiceBlockingStub
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/{pixId}")
    fun registraChavePix(
        clienteId: UUID, pixId:UUID): HttpResponse<Any>{
        logger.info("Buscando dados chavePix cliente: $clienteId")

        val response = carregaGrpc.carrega(CarregaChavePixRequest.newBuilder()
            .setPixId(CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                .setClienteId(clienteId.toString())
                .setPixId(pixId.toString())
                .build())
            .build()
        )

        logger.info("Busca completada com sucesso")

        return HttpResponse.ok(DetalhesContaResponse(response))
    }
}