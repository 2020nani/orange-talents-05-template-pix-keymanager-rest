package com.api.removeChavePix

import com.api.ExcluiPixKeyResponse
import com.api.KeyManagerGrpcResponse
import com.api.KeyManagerGrpcServiceGrpc
import com.api.RemovePixKeyServiceGrpc
import com.api.registraChavePix.NovaChavePixForm
import com.api.registraChavePix.TipoDeChave
import com.api.registraChavePix.TipoDeConta
import com.api.shared.GrpcFactory.KeyManagerGrpcFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RemoveChavePixControllerTest {

    @field:Inject
    lateinit var deleteStub: RemovePixKeyServiceGrpc.RemovePixKeyServiceBlockingStub

    /*
    * Instancia um client http para fazer requisicao
    */
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve remover chave pix`() {

        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        /*retorno que sera mockado no stub do arquivo proto*/
        val respostaGrpc = ExcluiPixKeyResponse.newBuilder()
            .setMessage("Chave Pix Deletada com sucesso")
            .build()

        BDDMockito.given(deleteStub.excluiPixKey(Mockito.any())).willReturn(respostaGrpc)


        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)

    }

    /*
    * Substitui o stub e mocka o retorno contido arquivo proto
    *
    */
    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() = Mockito.mock(RemovePixKeyServiceGrpc.RemovePixKeyServiceBlockingStub::class.java)
    }
}