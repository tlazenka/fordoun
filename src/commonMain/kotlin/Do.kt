/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the
 * https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt file.
 *
 * The below code was modified from the original.
 */

package fordoun

import kotlin.coroutines.*
import kotlin.experimental.*

// see https://github.com/Kotlin/coroutines-examples/blob/master/examples/sequence/sequence.kt

@RestrictsSuspension
interface SequenceScope<in T> {
    suspend fun yield(value: T)
}

@OptIn(ExperimentalTypeInference::class)
fun sequence(@BuilderInference block: suspend DoController.() -> Unit): Sequence<Any> = Sequence {
    DoController().apply {
        nextStep = block.createCoroutine(receiver = this, completion = this)
    }
}

open class SequenceCoroutine<T> : AbstractIterator<T>(), SequenceScope<T>, Continuation<Unit> {
    lateinit var nextStep: Continuation<Unit>

    var yielded: T? = null

    // AbstractIterator implementation
    override fun computeNext() { nextStep.resume(Unit) }

    // Completion continuation implementation
    override val context: CoroutineContext get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow() // bail out on error
        done()
    }

    // Generator implementation
    override suspend fun yield(value: T) {
        this.yielded = value
        setNext(value)
        return suspendCoroutine { cont -> nextStep = cont }
    }
}

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

fun <T> returns(t: T): Result<T> {
    return Result.success(value = t)
}

class DoController : SequenceCoroutine<Any>() {
    suspend fun <T> returns(t: T) {
        this.yield(value = t as Any)
    }

    suspend fun <T> bind(m: Result<T>): T {
        this.yield(value = m)
        @Suppress("UNCHECKED_CAST")
        return (this.yielded as Result<T>).getOrThrow()
    }
}
