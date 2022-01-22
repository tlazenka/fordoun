package fordoun

inline fun <reified T> `do`(noinline block: suspend DoController.() -> Unit): Result<T> {
    for (value in sequence(block = block)) {
        if (value is Result<*>) {
            if (value.isSuccess) {
                continue
            } else {
                @Suppress("UNCHECKED_CAST")
                return value as Result<T>
            }
        } else {
            return Result.success(value = value as T)
        }
    }
    throw AssertionError("Invalid do block")
}

fun <T> returns(value: T): Result<T> {
    return Result.success(value = value)
}

class DoController : SequenceCoroutine<Any>() {
    suspend fun <T> returns(value: T) {
        this.yield(value = value as Any)
    }

    suspend fun <T> bind(value: Result<T>): T {
        this.yield(value = value)
        @Suppress("UNCHECKED_CAST")
        return value.getOrThrow()
    }
}
