package com.api.carregaChavePix

import com.api.CarregaChavePixResponse
import com.api.CarregaPixKeyServiceGrpc
import com.api.TipoChave
import com.api.TipoConta
import com.api.registraChavePix.TipoDeConta
import com.google.protobuf.Timestamp
import com.api.shared.GrpcFactory.KeyManagerGrpcFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.http.hateoas.JsonError
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class CarregaChavePixControllerTest {

    @field:Inject
    lateinit var carregaStub: CarregaPixKeyServiceGrpc.CarregaPixKeyServiceBlockingStub


    /*
    * Instancia um client http para fazer requisicao
    */
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve consultar e trazer os dados de uma chave pix`() {

        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        /*retorno que sera mockado no stub do arquivo proto*/
        val respostaGrpc = carregaChavePixResponse(clienteId, pixId)


        BDDMockito.given(carregaStub.carrega(Mockito.any())).willReturn(respostaGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)
        println(response)
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())

    }

    private fun carregaChavePixResponse(clienteId: String, pixId: String) =
        CarregaChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .setChavePix(CarregaChavePixResponse.ChavePix
                .newBuilder()
                .setTipo(TipoChave.EMAIL)
                .setChave("her@gmail.com")
                .setConta(
                    CarregaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                        .setTipo(TipoConta.CONTA_CORRENTE)
                        .setInstituicao("ITA\\303\\232 UNIBANCO S.A.")
                        .setNomeDoTitular("hernani almeida")
                        .setCpfDoTitular("02467781054")
                        .setAgencia("0001")
                        .setNumeroDaConta("291900")
                        .build()
                )
                .setCriadaEm(LocalDateTime.now().let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
            ).build()


    /*
    * Substitui o stub e mocka o retorno contido arquivo proto
    *
    */
    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() = Mockito.mock(CarregaPixKeyServiceGrpc.CarregaPixKeyServiceBlockingStub::class.java)
    }
}

