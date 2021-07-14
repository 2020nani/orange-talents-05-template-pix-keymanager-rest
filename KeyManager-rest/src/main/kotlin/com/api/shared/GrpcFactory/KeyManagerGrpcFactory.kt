package com.api.shared.GrpcFactory

import com.api.CarregaPixKeyServiceGrpc
import com.api.KeyManagerGrpcServiceGrpc
import com.api.ListaPixKeyServiceGrpc
import com.api.RemovePixKeyServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class KeyManagerGrpcFactory(@GrpcChannel("KeyManager") val channel: ManagedChannel) {

    @Singleton
    fun registraChave() = KeyManagerGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChave() = RemovePixKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultaChave() = CarregaPixKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaPixKey() = ListaPixKeyServiceGrpc.newBlockingStub(channel)

}