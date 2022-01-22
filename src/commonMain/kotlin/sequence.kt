/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the
 * https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt file.
 *
 * The below code was modified from the original.
 */

package fordoun

import kotlin.coroutines.*

// see https://github.com/Kotlin/coroutines-examples/blob/master/examples/sequence/sequence.kt

@RestrictsSuspension
interface SequenceScope<in T> {
    suspend fun yield(value: T)
}

fun sequence(block: suspend DoController.() -> Unit): Sequence<Any> = Sequence {
    DoController().apply {
        nextStep = block.createCoroutine(receiver = this, completion = this)
    }
}

open class SequenceCoroutine<T> : AbstractIterator<T>(), SequenceScope<T>, Continuation<Unit> {
    lateinit var nextStep: Continuation<Unit>

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
        setNext(value)
        return suspendCoroutine { cont -> nextStep = cont }
    }
}
