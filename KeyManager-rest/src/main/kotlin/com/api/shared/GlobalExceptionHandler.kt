package com.api.shared

import com.google.rpc.BadRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.server.exceptions.ExceptionHandler
import org.slf4j.LoggerFactory
import java.util.stream.Collectors
import javax.inject.Singleton

@Singleton
class GlobalExceptionHandler : ExceptionHandler<StatusRuntimeException, HttpResponse<Any>> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handle(request: HttpRequest<*>, exception: StatusRuntimeException): HttpResponse<Any> {
        val status = io.grpc.protobuf.StatusProto.fromThrowable(exception)
        val statusCode = exception.status.code
        val statusDescription = exception.status.description ?: ""
        when (statusCode) {
            Status.INVALID_ARGUMENT.code -> {
                val details = if (status?.detailsList?.isEmpty() == true) {
                    return HttpResponse.badRequest("Dados da requisição estão inválidos")
                } else {
                    status?.detailsList?.get(0)?.unpack(BadRequest::class.java)
                }
                val body = details?.fieldViolationsList?.stream()!!.map {
                    ListaDetalhes(Status.INVALID_ARGUMENT, status!!.message, it.field, it.description)
                }.collect(Collectors.toList())
                return HttpResponse.badRequest(body)
            }
            Status.FAILED_PRECONDITION.code -> return HttpResponse.unprocessableEntity()
            Status.NOT_FOUND.code -> return HttpResponse.notFound(statusDescription)
            Status.ALREADY_EXISTS.code -> return HttpResponse.status(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Chave ja existente"
            )
            else -> {
                logger.error("Erro inesperado '${exception.javaClass.name}' ao processar requisição", exception)
                return HttpResponse.serverError("Nao foi possivel completar a requisição devido ao erro: ${statusDescription} (${statusCode})")
            }
        }

    }
}

data class ListaDetalhes(
    val codigo: Status,
    val message: String,
    val field: String,
    val descricao: String
)