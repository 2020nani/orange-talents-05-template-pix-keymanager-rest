package com.api.carregaChavePix

import com.api.CarregaChavePixResponse
import com.api.CarregaPixKeyServiceGrpc
import com.api.TipoChave
import com.api.TipoConta
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
internal class CarregaChavePixControllerTest{

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
        val respostaGrpc = carregaChavePixResponse(clienteId,pixId)


        BDDMockito.given(carregaStub.carrega(Mockito.any())).willReturn(respostaGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())

    }

    @Test
    internal fun `deve retornar erro ao consultar e trazer os dados de uma chave pix`() {

        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        Mockito.`when`(carregaStub.carrega(Mockito.any())).thenThrow(HttpStatusException(HttpStatus.NOT_FOUND, "A chave não existe"))

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, response.status)
        assertEquals(response.message,"A chave não existe" )

    }


    private fun carregaChavePixResponse(clienteId: String, pixId: String) =
        CarregaChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .setChavePix(CarregaChavePixResponse.ChavePix
                .newBuilder()
                .setTipo(TipoChave.EMAIL)
                .setChave("teste@Gmail.com")
                .setConta(CarregaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                    .setTipo(TipoConta.CONTA_CORRENTE)
                    .setInstituicao("Itau SA")
                    .setNomeDoTitular("hernani almeida")
                    .setCpfDoTitular("33333333333")
                    .setAgencia("001")
                    .setNumeroDaConta("123456")
                    .build()
                )
                .setCriadaEm(LocalDateTime.now().let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })).build()


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

