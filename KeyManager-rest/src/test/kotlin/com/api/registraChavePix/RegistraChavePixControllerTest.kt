package com.api.registraChavePix

import com.api.KeyManagerGrpcResponse
import com.api.KeyManagerGrpcServiceGrpc
import com.api.shared.GrpcFactory.KeyManagerGrpcFactory
import org.junit.jupiter.api.Assertions.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Answers
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistraChavePixControllerTest {


    @field:Inject
    lateinit var registraStub: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

    /*
    * Instancia um client http para fazer requisicao
    */
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve registrar uma nova chave pix`() {

        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        /*retorno que sera mockado no stub do arquivo proto*/
        val respostaGrpc = KeyManagerGrpcResponse.newBuilder()
            .setIdCliente(clienteId)
            .setPixId(pixId)
            .build()

        given(registraStub.cadastraPixKey(Mockito.any())).willReturn(respostaGrpc)


        val novaChavePix = NovaChavePixForm(
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            chave = "teste@teste.com.br",
            tipoDeChave = TipoDeChave.EMAIL
        )

        val request = HttpRequest.POST("/api/v1/clientes/$clienteId/pix", novaChavePix)
        val response = client.toBlocking().exchange(request, NovaChavePixForm::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixId))
    }

    @Test
    internal fun `nao deve registrar uma nova chave pix se dados invalidos`() {

        val clienteId = UUID.randomUUID().toString()
        val novaChavePix = NovaChavePixForm(
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            chave = "teste@teste.com.br",
            tipoDeChave = TipoDeChave.CPF
        )

        val request = HttpRequest.POST("/api/v1/clientes/$clienteId/pix", novaChavePix)
        val thrown = assertThrows<HttpClientResponseException> {
            val response = client.toBlocking().exchange(request, NovaChavePixForm::class.java)
        }
        with(thrown) {
            assertEquals(400, status.code)
            assertEquals("Bad Request", this.status.reason)
        }

    }

    /*
    * Substitui o stub e mocka o retorno contido arquivo proto
    *
    */
    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub::class.java)
    }


}
