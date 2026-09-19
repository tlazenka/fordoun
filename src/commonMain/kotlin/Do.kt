@file:OptIn(ExperimentalJsExport::class)

package fordoun

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

inline fun <reified T> `do`(
    noinline block: suspend SequenceScope<Result<T>>.() -> Unit
): Result<T> {
    var result: Result<T>? = null
    for (value in sequence(block = block)) {
        if (value.isSuccess) {
            result = value
        } else {
            return value
        }
    }
    return result ?: throw AssertionError("Invalid do block")
}

fun <T> returns(value: T): Result<T> {
    return Result.success(value = value)
}

@JsExport fun <T> jsReturns(value: T) = returns(value = value)

@JsExport
suspend fun <T> SequenceScope<Result<T>>.returns(value: T) {
    this.yield(value = Result.success(value))
}

@JsExport
suspend fun <T> SequenceScope<Result<T>>.bind(value: Result<T>): T {
    if (value.isFailure) {
        this.yield(value = value)
    }
    return value.getOrThrow()
}
