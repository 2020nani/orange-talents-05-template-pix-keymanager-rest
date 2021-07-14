package com.api.listaChavePix

import com.api.*
import com.api.registraChavePix.TipoDeChave
import com.api.registraChavePix.TipoDeConta
import com.api.shared.GrpcFactory.KeyManagerGrpcFactory
import com.google.protobuf.Timestamp
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListaChavePixControllerTest {

    @field:Inject
    lateinit var listaStub: ListaPixKeyServiceGrpc.ListaPixKeyServiceBlockingStub


    /*
    * Instancia um client http para fazer requisicao
    */
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve listar todas as chavesPix do cliente por id `() {

        val clienteId = UUID.randomUUID().toString()


        /*retorno que sera mockado no stub do arquivo proto*/
        val respostaGrpc = listaChavePixResponse(clienteId)
        println(respostaGrpc.toString())

        BDDMockito.given(listaStub.listar(Mockito.any())).willReturn(respostaGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(response.body().size, 2)

    }


    private fun listaChavePixResponse(clienteId: String): ListaChavesPixResponse {

        val chaveEmail = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoChave(TipoChave.EMAIL)
            .setChave("teste@gmail.com")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadaEm(
                LocalDateTime.now().let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                }
            )
            .build()

        val chaveCelular = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoChave(TipoChave.CELULAR)
            .setChave("+55149967088")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadaEm(
                LocalDateTime.now().let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build() }
            )
            .build()

        return ListaChavesPixResponse.newBuilder()
            .setClienteId(clienteId)
            .addAllChaves(listOf(chaveCelular,chaveEmail))
            .build()
    }


    /*
    * Substitui o stub e mocka o retorno contido arquivo proto
    *
    */
    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() = Mockito.mock(ListaPixKeyServiceGrpc.ListaPixKeyServiceBlockingStub::class.java)
    }
}