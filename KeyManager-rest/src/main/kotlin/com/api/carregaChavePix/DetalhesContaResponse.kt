package com.api.carregaChavePix

import com.api.CarregaChavePixResponse
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class DetalhesContaResponse(response: CarregaChavePixResponse) {

    val pixId = response.pixId
    val tipoChave = response.chavePix.tipo.toString()
    val chave = response.chavePix.chave
    val tipoConta = response.chavePix.conta.tipo.toString()
    val criadaEm = response.chavePix.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
    val agencia = response.chavePix.conta.agencia
    val numeroConta = response.chavePix.conta.numeroDaConta
    val cpfTitular = response.chavePix.conta.cpfDoTitular
    val nomeTitular = response.chavePix.conta.nomeDoTitular
    val nomeInstituicao = response.chavePix.conta.instituicao
}
