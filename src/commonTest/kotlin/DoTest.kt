import fordoun.`do`
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DoTest {
    @Test
    fun testDoSuccess() {
        val result: Result<Int> = `do` {
            val result1 = bind(fordoun.returns(1))
            val result2 = bind(fordoun.returns(2))
            val result3 = bind(fordoun.returns(3))
            returns(result1 * result2 * result3)
        }
        assertEquals(Result.success(6), result)
    }

    @Test
    fun testDoSuccessWithoutBind() {
        val result: Result<Int> = `do` {
            val result1 = bind(fordoun.returns(1))
            val result2 = 2
            val result3 = bind(fordoun.returns(3))
            returns(result1 * result2 * result3)
        }
        assertEquals(Result.success(6), result)
    }

    @Test
    fun testMixedResult() {
        val result: Result<Any> = `do` {
            val result1 = bind(fordoun.returns("1"))
            val result2 = bind(fordoun.returns(2))
            val result3 = bind(fordoun.returns(3))
            returns(result1 + result2 + result3)
        }
        assertEquals(Result.success("123"), result)
    }

    @Test
    fun testDoFailure() {
        class DoException : Exception()
        val result: Result<Any> = `do` {
            val result1 = bind(fordoun.returns(1))
            val result2: Any = bind(Result.failure(DoException()))
            val result3 = bind(fordoun.returns(3))
            returns(result1 * result3)
        }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DoException)
    }

    @Test
    fun testEarlyReturn() {
        class DoException : Exception()
        class UnexpectedException : Exception()
        val result: Result<Any> = `do` {
            val result1 = bind(fordoun.returns(1))
            val result2: Any = bind(Result.failure(DoException()))
            throw UnexpectedException()
            val result3 = bind(fordoun.returns(3))
            returns(result1 * result3)
        }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DoException)
    }

    @Test
    fun testException() {
        class UnexpectedException : Exception()
        assertFailsWith<UnexpectedException> {
            val result: Result<Any> =
                `do` {
                    val result1 = bind(fordoun.returns(1))
                    val result2 = bind(fordoun.returns(2))
                    throw UnexpectedException()
                    val result3 = bind(fordoun.returns(3))
                    returns(result1 * result3)
                }
        }
    }

    @Test
    fun testInvalidDoBlockThrowsAssertionError() {
        class UnexpectedException : Exception()
        assertFailsWith<AssertionError> {
            val result: Result<Any> =
                `do` {
                }
        }
    }
}
