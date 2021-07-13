package com.api.registraChavePix

import com.api.KeyManagerGrpcRequest
import com.api.TipoChave
import com.api.TipoConta
import com.api.shared.validationscustomizadas.ValidPixKey
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePixForm(
    @field:NotNull val tipoDeConta: TipoDeConta?,
    @field:Size(max = 77) val chave: String?,
    @field:NotNull val tipoDeChave: TipoDeChave?
) {

    fun converteGrpc(clienteId: UUID): KeyManagerGrpcRequest {
        return KeyManagerGrpcRequest.newBuilder()
            .setIdCliente(clienteId.toString())
            .setTipodeconta(TipoConta.valueOf(this.tipoDeConta!!.name) ?: TipoConta.UNKNOW_CONTA)
            .setTipodechave(TipoChave.valueOf(this.tipoDeChave!!.name) ?: TipoChave.UNKNOW_CHAVE)
            .setChave(chave ?: "")
            .build()
    }
}
