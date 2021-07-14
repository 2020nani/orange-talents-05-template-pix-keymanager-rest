package com.api.listaChavePix

import com.api.ListaChavesPixResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class ListaChavesResponse(it: ListaChavesPixResponse.ChavePix?, clienteId: UUID) {
    val clienteIdentificador = clienteId.toString()
    val pixId = it?.pixId
    val tipoChave = it?.tipoChave.toString()
    val chave = it?.chave
    val conta = it?.tipoConta.toString()
    val criadaEm = it?.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it!!.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}
/*
message ChavePix {
    string pixId            = 1;
    TipoChave tipoChave     = 2;
    string chave            = 3;
    TipoConta tipoConta     = 4;
    google.protobuf.Timestamp criadaEm = 5;
}

string clienteId         = 1;
repeated ChavePix chaves = 2;

 */