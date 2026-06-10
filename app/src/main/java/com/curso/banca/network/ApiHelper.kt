package com.curso.banca.network
import com.google.gson.Gson
import retrofit2.HttpException

data class ApiErrorBody(val error: String, val message: String)

class ApiException(
    val errorCode: String,
    message: String,
    val statusCode: Int
) : Exception(message)

private val gson = Gson()

suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    val raw = e.response()?.errorBody()?.string()
    val apiError = try {
        gson.fromJson(raw, ApiErrorBody::class.java)
    } catch (_: Exception) {
        ApiErrorBody("unknown_error", e.message ?: "Error desconocido")
    }
    Result.failure(ApiException(apiError.error, apiError.message, e.code()))
} catch (e: Exception) {
    Result.failure(e)
}


/** Igual que apiCall, pero para endpoints que pueden responder con cuerpo vacío (p. ej. DELETE). */
suspend fun apiCallSinCuerpo(block: suspend () -> retrofit2.Response<Unit>): Result<Unit> = try {
    val resp = block()
    if (resp.isSuccessful) {
        Result.success(Unit)
    } else {
        val raw = resp.errorBody()?.string()
        val apiError = try {
            gson.fromJson(raw, ApiErrorBody::class.java)
        } catch (_: Exception) {
            ApiErrorBody("unknown_error", "Error ${resp.code()}")
        }
        Result.failure(ApiException(apiError.error, apiError.message, resp.code()))
    }
} catch (e: Exception) {
    Result.failure(e)
}